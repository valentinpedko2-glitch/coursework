package ua.edu.nupp.kpp.green;

import org.junit.jupiter.api.Test;
import ua.edu.nupp.kpp.green.model.Inspection;
import ua.edu.nupp.kpp.green.model.Tree;
import ua.edu.nupp.kpp.green.model.WorkRequest;
import ua.edu.nupp.kpp.green.rules.Validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidatorTest {

    @Test
    void validTreePasses() {
        assertDoesNotThrow(() -> Validator.validateTree(TestData.tree("LP-001", 420)));
    }

    @Test
    void treeWithBlankCodeIsRejected() {
        Tree t = TestData.tree(" ", 100);
        assertThrows(IllegalArgumentException.class, () -> Validator.validateTree(t));
    }

    @Test
    void inspectionWithHealthAbove100IsRejected() {
        Inspection i = TestData.inspection(1, 120);
        assertThrows(IllegalArgumentException.class, () -> Validator.validateInspection(i));
    }

    @Test
    void inspectionWithWrongDateFormatIsRejected() {
        Inspection i = TestData.inspection(1, 50);
        i.setInspectionDate("05.10.2026");
        assertThrows(IllegalArgumentException.class, () -> Validator.validateInspection(i));
    }

    @Test
    void requestWithUnknownWorkTypeIsRejected() {
        WorkRequest r = TestData.request(1, 1, 5);
        r.setWorkType("PAINTING");
        assertThrows(IllegalArgumentException.class, () -> Validator.validateRequest(r));
    }
}
