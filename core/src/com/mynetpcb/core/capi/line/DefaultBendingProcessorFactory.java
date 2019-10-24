package com.mynetpcb.core.capi.line;

public class DefaultBendingProcessorFactory extends AbstractBendingProcessorFactory{

    @Override
    public LineBendingProcessor resolve(String name, LineBendingProcessor current) {
        
        if(name.equalsIgnoreCase("defaultbend")){
            if(current==null){            
                return new  DefaultLineBendingProcessor();
            }else{
                return current;
            }
        }
                throw new IllegalStateException("Unknown Symbol line processor name-> "+name);               
    }

    @Override
    public LineBendingProcessor resolve(LineBendingProcessor current) {
        if(current==null){
            return new  DefaultLineBendingProcessor();
        }else if(current.getClass()==DefaultLineBendingProcessor.class){
            return current; 
        }else{
            throw new IllegalStateException("Unknown Symbol line processor class-> "+current.getClass());    
        }
    }
}


