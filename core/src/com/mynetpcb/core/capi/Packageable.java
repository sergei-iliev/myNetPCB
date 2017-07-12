package com.mynetpcb.core.capi;

import com.mynetpcb.core.pad.Packaging;


/**
 * Discribes the ability of the shape(Symbol/SCHSymbol) to be represented by a package
 * QFP,PLCC,DIP,LQFP
 */
public interface Packageable {
    
   public Packaging getPackaging();

}
