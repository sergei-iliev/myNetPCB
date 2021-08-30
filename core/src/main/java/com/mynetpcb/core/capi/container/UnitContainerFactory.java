package com.mynetpcb.core.capi.container;

public interface UnitContainerFactory<U extends UnitContainer> {  
    public U createUnitContainer();
}
