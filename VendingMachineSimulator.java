import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

public class VendingMachineSimulator {
    private static final Path FILE = Paths.get("products.txt");

    private static class Product {
        String name;
        double price;
        int stock;

        Product(String name, double price, int stock) {
            this.name = name;
            this.price = price;
            this.stock = stock;
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

    private static List<Product> defaultProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("ماء", 1.00, 10));
        products.add(new Product("عصير", 2.50, 8));
        products.add(new Product("شوكولاتة", 3.00, 6));
        products.add(new Product("رقائق بطاطس", 2.00, 7));
        return products;
    }

    private static List<Product> loadProducts() {
        if (!Files.exists(FILE)) {
            List<Product> products = defaultProducts();
            saveProducts(products);
            return products;
        }

        List<Product> products = new ArrayList<>();

        try {
            for (String line : Files.readAllLines(FILE, StandardCharsets.UTF_8)) {
                String[] parts = line.split("\\|", 3);

                if (parts.length != 3) {
                    continue;
                }

                try {
                    String name = decode(parts[0]);
                    double price = Double.parseDouble(parts[1]);
                    int stock = Integer.parseInt(parts[2]);

                    if (!name.isEmpty() && price > 0 && stock >= 0) {
                        products.add(new Product(name, price, stock));
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
        } catch (IOException e) {
            System.out.println("تعذرت قراءة ملف المنتجات.");
        }

        if (products.isEmpty()) {
            products = defaultProducts();
            saveProducts(products);
        }

        return products;
    }

    private static boolean saveProducts(List<Product> products) {
        List<String> lines = new ArrayList<>();

        for (Product product : products) {
            lines.add(
                    encode(product.name) + "|" +
                    product.price + "|" +
                    product.stock
            );
        }

        try {
            Files.write(FILE, lines, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.out.println("تعذر حفظ المنتجات.");
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

    private static double readAmount(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);

            try {
                double amount = Double.parseDouble(scanner.nextLine().trim());

                if (Double.isFinite(amount) && amount >= 0) {
                    return amount;
                }
            } catch (NumberFormatException ignored) {
            }

            System.out.println("أدخل مبلغًا صالحًا.");
        }
    }

    private static void showProducts(List<Product> products) {
        System.out.println("\n=== المنتجات ===");

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            System.out.printf(
                    "%d. %s | السعر: %.2f | المتوفر: %d%n",
                    i + 1,
                    product.name,
                    product.price,
                    product.stock
            );
        }
    }

    private static void buyProduct(
            Scanner scanner,
            List<Product> products
    ) {
        showProducts(products);
        int choice = readInt(scanner, "اختر رقم المنتج، أو 0 للعودة: ");

        if (choice == 0) {
            return;
        }

        if (choice < 1 || choice > products.size()) {
            System.out.println("رقم المنتج غير صحيح.");
            return;
        }

        Product product = products.get(choice - 1);

        if (product.stock <= 0) {
            System.out.println("هذا المنتج غير متوفر حاليًا.");
            return;
        }

        double payment = readAmount(scanner, "أدخل المبلغ المدفوع: ");

        if (payment < product.price) {
            System.out.printf(
                    "المبلغ غير كافٍ. السعر المطلوب: %.2f%n",
                    product.price
            );
            return;
        }

        product.stock--;

        if (!saveProducts(products)) {
            product.stock++;
            return;
        }

        System.out.println("تم شراء: " + product.name);
        System.out.printf("الباقي: %.2f%n", payment - product.price);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Product> products = loadProducts();

        while (true) {
            System.out.println("\n=== آلة البيع الذاتية ===");
            System.out.println("1. عرض المنتجات");
            System.out.println("2. شراء منتج");
            System.out.println("0. خروج");

            int choice = readInt(scanner, "اختر: ");

            switch (choice) {
                case 1:
                    showProducts(products);
                    break;
                case 2:
                    buyProduct(scanner, products);
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
