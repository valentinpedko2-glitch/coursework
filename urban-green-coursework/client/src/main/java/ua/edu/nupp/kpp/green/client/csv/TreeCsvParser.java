package ua.edu.nupp.kpp.green.client.csv;

import ua.edu.nupp.kpp.green.client.model.Tree;

// Розбір одного рядка CSV реєстру дерев (роздільник - крапка з комою, без лапок).
public final class TreeCsvParser {
    public static final String HEADER = "code;species;location;planting_year;pedestrians_per_hour";
    private static final int COLUMNS = 5;

    private TreeCsvParser() {
    }

    // Перевіряє заголовок; BOM на початку файлу ігнорується.
    public static void checkHeader(String line) {
        String normalized = line == null ? "" : line.replace("\uFEFF", "").trim();
        if (!HEADER.equalsIgnoreCase(normalized)) {
            throw new IllegalArgumentException("Невірний заголовок CSV. Очікується: " + HEADER);
        }
    }

    public static Tree parseRow(String line, int lineNumber) {
        String[] parts = line.split(";", -1);
        if (parts.length != COLUMNS) {
            throw new IllegalArgumentException("Рядок " + lineNumber + ": очікується " + COLUMNS
                    + " полів, знайдено " + parts.length);
        }
        Tree t = new Tree();
        t.setCode(required(parts[0], "code", lineNumber));
        t.setSpecies(required(parts[1], "species", lineNumber));
        t.setLocation(required(parts[2], "location", lineNumber));
        t.setPlantingYear(integer(parts[3], "planting_year", lineNumber));
        t.setPedestriansPerHour(integer(parts[4], "pedestrians_per_hour", lineNumber));
        return t;
    }

    private static String required(String value, String column, int line) {
        String v = value.trim();
        if (v.isEmpty()) {
            throw new IllegalArgumentException("Рядок " + line + ": поле " + column + " порожнє");
        }
        return v;
    }

    private static int integer(String value, String column, int line) {
        try {
            return Integer.parseInt(required(value, column, line));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Рядок " + line + ": поле " + column + " має бути цілим числом");
        }
    }
}
