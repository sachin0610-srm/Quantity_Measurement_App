import java.util.Objects;
import java.util.function.DoubleBinaryOperator;

public class QuantityMeasureApp {

    interface IMeasurable {
        double convertToBaseUnit(double value);
        double convertFromBaseUnit(double value);
    }

    public enum LengthUnit implements IMeasurable {
        FEET(12.0), INCHES(1.0);
        private final double factor;
        LengthUnit(double factor) { this.factor = factor; }
        @Override public double convertToBaseUnit(double v) { return v * factor; }
        @Override public double convertFromBaseUnit(double v) { return v / factor; }
    }

    public enum WeightUnit implements IMeasurable {
        KILOGRAM(1000.0), GRAM(1.0);
        private final double factor;
        WeightUnit(double factor) { this.factor = factor; }
        @Override public double convertToBaseUnit(double v) { return v * factor; }
        @Override public double convertFromBaseUnit(double v) { return v / factor; }
    }

    private enum ArithmeticOperation {
        ADD((a, b) -> a + b),
        SUBTRACT((a, b) -> a - b),
        DIVIDE((a, b) -> {
            if (b == 0) throw new ArithmeticException("Division by zero");
            return a / b;
        });

        private final DoubleBinaryOperator operator;
        ArithmeticOperation(DoubleBinaryOperator operator) { this.operator = operator; }
        public double compute(double a, double b) { return operator.applyAsDouble(a, b); }
    }

    public static class Quantity<U extends Enum<U> & IMeasurable> {
        private final double value;
        private final U unit;

        public Quantity(double value, U unit) {
            this.value = value;
            this.unit = unit;
        }

        public Quantity<U> add(Quantity<U> other) {
            return add(other, this.unit);
        }

        public Quantity<U> add(Quantity<U> other, U targetUnit) {
            double resultBase = performBaseArithmetic(other, ArithmeticOperation.ADD, targetUnit);
            return new Quantity<>(round(targetUnit.convertFromBaseUnit(resultBase)), targetUnit);
        }

        public Quantity<U> subtract(Quantity<U> other) {
            return subtract(other, this.unit);
        }

        public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
            double resultBase = performBaseArithmetic(other, ArithmeticOperation.SUBTRACT, targetUnit);
            return new Quantity<>(round(targetUnit.convertFromBaseUnit(resultBase)), targetUnit);
        }

        public Double divide(Quantity<U> other) {
            return performBaseArithmetic(other, ArithmeticOperation.DIVIDE, null);
        }

        private void validateArithmeticOperands(Quantity<U> other, U targetUnit, boolean targetUnitRequired) {
            if (other == null) throw new IllegalArgumentException("Operand cannot be null");
            if (targetUnitRequired && targetUnit == null) throw new IllegalArgumentException("Target unit required");
            if (!this.unit.getClass().equals(other.unit.getClass())) {
                throw new IllegalArgumentException("Incompatible measurement categories");
            }
            if (!Double.isFinite(this.value) || !Double.isFinite(other.value)) {
                throw new IllegalArgumentException("Values must be finite");
            }
        }

        private double performBaseArithmetic(Quantity<U> other, ArithmeticOperation operation, U targetUnit) {
            validateArithmeticOperands(other, targetUnit, operation != ArithmeticOperation.DIVIDE);
            double base1 = this.unit.convertToBaseUnit(this.value);
            double base2 = other.unit.convertToBaseUnit(other.value);
            return operation.compute(base1, base2);
        }

        private double round(double v) { return Math.round(v * 100.0) / 100.0; }

        @Override
        public String toString() { return "Quantity(" + value + ", " + unit + ")"; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Quantity<?> quantity = (Quantity<?>) o;
            return Double.compare(quantity.value, value) == 0 && Objects.equals(unit, quantity.unit);
        }
    }

    public static void main(String[] args) {
        Quantity<LengthUnit> feet = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> inches = new Quantity<>(12.0, LengthUnit.INCHES);

        System.out.println(feet.add(inches));
        System.out.println(feet.subtract(new Quantity<>(6.0, LengthUnit.INCHES)));
        System.out.println(inches.divide(feet));

        Quantity<WeightUnit> kg = new Quantity<>(10.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> g = new Quantity<>(5000.0, WeightUnit.GRAM);
        System.out.println(kg.add(g, WeightUnit.GRAM));
    }
}