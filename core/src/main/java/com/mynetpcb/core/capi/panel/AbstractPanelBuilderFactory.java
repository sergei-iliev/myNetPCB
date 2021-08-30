package com.mynetpcb.core.capi.panel;


import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.shape.Shape;

import java.util.HashMap;
import java.util.Map;

/**
 *The abstract factory to produce diffrent shapes'panels
 * @param <S>
 * @param <C>
 * @author Sergey Iliev
 */
public abstract class AbstractPanelBuilderFactory<S extends Shape> {

protected final Map<Class<?>,AbstractPanelBuilder<S>> panelsMap=new HashMap<Class<?>,AbstractPanelBuilder<S>>();    

public AbstractPanelBuilder getBuilder(Class type){  
      return panelsMap.get(type);  
}

}
