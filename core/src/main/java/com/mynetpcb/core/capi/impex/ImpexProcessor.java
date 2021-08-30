package com.mynetpcb.core.capi.impex;


import com.mynetpcb.core.capi.container.UnitContainer;

import java.io.IOException;

import java.util.Map;

/**
 * Strategy pattern in action
 */
public class ImpexProcessor extends AbstractImpexProcessor {
    
    private final Impex exporter;
    
    public ImpexProcessor(Impex exporter){
       this.exporter=exporter;   
    }
    
    @Override
    public void process(UnitContainer unitContainer,Map<String,?> context)throws IOException {
       exporter.process(unitContainer,context);   
    }

}
