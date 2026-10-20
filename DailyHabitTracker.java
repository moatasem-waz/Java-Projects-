import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

public class DailyHabitTracker {
    private static final Path FILE = Path.of("habits.txt");

    private static class Habit {
        int id;
        String name;
        String lastCompletedDate;
        int completionCount;

        Habit(int id, String name, String lastCompletedDate, int completionCount) {
            this.id = id;
            this.name = name;
            this.lastCompletedDate = lastCompletedDate;
            this.completionCount = completionCount;
        }
    }

    private static String encode(String value) {
        return Base64.getEncoder()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(
                Base64.getDecoder().decode(value),
                StandardCharsets.UTF_8
        );
    }

    private static List<Habit> loadHabits() {
        List<Habit> habits = new ArrayList<>();

        if (!Files.exists(FILE)) {
            return habits;
        }

        try {
            for (String line : Files.readAllLines(FILE, StandardCharsets.UTF_8)) {
                String[] parts = line.split("\\|", 4);

                if (parts.length != 4) {
                    continue;
                }

                try {
                    habits.add(new Habit(
                            Integer.parseInt(parts[0]),
                            decode(parts[1]),
                            parts[2],
                            Integer.parseInt(parts[3])
                    ));
                } catch (IllegalArgumentException ignored) {
                }
            }
        } catch (IOException e) {
            System.out.println("تعذرت قراءة ملف العادات.");
        }

        return habits;
    }

    private static boolean saveHabits(List<Habit> habits) {
        List<String> lines = new ArrayList<>();

        for (Habit habit : habits) {
            lines.add(
                    habit.id + "|" +
                    encode(habit.name) + "|" +
                    habit.lastCompletedDate + "|" +
                    habit.completionCount
            );
        }

        try {
            Files.write(FILE, lines, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.out.println("تعذر حفظ العادات.");
            return false;
        }
    }

    private static int nextId(List<Habit> habits) {
        int next = 1;

        for (Habit habit : habits) {
            next = Math.max(next, habit.id + 1);
        }

        return next;
    }

    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);

            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("أدخل رقمًا صحيحًا.");
            }
        }
    }

    private static void addHabit(Scanner scanner, List<Habit> habits) {
        System.out.print("اسم العادة: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("اسم العادة مطلوب.");
            return;
        }

        Habit habit = new Habit(nextId(habits), name, "", 0);
        habits.add(habit);

        if (saveHabits(habits)) {
            System.out.println("تمت إضافة العادة. رقمها: " + habit.id);
        }
    }

    private static void listHabits(List<Habit> habits) {
        if (habits.isEmpty()) {
            System.out.println("لا توجد عادات مسجلة.");
            return;
        }

        String today = LocalDate.now().toString();

        for (Habit habit : habits) {
            String status = today.equals(habit.lastCompletedDate)
                    ? "تم إنجازها اليوم"
                    : "لم تُنجز اليوم";

            System.out.printf(
                    "الرقم: %d | العادة: %s | مرات الإنجاز: %d | الحالة: %s%n",
                    habit.id,
                    habit.name,
                    habit.completionCount,
                    status
            );
        }
    }

    private static void completeHabit(Scanner scanner, List<Habit> habits) {
        int id = readInt(scanner, "أدخل رقم العادة التي أنجزتها: ");
        String today = LocalDate.now().toString();

        for (Habit habit : habits) {
            if (habit.id == id) {
                if (today.equals(habit.lastCompletedDate)) {
                    System.out.println("تم تسجيل إنجاز هذه العادة اليوم بالفعل.");
                    return;
                }

                habit.lastCompletedDate = today;
                habit.completionCount++;

                if (saveHabits(habits)) {
                    System.out.println("أحسنت! تم تسجيل إنجاز العادة.");
                }

                return;
            }
        }

        System.out.println("لم يتم العثور على هذه العادة.");
    }

    private static void deleteHabit(Scanner scanner, List<Habit> habits) {
        int id = readInt(scanner, "أدخل رقم العادة المراد حذفها: ");

        for (int i = 0; i < habits.size(); i++) {
            if (habits.get(i).id == id) {
                habits.remove(i);

                if (saveHabits(habits)) {
                    System.out.println("تم حذف العادة.");
                }

                return;
            }
        }

        System.out.println("لم يتم العثور على هذه العادة.");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Habit> habits = loadHabits();

        while (true) {
            System.out.println("\n=== متتبّع العادات اليومية ===");
            System.out.println("1. إضافة عادة");
            System.out.println("2. عرض العادات");
            System.out.println("3. تسجيل إنجاز عادة اليوم");
            System.out.println("4. حذف عادة");
            System.out.println("0. خروج");

            int choice = readInt(scanner, "اختر: ");

            switch (choice) {
                case 1:
                    addHabit(scanner, habits);
                    break;
                case 2:
                    listHabits(habits);
                    break;
                case 3:
                    completeHabit(scanner, habits);
                    break;
                case 4:
                    deleteHabit(scanner, habits);
                    break;
                case 0:
                    scanner.close();
                    return;
                default:
                    System.out.println("اختيار غير صحيح.");
            }
        }
    }
}
