package ua.edu.nupp.kpp.green.client.csv;

import ua.edu.nupp.kpp.green.client.model.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

// Логіка імпорту без JavaFX: її викликає ImportTreesTask, а тести перевіряють без графіки.
// Помилка одного рядка не зупиняє імпорт: рядок враховується як помилковий і імпорт триває.
public final class TreeCsvImporter {

    public record Result(int imported, int failed, boolean cancelled, List<String> errors) {
    }

    @FunctionalInterface
    public interface Progress {
        void update(int done, int total, String message);
    }

    private TreeCsvImporter() {
    }

    public static Result run(List<String> lines, Consumer<Tree> sink, Progress progress,
                             BooleanSupplier cancelled) {
        int headerIndex = firstNonBlank(lines);
        if (headerIndex < 0) {
            throw new IllegalArgumentException("CSV-файл порожній");
        }
        TreeCsvParser.checkHeader(lines.get(headerIndex));

        int total = 0;
        for (int i = headerIndex + 1; i < lines.size(); i++) {
            if (!lines.get(i).isBlank()) {
                total++;
            }
        }

        int done = 0;
        int imported = 0;
        List<String> errors = new ArrayList<>();
        boolean wasCancelled = false;
        for (int i = headerIndex + 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            if (cancelled.getAsBoolean()) {
                wasCancelled = true;
                break;
            }
            int lineNumber = i + 1;
            try {
                sink.accept(TreeCsvParser.parseRow(line, lineNumber));
                imported++;
            } catch (RuntimeException e) {
                String message = e.getMessage() != null && e.getMessage().startsWith("Рядок")
                        ? e.getMessage()
                        : "Рядок " + lineNumber + ": " + e.getMessage();
                errors.add(message);
            }
            done++;
            progress.update(done, total, "Оброблено " + done + " з " + total);
        }
        int failed = errors.size();
        String summary = (wasCancelled ? "Скасовано. " : "Готово. ")
                + "Імпортовано: " + imported + ", помилок: " + failed;
        progress.update(done, total, summary);
        return new Result(imported, failed, wasCancelled, errors);
    }

    private static int firstNonBlank(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            if (!lines.get(i).isBlank()) {
                return i;
            }
        }
        return -1;
    }
}
