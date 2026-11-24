package ua.edu.nupp.kpp.green.rules;

import ua.edu.nupp.kpp.green.model.Inspection;
import ua.edu.nupp.kpp.green.model.Tree;
import ua.edu.nupp.kpp.green.model.WorkAssignment;
import ua.edu.nupp.kpp.green.model.WorkRequest;
import ua.edu.nupp.kpp.green.model.WorkType;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

// Предметна валідація вхідних даних. Не залежить від БД, HTTP і GUI, тому легко тестується.
public final class Validator {

    private Validator() {
    }

    public static void validateTree(Tree t) {
        if (t == null) {
            throw new IllegalArgumentException("Порожній запис дерева");
        }
        requireText(t.getCode(), "Код дерева", 20);
        requireText(t.getSpecies(), "Порода", 100);
        requireText(t.getLocation(), "Місцезнаходження", 150);
        int year = LocalDate.now().getYear();
        if (t.getPlantingYear() == null || t.getPlantingYear() < 1800 || t.getPlantingYear() > year) {
            throw new IllegalArgumentException("Рік посадки має бути в межах 1800.." + year);
        }
        if (t.getPedestriansPerHour() == null || t.getPedestriansPerHour() < 0) {
            throw new IllegalArgumentException("Кількість пішоходів за годину не може бути від'ємною");
        }
    }

    public static void validateInspection(Inspection i) {
        if (i == null) {
            throw new IllegalArgumentException("Порожній запис огляду");
        }
        requirePositive(i.getTreeId(), "ID дерева");
        LocalDate date = parseDate(i.getInspectionDate(), "Дата огляду");
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Дата огляду не може бути в майбутньому");
        }
        if (i.getHealthScore() == null || i.getHealthScore() < 0 || i.getHealthScore() > 100) {
            throw new IllegalArgumentException("Оцінка стану дерева має бути в діапазоні 0..100");
        }
        optionalText(i.getDefects(), "Дефекти", 255);
    }

    public static void validateRequest(WorkRequest r) {
        if (r == null) {
            throw new IllegalArgumentException("Порожній запис заявки");
        }
        requirePositive(r.getTreeId(), "ID дерева");
        requirePositive(r.getInspectionId(), "ID огляду");
        if (!WorkType.isValid(r.getWorkType())) {
            throw new IllegalArgumentException("Невідомий вид робіт: " + r.getWorkType());
        }
        requireText(r.getDescription(), "Опис робіт", 255);
        if (r.getDeadlineDays() == null || r.getDeadlineDays() < 0 || r.getDeadlineDays() > 365) {
            throw new IllegalArgumentException("Строк виконання має бути в межах 0..365 діб");
        }
    }

    public static void validateAssignment(WorkAssignment a) {
        if (a == null) {
            throw new IllegalArgumentException("Порожній запис наряду");
        }
        requirePositive(a.getRequestId(), "ID заявки");
        requireText(a.getCrewName(), "Бригада", 100);
        parseDate(a.getPlannedDate(), "Планова дата");
        optionalText(a.getNote(), "Примітка", 255);
    }

    private static void requireText(String value, String name, int max) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Поле «" + name + "» не може бути порожнім");
        }
        if (value.length() > max) {
            throw new IllegalArgumentException("Поле «" + name + "» задовге (максимум " + max + " символів)");
        }
    }

    private static void optionalText(String value, String name, int max) {
        if (value != null && value.length() > max) {
            throw new IllegalArgumentException("Поле «" + name + "» задовге (максимум " + max + " символів)");
        }
    }

    private static void requirePositive(Integer value, String name) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Поле «" + name + "» має бути додатним числом");
        }
    }

    private static LocalDate parseDate(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Поле «" + name + "» не може бути порожнім");
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Поле «" + name + "» має формат рррр-мм-дд");
        }
    }
}
