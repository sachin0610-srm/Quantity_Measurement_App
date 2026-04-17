import java.util.Objects;

/**
 * Main Class for Quantity Measurement Application
 * Implements UC4: Extended Unit Support (Feet, Inches, Yards, Centimeters)
 */
public class QuantityMeasureApp {

    public enum LengthUnit {

        INCHES(1.0),
        FEET(12.0),
        YARDS(36.0),
        CENTIMETERS(0.393701);

        private final double conversionFactor;

        LengthUnit(double conversionFactor) {
            this.conversionFactor = conversionFactor;
        }

        public double convertToBase(double value) {
            return value * this.conversionFactor;
        }
    }

    public static class QuantityLength {
        private final double value;
        private final LengthUnit unit;

        public QuantityLength(Double value, LengthUnit unit) {
            if (value == null) throw new IllegalArgumentException("Value cannot be null");
            if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
            this.value = value;
            this.unit = unit;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            QuantityLength that = (QuantityLength) o;

            double baseValue1 = this.unit.convertToBase(this.value);
            double baseValue2 = that.unit.convertToBase(that.value);

            return Math.abs(baseValue1 - baseValue2) < 0.00001;
        }

        @Override
        public int hashCode() {
            return Objects.hash(unit.convertToBase(value));
        }

        @Override
        public String toString() {
            return value + " " + unit;
        }
    }

    public static void main(String[] args) {
        System.out.println("--- UC4: Extended Unit Support Comparison Results ---");

        compare(new QuantityLength(1.0, LengthUnit.YARDS), new QuantityLength(3.0, LengthUnit.FEET));

        compare(new QuantityLength(1.0, LengthUnit.YARDS), new QuantityLength(36.0, LengthUnit.INCHES));

        compare(new QuantityLength(2.0, LengthUnit.YARDS), new QuantityLength(2.0, LengthUnit.YARDS));

        compare(new QuantityLength(2.0, LengthUnit.CENTIMETERS), new QuantityLength(2.0, LengthUnit.CENTIMETERS));

        compare(new QuantityLength(1.0, LengthUnit.CENTIMETERS), new QuantityLength(0.393701, LengthUnit.INCHES));

        compare(new QuantityLength(1.0, LengthUnit.YARDS), new QuantityLength(2.0, LengthUnit.FEET));
    }

    private static void compare(QuantityLength q1, QuantityLength q2) {
        boolean result = q1.equals(q2);
        System.out.printf("Input: %s and %s | Output: Equal (%b)%n", q1, q2, result);
    }
}