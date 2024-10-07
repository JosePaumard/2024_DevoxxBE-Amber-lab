package org.paumard.hol.sealedswitch.model;

public record Square(double edge) implements Shape {

//    @Override
    public double surface() {
        return edge*edge;
    }
}
