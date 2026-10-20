import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

public class StudentManager {
    private static final Path FILE = Path.of("students.txt");

    private static class Student {
        int id;
        String name;
        double grade;

        Student(int id, String name, double grade) {
            this.id = id;
            this.name = name;
            this.grade = grade;
        }
    }

    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    private static List<Student> loadStudents() {
        List<Student> students = new ArrayList<>();

        if (!Files.exists(FILE)) return students;

        try {
            for (String line : Files.readAllLines(FILE, StandardCharsets.UTF_8)) {
                String[] parts = line.split("\\|", 3);
                if (parts.length != 3) continue;

                try {
                    students.add(new Student(
                            Integer.parseInt(parts[0]),
                            decode(parts[1]),
                            Double.parseDouble(parts[2])
                    ));
                } catch (IllegalArgumentException ignored) {
                }
            }
        } catch (IOException e) {
            System.out.println("تعذر قراءة ملف الطلاب.");
        }

        return students;
    }

    private static boolean saveStudents(List<Student> students) {
        List<String> lines = new ArrayList<>();

        for (Student student : students) {
            lines.add(student.id + "|" + encode(student.name) + "|" + student.grade);
        }

        try {
            Files.write(FILE, lines, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.out.println("تعذر حفظ بيانات الطلاب.");
            return false;
        }
    }

    private static int nextId(List<Student> students) {
        int id = 1;
        for (Student student : students) id = Math.max(id, student.id + 1);
        return id;
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

    private static double readDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextDouble()) {
                double value = scanner.nextDouble();
                scanner.nextLine();
                return value;
            }
            System.out.println("أدخل رقمًا صالحًا.");
            scanner.nextLine();
        }
    }

    private static void addStudent(Scanner scanner, List<Student> students) {
        System.out.print("اسم الطالب: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("اسم الطالب مطلوب.");
            return;
        }

        double grade = readDouble(scanner, "الدرجة من 0 إلى 100: ");
        if (grade < 0 || grade > 100) {
            System.out.println("يجب أن تكون الدرجة بين 0 و100.");
            return;
        }

        Student student = new Student(nextId(students), name, grade);
        students.add(student);

        if (saveStudents(students)) {
            System.out.println("تمت إضافة الطالب. رقمه: " + student.id);
        }
    }

    private static void listStudents(List<Student> students) {
        if (students.isEmpty()) {
            System.out.println("لا توجد بيانات للطلاب.");
            return;
        }

        for (Student student : students) {
            System.out.printf("الرقم: %d | الاسم: %s | الدرجة: %.2f%n",
                    student.id, student.name, student.grade);
        }
    }

    private static void searchStudent(Scanner scanner, List<Student> students) {
        int id = readInt(scanner, "أدخل رقم الطالب: ");

        for (Student student : students) {
            if (student.id == id) {
                System.out.printf("الرقم: %d | الاسم: %s | الدرجة: %.2f%n",
                        student.id, student.name, student.grade);
                return;
            }
        }

        System.out.println("لم يتم العثور على الطالب.");
    }

    private static void showAverage(List<Student> students) {
        if (students.isEmpty()) {
            System.out.println("لا توجد درجات لحساب المتوسط.");
            return;
        }

        double total = 0;
        for (Student student : students) total += student.grade;

        System.out.printf("متوسط درجات الطلاب: %.2f%n", total / students.size());
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Student> students = loadStudents();

        while (true) {
            System.out.println("\n=== نظام إدارة الطلاب ===");
            System.out.println("1. إضافة طالب");
            System.out.println("2. عرض الطلاب");
            System.out.println("3. البحث عن طالب");
            System.out.println("4. حساب متوسط الدرجات");
            System.out.println("0. خروج");

            int choice = readInt(scanner, "اختر: ");

            switch (choice) {
                case 1 -> addStudent(scanner, students);
                case 2 -> listStudents(students);
                case 3 -> searchStudent(scanner, students);
                case 4 -> showAverage(students);
                case 0 -> {
                    scanner.close();
                    return;
                }
                default -> System.out.println("اختيار غير صحيح.");
            }
        }
    }
}
