package com.mynetpcb.core.capi.gui;


import com.mynetpcb.core.utils.Utilities;

import java.awt.Rectangle;


public class UnitSelectionCell {
    public static final int OFFSET_X = 10;
    public static final int OFFSET_Y = 10;
    public static final int CHECKBOX_WIDTH = 32;
    public static final int CHECKBOX_HEIDTH = 32;
    
    private int x ,y,width,height,unitWidth,unitHeight;
    boolean selected;
    public  final Rectangle checkboxRect, nameRect, unitRect;
    
    public UnitSelectionCell(int x, int y, int width, int height,
                                  int unitWidth, int unitHeight,
                                  boolean selected) {   
      this.x=x;
      this.y=y;
      this.width=width;
      this.height=height;
      this.unitWidth=unitWidth;
      this.unitHeight=unitHeight;
      this.selected=selected;
      this.checkboxRect = new Rectangle();
      this.nameRect = new Rectangle();
      this.unitRect = new Rectangle();
      init();
    }
    private void init(){
        checkboxRect.setRect(x + OFFSET_X,
                             y + height - CHECKBOX_HEIDTH,
                             CHECKBOX_WIDTH, CHECKBOX_HEIDTH);
        nameRect.setRect(x + OFFSET_X + CHECKBOX_WIDTH,
                         y + height - CHECKBOX_HEIDTH,
                         width - CHECKBOX_WIDTH - OFFSET_X,
                         CHECKBOX_HEIDTH);
        unitRect.setRect(x + CHECKBOX_WIDTH+OFFSET_X, y + OFFSET_Y,
                         unitWidth, unitHeight);  
        Utilities.IncrementRect(unitRect, UnitSelectionCell.OFFSET_X, UnitSelectionCell.OFFSET_Y);
    }
}
