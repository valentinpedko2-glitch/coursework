package ua.edu.nupp.kpp.green.model;

// Чотири стани заявки на роботи. Єдине правило переходів: рівно на один крок уперед.
public enum RequestState {
    CREATED("СТВОРЕНО"),
    VERIFIED("ПЕРЕВІРЕНО"),
    PLANNED("ЗАПЛАНОВАНО"),
    DONE("ВИКОНАНО");

    private final String label;

    RequestState(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    // Наступний стан або null, якщо стан кінцевий.
    public RequestState next() {
        return ordinal() == values().length - 1 ? null : values()[ordinal() + 1];
    }

    public boolean canMoveTo(RequestState target) {
        return target != null && next() == target;
    }

    public static RequestState parse(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Не вказано стан заявки");
        }
        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Невідомий стан заявки: " + value);
        }
    }
}
