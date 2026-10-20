import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

public class LibrarySystem {
    private static final Path FILE = Path.of("books.txt");

    private static class Book {
        int id;
        String title;
        String author;
        boolean borrowed;

        Book(int id, String title, String author, boolean borrowed) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.borrowed = borrowed;
        }
    }

    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    private static List<Book> loadBooks() {
        List<Book> books = new ArrayList<>();

        if (!Files.exists(FILE)) return books;

        try {
            for (String line : Files.readAllLines(FILE, StandardCharsets.UTF_8)) {
                String[] parts = line.split("\\|", 4);
                if (parts.length != 4) continue;

                try {
                    books.add(new Book(
                            Integer.parseInt(parts[0]),
                            decode(parts[1]),
                            decode(parts[2]),
                            Boolean.parseBoolean(parts[3])
                    ));
                } catch (IllegalArgumentException ignored) {
                }
            }
        } catch (IOException e) {
            System.out.println("تعذر قراءة ملف الكتب.");
        }

        return books;
    }

    private static boolean saveBooks(List<Book> books) {
        List<String> lines = new ArrayList<>();

        for (Book book : books) {
            lines.add(book.id + "|" + encode(book.title) + "|"
                    + encode(book.author) + "|" + book.borrowed);
        }

        try {
            Files.write(FILE, lines, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.out.println("تعذر حفظ بيانات الكتب.");
            return false;
        }
    }

    private static int nextId(List<Book> books) {
        int id = 1;
        for (Book book : books) id = Math.max(id, book.id + 1);
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

    private static void addBook(Scanner scanner, List<Book> books) {
        System.out.print("عنوان الكتاب: ");
        String title = scanner.nextLine().trim();
        System.out.print("اسم المؤلف: ");
        String author = scanner.nextLine().trim();

        if (title.isEmpty() || author.isEmpty()) {
            System.out.println("العنوان واسم المؤلف مطلوبان.");
            return;
        }

        Book book = new Book(nextId(books), title, author, false);
        books.add(book);

        if (saveBooks(books)) {
            System.out.println("تمت إضافة الكتاب. رقمه: " + book.id);
        }
    }

    private static void listBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("لا توجد كتب مسجلة.");
            return;
        }

        for (Book book : books) {
            String status = book.borrowed ? "مُعار" : "متاح";
            System.out.println(book.id + ". " + book.title + " — "
                    + book.author + " [" + status + "]");
        }
    }

    private static void searchBooks(Scanner scanner, List<Book> books) {
        System.out.print("أدخل عنوان الكتاب أو اسم المؤلف: ");
        String query = scanner.nextLine().trim().toLowerCase();
        boolean found = false;

        for (Book book : books) {
            if (book.title.toLowerCase().contains(query)
                    || book.author.toLowerCase().contains(query)) {
                String status = book.borrowed ? "مُعار" : "متاح";
                System.out.println(book.id + ". " + book.title + " — "
                        + book.author + " [" + status + "]");
                found = true;
            }
        }

        if (!found) System.out.println("لم يتم العثور على كتب مطابقة.");
    }

    private static void borrowBook(Scanner scanner, List<Book> books) {
        int id = readInt(scanner, "رقم الكتاب لاستعارته: ");

        for (Book book : books) {
            if (book.id == id) {
                if (book.borrowed) {
                    System.out.println("الكتاب مُعار بالفعل.");
                    return;
                }
                book.borrowed = true;
                if (saveBooks(books)) System.out.println("تم تسجيل استعارة الكتاب.");
                return;
            }
        }

        System.out.println("لم يتم العثور على هذا الكتاب.");
    }

    private static void returnBook(Scanner scanner, List<Book> books) {
        int id = readInt(scanner, "رقم الكتاب لإرجاعه: ");

        for (Book book : books) {
            if (book.id == id) {
                if (!book.borrowed) {
                    System.out.println("الكتاب غير مُعار.");
                    return;
                }
                book.borrowed = false;
                if (saveBooks(books)) System.out.println("تم تسجيل إرجاع الكتاب.");
                return;
            }
        }

        System.out.println("لم يتم العثور على هذا الكتاب.");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Book> books = loadBooks();

        while (true) {
            System.out.println("\n=== نظام إدارة المكتبة ===");
            System.out.println("1. إضافة كتاب");
            System.out.println("2. عرض الكتب");
            System.out.println("3. البحث عن كتاب");
            System.out.println("4. استعارة كتاب");
            System.out.println("5. إرجاع كتاب");
            System.out.println("0. خروج");

            int choice = readInt(scanner, "اختر: ");

            switch (choice) {
                case 1 -> addBook(scanner, books);
                case 2 -> listBooks(books);
                case 3 -> searchBooks(scanner, books);
                case 4 -> borrowBook(scanner, books);
                case 5 -> returnBook(scanner, books);
                case 0 -> {
                    scanner.close();
                    return;
                }
                default -> System.out.println("اختيار غير صحيح.");
            }
        }
    }
}
