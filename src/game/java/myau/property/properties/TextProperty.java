package myau.property.properties;

import myau.property.Property;

public class TextProperty extends Property {
    private String value;
    public TextProperty(String name, String def) { super(name); value = def; }
    public TextProperty(String name, String def, myau.property.Condition c) { this(name, def); setCondition(c); }
    public String getValue() { return value; }
    public void setValue(String v) {
        if (value.equals(v)) return;
        value = v;
        myau.Myau.saveConfig();
    }
    public String formatValue() { return value; }
}
