import java.util.Objects;

public class QuantityMeasureApp {

    enum LengthUnit {
        FEET(1.0),
        INCH(1.0 / 12.0);

        public final double conversionFactor;

        LengthUnit(double conversionFactor) {
            this.conversionFactor = conversionFactor;
        }

        public double convertToBase(double value) {
            return value * this.conversionFactor;
        }
    }

    static class Quantity {
        private final double value;
        private final LengthUnit unit;

        public Quantity(double value, LengthUnit unit) {
            if (unit == null) {
                throw new IllegalArgumentException("Unit cannot be null");
            }
            this.value = value;
            this.unit = unit;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            Quantity that = (Quantity) o;

            double valueInBase1 = this.unit.convertToBase(this.value);
            double valueInBase2 = that.unit.convertToBase(that.value);

            return Math.abs(valueInBase1 - valueInBase2) < 0.0001;
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
        System.out.println("Running UC3: Generic Quantity Class Test Suite...\n");

        test("Feet to Feet Same Value",
                new Quantity(1.0, LengthUnit.FEET), new Quantity(1.0, LengthUnit.FEET), true);

        test("Inch to Inch Same Value",
                new Quantity(1.0, LengthUnit.INCH), new Quantity(1.0, LengthUnit.INCH), true);

        test("Feet to Inch Equivalent",
                new Quantity(1.0, LengthUnit.FEET), new Quantity(12.0, LengthUnit.INCH), true);

        test("Inch to Feet Equivalent (Symmetry)",
                new Quantity(12.0, LengthUnit.INCH), new Quantity(1.0, LengthUnit.FEET), true);

        test("Feet to Feet Different Value",
                new Quantity(1.0, LengthUnit.FEET), new Quantity(2.0, LengthUnit.FEET), false);

        test("Inch to Inch Different Value",
                new Quantity(1.0, LengthUnit.INCH), new Quantity(2.0, LengthUnit.INCH), false);

        Quantity q = new Quantity(1.0, LengthUnit.FEET);
        System.out.println("[Test] Same Reference: " + (q.equals(q) ? "PASSED" : "FAILED"));

        System.out.println("[Test] Null Comparison: " + (!q.equals(null) ? "PASSED" : "FAILED"));

        try {
            new Quantity(1.0, null);
        } catch (IllegalArgumentException e) {
            System.out.println("[Test] Null Unit Rejected: PASSED");
        }
    }

    private static void test(String name, Quantity q1, Quantity q2, boolean expected) {
        boolean result = q1.equals(q2);
        String status = (result == expected) ? "PASSED" : "FAILED";
        System.out.printf("[Test] %-35s | Input: %-15s vs %-15s | Result: %b | Status: %s%n",
                name, q1, q2, result, status);
    }
}