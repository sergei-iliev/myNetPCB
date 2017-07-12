package com.mynetpcb.core.board;

import com.mynetpcb.core.pad.Layer;


public interface CompositeLayerable {

    /**
     *Check if layer is visible
     * @param layer mask
     * @return layer visibility
     */
    public boolean isLayerVisible(int mask);

    /**
     *Set layer visibility
     * @param mask
     * @param flag
     */
    public void setLayerVisible(int mask, boolean flag);


    /**
     *Each layer is represented by mask
     * @return
     */
    public int getLayerMaskID();

    /**
     *Change layer at run time
     * @param latermask
     */
    public void setLayerMaskID(int layermask);
    
    public void setActiveSide(Layer.Side side);
    /**
     *Viewer perspective is either top or bottom
     * @return
     */
    public Layer.Side getActiveSide();

}
