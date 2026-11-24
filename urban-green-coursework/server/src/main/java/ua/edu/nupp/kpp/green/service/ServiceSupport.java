package ua.edu.nupp.kpp.green.service;

// Спільні дрібні перевірки сервісів.
final class ServiceSupport {

    private ServiceSupport() {
    }

    static void requireId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Не вказано ідентифікатор запису");
        }
    }
}
