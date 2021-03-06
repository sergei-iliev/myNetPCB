package com.mynetpcb.core.capi.unit;

import com.mynetpcb.d2.shapes.Point;


/**
 * Shape belongs to a unit.
 * @param <U>
 * @author Sergey Iliev
 */
public interface Unitable<U extends Unit> {
    /*
     * this returns the Moveable element context{Circuit,Module,Package}
     */

    public U getOwningUnit();
    
    /*
     * if element is cloned the initial owner is null
     */
    public void setOwningUnit(U unit);    
    
}

