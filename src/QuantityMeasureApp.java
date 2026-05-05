import java.util.Objects;

interface IMeasurable {
    double getConversionFactor();
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
}

enum VolumeUnit implements IMeasurable {
    LITRE(1.0, "Litre"),
    MILLILITRE(0.001, "Millilitre"),
    GALLON(3.78541, "Gallon");

    private final double conversionFactor;
    private final String unitName;

    VolumeUnit(double conversionFactor, String unitName) {
        this.conversionFactor = conversionFactor;
        this.unitName = unitName;
    }

    @Override
    public double getConversionFactor() {
        return conversionFactor;
    }

    @Override
    public double convertToBaseUnit(double value) {
        return value * conversionFactor;
    }

    @Override
    public double convertFromBaseUnit(double baseValue) {
        return baseValue / conversionFactor;
    }

    @Override
    public String getUnitName() {
        return unitName;
    }
}

class Quantity<U extends IMeasurable> {
    private final double value;
    private final U unit;
    private final double epsilon = 1e-6;

    public Quantity(double value, U unit) {
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
        this.value = value;
        this.unit = unit;
    }

    public Quantity<U> convertTo(U targetUnit) {
        double baseValue = this.unit.convertToBaseUnit(this.value);
        double convertedValue = targetUnit.convertFromBaseUnit(baseValue);
        return new Quantity<>(convertedValue, targetUnit);
    }

    public Quantity<U> add(Quantity<U> other) {
        return add(other, this.unit);
    }

    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        double sumInBase = this.unit.convertToBaseUnit(this.value) +
                other.unit.convertToBaseUnit(other.value);
        double finalValue = targetUnit.convertFromBaseUnit(sumInBase);
        return new Quantity<>(finalValue, targetUnit);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quantity<?> that = (Quantity<?>) o;
        if (!this.unit.getClass().equals(that.unit.getClass())) return false;
        double val1 = this.unit.convertToBaseUnit(this.value);
        double val2 = ((IMeasurable) that.unit).convertToBaseUnit(that.value);
        return Math.abs(val1 - val2) < epsilon;
    }

    @Override
    public int hashCode() {
        return Objects.hash(unit.convertToBaseUnit(value));
    }

    @Override
    public String toString() {
        if (unit == VolumeUnit.GALLON || unit == VolumeUnit.LITRE) {
            return String.format("Quantity(~%.6f, %s)", value, unit.getUnitName().toUpperCase());
        }
        return String.format("Quantity(%.1f, %s)", value, unit.getUnitName().toUpperCase());
    }
}

public class QuantityMeasureApp {
    public static void main(String[] args) {
        System.out.println("Equality Comparisons:");
        System.out.println("Input: new Quantity<>(1.0, LITRE).equals(new Quantity<>(1.0, LITRE)) -> Output: " +
                new Quantity<>(1.0, VolumeUnit.LITRE).equals(new Quantity<>(1.0, VolumeUnit.LITRE)));
        System.out.println("Input: new Quantity<>(1.0, LITRE).equals(new Quantity<>(1000.0, MILLILITRE)) -> Output: " +
                new Quantity<>(1.0, VolumeUnit.LITRE).equals(new Quantity<>(1000.0, VolumeUnit.MILLILITRE)));
        System.out.println("Input: new Quantity<>(1.0, GALLON).equals(new Quantity<>(1.0, GALLON)) -> Output: " +
                new Quantity<>(1.0, VolumeUnit.GALLON).equals(new Quantity<>(1.0, VolumeUnit.GALLON)));
        System.out.println("Input: new Quantity<>(1.0, LITRE).equals(new Quantity<>(0.264172, GALLON)) -> Output: " +
                new Quantity<>(1.0, VolumeUnit.LITRE).equals(new Quantity<>(0.264172, VolumeUnit.GALLON)));

        System.out.println("\nUnit Conversions:");
        System.out.println("Input: new Quantity<>(1.0, LITRE).convertTo(MILLILITRE) -> Output: " +
                new Quantity<>(1.0, VolumeUnit.LITRE).convertTo(VolumeUnit.MILLILITRE));
        System.out.println("Input: new Quantity<>(2.0, GALLON).convertTo(LITRE) -> Output: " +
                new Quantity<>(2.0, VolumeUnit.GALLON).convertTo(VolumeUnit.LITRE));

        System.out.println("\nAddition Operations (Implicit):");
        System.out.println("Input: 1.0L + 2.0L -> Output: " +
                new Quantity<>(1.0, VolumeUnit.LITRE).add(new Quantity<>(2.0, VolumeUnit.LITRE)));
        System.out.println("Input: 2.0 GAL + 3.78541L -> Output: " +
                new Quantity<>(2.0, VolumeUnit.GALLON).add(new Quantity<>(3.78541, VolumeUnit.LITRE)));

        System.out.println("\nAddition Operations (Explicit):");
        System.out.println("Input: 1.0L + 1000.0mL (to mL) -> Output: " +
                new Quantity<>(1.0, VolumeUnit.LITRE).add(new Quantity<>(1000.0, VolumeUnit.MILLILITRE), VolumeUnit.MILLILITRE));
    }
}