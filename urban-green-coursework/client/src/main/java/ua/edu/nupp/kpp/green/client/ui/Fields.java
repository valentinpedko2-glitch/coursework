package ua.edu.nupp.kpp.green.client.ui;

import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

// Клієнтська валідація полів форми: швидкі зрозумілі повідомлення до звернення до сервера.
// Остаточні предметні правила все одно перевіряє Service на сервері.
final class Fields {

    private Fields() {
    }

    static String text(TextField field, String name) {
        String v = field.getText() == null ? "" : field.getText().trim();
        if (v.isEmpty()) {
            throw new IllegalArgumentException("Поле «" + name + "» обов'язкове");
        }
        return v;
    }

    static String optional(TextField field) {
        String v = field.getText() == null ? "" : field.getText().trim();
        return v.isEmpty() ? null : v;
    }

    static int integer(TextField field, String name, int min, int max) {
        String v = text(field, name);
        try {
            int n = Integer.parseInt(v);
            if (n < min || n > max) {
                throw new IllegalArgumentException("Поле «" + name + "» має бути в діапазоні " + min + ".." + max);
            }
            return n;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Поле «" + name + "» має бути цілим числом");
        }
    }

    static String date(TextField field, String name) {
        String v = text(field, name);
        try {
            return LocalDate.parse(v).toString();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Поле «" + name + "» має формат рррр-мм-дд");
        }
    }
}
