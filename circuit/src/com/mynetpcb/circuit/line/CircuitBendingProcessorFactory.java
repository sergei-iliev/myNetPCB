package com.mynetpcb.circuit.line;

import com.mynetpcb.core.capi.line.AbstractBendingProcessorFactory;
import com.mynetpcb.core.capi.line.DefaultLineBendingProcessor;
import com.mynetpcb.core.capi.line.HorizontalToVerticalBendingProcessor;
import com.mynetpcb.core.capi.line.LineBendingProcessor;
import com.mynetpcb.core.capi.line.LineSlopeBendingProcessor;
import com.mynetpcb.core.capi.line.SlopeLineBendingProcessor;
import com.mynetpcb.core.capi.line.VerticalToHorizontalBendingProcessor;

public class CircuitBendingProcessorFactory extends AbstractBendingProcessorFactory{

    @Override
    public LineBendingProcessor resolve(String name, LineBendingProcessor current) {

        LineBendingProcessor next=null;
        
        if(name.equals("defaultbend")){            
                next= new  DefaultLineBendingProcessor();
        }else if(name.equals("hvbend")){
                next= new HorizontalToVerticalBendingProcessor();
        }else if(name.equals("vhbend")){
                next=new  VerticalToHorizontalBendingProcessor();                
        }else
                throw new IllegalStateException("Unknown Symbol line processor name-> "+name);               
        if(current!=null){
          next.initialize(current.getLine());
        }
        
        return next;
    }

    @Override
    public LineBendingProcessor resolve(LineBendingProcessor current) {
        
        if(current==null){
            return new  DefaultLineBendingProcessor();
        }    
        
        LineBendingProcessor next=null;
            
        if(current.getClass()==DefaultLineBendingProcessor.class){
            next =new SlopeLineBendingProcessor(); 
        }else if(current.getClass()==SlopeLineBendingProcessor.class){
            next =new LineSlopeBendingProcessor(); 
        }else if(current.getClass()==LineSlopeBendingProcessor.class){
            next =new DefaultLineBendingProcessor();  
        }else
            throw new IllegalStateException("Unknown Symbol line processor class-> "+current.getClass());    
        
        next.initialize(current.getLine());
        return next;
    
    }
}
