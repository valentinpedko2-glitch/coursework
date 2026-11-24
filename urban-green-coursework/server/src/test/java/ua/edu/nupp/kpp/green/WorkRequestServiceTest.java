package ua.edu.nupp.kpp.green;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.edu.nupp.kpp.green.model.Inspection;
import ua.edu.nupp.kpp.green.model.RequestState;
import ua.edu.nupp.kpp.green.model.Tree;
import ua.edu.nupp.kpp.green.model.WorkRequest;
import ua.edu.nupp.kpp.green.service.WorkRequestService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WorkRequestServiceTest {
    private WorkRequestService service;

    @BeforeEach
    void setUp() {
        InMemoryDao<Tree> trees = new InMemoryDao<>();
        InMemoryDao<Inspection> inspections = new InMemoryDao<>();
        trees.insert(TestData.tree("LP-001", 350));       // id=1
        trees.insert(TestData.tree("KS-014", 100));       // id=2
        inspections.insert(TestData.inspection(1, 40));   // id=1, належить дереву 1
        service = new WorkRequestService(new InMemoryDao<>(), trees, inspections);
    }

    @Test
    void createComputesFactorsAndStartsInCreatedState() {
        WorkRequest saved = service.create(TestData.request(1, 1, 6));
        assertEquals(RequestState.CREATED, saved.getState());
        assertEquals(60.0, saved.getF1(), 0.001);
        assertEquals(70.0, saved.getF2(), 0.001);
        assertEquals(80.0, saved.getF3(), 0.001);
        assertEquals(68.5, saved.getPriorityIndex(), 0.001);
    }

    @Test
    void createRejectsInspectionOfAnotherTree() {
        WorkRequest r = TestData.request(2, 1, 6);
        assertThrows(IllegalArgumentException.class, () -> service.create(r));
    }

    @Test
    void transitionPassesThroughAllFourStates() {
        int id = service.create(TestData.request(1, 1, 6)).getId();
        assertEquals(RequestState.VERIFIED, service.transition(id, RequestState.VERIFIED).getState());
        assertEquals(RequestState.PLANNED, service.transition(id, RequestState.PLANNED).getState());
        assertEquals(RequestState.DONE, service.transition(id, RequestState.DONE).getState());
    }

    @Test
    void transitionThatSkipsAStateIsRejected() {
        int id = service.create(TestData.request(1, 1, 6)).getId();
        assertThrows(IllegalArgumentException.class, () -> service.transition(id, RequestState.DONE));
    }

    @Test
    void requestCannotBeEditedAfterVerification() {
        int id = service.create(TestData.request(1, 1, 6)).getId();
        service.transition(id, RequestState.VERIFIED);
        WorkRequest edit = TestData.request(1, 1, 3);
        edit.setId(id);
        assertThrows(IllegalArgumentException.class, () -> service.update(edit));
    }
}
