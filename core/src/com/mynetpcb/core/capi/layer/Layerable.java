package com.mynetpcb.core.capi.layer;

import com.mynetpcb.d2.shapes.Line;


public interface Layerable {
    /**
     *Set layer the shape belongs to -> may be Copper is not the right word
     * @param copper
     */
    public void setCopper(Layer.Copper copper);

    /**
     *
     * @return layer the shape belongs to
     */
    public Layer.Copper getCopper();

    /**
     * calculate the drawing position in regard to layer position
     * @return
     */
    public int getDrawingOrder();

    /**
     *Check if shape belongs to one of currently visible layers
     * @param layermasks
     * @return
     */
    public boolean isVisibleOnLayers(int layermasks);

    /**
     * Sets the viewing side {TOP,BOTTOM}
     * Mirroring happens around the line
     */
    public default void setSide(Layer.Side side, Line line,double angle) {
    }
}
