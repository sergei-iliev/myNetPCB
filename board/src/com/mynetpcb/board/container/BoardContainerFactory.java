package com.mynetpcb.board.container;

import com.mynetpcb.core.capi.container.UnitContainerFactory;

public class BoardContainerFactory implements UnitContainerFactory<BoardContainer>{

    @Override
    public BoardContainer createUnitContainer() {
        return new BoardContainer();
    }
}
