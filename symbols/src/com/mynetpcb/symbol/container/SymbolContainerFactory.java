package com.mynetpcb.symbol.container;

import com.mynetpcb.core.capi.container.UnitContainerFactory;

public class SymbolContainerFactory implements UnitContainerFactory<SymbolContainer>{

    @Override
    public SymbolContainer createUnitContainer() {
        return new SymbolContainer();
    }
}
