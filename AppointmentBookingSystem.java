import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

public class AppointmentBookingSystem {
    private static final Path FILE = Path.of("appointments.txt");

    private static class Appointment {
        int id;
        String name;
        String date;
        String time;
        String reason;

        Appointment(int id, String name, String date, String time, String reason) {
            this.id = id;
            this.name = name;
            this.date = date;
            this.time = time;
            this.reason = reason;
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

    private static List<Appointment> loadAppointments() {
        List<Appointment> appointments = new ArrayList<>();

        if (!Files.exists(FILE)) {
            return appointments;
        }

        try {
            for (String line : Files.readAllLines(FILE, StandardCharsets.UTF_8)) {
                String[] parts = line.split("\\|", 5);

                if (parts.length != 5) {
                    continue;
                }

                try {
                    appointments.add(new Appointment(
                            Integer.parseInt(parts[0]),
                            decode(parts[1]),
                            parts[2],
                            parts[3],
                            decode(parts[4])
                    ));
                } catch (IllegalArgumentException ignored) {
                }
            }
        } catch (IOException e) {
            System.out.println("تعذرت قراءة ملف المواعيد.");
        }

        return appointments;
    }

    private static boolean saveAppointments(List<Appointment> appointments) {
        List<String> lines = new ArrayList<>();

        for (Appointment appointment : appointments) {
            lines.add(
                    appointment.id + "|" +
                    encode(appointment.name) + "|" +
                    appointment.date + "|" +
                    appointment.time + "|" +
                    encode(appointment.reason)
            );
        }

        try {
            Files.write(FILE, lines, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.out.println("تعذر حفظ المواعيد.");
            return false;
        }
    }

    private static int nextId(List<Appointment> appointments) {
        int id = 1;

        for (Appointment appointment : appointments) {
            id = Math.max(id, appointment.id + 1);
        }

        return id;
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

    private static void addAppointment(
            Scanner scanner,
            List<Appointment> appointments
    ) {
        System.out.print("اسم المراجع: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("اسم المراجع مطلوب.");
            return;
        }

        System.out.print("التاريخ (yyyy-MM-dd): ");
        String dateInput = scanner.nextLine().trim();
        LocalDate date;

        try {
            date = LocalDate.parse(dateInput);
        } catch (DateTimeParseException e) {
            System.out.println("صيغة التاريخ غير صحيحة.");
            return;
        }

        System.out.print("الوقت (HH:mm): ");
        String timeInput = scanner.nextLine().trim();
        LocalTime time;

        try {
            time = LocalTime.parse(timeInput);
        } catch (DateTimeParseException e) {
            System.out.println("صيغة الوقت غير صحيحة.");
            return;
        }

        for (Appointment appointment : appointments) {
            if (appointment.date.equals(date.toString())
                    && appointment.time.equals(time.toString())) {
                System.out.println("يوجد موعد مسجل في هذا التاريخ والوقت.");
                return;
            }
        }

        System.out.print("سبب الموعد: ");
        String reason = scanner.nextLine().trim();

        if (reason.isEmpty()) {
            System.out.println("سبب الموعد مطلوب.");
            return;
        }

        Appointment appointment = new Appointment(
                nextId(appointments),
                name,
                date.toString(),
                time.toString(),
                reason
        );

        appointments.add(appointment);

        if (saveAppointments(appointments)) {
            System.out.println("تم حجز الموعد. رقمه: " + appointment.id);
        }
    }

    private static void listAppointments(List<Appointment> appointments) {
        if (appointments.isEmpty()) {
            System.out.println("لا توجد مواعيد مسجلة.");
            return;
        }

        for (Appointment appointment : appointments) {
            System.out.printf(
                    "الرقم: %d | المراجع: %s | التاريخ: %s | الوقت: %s | السبب: %s%n",
                    appointment.id,
                    appointment.name,
                    appointment.date,
                    appointment.time,
                    appointment.reason
            );
        }
    }

    private static void searchAppointment(
            Scanner scanner,
            List<Appointment> appointments
    ) {
        int id = readInt(scanner, "أدخل رقم الموعد: ");

        for (Appointment appointment : appointments) {
            if (appointment.id == id) {
                System.out.printf(
                        "الرقم: %d | المراجع: %s | التاريخ: %s | الوقت: %s | السبب: %s%n",
                        appointment.id,
                        appointment.name,
                        appointment.date,
                        appointment.time,
                        appointment.reason
                );
                return;
            }
        }

        System.out.println("لم يتم العثور على الموعد.");
    }

    private static void deleteAppointment(
            Scanner scanner,
            List<Appointment> appointments
    ) {
        int id = readInt(scanner, "أدخل رقم الموعد المراد حذفه: ");

        for (int i = 0; i < appointments.size(); i++) {
            if (appointments.get(i).id == id) {
                appointments.remove(i);

                if (saveAppointments(appointments)) {
                    System.out.println("تم حذف الموعد.");
                }

                return;
            }
        }

        System.out.println("لم يتم العثور على الموعد.");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Appointment> appointments = loadAppointments();

        while (true) {
            System.out.println("\n=== نظام حجز المواعيد ===");
            System.out.println("1. حجز موعد");
            System.out.println("2. عرض المواعيد");
            System.out.println("3. البحث عن موعد");
            System.out.println("4. حذف موعد");
            System.out.println("0. خروج");

            int choice = readInt(scanner, "اختر: ");

            switch (choice) {
                case 1:
                    addAppointment(scanner, appointments);
                    break;
                case 2:
                    listAppointments(appointments);
                    break;
                case 3:
                    searchAppointment(scanner, appointments);
                    break;
                case 4:
                    deleteAppointment(scanner, appointments);
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
