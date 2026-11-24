package ua.edu.nupp.kpp.green.client.model;

// Стани заявки на клієнті: лише значення та підписи. Правила переходів діють тільки на сервері.
public enum RequestState {
    CREATED("СТВОРЕНО"),
    VERIFIED("ПЕРЕВІРЕНО"),
    PLANNED("ЗАПЛАНОВАНО"),
    DONE("ВИКОНАНО");

    private final String label;

    RequestState(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
