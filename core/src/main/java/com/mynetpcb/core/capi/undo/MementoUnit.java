package com.mynetpcb.core.capi.undo;

import com.mynetpcb.core.capi.unit.Unit;

@Deprecated
public interface MementoUnit<U extends Unit> {
    
    public void loadStateTo(U unit);
    
    public void saveStateFrom(U unit);        
}
