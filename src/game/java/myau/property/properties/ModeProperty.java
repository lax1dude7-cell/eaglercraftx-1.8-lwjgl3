package myau.property.properties;

import myau.property.Property;

public class ModeProperty extends Property {
    private int value;
    private final String[] modes;
    public ModeProperty(String name, int def, String[] modes) { super(name); value = def; this.modes = modes; }
    public ModeProperty(String name, int def, String[] modes, myau.property.Condition c) { this(name, def, modes); setCondition(c); }
    public int getValue() { return value; }
    public void setValue(int v) { value = v; }
    public String getModeString() { return modes[value]; }
    public void nextMode() { value = (value + 1) % modes.length; }
    public void previousMode() { value = (value - 1 + modes.length) % modes.length; }
    public String formatValue() { return modes[value]; }
}
