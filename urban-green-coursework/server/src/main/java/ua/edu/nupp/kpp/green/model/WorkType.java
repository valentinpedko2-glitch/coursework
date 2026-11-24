package ua.edu.nupp.kpp.green.model;

// Допустимі види робіт у заявці.
public enum WorkType {
    PRUNING("Обрізка"),
    TREATMENT("Лікування"),
    REMOVAL("Видалення"),
    WATERING("Полив");

    private final String label;

    WorkType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static boolean isValid(String value) {
        if (value == null) {
            return false;
        }
        for (WorkType t : values()) {
            if (t.name().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
