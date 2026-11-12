package myau.property.properties;

import myau.property.Property;
import java.util.function.BooleanSupplier;

public class TextProperty extends Property<String> {
    public TextProperty(String name, String def) { super(name, def, null); }
    public TextProperty(String name, String def, BooleanSupplier vis) { super(name, def, vis); }
    public String formatValue() { return value; }
}
