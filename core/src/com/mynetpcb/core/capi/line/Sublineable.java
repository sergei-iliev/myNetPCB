package com.mynetpcb.core.capi.line;

import java.awt.Rectangle;

import java.util.Set;


/**
 * Represent sub line selection
 * @author Sergey Iliev
 */
public interface Sublineable {
    
    public boolean isSublineSelected();
    
    public boolean isSublineInRect(Rectangle r);
    
    public void setSublineSelected(Rectangle r,boolean selected);
    
    public Set<LinePoint> getSublinePoints(); 
}
