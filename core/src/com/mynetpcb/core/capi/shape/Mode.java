package com.mynetpcb.core.capi.shape;


public interface Mode {
    public static final int PAD_MODE = 1;

    public static final int RECT_MODE = 2;
    
    public static final int LINE_MODE = 3;
    
    public static final int ELLIPSE_MODE = 4;
    
    public static final int ARC_MODE = 5;
    
    public static final int LABEL_MODE = 6;
    
    public static final int SOLID_REGION = 7;
    
    public static final int ORIGIN_SHIFT_MODE= 8;
    
    public static final int  DRAGHEAND_MODE= 9;
    
    public static final int COMPONENT_MODE=10;
    
    public static final int MEASUMENT_MODE=11;
    
    public static final int TRACK_MODE = 12;

    public static final int FOOTPRINT_MODE = 13;
    
    public static final int VIA_MODE = 14;

    public static final int COPPERAREA_MODE = 15;
    
    public static final int HOLE_MODE = 16;
    
    public static final int PIN_MODE = 17;
    
    public static final int TRIANGLE_MODE = 18;
    
    public static final int ARROW_MODE = 19;
    
    public static final int WIRE_MODE = 20;
    
    public static final int BUS_MODE = 21;

    public static final int SYMBOL_MODE = 22;
    
    public static final int BUSPIN_MODE = 23;
    
    public static final int CONNECTOR_MODE = 24;
    
    public static final int JUNCTION_MODE = 25;
   
    public static final int NOCONNECTOR_MODE = 26; 
    
    public static final int NETLABEL_MODE = 27; 
    
}
