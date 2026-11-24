package ua.edu.nupp.kpp.green.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.logging.Level;
import java.util.logging.Logger;

// Підключення до MySQL. Параметри беруться зі змінних середовища, секрети в коді не зберігаються.
//   GREEN_DB_URL      (за замовчуванням jdbc:mysql://localhost:3306/urban_green)
//   GREEN_DB_USER     (за замовчуванням root)
//   GREEN_DB_PASSWORD (за замовчуванням порожній)
public final class Db {
    private static final Logger LOG = Logger.getLogger(Db.class.getName());

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/urban_green?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";

    // Tomcat не завжди реєструє JDBC-драйвер із WAR автоматично, тому завантажуємо його явно.
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOG.log(Level.SEVERE, "JDBC-драйвер MySQL не знайдено в застосунку", e);
        }
    }

    private Db() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                env("GREEN_DB_URL", DEFAULT_URL),
                env("GREEN_DB_USER", "root"),
                env("GREEN_DB_PASSWORD", ""));
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }

    // Виконує INSERT і повертає згенерований ідентифікатор.
    static int executeInsert(PreparedStatement p) throws SQLException {
        p.executeUpdate();
        try (ResultSet keys = p.getGeneratedKeys()) {
            if (keys.next()) {
                return keys.getInt(1);
            }
            throw new SQLException("СУБД не повернула згенерований ключ");
        }
    }

    // Порушення обмежень цілісності - помилка даних користувача (400); решта - технічна (500).
    static RuntimeException wrap(SQLException e) {
        if (e instanceof SQLIntegrityConstraintViolationException) {
            LOG.warning("Порушення обмеження цілісності: " + e.getMessage());
            return new IllegalArgumentException(
                    "Операція порушує обмеження даних (дублікат коду або наявні пов'язані записи)");
        }
        LOG.log(Level.SEVERE, "Помилка доступу до БД", e);
        return new DataAccessException("Помилка доступу до бази даних", e);
    }

    static void requireChanged(int rows) {
        if (rows == 0) {
            throw new IllegalArgumentException("Запис не знайдено");
        }
    }
}
