import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

public class TaskManager {
    private static final Path FILE = Path.of("tasks.txt");

    private static class Task {
        int id;
        String description;
        boolean completed;

        Task(int id, String description, boolean completed) {
            this.id = id;
            this.description = description;
            this.completed = completed;
        }
    }

    private static List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<>();

        if (!Files.exists(FILE)) {
            return tasks;
        }

        try {
            for (String line : Files.readAllLines(FILE, StandardCharsets.UTF_8)) {
                String[] parts = line.split("\\|", 3);
                if (parts.length != 3) {
                    continue;
                }

                try {
                    int id = Integer.parseInt(parts[0]);
                    boolean completed = Boolean.parseBoolean(parts[1]);
                    String description = new String(
                            Base64.getDecoder().decode(parts[2]),
                            StandardCharsets.UTF_8
                    );
                    tasks.add(new Task(id, description, completed));
                } catch (IllegalArgumentException ignored) {
                }
            }
        } catch (IOException e) {
            System.out.println("تعذر قراءة ملف المهام.");
        }

        return tasks;
    }

    private static boolean saveTasks(List<Task> tasks) {
        List<String> lines = new ArrayList<>();

        for (Task task : tasks) {
            String encoded = Base64.getEncoder().encodeToString(
                    task.description.getBytes(StandardCharsets.UTF_8)
            );
            lines.add(task.id + "|" + task.completed + "|" + encoded);
        }

        try {
            Files.write(FILE, lines, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.out.println("تعذر حفظ المهام.");
            return false;
        }
    }

    private static int nextId(List<Task> tasks) {
        int next = 1;
        for (Task task : tasks) {
            next = Math.max(next, task.id + 1);
        }
        return next;
    }

    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            }
            System.out.println("أدخل رقمًا صحيحًا.");
            scanner.nextLine();
        }
    }

    private static void addTask(Scanner scanner, List<Task> tasks) {
        System.out.print("وصف المهمة: ");
        String description = scanner.nextLine().trim();

        if (description.isEmpty()) {
            System.out.println("وصف المهمة مطلوب.");
            return;
        }

        Task task = new Task(nextId(tasks), description, false);
        tasks.add(task);

        if (saveTasks(tasks)) {
            System.out.println("تمت إضافة المهمة. رقمها: " + task.id);
        }
    }

    private static void listTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("لا توجد مهام.");
            return;
        }

        for (Task task : tasks) {
            String status = task.completed ? "مكتملة" : "غير مكتملة";
            System.out.println(task.id + ". [" + status + "] " + task.description);
        }
    }

    private static void searchTasks(Scanner scanner, List<Task> tasks) {
        System.out.print("اكتب كلمة للبحث: ");
        String query = scanner.nextLine().trim().toLowerCase();

        boolean found = false;
        for (Task task : tasks) {
            if (task.description.toLowerCase().contains(query)) {
                String status = task.completed ? "مكتملة" : "غير مكتملة";
                System.out.println(task.id + ". [" + status + "] " + task.description);
                found = true;
            }
        }

        if (!found) {
            System.out.println("لم يتم العثور على مهام مطابقة.");
        }
    }

    private static void completeTask(Scanner scanner, List<Task> tasks) {
        int id = readInt(scanner, "رقم المهمة المكتملة: ");

        for (Task task : tasks) {
            if (task.id == id) {
                task.completed = true;
                if (saveTasks(tasks)) {
                    System.out.println("تم تحديث المهمة.");
                }
                return;
            }
        }

        System.out.println("لم يتم العثور على هذه المهمة.");
    }

    private static void deleteTask(Scanner scanner, List<Task> tasks) {
        int id = readInt(scanner, "رقم المهمة المراد حذفها: ");

        boolean removed = tasks.removeIf(task -> task.id == id);
        if (!removed) {
            System.out.println("لم يتم العثور على هذه المهمة.");
            return;
        }

        if (saveTasks(tasks)) {
            System.out.println("تم حذف المهمة.");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Task> tasks = loadTasks();

        while (true) {
            System.out.println("\n=== مدير المهام ===");
            System.out.println("1. إضافة مهمة");
            System.out.println("2. عرض المهام");
            System.out.println("3. البحث عن مهمة");
            System.out.println("4. إكمال مهمة");
            System.out.println("5. حذف مهمة");
            System.out.println("0. خروج");

            int choice = readInt(scanner, "اختر: ");

            switch (choice) {
                case 1 -> addTask(scanner, tasks);
                case 2 -> listTasks(tasks);
                case 3 -> searchTasks(scanner, tasks);
                case 4 -> completeTask(scanner, tasks);
                case 5 -> deleteTask(scanner, tasks);
                case 0 -> {
                    scanner.close();
                    return;
                }
                default -> System.out.println("اختيار غير صحيح.");
            }
        }
    }
}
