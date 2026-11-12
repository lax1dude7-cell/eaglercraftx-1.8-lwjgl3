package myau.property.properties;

import myau.property.Property;
import myau.property.SliderProperty;
import java.util.function.BooleanSupplier;

public class FloatProperty extends Property<Float> implements SliderProperty {
    private final float min, max;
    public FloatProperty(String name, float def, float min, float max) { this(name, def, min, max, null); }
    public FloatProperty(String name, float def, float min, float max, BooleanSupplier vis) {
        super(name, def, vis); this.min = min; this.max = max;
    }
    public double getMin() { return min; }
    public double getMax() { return max; }
    public double getIncrement() { return 0.01; }
    public double getStep() { return Math.max(0.01, (max - min) / 100.0); }
    public double getDouble() { return value; }
    public void setDouble(double v) { value = (float) Math.max(min, Math.min(max, v)); }
    public String getValueString() { return formatValue(); }
    public String formatValue() {
        float r = Math.round(value * 100f) / 100f;
        String s = String.valueOf(r);
        return s.endsWith(".0") ? s.substring(0, s.length() - 2) : s;
    }
}
