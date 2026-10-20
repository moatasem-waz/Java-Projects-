import java.util.Scanner;

public class Calculator {
    private static double readNumber(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);

            if (scanner.hasNextDouble()) {
                return scanner.nextDouble();
            }

            System.out.println("الرجاء إدخال رقم صالح.");
            scanner.next(); // تجاهل الإدخال غير الصالح
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== آلة حاسبة بسيطة ===");

        while (true) {
            double first = readNumber(scanner, "أدخل الرقم الأول: ");

            System.out.print("أدخل العملية (+، -، *، /) أو q للخروج: ");
            String input = scanner.next();

            if (input.equalsIgnoreCase("q")) {
                System.out.println("تم إغلاق الآلة الحاسبة.");
                break;
            }

            if (input.length() != 1 || "+-*/".indexOf(input.charAt(0)) == -1) {
                System.out.println("عملية غير معروفة.\n");
                continue;
            }

            char operator = input.charAt(0);
            double second = readNumber(scanner, "أدخل الرقم الثاني: ");
            double result;

            switch (operator) {
                case '+':
                    result = first + second;
                    break;
                case '-':
                    result = first - second;
                    break;
                case '*':
                    result = first * second;
                    break;
                case '/':
                    if (second == 0) {
                        System.out.println("لا يمكن القسمة على صفر.\n");
                        continue;
                    }
                    result = first / second;
                    break;
                default:
                    // لا يُفترض الوصول إلى هذا الفرع بعد التحقق من العملية.
                    System.out.println("عملية غير صالحة.\n");
                    continue;
            }

            System.out.println("النتيجة: " + result + "\n");
        }

        scanner.close();
    }
}
