package com.mynetpcb.core.capi.pin;

import com.mynetpcb.d2.shapes.Point;


public interface Pinable {
    public enum Orientation {
        NORTH,
        SOUTH,
        WEST,
        EAST;
        

        public Orientation rotate(boolean isClockwise) {
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

        public Orientation mirror(boolean isHorizontal) {
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
