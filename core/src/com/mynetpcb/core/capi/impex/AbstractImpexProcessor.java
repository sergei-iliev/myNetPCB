package com.mynetpcb.core.capi.impex;

import com.mynetpcb.core.capi.container.UnitContainer;

import java.io.IOException;

import java.util.Map;

public abstract class AbstractImpexProcessor {
   
   public abstract void process(UnitContainer unitContainer,Map<String,?> context)throws IOException;

}
