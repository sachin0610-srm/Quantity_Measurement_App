import java.util.Objects;

enum LengthUnit {
    FEET(1.0),
    INCHES(1.0 / 12.0),
    YARDS(3.0),
    CENTIMETERS(1.0 / 30.48);

    private final double conversionFactor;

    LengthUnit(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    public double convertToBaseUnit(double value) {
        return value * this.conversionFactor;
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / this.conversionFactor;
    }
}

class QuantityLength {
    private final double value;
    private final LengthUnit unit;

    public QuantityLength(double value, LengthUnit unit) {
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
        if (Double.isNaN(value)) throw new IllegalArgumentException("Invalid value");
        this.value = value;
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuantityLength that = (QuantityLength) o;

        double firstValueInBase = this.unit.convertToBaseUnit(this.value);
        double secondValueInBase = that.unit.convertToBaseUnit(that.value);

        return Math.abs(firstValueInBase - secondValueInBase) < 0.01;
    }

    public QuantityLength convertTo(LengthUnit targetUnit) {
        double baseValue = this.unit.convertToBaseUnit(this.value);
        double convertedValue = targetUnit.convertFromBaseUnit(baseValue);
        return new QuantityLength(convertedValue, targetUnit);
    }

    public QuantityLength add(QuantityLength that, LengthUnit targetUnit) {
        double sumInBase = this.unit.convertToBaseUnit(this.value) +
                that.unit.convertToBaseUnit(that.value);
        double finalValue = targetUnit.convertFromBaseUnit(sumInBase);
        return new QuantityLength(finalValue, targetUnit);
    }

    @Override
    public String toString() {
        return String.format("%.2f %s", value, unit);
    }
}

public class QuantityMeasureApp {
    public static void main(String[] args) {
        QuantityLength oneYard = new QuantityLength(1.0, LengthUnit.YARDS);
        QuantityLength threeFeet = new QuantityLength(3.0, LengthUnit.FEET);
        System.out.println("1 Yard equals 3 Feet: " + oneYard.equals(threeFeet));

        QuantityLength oneFoot = new QuantityLength(1.0, LengthUnit.FEET);
        System.out.println("1 Foot to Inches: " + oneFoot.convertTo(LengthUnit.INCHES));

        QuantityLength distance1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength distance2 = new QuantityLength(12.0, LengthUnit.INCHES);
        QuantityLength total = distance1.add(distance2, LengthUnit.YARDS);
        System.out.println("1 Foot + 12 Inches in Yards: " + total);

        QuantityLength cmValue = new QuantityLength(30.48, LengthUnit.CENTIMETERS);
        System.out.println("30.48 cm equals 1 Foot: " + cmValue.equals(oneFoot));
    }
}