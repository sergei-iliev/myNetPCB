package com.mynetpcb.circuit.container;

import com.mynetpcb.core.capi.container.UnitContainerFactory;

public class CircuitContainerFactory implements UnitContainerFactory<CircuitContainer>{

    @Override
    public CircuitContainer createUnitContainer() {
        return new CircuitContainer();
    }
}