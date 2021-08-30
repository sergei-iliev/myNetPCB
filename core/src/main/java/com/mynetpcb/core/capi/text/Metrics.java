package com.mynetpcb.core.capi.text;


import java.awt.Graphics2D;


/*Text Rectangle
     *
     *                  |-ascent----------------|
     *                  |                       |
     *       anchorPoint|-----------------------|
     *                  |_descent_______________|
     *
     *
     *
     *
     *
     */
public interface Metrics {
    
    public int getWidth();
    
    
    public int getHeight();
    
    public int getDescent();
    
    public int getAscent();
    
/**
 * Mark the current text metrics as obsolete,make them recalculated next time Canvas is drawn
 * Happens at text change->rotation,mirror,text change
 */
    public void updateMetrics();

/**
     *Calculate text rectangle metrics
     * @param g2 graphics - if null use internal canvas to calculate
     * @param text 
     */
    //public void calculateMetrics(Graphics2D g2,Text.Alignment alignment,Text.Style style,int fontSize, String text);

}

