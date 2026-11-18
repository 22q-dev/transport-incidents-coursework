package ua.edu.nupp.kpp.transport.server;

public final class IncidentImpactCalculator {

    private IncidentImpactCalculator() {
    }

    public static double calculatePassengerFactorF1(int affectedPassengers, int vehicleCapacity) {
        if (affectedPassengers < 0) {
            throw new IllegalArgumentException("Кількість пасажирів не може бути від'ємною");
        }
        if (vehicleCapacity <= 0) {
            throw new IllegalArgumentException("Місткість транспортного засобу має бути більшою за 0");
        }
        double raw = ((double) affectedPassengers / vehicleCapacity) * 100.0;
        double clamped = Math.max(0.0, Math.min(100.0, raw));
        return Math.round(clamped * 100.0) / 100.0;
    }

    public static double calculateIntegralIndex(double f1, double f2, double f3) {
        validateNormalizedFactor("F1", f1);
        validateNormalizedFactor("F2", f2);
        validateNormalizedFactor("F3", f3);
        double value = 0.40 * f1 + 0.35 * f2 + 0.25 * f3;
        return Math.round(value * 100.0) / 100.0;
    }

    public static void validateNormalizedFactor(String name, double value) {
        if (Double.isNaN(value) || value < 0.0 || value > 100.0) {
            throw new IllegalArgumentException("Фактор " + name + " має знаходитися в діапазоні 0..100");
        }
    }
}
