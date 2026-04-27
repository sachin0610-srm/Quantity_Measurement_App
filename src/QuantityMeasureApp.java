import java.util.Objects;

public class QuantityMeasureApp {

    public enum WeightUnit {
        KILOGRAM(1.0),
        GRAM(0.001),
        POUND(0.453592);

        private final double conversionFactor;

        WeightUnit(double conversionFactor) {
            this.conversionFactor = conversionFactor;
        }

        public double convertToBaseUnit(double value) {
            return value * conversionFactor;
        }

        public double convertFromBaseUnit(double baseValue) {
            return baseValue / conversionFactor;
        }
    }

    public static class QuantityWeight {
        private final double value;
        private final WeightUnit unit;
        private static final double EPSILON = 1e-6;

        public QuantityWeight(double value, WeightUnit unit) {
            if (unit == null) {
                throw new IllegalArgumentException("Unit cannot be null");
            }
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Value must be a finite number");
            }
            this.value = value;
            this.unit = unit;
        }

        public QuantityWeight convertTo(WeightUnit targetUnit) {
            if (targetUnit == null) throw new IllegalArgumentException("Target unit cannot be null");
            double baseValue = this.unit.convertToBaseUnit(this.value);
            double convertedValue = targetUnit.convertFromBaseUnit(baseValue);
            return new QuantityWeight(convertedValue, targetUnit);
        }

        public QuantityWeight add(QuantityWeight other) {
            return add(this, other, this.unit);
        }

        public QuantityWeight add(QuantityWeight other, WeightUnit targetUnit) {
            return add(this, other, targetUnit);
        }

        private static QuantityWeight add(QuantityWeight w1, QuantityWeight w2, WeightUnit targetUnit) {
            double sumInBase = w1.unit.convertToBaseUnit(w1.value) + w2.unit.convertToBaseUnit(w2.value);
            return new QuantityWeight(targetUnit.convertFromBaseUnit(sumInBase), targetUnit);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false; // Category Type Safety

            QuantityWeight other = (QuantityWeight) obj;
            double v1Base = this.unit.convertToBaseUnit(this.value);
            double v2Base = other.unit.convertToBaseUnit(other.value);

            return Math.abs(v1Base - v2Base) < EPSILON;
        }

        @Override
        public int hashCode() {
            return Objects.hash(unit.convertToBaseUnit(value));
        }

        @Override
        public String toString() {
            return String.format("Quantity(%.3f, %s)", value, unit);
        }
    }

    public static void main(String[] args) {
        System.out.println("--- Equality Comparisons ---");
        QuantityWeight oneKg = new QuantityWeight(1.0, WeightUnit.KILOGRAM);
        QuantityWeight thousandG = new QuantityWeight(1000.0, WeightUnit.GRAM);
        System.out.println("1.0 kg == 1000.0 g: " + oneKg.equals(thousandG));

        QuantityWeight oneLb = new QuantityWeight(1.0, WeightUnit.POUND);
        QuantityWeight standardG = new QuantityWeight(453.592, WeightUnit.GRAM);
        System.out.println("1.0 lb == 453.592 g: " + oneLb.equals(standardG));

        System.out.println("\n--- Unit Conversions ---");
        QuantityWeight converted = oneKg.convertTo(WeightUnit.GRAM);
        System.out.println("1.0 kg converted to GRAM: " + converted);

        System.out.println("\n--- Addition Operations ---");
        QuantityWeight sum1 = oneKg.add(thousandG);
        System.out.println("1.0 kg + 1000.0 g (default unit): " + sum1);

        QuantityWeight sum2 = oneKg.add(thousandG, WeightUnit.GRAM);
        System.out.println("1.0 kg + 1000.0 g (target GRAM): " + sum2);
    }
}