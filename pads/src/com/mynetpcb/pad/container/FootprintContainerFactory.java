package com.mynetpcb.pad.container;

import com.mynetpcb.core.capi.container.UnitContainerFactory;

public class FootprintContainerFactory implements UnitContainerFactory<FootprintContainer>{

    @Override
    public FootprintContainer createUnitContainer() {
        return new FootprintContainer();
    }
}
