package myau.property.properties;

import java.util.function.BooleanSupplier;

public class PercentProperty extends IntProperty {
    public PercentProperty(String name, int def) { super(name, def, 0, 100); }
    public PercentProperty(String name, int def, BooleanSupplier vis) { super(name, def, 0, 100, vis); }
    public String formatValue() { return value + "%"; }
}
