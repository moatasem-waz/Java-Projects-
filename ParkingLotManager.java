import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

public class ParkingLotManager {
    private static final int CAPACITY = 10;
    private static final Path FILE = Paths.get("parking.txt");

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

    private static List<String> loadCars() {
        List<String> cars = new ArrayList<>();

        for (int i = 0; i < CAPACITY; i++) {
            cars.add("");
        }

        if (!Files.exists(FILE)) {
            return cars;
        }

        try {
            for (String line : Files.readAllLines(FILE, StandardCharsets.UTF_8)) {
                String[] parts = line.split("\\|", 2);

                if (parts.length != 2) {
                    continue;
                }

                try {
                    int slot = Integer.parseInt(parts[0]);
                    String plate = decode(parts[1]);

                    if (slot >= 1 && slot <= CAPACITY) {
                        cars.set(slot - 1, plate);
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
        } catch (IOException e) {
            System.out.println("تعذرت قراءة ملف المواقف.");
        }

        return cars;
    }

    private static boolean saveCars(List<String> cars) {
        List<String> lines = new ArrayList<>();

        for (int i = 0; i < cars.size(); i++) {
            if (!cars.get(i).isEmpty()) {
                lines.add((i + 1) + "|" + encode(cars.get(i)));
            }
        }

        try {
            Files.write(FILE, lines, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.out.println("تعذر حفظ بيانات المواقف.");
            return false;
        }
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

    private static void showParking(List<String> cars) {
        int occupied = 0;

        System.out.println("\nحالة المواقف:");

        for (int i = 0; i < cars.size(); i++) {
            if (cars.get(i).isEmpty()) {
                System.out.println("الموقف " + (i + 1) + ": فارغ");
            } else {
                System.out.println(
                        "الموقف " + (i + 1) + ": " + cars.get(i)
                );
                occupied++;
            }
        }

        System.out.println(
                "المواقف المشغولة: " + occupied +
                " | المواقف المتاحة: " + (CAPACITY - occupied)
        );
    }

    private static void parkCar(Scanner scanner, List<String> cars) {
        System.out.print("أدخل رقم لوحة السيارة: ");
        String plate = scanner.nextLine().trim();

        if (plate.isEmpty()) {
            System.out.println("رقم اللوحة مطلوب.");
            return;
        }

        for (String car : cars) {
            if (car.equalsIgnoreCase(plate)) {
                System.out.println("هذه السيارة موجودة في الموقف بالفعل.");
                return;
            }
        }

        for (int i = 0; i < cars.size(); i++) {
            if (cars.get(i).isEmpty()) {
                cars.set(i, plate);

                if (saveCars(cars)) {
                    System.out.println(
                            "تم إدخال السيارة إلى الموقف رقم " + (i + 1)
                    );
                }

                return;
            }
        }

        System.out.println("المواقف ممتلئة.");
    }

    private static void removeCar(Scanner scanner, List<String> cars) {
        System.out.print("أدخل رقم لوحة السيارة المغادرة: ");
        String plate = scanner.nextLine().trim();

        for (int i = 0; i < cars.size(); i++) {
            if (cars.get(i).equalsIgnoreCase(plate)) {
                cars.set(i, "");

                if (saveCars(cars)) {
                    System.out.println("تم إخراج السيارة من الموقف.");
                }

                return;
            }
        }

        System.out.println("لم يتم العثور على السيارة.");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<String> cars = loadCars();

        while (true) {
            System.out.println("\n=== نظام مواقف السيارات ===");
            System.out.println("1. عرض المواقف");
            System.out.println("2. إدخال سيارة");
            System.out.println("3. إخراج سيارة");
            System.out.println("0. خروج");

            int choice = readInt(scanner, "اختر: ");

            switch (choice) {
                case 1:
                    showParking(cars);
                    break;
                case 2:
                    parkCar(scanner, cars);
                    break;
                case 3:
                    removeCar(scanner, cars);
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
