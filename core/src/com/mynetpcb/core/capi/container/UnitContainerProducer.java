package com.mynetpcb.core.capi.container;

import java.util.HashMap;
import java.util.Map;

public class UnitContainerProducer {
private final Map<String,UnitContainerFactory> map=new HashMap<String,UnitContainerFactory>(4);

    public UnitContainer createUnitContainerByName(String name){
      return map.get(name).createUnitContainer();        
    }
    
    public boolean extsts(String name){
        return map.containsKey(name);
    }
    public UnitContainerProducer withFactory(String name,UnitContainerFactory factory){
      map.put(name, factory);   
      return this;
    }

}
