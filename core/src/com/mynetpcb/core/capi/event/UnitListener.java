package com.mynetpcb.core.capi.event;

import java.util.EventListener;

public interface UnitListener extends EventListener{
    
    public void addUnitEvent(UnitEvent e);
    
    public void deleteUnitEvent(UnitEvent e);

    public void renameUnitEvent(UnitEvent e);    
    
    public void selectUnitEvent(UnitEvent e);   
    
    public void propertyChangeEvent(UnitEvent e);
}
