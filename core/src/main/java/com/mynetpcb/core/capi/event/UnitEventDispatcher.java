package com.mynetpcb.core.capi.event;

public interface UnitEventDispatcher {
    public void fireUnitEvent(UnitEvent e);

    public void addUnitListener(UnitListener listener);

    public void removeUnitListener(UnitListener listener);
}
