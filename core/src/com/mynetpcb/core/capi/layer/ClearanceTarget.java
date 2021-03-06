package com.mynetpcb.core.capi.layer;

import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.board.Net;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/*
 * All copper shapes are targets
 */
public interface ClearanceTarget extends Clearanceaware,Net{

    /**
       * Copper area initiates clearence drawing on all copper shapes 
       */
    public <T extends  ClearanceSource> void drawClearance(Graphics2D g2,ViewportWindow viewportWindow,AffineTransform scale,T source);
    /**
     * Copper area initiates clearence printing on all copper shapes
     */
    public <T extends  ClearanceSource> void printClearance(Graphics2D g2,PrintContext printContext,T source);
    
}
