package ua.edu.nupp.kpp.green.client.api;

// Помилка звернення до сервера (повідомлення призначене для показу користувачу).
public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
