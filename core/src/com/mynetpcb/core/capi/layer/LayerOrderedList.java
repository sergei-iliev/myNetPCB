package com.mynetpcb.core.capi.layer;

import com.mynetpcb.core.capi.shape.Shape;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class LayerOrderedList<S extends Shape> extends LinkedList<S> implements OrderedList<S> {

    @Override
    public OrderedList<S> clone() {
        return new LayerOrderedList<>();
    }
    
    private final Comparator<S> comparator = new Comparator<S>() {
        @Override
        public int compare(S s1, S s2) {
            return s1.getDrawingLayerPriority() - s2.getDrawingLayerPriority();
        }
    };

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
                if (s.getDrawingLayerPriority() >= shape.getDrawingLayerPriority()) {
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
