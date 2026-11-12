package myau.property;

/** Anything the GUI can draw as a slider. */
public interface SliderProperty {
    String getName();
    double getMin();
    double getMax();
    double getIncrement();
    double getStep();
    double getDouble();
    void setDouble(double v);
    String getValueString();
    boolean isVisible();
}
