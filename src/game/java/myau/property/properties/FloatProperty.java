package myau.property.properties;

import myau.property.Property;
import myau.property.SliderProperty;

public class FloatProperty extends Property implements SliderProperty {
    private float value;
    private final float min, max;
    public FloatProperty(String name, float def, float min, float max) { super(name); value = def; this.min = min; this.max = max; }
    public FloatProperty(String name, float def, float min, float max, myau.property.Condition c) { this(name, def, min, max); setCondition(c); }
    public float getValue() { return value; }
    public void setValue(float v) {
        float bounded = Math.max(min, Math.min(max, v));
        if (value == bounded) return;
        value = bounded;
        myau.Myau.saveConfig();
    }
    public double getMin() { return min; }
    public double getMax() { return max; }
    public double getIncrement() { return 0.01; }
    public double getStep() { return Math.max(0.01, (max - min) / 100.0); }
    public double getDouble() { return value; }
    public void setDouble(double v) { setValue((float) v); }
    public String getValueString() { return formatValue(); }
    public String formatValue() {
        float r = Math.round(value * 100f) / 100f;
        String s = String.valueOf(r);
        return s.endsWith(".0") ? s.substring(0, s.length() - 2) : s;
    }
}
