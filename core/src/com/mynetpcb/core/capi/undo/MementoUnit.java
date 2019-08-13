package com.mynetpcb.core.capi.undo;

import com.mynetpcb.core.capi.unit.Unit;

public interface MementoUnit<U extends Unit> {
    
    public void loadStateTo(U unit);
    
    public void saveStateFrom(U unit);        
}
