package com.mynetpcb.core.capi.line;


public abstract class AbstractBendingProcessorFactory {
     
    /*
     * Factory method -> resolve by processor name
     */
    public abstract  LineBendingProcessor resolve(String processor,LineBendingProcessor current);

    
    /*
     * Factory method -> resolve by round robin selection
     */
    public abstract LineBendingProcessor resolve(LineBendingProcessor current);   
}
