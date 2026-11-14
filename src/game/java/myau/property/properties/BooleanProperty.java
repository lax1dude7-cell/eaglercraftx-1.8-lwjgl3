package myau.property.properties;

import myau.property.Property;

public class BooleanProperty extends Property {
    private boolean value;
    public BooleanProperty(String name, boolean def) { super(name); value = def; }
    public BooleanProperty(String name, boolean def, myau.property.Condition c) { this(name, def); setCondition(c); }
    public boolean getValue() { return value; }
    public void setValue(boolean v) {
        if (value == v) return;
        value = v;
        myau.Myau.saveConfig();
    }
    public String formatValue() { return value ? "On" : "Off"; }
}
