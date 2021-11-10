package ru.nucodelabs.gem;

public class ModelTable {
    public double spacing;
    public double resistance;

    public ModelTable(double spacing, double resistance) {
        setSpacing(spacing);
        setResistance(resistance);
    }

    public void setResistance(double resistance) {
        this.resistance = resistance;
    }

    public void setSpacing(double spacing) {
        this.spacing = spacing;
    }
}
