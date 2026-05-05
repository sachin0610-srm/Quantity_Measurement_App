import java.util.Objects;

interface IMeasurable {
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double value);
}

enum LengthUnit implements IMeasurable {
    FEET(12.0), INCHES(1.0);
    private final double baseFactor;
    LengthUnit(double baseFactor) { this.baseFactor = baseFactor; }
    @Override public double convertToBaseUnit(double value) { return value * baseFactor; }
    @Override public double convertFromBaseUnit(double value) { return value / baseFactor; }
}

enum WeightUnit implements IMeasurable {
    KILOGRAM(1000.0), GRAM(1.0);
    private final double baseFactor;
    WeightUnit(double baseFactor) { this.baseFactor = baseFactor; }
    @Override public double convertToBaseUnit(double value) { return value * baseFactor; }
    @Override public double convertFromBaseUnit(double value) { return value / baseFactor; }
}

enum VolumeUnit implements IMeasurable {
    LITRE(1000.0), MILLILITRE(1.0);
    private final double baseFactor;
    VolumeUnit(double baseFactor) { this.baseFactor = baseFactor; }
    @Override public double convertToBaseUnit(double value) { return value * baseFactor; }
    @Override public double convertFromBaseUnit(double value) { return value / baseFactor; }
}

class Quantity<U extends IMeasurable> {
    private final double value;
    private final U unit;

    public Quantity(double value, U unit) {
        this.value = value;
        this.unit = unit;
    }

    public Quantity<U> subtract(Quantity<U> other) {
        return subtract(other, this.unit);
    }

    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        validate(other);
        if (targetUnit == null) throw new IllegalArgumentException("Target unit cannot be null");

        double baseValue1 = this.unit.convertToBaseUnit(this.value);
        double baseValue2 = other.unit.convertToBaseUnit(other.value);
        double resultBase = baseValue1 - baseValue2;

        double finalValue = Math.round(targetUnit.convertFromBaseUnit(resultBase) * 100.0) / 100.0;
        return new Quantity<>(finalValue, targetUnit);
    }

    public double divide(Quantity<U> other) {
        validate(other);
        if (other.value == 0) throw new ArithmeticException("Division by zero");

        double baseValue1 = this.unit.convertToBaseUnit(this.value);
        double baseValue2 = other.unit.convertToBaseUnit(other.value);

        return baseValue1 / baseValue2;
    }

    private void validate(Quantity<U> other) {
        if (other == null) throw new IllegalArgumentException("Operand cannot be null");
        if (!this.unit.getClass().equals(other.unit.getClass())) {
            throw new IllegalArgumentException("Cross-category arithmetic is not allowed");
        }
    }

    @Override
    public String toString() { return "Quantity(" + value + ", " + unit + ")"; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quantity<?> quantity = (Quantity<?>) o;
        return Double.compare(quantity.value, value) == 0 && unit.equals(quantity.unit);
    }
}

public class QuantityMeasureApp {
    public static void main(String[] args) {
        try {
            System.out.println("--- Subtraction (Implicit) ---");
            System.out.println(new Quantity<>(10.0, LengthUnit.FEET).subtract(new Quantity<>(6.0, LengthUnit.INCHES)));
            System.out.println(new Quantity<>(10.0, WeightUnit.KILOGRAM).subtract(new Quantity<>(5000.0, WeightUnit.GRAM)));
            System.out.println(new Quantity<>(5.0, VolumeUnit.LITRE).subtract(new Quantity<>(500.0, VolumeUnit.MILLILITRE)));

            System.out.println("\n--- Subtraction (Explicit) ---");
            System.out.println(new Quantity<>(10.0, LengthUnit.FEET).subtract(new Quantity<>(6.0, LengthUnit.INCHES), LengthUnit.INCHES));
            System.out.println(new Quantity<>(5.0, VolumeUnit.LITRE).subtract(new Quantity<>(2.0, VolumeUnit.LITRE), VolumeUnit.MILLILITRE));

            System.out.println("\n--- Subtraction (Negative/Zero) ---");
            System.out.println(new Quantity<>(5.0, LengthUnit.FEET).subtract(new Quantity<>(10.0, LengthUnit.FEET)));
            System.out.println(new Quantity<>(10.0, LengthUnit.FEET).subtract(new Quantity<>(120.0, LengthUnit.INCHES)));

            System.out.println("\n--- Division ---");
            System.out.println(new Quantity<>(10.0, LengthUnit.FEET).divide(new Quantity<>(2.0, LengthUnit.FEET)));
            System.out.println(new Quantity<>(24.0, LengthUnit.INCHES).divide(new Quantity<>(2.0, LengthUnit.FEET)));
            System.out.println(new Quantity<>(2000.0, WeightUnit.GRAM).divide(new Quantity<>(1.0, WeightUnit.KILOGRAM)));

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}