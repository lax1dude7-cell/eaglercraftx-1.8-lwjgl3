package myau.property.properties;

import myau.property.Property;
import myau.property.SliderProperty;
import java.util.function.BooleanSupplier;

public class IntProperty extends Property<Integer> implements SliderProperty {
    private final int min, max;
    public IntProperty(String name, int def, int min, int max) { this(name, def, min, max, null); }
    public IntProperty(String name, int def, int min, int max, BooleanSupplier vis) {
        super(name, def, vis); this.min = min; this.max = max;
    }
    public double getMin() { return min; }
    public double getMax() { return max; }
    public double getIncrement() { return 1; }
    public double getStep() { return 1; }
    public double getDouble() { return value; }
    public void setDouble(double v) { value = (int) Math.max(min, Math.min(max, Math.round(v))); }
    public String getValueString() { return formatValue(); }
    public String formatValue() { return String.valueOf(value); }
}
