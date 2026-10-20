import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final Path FILE = Path.of("patients.txt");

    private static class Patient {
        int id;
        String name;
        int age;
        String diagnosis;

        Patient(int id, String name, int age, String diagnosis) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.diagnosis = diagnosis;
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

    private static List<Patient> loadPatients() {
        List<Patient> patients = new ArrayList<>();

        if (!Files.exists(FILE)) {
            return patients;
        }

        try {
            for (String line : Files.readAllLines(FILE, StandardCharsets.UTF_8)) {
                String[] parts = line.split("\\|", 4);

                if (parts.length != 4) {
                    continue;
                }

                try {
                    patients.add(new Patient(
                            Integer.parseInt(parts[0]),
                            decode(parts[1]),
                            Integer.parseInt(parts[2]),
                            decode(parts[3])
                    ));
                } catch (IllegalArgumentException ignored) {
                }
            }
        } catch (IOException e) {
            System.out.println("تعذرت قراءة ملف المرضى.");
        }

        return patients;
    }

    private static boolean savePatients(List<Patient> patients) {
        List<String> lines = new ArrayList<>();

        for (Patient patient : patients) {
            lines.add(
                    patient.id + "|" +
                    encode(patient.name) + "|" +
                    patient.age + "|" +
                    encode(patient.diagnosis)
            );
        }

        try {
            Files.write(FILE, lines, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.out.println("تعذر حفظ بيانات المرضى.");
            return false;
        }
    }

    private static int nextId(List<Patient> patients) {
        int id = 1;

        for (Patient patient : patients) {
            id = Math.max(id, patient.id + 1);
        }

        return id;
    }

    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("أدخل رقمًا صحيحًا.");
            }
        }
    }

    private static void addPatient(
            Scanner scanner,
            List<Patient> patients
    ) {
        System.out.print("اسم المريض: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("اسم المريض مطلوب.");
            return;
        }

        int age = readInt(scanner, "عمر المريض: ");

        if (age < 0 || age > 130) {
            System.out.println("أدخل عمرًا بين 0 و130.");
            return;
        }

        System.out.print("التشخيص أو سبب الزيارة: ");
        String diagnosis = scanner.nextLine().trim();

        if (diagnosis.isEmpty()) {
            System.out.println("التشخيص أو سبب الزيارة مطلوب.");
            return;
        }

        Patient patient = new Patient(
                nextId(patients),
                name,
                age,
                diagnosis
        );

        patients.add(patient);

        if (savePatients(patients)) {
            System.out.println("تمت إضافة المريض. رقمه: " + patient.id);
        }
    }

    private static void listPatients(List<Patient> patients) {
        if (patients.isEmpty()) {
            System.out.println("لا توجد بيانات للمرضى.");
            return;
        }

        for (Patient patient : patients) {
            System.out.printf(
                    "الرقم: %d | الاسم: %s | العمر: %d | التشخيص: %s%n",
                    patient.id,
                    patient.name,
                    patient.age,
                    patient.diagnosis
            );
        }
    }

    private static void searchPatient(
            Scanner scanner,
            List<Patient> patients
    ) {
        int id = readInt(scanner, "أدخل رقم المريض: ");

        for (Patient patient : patients) {
            if (patient.id == id) {
                System.out.printf(
                        "الرقم: %d | الاسم: %s | العمر: %d | التشخيص: %s%n",
                        patient.id,
                        patient.name,
                        patient.age,
                        patient.diagnosis
                );
                return;
            }
        }

        System.out.println("لم يتم العثور على المريض.");
    }

    private static void deletePatient(
            Scanner scanner,
            List<Patient> patients
    ) {
        int id = readInt(scanner, "أدخل رقم المريض المراد حذفه: ");

        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).id == id) {
                Patient removed = patients.remove(i);

                if (savePatients(patients)) {
                    System.out.println("تم حذف بيانات المريض: " + removed.name);
                }

                return;
            }
        }

        System.out.println("لم يتم العثور على المريض.");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Patient> patients = loadPatients();

        while (true) {
            System.out.println("\n=== نظام إدارة المستشفى ===");
            System.out.println("1. إضافة مريض");
            System.out.println("2. عرض جميع المرضى");
            System.out.println("3. البحث عن مريض");
            System.out.println("4. حذف مريض");
            System.out.println("0. خروج");

            int choice = readInt(scanner, "اختر: ");

            switch (choice) {
                case 1:
                    addPatient(scanner, patients);
                    break;
                case 2:
                    listPatients(patients);
                    break;
                case 3:
                    searchPatient(scanner, patients);
                    break;
                case 4:
                    deletePatient(scanner, patients);
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
