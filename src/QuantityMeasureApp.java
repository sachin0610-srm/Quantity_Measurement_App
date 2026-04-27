import java.util.Objects;

enum LengthUnit {
    INCHES(1.0),
    FEET(12.0),
    YARDS(36.0),
    CENTIMETERS(0.393701);

    private final double conversionFactor;

    LengthUnit(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    public double getConversionFactor() {
        return conversionFactor;
    }
}

class QuantityLength {
    private final double value;
    private final LengthUnit unit;

    public QuantityLength(double value, LengthUnit unit) {
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Value must be a finite number");
        this.value = value;
        this.unit = unit;
    }

    public static double convert(double value, LengthUnit sourceUnit, LengthUnit targetUnit) {
        if (sourceUnit == null || targetUnit == null) {
            throw new IllegalArgumentException("Units cannot be null");
        }
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Value must be finite");
        }
        double valueInBase = value * sourceUnit.getConversionFactor();
        return valueInBase / targetUnit.getConversionFactor();
    }

    public QuantityLength convertTo(LengthUnit targetUnit) {
        double convertedValue = convert(this.value, this.unit, targetUnit);
        return new QuantityLength(convertedValue, targetUnit);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof QuantityLength)) return false;

        QuantityLength other = (QuantityLength) obj;
        double v1 = this.value * this.unit.getConversionFactor();
        double v2 = other.value * other.unit.getConversionFactor();
        return Math.abs(v1 - v2) < 1e-6;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value * unit.getConversionFactor());
    }

    @Override
    public String toString() {
        return String.format("%.2f %s", value, unit);
    }
}

public class QuantityMeasureApp {

    public static void main(String[] args) {
        System.out.println("=== UC5 Unit Conversion Demo ===\n");

        System.out.println("--- Static Method Conversions ---");
        showStaticConversion(1.0, LengthUnit.FEET, LengthUnit.INCHES);
        showStaticConversion(3.0, LengthUnit.YARDS, LengthUnit.FEET);
        showStaticConversion(1.0, LengthUnit.CENTIMETERS, LengthUnit.INCHES);

        System.out.println("\n--- Instance Object Conversions ---");
        QuantityLength yard = new QuantityLength(1.0, LengthUnit.YARDS);
        showInstanceConversion(yard, LengthUnit.INCHES);

        System.out.println("\n--- Equality Checks ---");
        QuantityLength twelveInches = new QuantityLength(12.0, LengthUnit.INCHES);
        QuantityLength oneFoot = new QuantityLength(1.0, LengthUnit.FEET);
        System.out.println(twelveInches + " equals " + oneFoot + " -> " + twelveInches.equals(oneFoot));
    }

    private static void showStaticConversion(double val, LengthUnit from, LengthUnit to) {
        double result = QuantityLength.convert(val, from, to);
        System.out.printf("Input: %s %s -> Result: %.4f %s%n", val, from, result, to);
    }

    private static void showInstanceConversion(QuantityLength length, LengthUnit to) {
        QuantityLength resultObj = length.convertTo(to);
        System.out.println("Original: " + length + " | Converted: " + resultObj);
    }
}