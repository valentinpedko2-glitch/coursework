package ua.edu.nupp.kpp.green;

import ua.edu.nupp.kpp.green.model.Inspection;
import ua.edu.nupp.kpp.green.model.Tree;
import ua.edu.nupp.kpp.green.model.WorkAssignment;
import ua.edu.nupp.kpp.green.model.WorkRequest;

// Фабрики коректних тестових об'єктів.
final class TestData {
    private TestData() {
    }

    static Tree tree(String code, int pedestrians) {
        Tree t = new Tree();
        t.setCode(code);
        t.setSpecies("Липа серцелиста");
        t.setLocation("вул. Соборності, 12");
        t.setPlantingYear(1987);
        t.setPedestriansPerHour(pedestrians);
        return t;
    }

    static Inspection inspection(int treeId, int health) {
        Inspection i = new Inspection();
        i.setTreeId(treeId);
        i.setInspectionDate("2026-10-05");
        i.setHealthScore(health);
        i.setDefects("Суховершинність");
        return i;
    }

    static WorkRequest request(int treeId, int inspectionId, int deadlineDays) {
        WorkRequest r = new WorkRequest();
        r.setTreeId(treeId);
        r.setInspectionId(inspectionId);
        r.setWorkType("PRUNING");
        r.setDescription("Санітарна обрізка крони");
        r.setDeadlineDays(deadlineDays);
        return r;
    }

    static WorkAssignment assignment(int requestId) {
        WorkAssignment a = new WorkAssignment();
        a.setRequestId(requestId);
        a.setCrewName("Бригада №1");
        a.setPlannedDate("2026-11-25");
        return a;
    }
}
