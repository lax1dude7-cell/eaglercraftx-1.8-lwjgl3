package myau.property.properties;

import myau.property.Property;
import java.util.function.BooleanSupplier;

public class BooleanProperty extends Property<Boolean> {
    public BooleanProperty(String name, boolean def) { super(name, def, null); }
    public BooleanProperty(String name, boolean def, BooleanSupplier vis) { super(name, def, vis); }
    public String formatValue() { return value ? "On" : "Off"; }
}
