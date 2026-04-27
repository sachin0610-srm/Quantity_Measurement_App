import java.util.Objects;

public class QuantityMeasureApp {

    public static void main(String[] args) {
        runAdditionExamples();
    }

    public static void runAdditionExamples() {
        System.out.println("--- UC7: Addition with Target Unit Specification ---");

        try {
            QuantityLength l1 = new QuantityLength(1.0, LengthUnit.FEET);
            QuantityLength l2 = new QuantityLength(12.0, LengthUnit.INCHES);
            System.out.println("Result (FEET):   " + QuantityLength.add(l1, l2, LengthUnit.FEET));

            System.out.println("Result (INCHES): " + QuantityLength.add(l1, l2, LengthUnit.INCHES));

            System.out.println("Result (YARDS):  " + QuantityLength.add(l1, l2, LengthUnit.YARDS));

            QuantityLength l3 = new QuantityLength(2.54, LengthUnit.CENTIMETERS);
            QuantityLength l4 = new QuantityLength(1.0, LengthUnit.INCHES);
            System.out.println("Result (CM):     " + QuantityLength.add(l3, l4, LengthUnit.CENTIMETERS));

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}

enum LengthUnit {
    FEET(1.0),
    INCHES(1.0 / 12.0),
    YARDS(3.0),
    CENTIMETERS(1.0 / 30.48);

    public final double conversionFactor;

    LengthUnit(double factor) {
        this.conversionFactor = factor;
    }
}

class QuantityLength {
    private final double value;
    private final LengthUnit unit;

    public QuantityLength(double value, LengthUnit unit) {
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Value must be finite.");
        this.value = value;
        this.unit = Objects.requireNonNull(unit, "Unit cannot be null.");
    }

    public static QuantityLength add(QuantityLength l1, QuantityLength l2, LengthUnit targetUnit) {
        Objects.requireNonNull(l1, "First operand cannot be null.");
        Objects.requireNonNull(l2, "Second operand cannot be null.");
        Objects.requireNonNull(targetUnit, "Target unit cannot be null.");

        double baseSum = (l1.value * l1.unit.conversionFactor) + (l2.value * l2.unit.conversionFactor);

        double finalValue = baseSum / targetUnit.conversionFactor;

        double roundedValue = Math.round(finalValue * 1000.0) / 1000.0;

        return new QuantityLength(roundedValue, targetUnit);
    }

    @Override
    public String toString() {
        return value + " " + unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuantityLength that = (QuantityLength) o;
        return Double.compare(that.value, value) == 0 && unit == that.unit;
    }
}