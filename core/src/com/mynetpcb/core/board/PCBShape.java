package com.mynetpcb.core.board;


public interface PCBShape {
    
//    /**
//     * Main pcb asset constructor
//     * @param owningUnit
//     * @param x
//     * @param y
//     * @param width
//     * @param height
//     * @param thickness
//     * @param layermask  to which this pcb asset will be attached
//     */
//    public PCBShape(Unit owningUnit,int x,int y,int width,int height,int thickness,int layermask) {
//        super(owningUnit,x,y, width, height,thickness,layermask); 
//    }
//    
//    public PCBShape clone() throws CloneNotSupportedException{
//        return (PCBShape)super.clone();
//    }
//    /**
//     * Paint pcb asset considering the active/visible pcb layer
//     * @param g2
//     * @param viewportWindow
//     * @param scale
//     * @param layermask visible layer
//     */
//    public abstract void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermask);
//
//    @Override
//    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale) {
//       this.Paint(g2, viewportWindow, scale, copper.getLayerMaskID());
//    }
}
