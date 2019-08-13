package com.mynetpcb.core.capi.flyweight;

import java.awt.geom.GeneralPath;

import java.util.ArrayList;

public class GeneralPathFlyweightProvider extends FlyweightProvider<GeneralPath> {
    public GeneralPathFlyweightProvider() {
        pool = new ArrayList<GeneralPath>(getMaxPoolSize());
        for (int i = 0; i < getMaxPoolSize(); i++) {
            pool.add(new GeneralPath(GeneralPath.WIND_EVEN_ODD));
        }
    }

    @Override
    public GeneralPath getShape() {
        GeneralPath path = super.getShape();
        path.reset();
        return path;
    }

}
