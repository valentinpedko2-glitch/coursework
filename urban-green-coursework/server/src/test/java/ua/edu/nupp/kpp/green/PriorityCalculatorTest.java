package ua.edu.nupp.kpp.green;

import org.junit.jupiter.api.Test;
import ua.edu.nupp.kpp.green.rules.PriorityCalculator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PriorityCalculatorTest {

    @Test
    void f1GrowsAsHealthDrops() {
        assertEquals(60.0, PriorityCalculator.f1(40), 0.001);
        assertEquals(0.0, PriorityCalculator.f1(100), 0.001);
    }

    @Test
    void f1RejectsHealthAbove100() {
        assertThrows(IllegalArgumentException.class, () -> PriorityCalculator.f1(101));
    }

    @Test
    void f2ScalesPedestriansLinearly() {
        assertEquals(70.0, PriorityCalculator.f2(350), 0.001);
    }

    @Test
    void f2IsCappedAt100() {
        assertEquals(100.0, PriorityCalculator.f2(900), 0.001);
    }

    @Test
    void f3IsHigherForShorterDeadline() {
        assertEquals(80.0, PriorityCalculator.f3(6), 0.001);
        assertEquals(100.0, PriorityCalculator.f3(0), 0.001);
    }

    @Test
    void f3IsZeroForLongDeadline() {
        assertEquals(0.0, PriorityCalculator.f3(45), 0.001);
    }

    @Test
    void indexForDemoDataIs68_5() {
        assertEquals(68.5, PriorityCalculator.index(60, 70, 80), 0.001);
    }

    @Test
    void indexRejectsFactorAbove100() {
        assertThrows(IllegalArgumentException.class, () -> PriorityCalculator.index(101, 10, 10));
    }
}
