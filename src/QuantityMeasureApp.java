import java.util.Objects;

public class QuantityMeasureApp {

    public static void main(String[] args) {
        System.out.println("--- Length Operations (UC1–UC8) ---");
        Quantity<LengthUnit> oneFoot = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> twelveInches = new Quantity<>(12.0, LengthUnit.INCHES);

        demonstrateEquality(oneFoot, twelveInches, "1.0 Feet == 12.0 Inches");
        System.out.println("Conversion: " + oneFoot.convertTo(LengthUnit.INCHES));
        System.out.println("Addition (1ft + 12in): " + oneFoot.add(twelveInches, LengthUnit.FEET));

        System.out.println("\n--- Weight Operations (UC9) ---");
        Quantity<WeightUnit> oneKg = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> thousandGrams = new Quantity<>(1000.0, WeightUnit.GRAM);

        demonstrateEquality(oneKg, thousandGrams, "1.0 KG == 1000.0 Grams");
        System.out.println("Addition (1kg + 1000g): " + oneKg.add(thousandGrams, WeightUnit.KILOGRAM));

        System.out.println("\n--- Cross-Category Prevention (UC10) ---");
        System.out.println("Comparing 1.0 Feet to 1.0 KG -> " + oneFoot.equals(oneKg));
    }

    public static <U extends IMeasurable> void demonstrateEquality(Quantity<U> q1, Quantity<U> q2, String label) {
        System.out.println(label + " -> Result: " + q1.equals(q2));
    }
}

interface IMeasurable {
    double getConversionFactor();
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
}

enum LengthUnit implements IMeasurable {
    FEET(12.0), INCHES(1.0), YARDS(36.0), CENTIMETERS(0.4);

    private final double factor;
    LengthUnit(double factor) { this.factor = factor; }

    @Override public double getConversionFactor() { return factor; }
    @Override public double convertToBaseUnit(double v) { return v * factor; }
    @Override public double convertFromBaseUnit(double bv) { return bv / factor; }
    @Override public String getUnitName() { return this.name(); }
}

enum WeightUnit implements IMeasurable {
    GRAM(1.0), KILOGRAM(1000.0), TONNE(1000000.0);

    private final double factor;
    WeightUnit(double factor) { this.factor = factor; }

    @Override public double getConversionFactor() { return factor; }
    @Override public double convertToBaseUnit(double v) { return v * factor; }
    @Override public double convertFromBaseUnit(double bv) { return bv / factor; }
    @Override public String getUnitName() { return this.name(); }
}

class Quantity<U extends IMeasurable> {
    private final double value;
    private final U unit;

    public Quantity(double value, U unit) {
        // Snippet 4.1: Constructor Validation
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Value must be finite");
        this.value = value;
        this.unit = unit;
    }

    public Quantity<U> convertTo(U targetUnit) {
        double baseValue = unit.convertToBaseUnit(this.value);
        double convertedValue = targetUnit.convertFromBaseUnit(baseValue);
        double roundedValue = Math.round(convertedValue * 100.0) / 100.0;
        return new Quantity<>(roundedValue, targetUnit);
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

        if (this.unit.getClass() != that.unit.getClass()) return false;

        double v1 = this.unit.convertToBaseUnit(this.value);
        double v2 = ((IMeasurable)that.unit).convertToBaseUnit(that.value);
        return Double.compare(v1, v2) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(unit.convertToBaseUnit(value), unit.getClass());
    }

    @Override
    public String toString() {
        return String.format("Quantity(%.1f, %s)", value, unit.getUnitName());
    }

    public double getValue() { return value; }
}