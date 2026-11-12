package myau.property;

import java.util.function.BooleanSupplier;

public abstract class Property<T> {
    private final String name;
    protected T value;
    private final BooleanSupplier visible;

    protected Property(String name, T def, BooleanSupplier visible) {
        this.name = name;
        this.value = def;
        this.visible = visible;
    }

    public String getName() { return name; }
    public T getValue() { return value; }
    public void setValue(T v) { this.value = v; }
    public boolean isVisible() { return visible == null || visible.getAsBoolean(); }
    public abstract String formatValue();
}
