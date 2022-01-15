package com.mynetpcb.core.capi.layer;

import java.awt.Color;

import java.util.Arrays;

public class Layer {


    public enum Copper {
        FCu() {
            public String toString() {
                return "F.Cu";
            }

            public String getName() {
                return "FCu";
            }

            public int getLayerMaskID() {
                return LAYER_FRONT;
            }

            public Color getColor() {
                return Color.red;
            }

//            public Color getBoardColor() {
//                return BOARD_COLOR_FRONT;
//            }
        },
        BCu() {
            public String toString() {
                return "B.Cu";
            }

            public String getName() {
                return "BCu";
            }

            public int getLayerMaskID() {
                return LAYER_BACK;
            }

            public Color getColor() {
                return Color.green;
            }

//            public Color getBoardColor() {
//                return BOARD_COLOR_BACK;
//            }
        },
        Cu() {
            private Color c = new Color(128, 128, 0);

            public String toString() {
                return "Cu";
            }

            public String getName() {
                return "Cu";
            }

            public int getLayerMaskID() {
                return LAYER_FRONT|LAYER_BACK;
            }

            public Color getColor() {
                return c;
            }

//            public Color getBoardColor() {
//                return Color.WHITE;
//            }
        },
        FSilkS() {
            public String toString() {
                return "F.SilkS";
            }

            public String getName() {
                return "FSilkS";
            }

            public int getLayerMaskID() {
                return SILKSCREEN_LAYER_FRONT;
            }

            public Color getColor() {
                return Color.cyan;
            }

//            public Color getBoardColor() {
//                return BOARD_COLOR_FRONT;
//            }
        },
        BSilkS() {
            public String toString() {
                return "B.SilkS";
            }

            public String getName() {
                return "BSilkS";
            }

            public int getLayerMaskID() {
                return SILKSCREEN_LAYER_BACK;
            }

            public Color getColor() {
                return Color.magenta;
            }

//            public Color getBoardColor() {
//                return BOARD_COLOR_BACK;
//            }
        },
        BMask() {
            private Color c = new Color(128, 128, 0);

            public String toString() {
                return "B.Mask";
            }

            public String getName() {
                return "BMask";
            }

            public int getLayerMaskID() {
                return SOLDERMASK_LAYER_BACK;
            }

            public Color getColor() {
                return c;
            }

//            public Color getBoardColor() {
//                return BOARD_COLOR_BACK;
//            }
        },
        FMask() {
            public String toString() {
                return "F.Mask";
            }

            public String getName() {
                return "FMask";
            }

            public int getLayerMaskID() {
                return SOLDERMASK_LAYER_FRONT;
            }

            public Color getColor() {
                return Color.magenta;
            }

//            public Color getBoardColor() {
//                return BOARD_COLOR_BACK;
//            }
        },
        BOutln() {
           

            public String toString() {
                return "B.Outline";
            }

            public String getName() {
                return "BOutln";
            }

            public int getLayerMaskID() {
                return BOARD_OUTLINE_LAYER;
            }

            public Color getColor() {
                return Color.YELLOW;
            }

//            public Color getBoardColor() {
//                return Color.WHITE;
//            }
        },        
        All() {
            private Color c = new Color(128, 128, 0);

            public String toString() {
                return "All";
            }

            public String getName() {
                return "All";
            }

            public int getLayerMaskID() {
                return LAYER_ALL;
            }

            public Color getColor() {
                return c;
            }

//            public Color getBoardColor() {
//                return Color.WHITE;
//            }
        },
        None() {
            public String toString() {
                return "None";
            }

            public String getName() {
                return "None";
            }

            public int getLayerMaskID() {
                return 0;
            }

            public Color getColor() {
                return Color.BLACK;
            }

//            public Color getBoardColor() {
//                return Color.BLACK;
//            }
        };

        public Color getBoardColor() {
            return this.getBoardColor();
        }

        public Color getColor() {
            return this.getColor();
        }

        public String getName() {
            return this.getName();
        }

        public int getLayerMaskID() {
            return this.getLayerMaskID();
        }

        public boolean isCopperLayer() {
            return ((this.getLayerMaskID() & Layer.LAYER_FRONT) != 0) ||
                   ((this.getLayerMaskID() & Layer.LAYER_BACK) != 0);
        }

        public static Copper resolve(int layermask) {
        	if (layermask == LAYER_ALL) {
                return Copper.All;
            }
        	if (layermask == SILKSCREEN_LAYER_FRONT) {
                return Copper.FSilkS;
            }
            if (layermask == SOLDERMASK_LAYER_FRONT) {
                return Copper.FMask;
            }
            if (layermask == LAYER_FRONT) {
                return Copper.FCu;
            }
            //*************************
            if (layermask == LAYER_BACK) {
                return Copper.BCu;
            }
            if (layermask == SOLDERMASK_LAYER_BACK) {
                return Copper.BMask;
            }
            if (layermask == SILKSCREEN_LAYER_BACK) {
                return Copper.BSilkS;
            }
            if(layermask==BOARD_OUTLINE_LAYER){
                return Copper.BOutln;
            }
            if ((layermask & (LAYER_BACK|LAYER_FRONT))!=0) {
                return Copper.Cu;
            }             
            else {
                return Copper.None;
            }


        }
    }

    public enum Side {
        TOP,
        BOTTOM;

        public static Copper change(int layermaskId) {
            if (layermaskId == LAYER_FRONT) {
                return Copper.BCu;
            } else if (layermaskId == SILKSCREEN_LAYER_FRONT) {
                return Copper.BSilkS;
            } else if (layermaskId == SOLDERMASK_LAYER_FRONT) {
                return Copper.BMask;
            } else if (layermaskId == LAYER_BACK) {
                return Copper.FCu;
            } else if (layermaskId == SILKSCREEN_LAYER_BACK) {
                return Copper.FSilkS;
            } else if (layermaskId == SOLDERMASK_LAYER_BACK) {
                return Copper.FMask;
            } else if (layermaskId == BOARD_OUTLINE_LAYER) {
                return Copper.BOutln;
            } 

            return Copper.All;
        }
//        public static Copper change(Copper copper) {
//            if (copper.getLayerMaskID() == LAYER_FRONT) {
//                return Copper.BCu;
//            } else if (copper.getLayerMaskID() == SILKSCREEN_LAYER_FRONT) {
//                return Copper.BSilkS;
//            } else if (copper.getLayerMaskID() == SOLDERMASK_LAYER_FRONT) {
//                return Copper.BMask;
//            } else if (copper.getLayerMaskID() == LAYER_BACK) {
//                return Copper.FCu;
//            } else if (copper.getLayerMaskID() == SILKSCREEN_LAYER_BACK) {
//                return Copper.FSilkS;
//            } else if (copper.getLayerMaskID() == SOLDERMASK_LAYER_BACK) {
//                return Copper.FMask;
//            }
//
//            return copper;
//        }

        public static Side resolve(int layermask) {
            if (layermask == LAYER_BACK) {
                return BOTTOM;
            } else if (layermask == SILKSCREEN_LAYER_BACK) {
                return BOTTOM;
            } else if (layermask == SOLDERMASK_LAYER_BACK) {
                return BOTTOM;
            }
            return TOP;
        }

    }

    public static final Layer.Copper[] BOARD_LAYERS =
        Arrays.asList(Layer.Copper.BCu, Layer.Copper.FCu, Layer.Copper.BSilkS, Layer.Copper.FSilkS, Layer.Copper.BMask,
                      Layer.Copper.FMask, Layer.Copper.All).toArray(new Copper[7]);
    public static final Layer.Copper[] PAD_LAYERS =
        Arrays.asList(Layer.Copper.FCu, Layer.Copper.BCu, Layer.Copper.Cu).toArray(new Copper[3]);

    public static final Layer.Copper[] PCB_SYMBOL_LAYERS =
        Arrays.asList(Layer.Copper.FCu, Layer.Copper.BCu, Layer.Copper.BSilkS, Layer.Copper.FSilkS).toArray(new Copper[3]);
 
    public static final Layer.Copper[] PCB_SYMBOL_OUTLINE_LAYERS =
            Arrays.asList(Layer.Copper.FCu, Layer.Copper.BCu, Layer.Copper.BSilkS, Layer.Copper.FSilkS,Layer.Copper.BOutln).toArray(new Copper[3]);
     
    
    public static final Layer.Copper[] GRAPHICS_LAYERS =
        Arrays.asList(Layer.Copper.BSilkS, Layer.Copper.FSilkS, Layer.Copper.BMask, Layer.Copper.FMask,
                      Layer.Copper.All).toArray(new Copper[5]);
    
    //public static final Layer.Copper[] GRAPHICS_EDGECUTS_LAYERS =
    //    Arrays.asList(Layer.Copper.BSilkS, Layer.Copper.FSilkS, Layer.Copper.BMask, Layer.Copper.FMask,Layer.Copper.BoardEdgeCuts,
    //                  Layer.Copper.All).toArray(new Copper[6]);


    public final static Color BOARD_COLOR_FRONT = new Color(79, 0, 0);
    public final static Color BOARD_COLOR_BACK = new Color(0, 0, 79);
    public final static Color BOARD_COLOR_ALL = Color.BLACK;

    /* Layer identification (layer number) */
    public final static int FIRST_COPPER_LAYER = 0;
    public final static int LAYER_N_BACK = 0;
    public final static int LAYER_N_2 = 1;
    public final static int LAYER_N_3 = 2;
    public final static int LAYER_N_4 = 3;
    public final static int LAYER_N_5 = 4;
    public final static int LAYER_N_6 = 5;
    public final static int LAYER_N_7 = 6;
    public final static int LAYER_N_8 = 7;
    public final static int LAYER_N_9 = 8;
    public final static int LAYER_N_10 = 9;
    public final static int LAYER_N_11 = 10;
    public final static int LAYER_N_12 = 11;
    public final static int LAYER_N_13 = 12;
    public final static int LAYER_N_14 = 13;
    public final static int LAYER_N_15 = 14;
    public final static int LAYER_N_FRONT = 15;
    public final static int LAST_COPPER_LAYER = LAYER_N_FRONT;
    public final static int NB_COPPER_LAYERS = (LAST_COPPER_LAYER + 1);

    public final static int FIRST_NO_COPPER_LAYER = 16;
    public final static int ADHESIVE_N_BACK = 16;
    public final static int ADHESIVE_N_FRONT = 17;
    public final static int SOLDERPASTE_N_BACK = 18;
    public final static int SOLDERPASTE_N_FRONT = 19;
    public final static int SILKSCREEN_N_BACK = 20;
    public final static int SILKSCREEN_N_FRONT = 21;
    public final static int SOLDERMASK_N_BACK = 22;
    public final static int SOLDERMASK_N_FRONT = 23;
    public final static int BOARD_EDGE_CUTS_N = 24;
    public final static int COMMENT_N = 25;
    public final static int ECO1_N = 26;
    public final static int ECO2_N = 27;
    public final static int EDGE_N = 28;
    public final static int LAST_NO_COPPER_LAYER = 28;
    public final static int UNUSED_LAYER_29 = 29;
    public final static int UNUSED_LAYER_30 = 30;
    public final static int UNUSED_LAYER_31 = 31;
    public final static int NB_LAYERS = (LAST_NO_COPPER_LAYER + 1);

    public final static int LAYER_COUNT = 32;

    // Masks to identify a layer by a bit map
    public final static int LAYER_NONE = 0;
    public final static int LAYER_BACK = (1 << LAYER_N_BACK); ///< bit mask for copper layer
    public final static int LAYER_2 = (1 << LAYER_N_2); ///< bit mask for layer 2
    public final static int LAYER_3 = (1 << LAYER_N_3); ///< bit mask for layer 3
    public final static int LAYER_4 = (1 << LAYER_N_4); ///< bit mask for layer 4
    public final static int LAYER_5 = (1 << LAYER_N_5); ///< bit mask for layer 5
    public final static int LAYER_6 = (1 << LAYER_N_6); ///< bit mask for layer 6
    public final static int LAYER_7 = (1 << LAYER_N_7); ///< bit mask for layer 7
    public final static int LAYER_8 = (1 << LAYER_N_8); ///< bit mask for layer 8
    public final static int LAYER_9 = (1 << LAYER_N_9); ///< bit mask for layer 9
    public final static int LAYER_10 = (1 << LAYER_N_10); ///< bit mask for layer 10
    public final static int LAYER_11 = (1 << LAYER_N_11); ///< bit mask for layer 11
    public final static int LAYER_12 = (1 << LAYER_N_12); ///< bit mask for layer 12
    public final static int LAYER_13 = (1 << LAYER_N_13); ///< bit mask for layer 13
    public final static int LAYER_14 = (1 << LAYER_N_14); ///< bit mask for layer 14
    public final static int LAYER_15 = (1 << LAYER_N_15); ///< bit mask for layer 15
    public final static int LAYER_FRONT = (1 << LAYER_N_FRONT); ///< bit mask for component layer
    public final static int ADHESIVE_LAYER_BACK = (1 << ADHESIVE_N_BACK);
    public final static int ADHESIVE_LAYER_FRONT = (1 << ADHESIVE_N_FRONT);
    public final static int SILKSCREEN_LAYER_BACK = (1 << SILKSCREEN_N_BACK);
    public final static int SILKSCREEN_LAYER_FRONT = (1 << SILKSCREEN_N_FRONT);
    public final static int SOLDERMASK_LAYER_BACK = (1 << SOLDERMASK_N_BACK);
    public final static int SOLDERMASK_LAYER_FRONT = (1 << SOLDERMASK_N_FRONT);
    public final static int BOARD_EDGE_CUTS = (1 << BOARD_EDGE_CUTS_N);
    public final static int COMMENT_LAYER = (1 << COMMENT_N);
    public final static int ECO1_LAYER = (1 << ECO1_N);
    public final static int ECO2_LAYER = (1 << ECO2_N);
    public final static int BOARD_OUTLINE_LAYER = (1 << EDGE_N);
    public final static int PTH_LAYER_DRILL = (1 << UNUSED_LAYER_29);
    public final static int NPTH_LAYER_DRILL = (1 << UNUSED_LAYER_30);
    
    public final static int LAYER_ALL =
        LAYER_BACK | LAYER_FRONT | ADHESIVE_LAYER_BACK | ADHESIVE_LAYER_FRONT | SILKSCREEN_LAYER_BACK |
        SILKSCREEN_LAYER_FRONT | SOLDERMASK_LAYER_BACK | SOLDERMASK_LAYER_FRONT | BOARD_OUTLINE_LAYER ;

}

