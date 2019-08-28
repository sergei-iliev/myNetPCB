package com.mynetpcb.core.capi.unit;

import java.awt.Point;

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
    
    /**
    * align symbol to grid
    * @return difference between fixed and aligned point
    */
     public Point alignToGrid(boolean isRequired) ;
}

