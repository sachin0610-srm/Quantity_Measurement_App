import java.util.Objects;

public class QuantityMeasureApp {

    public static void main(String[] args) {
        try {
            QuantityLength l1 = new QuantityLength(1.0, LengthUnit.FEET);
            QuantityLength l2 = new QuantityLength(12.0, LengthUnit.INCHES);
            QuantityLength result1 = l1.add(l2);
            System.out.println("Result 1: " + result1);

            QuantityLength l3 = new QuantityLength(1.0, LengthUnit.YARDS);
            QuantityLength l4 = new QuantityLength(3.0, LengthUnit.FEET);
            QuantityLength result2 = l3.add(l4);
            System.out.println("Result 2: " + result2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class QuantityLength {
    private final double value;
    private final LengthUnit unit;

    public QuantityLength(double value, LengthUnit unit) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Value must be a finite number.");
        }
        this.value = value;
        this.unit = Objects.requireNonNull(unit, "Unit cannot be null.");
    }

    public QuantityLength add(QuantityLength other) {
        Objects.requireNonNull(other, "Second operand cannot be null.");

        double firstInBase = this.value * this.unit.conversionFactor;
        double secondInBase = other.value * other.unit.conversionFactor;

        double totalInBase = firstInBase + secondInBase;

        double finalValue = totalInBase / this.unit.conversionFactor;

        return new QuantityLength(finalValue, this.unit);
    }

    @Override
    public String toString() {
        return String.format("Quantity(%.2f, %s)", value, unit);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuantityLength that = (QuantityLength) o;
        // Use epsilon for floating point comparison
        return Math.abs((that.value * that.unit.conversionFactor) -
                (this.value * this.unit.conversionFactor)) < 1e-9;
    }
}

enum LengthUnit {
    FEET(1.0),
    INCHES(1.0 / 12.0),
    YARDS(3.0),
    CENTIMETERS(1.0 / 30.48);

    public final double conversionFactor;

    LengthUnit(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }
}