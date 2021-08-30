package com.mynetpcb.core.capi;

import java.awt.Point;

public interface Scrollable {
    public void scrollX(int x);

    public void scrollY(int y); 
    /**
     *Rotate the content of the component tweaking the scrolls in
     * regard to point p
     * @param p
     */
    public void rotate(int rotationType,Point p);
}
