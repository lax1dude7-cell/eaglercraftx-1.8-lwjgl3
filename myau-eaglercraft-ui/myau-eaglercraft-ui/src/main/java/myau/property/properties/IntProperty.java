package myau.property.properties;

import myau.property.Property;
import myau.property.SliderProperty;

public class IntProperty extends Property implements SliderProperty {
    private int value;
    private final int min, max;
    public IntProperty(String name, int def, int min, int max) { super(name); value = def; this.min = min; this.max = max; }
    public IntProperty(String name, int def, int min, int max, myau.property.Condition c) { this(name, def, min, max); setCondition(c); }
    public int getValue() { return value; }
    public void setValue(int v) { value = Math.max(min, Math.min(max, v)); }
    public double getMin() { return min; }
    public double getMax() { return max; }
    public double getIncrement() { return 1; }
    public double getStep() { return 1; }
    public double getDouble() { return value; }
    public void setDouble(double v) { setValue((int) Math.round(v)); }
    public String getValueString() { return formatValue(); }
    public String formatValue() { return String.valueOf(value); }
}
