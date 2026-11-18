package ua.edu.nupp.kpp.transport.server;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CourseworkTest {

    @Test
    void f1ForZeroPassengersIsZero() {
        double f1 = IncidentImpactCalculator.calculatePassengerFactorF1(0, 100);
        assertEquals(0.0, f1, 0.001);
    }

    @Test
    void f1ForDemoIncidentIs65() {
        double f1 = IncidentImpactCalculator.calculatePassengerFactorF1(65, 100);
        assertEquals(65.0, f1, 0.001);
    }

    @Test
    void f1ClampedTo100WhenOverloaded() {
        double f1 = IncidentImpactCalculator.calculatePassengerFactorF1(135, 100);
        assertEquals(100.0, f1, 0.001);
    }

    @Test
    void integralIndexForDemoIncidentIs665() {
        double index = IncidentImpactCalculator.calculateIntegralIndex(65.0, 80.0, 50.0);
        assertEquals(66.50, index, 0.001);
    }

    @Test
    void integralIndexRejectsFactorAbove100() {
        assertThrows(IllegalArgumentException.class, () ->
                IncidentImpactCalculator.calculateIntegralIndex(65.0, 105.0, 50.0));
    }

    @Test
    void transitionReportedToConfirmedAllowed() {
        assertTrue(IncidentService.isTransitionAllowed(IncidentState.REPORTED, IncidentState.CONFIRMED));
    }

    @Test
    void transitionConfirmedToResponseAllowed() {
        assertTrue(IncidentService.isTransitionAllowed(IncidentState.CONFIRMED, IncidentState.RESPONSE));
    }

    @Test
    void transitionResponseToClosedAllowed() {
        assertTrue(IncidentService.isTransitionAllowed(IncidentState.RESPONSE, IncidentState.CLOSED));
    }

    @Test
    void transitionReportedToClosedRejected() {
        assertFalse(IncidentService.isTransitionAllowed(IncidentState.REPORTED, IncidentState.CLOSED));
    }

    @Test
    void transitionClosedToResponseRejected() {
        assertFalse(IncidentService.isTransitionAllowed(IncidentState.CLOSED, IncidentState.RESPONSE));
    }

    @Test
    void routeValidationRejectsEmptyCode() {
        Route invalid = new Route(0, "   ", "Кільцевий", 75.0);
        assertThrows(IllegalArgumentException.class, () -> RouteService.validateRoute(invalid));
    }

    @Test
    void vehicleValidationRejectsNonPositiveCapacity() {
        Vehicle invalid = new Vehicle(0, 1, "ТП-104", "Богдан Т70117", 0);
        assertThrows(IllegalArgumentException.class, () -> VehicleService.validateVehicleFields(invalid));
    }
}
