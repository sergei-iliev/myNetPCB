package com.mynetpcb.core.capi.impex;


import com.mynetpcb.core.capi.container.UnitContainer;

import java.io.IOException;

import java.util.Map;

public interface Impex<C extends UnitContainer>{
    
 public void process(C unitContainer,Map<String,?> context)throws IOException;
 
}
