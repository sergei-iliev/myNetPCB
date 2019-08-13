package com.mynetpcb.core.capi;

import java.awt.Point;


public interface Pinable {
    public enum Orientation {
        NORTH,
        SOUTH,
        WEST,
        EAST;
        

        public Orientation Rotate(boolean isClockwise) {
            if (isClockwise) {
                if (this == NORTH)
                    return EAST;
                else if (this == EAST)
                    return SOUTH;
                else if (this == SOUTH)
                    return WEST;
                else
                    return NORTH;
            } else {
                if (this == NORTH)
                    return WEST;
                else if (this == WEST)
                    return SOUTH;
                else if (this == SOUTH)
                    return EAST;
                else
                    return NORTH;
            }
        }

        public Orientation Mirror(boolean isHorizontal) {
            if (isHorizontal) {
                if (this == EAST)
                    return WEST;
                else if (this == WEST)
                    return EAST;
                else
                    return this;
            } else {
                if (this == NORTH)
                    return SOUTH;
                else if (this == SOUTH)
                    return NORTH;
                else
                    return this;
            }
        }

    }
    
    /**     
      * @return single pin origin
      */
    public Point getPinPoint();
}
