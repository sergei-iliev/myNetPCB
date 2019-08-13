package com.mynetpcb.circuit.line;

import com.mynetpcb.core.capi.line.AbstractBendingProcessorFactory;
import com.mynetpcb.core.capi.line.DefaultLineBendingProcessor;
import com.mynetpcb.core.capi.line.LineBendingProcessor;
import com.mynetpcb.core.capi.line.RightLineBendingProcessor;
import com.mynetpcb.core.capi.line.TopLineBendingProcessor;

public class CircuitBendingProcessorFactory extends AbstractBendingProcessorFactory{

    @Override
    public LineBendingProcessor resolve(String name, LineBendingProcessor current) {
        if(current==null){            
            return new  DefaultLineBendingProcessor();
        } 
        
        LineBendingProcessor next=null;
        
        if(name.equals("defaultbend")){            
                next= new  DefaultLineBendingProcessor();
        }else if(name.equals("topbend")){
                next= new  TopLineBendingProcessor(); 
        }else if(name.equals("rightbend")){
                next=new  RightLineBendingProcessor();                
        }else
                throw new IllegalStateException("Unknown Symbol line processor name-> "+name);               
        
        next.Initialize(current.getLine());
        
        return next;
    }

    @Override
    public LineBendingProcessor resolve(LineBendingProcessor current) {
        
        if(current==null){
            return new  DefaultLineBendingProcessor();
        }    
        
        LineBendingProcessor next=null;
            
        if(current.getClass()==DefaultLineBendingProcessor.class){
            next =new TopLineBendingProcessor(); 
        }else if(current.getClass()==TopLineBendingProcessor.class){
            next =new RightLineBendingProcessor(); 
        }else if(current.getClass()==RightLineBendingProcessor.class){
            next =new DefaultLineBendingProcessor();  
        }else
            throw new IllegalStateException("Unknown Symbol line processor class-> "+current.getClass());    
        
        next.Initialize(current.getLine());
        return next;
    
    }
}

