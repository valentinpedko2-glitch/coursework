package ua.edu.nupp.kpp.green.client.csv;

import javafx.concurrent.Task;
import ua.edu.nupp.kpp.green.client.model.Tree;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

// Фоновий імпорт реєстру дерев. Виконується поза потоком JavaFX, тому інтерфейс не блокується.
// ProgressBar зв'язується з progressProperty(), Label - з messageProperty().
public class ImportTreesTask extends Task<TreeCsvImporter.Result> {
    private static final Logger LOG = Logger.getLogger(ImportTreesTask.class.getName());
    // Невелика пауза на рядок, щоб на навчальних файлах було видно рух ProgressBar.
    private static final long ROW_DELAY_MS = 120;

    private final Path file;
    private final Consumer<Tree> sink;

    public ImportTreesTask(Path file, Consumer<Tree> sink) {
        this.file = file;
        this.sink = sink;
    }

    @Override
    protected TreeCsvImporter.Result call() throws Exception {
        LOG.info("Початок імпорту CSV: " + file.getFileName());
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        TreeCsvImporter.Result result = TreeCsvImporter.run(lines, sink, (done, total, message) -> {
            updateProgress(done, Math.max(total, 1));
            updateMessage(message);
            pause();
        }, this::isCancelled);
        LOG.info("Імпорт завершено: імпортовано " + result.imported() + ", помилок " + result.failed());
        return result;
    }

    private void pause() {
        try {
            Thread.sleep(ROW_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
