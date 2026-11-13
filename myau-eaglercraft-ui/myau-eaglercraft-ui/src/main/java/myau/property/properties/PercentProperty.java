package myau.property.properties;

public class PercentProperty extends IntProperty {
    public PercentProperty(String name, int def) { super(name, def, 0, 100); }
    public PercentProperty(String name, int def, myau.property.Condition c) { this(name, def); setCondition(c); }
    public String formatValue() { return getValue() + "%"; }
}
