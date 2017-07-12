package com.mynetpcb.core.capi;

import com.mynetpcb.core.capi.shape.Shape;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class SortedList<S extends Shape> extends LinkedList<S> {

    private static class DEFAULT<S extends Shape> implements Comparator<S> {
        @Override
        public int compare(S s1, S s2) {
            return s1.getDrawingOrder() - s2.getDrawingOrder();
        }
    }

    private final DEFAULT comparator = new DEFAULT();

    /**
     *Add shape at the right drawing place in list
     * @param shape
     * @return
     */
    @Override
    public boolean add(S shape) {
        if (this.size() == 0) {
            return super.add(shape);
        } else {
            for (Shape s : this) {              
                if (s.getDrawingOrder() >= shape.getDrawingOrder()) {
                    super.add(this.indexOf(s), shape);
                    return true;
                }
            }
            return super.add(shape);

        }
    }

    /**
     * Reorder list as a result of shape' s layer change
     */

    public void reorder() {
        Collections.sort(this, comparator);
    }

}
