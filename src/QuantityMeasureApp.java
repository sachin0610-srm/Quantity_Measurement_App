public class QuantityMeasureApp {

    public static boolean compareFeet(double val1, double val2) {
        Feet f1 = new Feet(val1);
        Feet f2 = new Feet(val2);
        return f1.equals(f2);
    }

    public static boolean compareInches(double val1, double val2) {
        Inches i1 = new Inches(val1);
        Inches i2 = new Inches(val2);
        return i1.equals(i2);
    }

    public static void main(String[] args) {
        System.out.println("Input: 1.0 inch and 1.0 inch | Output: " + compareInches(1.0, 1.0));
        System.out.println("Input: 1.0 ft and 1.0 ft     | Output: " + compareFeet(1.0, 1.0));
        System.out.println("Input: 1.0 ft and 2.0 ft     | Output: " + compareFeet(1.0, 2.0));
    }
}

class Feet {
    private final Double value;

    public Feet(Double value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feet feet = (Feet) o;
        return Double.compare(feet.value, value) == 0;
    }
}

class Inches {
    private final Double value;

    public Inches(Double value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inches inches = (Inches) o;
        return Double.compare(inches.value, value) == 0;
    }
}