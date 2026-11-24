package ua.edu.nupp.kpp.green.client;

import org.junit.jupiter.api.Test;
import ua.edu.nupp.kpp.green.client.csv.TreeCsvImporter;
import ua.edu.nupp.kpp.green.client.csv.TreeCsvParser;
import ua.edu.nupp.kpp.green.client.model.Tree;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TreeCsvImporterTest {
    private static final String HEADER = TreeCsvParser.HEADER;

    @Test
    void parsesValidRow() {
        Tree t = TreeCsvParser.parseRow("LP-001;Липа;вул. Соборності, 12;1987;420", 2);
        assertEquals("LP-001", t.getCode());
        assertEquals(1987, t.getPlantingYear());
        assertEquals(420, t.getPedestriansPerHour());
    }

    @Test
    void rejectsRowWithWrongColumnCount() {
        assertThrows(IllegalArgumentException.class, () -> TreeCsvParser.parseRow("LP-001;Липа;1987", 2));
    }

    @Test
    void rejectsNonNumericYear() {
        assertThrows(IllegalArgumentException.class,
                () -> TreeCsvParser.parseRow("LP-001;Липа;Парк;abcd;10", 2));
    }

    @Test
    void wrongHeaderIsRejected() {
        List<String> lines = List.of("id;name", "1;x");
        assertThrows(IllegalArgumentException.class,
                () -> TreeCsvImporter.run(lines, t -> { }, (d, n, m) -> { }, () -> false));
    }

    @Test
    void badRowDoesNotStopImport() {
        List<String> lines = List.of(HEADER,
                "A-1;Липа;Парк;1990;10",
                "A-2;Клен;Парк;abcd;20",
                "A-3;Дуб;Парк;1980;30");
        List<Tree> saved = new ArrayList<>();
        TreeCsvImporter.Result r = TreeCsvImporter.run(lines, saved::add, (d, n, m) -> { }, () -> false);
        assertEquals(2, r.imported());
        assertEquals(1, r.failed());
        assertEquals(2, saved.size());
        assertTrue(r.errors().get(0).startsWith("Рядок 3"));
    }

    @Test
    void serverErrorOnOneRowIsCountedAsFailure() {
        List<String> lines = List.of(HEADER, "A-1;Липа;Парк;1990;10", "DUP;Клен;Парк;1991;20");
        TreeCsvImporter.Result r = TreeCsvImporter.run(lines, t -> {
            if (t.getCode().equals("DUP")) {
                throw new IllegalStateException("Дублікат коду");
            }
        }, (d, n, m) -> { }, () -> false);
        assertEquals(1, r.imported());
        assertEquals(1, r.failed());
    }

    @Test
    void cancellationStopsImportAfterCurrentRow() {
        List<String> lines = List.of(HEADER, "A-1;Липа;Парк;1990;10", "A-2;Клен;Парк;1991;20");
        List<Tree> saved = new ArrayList<>();
        int[] calls = {0};
        TreeCsvImporter.Result r = TreeCsvImporter.run(lines, saved::add, (d, n, m) -> { },
                () -> calls[0]++ >= 1);
        assertTrue(r.cancelled());
        assertEquals(1, saved.size());
        assertFalse(r.errors().iterator().hasNext());
    }
}
