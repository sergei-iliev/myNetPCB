package com.mynetpcb.core.board;

import com.mynetpcb.core.pad.Layer;

/**
 * Describes the multilayering. Board consists of many layers which could be visible,drawable,eventable
 * For faster layer manipulation each layer is represented as a single bit in int variable.
 * Bit manipulation should be fastest in term of memory usage and execution.
 */
public class CompositeLayer implements CompositeLayerable{
    
    
    private int compositelayer;
    
    private Layer.Side activeside;
    
    public CompositeLayer() {
        compositelayer=Layer.LAYER_ALL;   
        activeside=Layer.Side.TOP;
    }


    @Override
    public boolean isLayerVisible(int mask) {
       return (compositelayer & mask)!=0;          
    }

    @Override
    public void setLayerVisible(int mask, boolean flag) {
        if(flag){
            compositelayer |= mask;     
        }else{
            compositelayer &= ~mask;
        }
    }


    @Override
    public int getLayerMaskID() {
        return compositelayer;
    }

    @Override
    public void setLayerMaskID(int layermask) {
        
    }

    /**
     *Set the active layer
     * @param copper
     */
    @Override
    public void setActiveSide(Layer.Side side) {
        this.activeside=side;
    }

    @Override
    public Layer.Side getActiveSide() {

        return activeside;
    }
}
