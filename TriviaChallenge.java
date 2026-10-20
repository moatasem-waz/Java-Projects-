import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class TriviaChallenge {
    private static class Question {
        String text;
        String[] choices;
        int correctAnswer;

        Question(String text, String[] choices, int correctAnswer) {
            this.text = text;
            this.choices = choices;
            this.correctAnswer = correctAnswer;
        }
    }

    private static List<Question> createQuestions() {
        List<Question> questions = new ArrayList<>();

        questions.add(new Question(
                "ما عاصمة اليابان؟",
                new String[]{"بكين", "طوكيو", "سيول", "بانكوك"},
                2
        ));

        questions.add(new Question(
                "كم عدد أضلاع المثلث؟",
                new String[]{"2", "3", "4", "5"},
                2
        ));

        questions.add(new Question(
                "ما أكبر كوكب في المجموعة الشمسية؟",
                new String[]{"الأرض", "المريخ", "زحل", "المشتري"},
                4
        ));

        questions.add(new Question(
                "ما ناتج 8 × 7؟",
                new String[]{"54", "56", "58", "64"},
                2
        ));

        questions.add(new Question(
                "أي حيوان يُعرف بلقب سفينة الصحراء؟",
                new String[]{"الحصان", "الجمل", "الفيل", "الغزال"},
                2
        ));

        questions.add(new Question(
                "ما اللغة المستخدمة لتنسيق صفحات الويب؟",
                new String[]{"HTML", "CSS", "SQL", "Bash"},
                2
        ));

        questions.add(new Question(
                "كم دقيقة في ساعتين؟",
                new String[]{"60", "100", "120", "180"},
                3
        ));

        questions.add(new Question(
                "ما اسم النجم الأقرب إلى الأرض؟",
                new String[]{"الشمس", "الشعرى اليمانية", "النسر الواقع", "سهيل"},
                1
        ));

        questions.add(new Question(
                "ما العنصر الذي يرمز له بالحرف O؟",
                new String[]{"الذهب", "الأكسجين", "الحديد", "الهيدروجين"},
                2
        ));

        questions.add(new Question(
                "كم عدد أيام الأسبوع؟",
                new String[]{"5", "6", "7", "8"},
                3
        ));

        return questions;
    }

    private static int readAnswer(Scanner scanner) {
        while (true) {
            System.out.print("إجابتك (1-4): ");
            String input = scanner.nextLine().trim();

            try {
                int answer = Integer.parseInt(input);
                if (answer >= 1 && answer <= 4) {
                    return answer;
                }
            } catch (NumberFormatException ignored) {
            }

            System.out.println("أدخل رقمًا من 1 إلى 4.");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Question> questions = createQuestions();
        Collections.shuffle(questions);

        int score = 0;

        System.out.println("=== لعبة تحدّي المعلومات ===");

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);

            System.out.println("\nالسؤال " + (i + 1) + ": " + question.text);

            for (int j = 0; j < question.choices.length; j++) {
                System.out.println((j + 1) + ". " + question.choices[j]);
            }

            int answer = readAnswer(scanner);

            if (answer == question.correctAnswer) {
                System.out.println("إجابة صحيحة!");
                score++;
            } else {
                System.out.println(
                        "إجابة غير صحيحة. الإجابة الصحيحة: " +
                        question.choices[question.correctAnswer - 1]
                );
            }
        }

        System.out.println("\nانتهت اللعبة.");
        System.out.println("نتيجتك: " + score + " من " + questions.size());

        if (score == questions.size()) {
            System.out.println("ممتاز! أجبت عن جميع الأسئلة بشكل صحيح.");
        } else if (score >= questions.size() / 2) {
            System.out.println("أداء جيد!");
        } else {
            System.out.println("حاول مرة أخرى لتحسين نتيجتك.");
        }

        scanner.close();
    }
}
