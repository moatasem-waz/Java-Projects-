import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class NumberMemoryGame {
    private static void showSequence(List<Integer> sequence) {
        System.out.print("احفظ هذه الأرقام: ");

        for (int number : sequence) {
            System.out.print(number + " ");
        }

        System.out.println();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        for (int i = 0; i < 30; i++) {
            System.out.println();
        }
    }

    private static String readAnswer(Scanner scanner) {
        while (true) {
            System.out.print("أدخل الأرقام بالترتيب، وبين كل رقم مسافة: ");
            String answer = scanner.nextLine().trim();

            if (!answer.isEmpty()) {
                return answer;
            }

            System.out.println("يجب إدخال الأرقام.");
        }
    }

    private static boolean isCorrect(List<Integer> sequence, String answer) {
        String[] parts = answer.split("\\s+");

        if (parts.length != sequence.size()) {
            return false;
        }

        for (int i = 0; i < parts.length; i++) {
            try {
                if (Integer.parseInt(parts[i]) != sequence.get(i)) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        List<Integer> sequence = new ArrayList<>();
        int level = 1;

        System.out.println("=== لعبة اختبار الذاكرة ===");
        System.out.println("احفظ الأرقام ثم أدخلها بالترتيب.");

        while (true) {
            sequence.add(random.nextInt(9) + 1);
            System.out.println("\nالمستوى: " + level);
            showSequence(sequence);

            String answer = readAnswer(scanner);

            if (!isCorrect(sequence, answer)) {
                System.out.println("\nإجابة غير صحيحة.");
                System.out.println("وصلت إلى المستوى: " + level);
                System.out.print("السلسلة الصحيحة كانت: ");

                for (int number : sequence) {
                    System.out.print(number + " ");
                }

                System.out.println();
                break;
            }

            System.out.println("صحيح! استعد للمستوى التالي.");
            level++;
        }

        scanner.close();
    }
}
