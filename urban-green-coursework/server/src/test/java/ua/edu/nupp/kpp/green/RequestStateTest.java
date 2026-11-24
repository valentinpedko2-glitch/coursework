package ua.edu.nupp.kpp.green;

import org.junit.jupiter.api.Test;
import ua.edu.nupp.kpp.green.model.RequestState;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestStateTest {

    @Test
    void forwardChainIsAllowed() {
        assertTrue(RequestState.CREATED.canMoveTo(RequestState.VERIFIED));
        assertTrue(RequestState.VERIFIED.canMoveTo(RequestState.PLANNED));
        assertTrue(RequestState.PLANNED.canMoveTo(RequestState.DONE));
    }

    @Test
    void skippingStatesIsRejected() {
        assertFalse(RequestState.CREATED.canMoveTo(RequestState.DONE));
        assertFalse(RequestState.CREATED.canMoveTo(RequestState.PLANNED));
    }

    @Test
    void backwardAndSameStateAreRejected() {
        assertFalse(RequestState.VERIFIED.canMoveTo(RequestState.CREATED));
        assertFalse(RequestState.DONE.canMoveTo(RequestState.DONE));
    }

    @Test
    void unknownStateNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> RequestState.parse("ARCHIVED"));
    }
}
