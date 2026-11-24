package ua.edu.nupp.kpp.green.rules;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Інтегральний показник пріоритету I = 0,40*F1 + 0,35*F2 + 0,25*F3.
// Усі фактори нормуються до діапазону 0..100. Формула існує тільки тут.
public final class PriorityCalculator {

    public static final double W1 = 0.40;
    public static final double W2 = 0.35;
    public static final double W3 = 0.25;

    // 500 і більше пішоходів на годину дають F2 = 100.
    public static final int PEDESTRIANS_FOR_MAX = 500;
    // Строк 30 діб і більше дає F3 = 0, строк 0 діб дає F3 = 100.
    public static final int DEADLINE_HORIZON_DAYS = 30;

    private PriorityCalculator() {
    }

    // F1 - технічний стан: чим гірший стан дерева (health_score), тим більше F1.
    public static double f1(int healthScore) {
        if (healthScore < 0 || healthScore > 100) {
            throw new IllegalArgumentException("Оцінка стану дерева має бути в діапазоні 0..100");
        }
        return round2(100 - healthScore);
    }

    // F2 - інтенсивність перебування людей: лінійне нормування пішоходів за годину.
    public static double f2(int pedestriansPerHour) {
        if (pedestriansPerHour < 0) {
            throw new IllegalArgumentException("Кількість пішоходів не може бути від'ємною");
        }
        return round2(Math.min(100.0, pedestriansPerHour * 100.0 / PEDESTRIANS_FOR_MAX));
    }

    // F3 - терміновість втручання: чим менше діб до кінцевого строку, тим більше F3.
    public static double f3(int deadlineDays) {
        if (deadlineDays < 0) {
            throw new IllegalArgumentException("Строк виконання не може бути від'ємним");
        }
        double v = (DEADLINE_HORIZON_DAYS - deadlineDays) * 100.0 / DEADLINE_HORIZON_DAYS;
        return round2(Math.max(0.0, Math.min(100.0, v)));
    }

    public static double index(double f1, double f2, double f3) {
        checkFactor(f1, "F1");
        checkFactor(f2, "F2");
        checkFactor(f3, "F3");
        return round2(W1 * f1 + W2 * f2 + W3 * f3);
    }

    private static void checkFactor(double value, String name) {
        if (Double.isNaN(value) || value < 0 || value > 100) {
            throw new IllegalArgumentException("Фактор " + name + " має бути в діапазоні 0..100");
        }
    }

    static double round2(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
