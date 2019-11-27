package com.mynetpcb.core.capi.pin;


import java.awt.Rectangle;

import java.util.Collection;


/**
 *SCHSymbol and PCBFootprint must implement it
 * @author Sergey Iliev
 */
@Deprecated
public interface Pinaware<P extends Pinable>{
  
    public Collection<P> getPins(); 
    
    public Rectangle getPinsRect();
    
    public P getPin(int x,int y);

}
