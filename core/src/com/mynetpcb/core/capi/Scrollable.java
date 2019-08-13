package com.mynetpcb.core.capi;

import java.awt.Point;

public interface Scrollable {
    public void ScrollX(int x);

    public void ScrollY(int y); 
    /**
     *Rotate the content of the component tweaking the scrolls in
     * regard to point p
     * @param p
     */
    public void Rotate(int rotationType,Point p);
}
