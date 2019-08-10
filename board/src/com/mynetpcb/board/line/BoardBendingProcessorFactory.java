package com.mynetpcb.board.line;

import com.mynetpcb.core.capi.line.AbstractBendingProcessorFactory;
import com.mynetpcb.core.capi.line.DefaultLineBendingProcessor;
import com.mynetpcb.core.capi.line.LineBendingProcessor;
import com.mynetpcb.core.capi.line.LineSlopeBendingProcessor;
import com.mynetpcb.core.capi.line.SlopeLineBendingProcessor;

public class BoardBendingProcessorFactory extends AbstractBendingProcessorFactory{

    @Override
    public LineBendingProcessor resolve(String name, LineBendingProcessor current) {

        LineBendingProcessor next=null;
        
        if(name.equals("defaultbend")){            
                next= new  DefaultLineBendingProcessor();
        }else if(name.equals("slopelinebend")){
                next= new SlopeLineBendingProcessor(); 
        }else if(name.equals("lineslopebend")){
                next=new  LineSlopeBendingProcessor();                
        }else
                throw new IllegalStateException("Unknown Symbol line processor name-> "+name);               
        if(current!=null){
          next.Initialize(current.getLine());
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
        
        next.Initialize(current.getLine());
        return next;
    
    }
}


