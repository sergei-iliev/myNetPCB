package com.mynetpcb.core.capi.print;

import java.awt.Graphics2D;

/**
 *Printing contract to have the drawing item printed out
 * @author Sergey Iliev
 */
public interface Printable {
    /**
     * Marks symbol as printable
     * @param g2 the printer graphics object
     * @param layermask current layer to print
     */
    public void print(Graphics2D g2,PrintContext printContext,int layermask);
}
