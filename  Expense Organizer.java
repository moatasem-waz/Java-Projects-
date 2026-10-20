import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

public class ExpenseOrganizer {
    private static final Path FILE = Path.of("expenses.txt");

    private static class Expense {
        int id;
        String description;
        double amount;

        Expense(int id, String description, double amount) {
            this.id = id;
            this.description = description;
            this.amount = amount;
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

    private static List<Expense> loadExpenses() {
        List<Expense> expenses = new ArrayList<>();

        if (!Files.exists(FILE)) {
            return expenses;
        }

        try {
            for (String line : Files.readAllLines(FILE, StandardCharsets.UTF_8)) {
                String[] parts = line.split("\\|", 3);

                if (parts.length != 3) {
                    continue;
                }

                try {
                    expenses.add(new Expense(
                            Integer.parseInt(parts[0]),
                            decode(parts[1]),
                            Double.parseDouble(parts[2])
                    ));
                } catch (IllegalArgumentException ignored) {
                }
            }
        } catch (IOException e) {
            System.out.println("تعذرت قراءة ملف المصروفات.");
        }

        return expenses;
    }

    private static boolean saveExpenses(List<Expense> expenses) {
        List<String> lines = new ArrayList<>();

        for (Expense expense : expenses) {
            lines.add(
                    expense.id + "|" +
                    encode(expense.description) + "|" +
                    expense.amount
            );
        }

        try {
            Files.write(FILE, lines, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.out.println("تعذر حفظ المصروفات.");
            return false;
        }
    }

    private static int nextId(List<Expense> expenses) {
        int id = 1;

        for (Expense expense : expenses) {
            id = Math.max(id, expense.id + 1);
        }

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

            System.out.println("أدخل مبلغًا صالحًا.");
            scanner.nextLine();
        }
    }

    private static void addExpense(Scanner scanner, List<Expense> expenses) {
        System.out.print("وصف المصروف: ");
        String description = scanner.nextLine().trim();

        if (description.isEmpty()) {
            System.out.println("وصف المصروف مطلوب.");
            return;
        }

        double amount = readDouble(scanner, "المبلغ: ");

        if (amount <= 0) {
            System.out.println("يجب أن يكون المبلغ أكبر من صفر.");
            return;
        }

        Expense expense = new Expense(nextId(expenses), description, amount);
        expenses.add(expense);

        if (saveExpenses(expenses)) {
            System.out.println("تمت إضافة المصروف. رقمه: " + expense.id);
        }
    }

    private static void listExpenses(List<Expense> expenses) {
        if (expenses.isEmpty()) {
            System.out.println("لا توجد مصروفات مسجلة.");
            return;
        }

        for (Expense expense : expenses) {
            System.out.printf(
                    "الرقم: %d | الوصف: %s | المبلغ: %.2f%n",
                    expense.id,
                    expense.description,
                    expense.amount
            );
        }
    }

    private static void showTotal(List<Expense> expenses) {
        double total = 0;

        for (Expense expense : expenses) {
            total += expense.amount;
        }

        System.out.printf("مجموع المصروفات: %.2f%n", total);
    }

    private static void deleteExpense(
            Scanner scanner,
            List<Expense> expenses
    ) {
        int id = readInt(scanner, "أدخل رقم المصروف المراد حذفه: ");

        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).id == id) {
                Expense removed = expenses.remove(i);

                if (saveExpenses(expenses)) {
                    System.out.println(
                            "تم حذف المصروف: " + removed.description
                    );
                }

                return;
            }
        }

        System.out.println("لم يتم العثور على المصروف.");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Expense> expenses = loadExpenses();

        while (true) {
            System.out.println("\n=== منظّم المصروفات ===");
            System.out.println("1. إضافة مصروف");
            System.out.println("2. عرض المصروفات");
            System.out.println("3. حساب مجموع المصروفات");
            System.out.println("4. حذف مصروف");
            System.out.println("0. خروج");

            int choice = readInt(scanner, "اختر: ");

            switch (choice) {
                case 1 -> addExpense(scanner, expenses);
                case 2 -> listExpenses(expenses);
                case 3 -> showTotal(expenses);
                case 4 -> deleteExpense(scanner, expenses);
                case 0 -> {
                    scanner.close();
                    return;
                }
                default -> System.out.println("اختيار غير صحيح.");
            }
        }
    }
}
