package ua.edu.nupp.kpp.green.dao;

// Технічна помилка доступу до БД (не предметна помилка користувача).
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
