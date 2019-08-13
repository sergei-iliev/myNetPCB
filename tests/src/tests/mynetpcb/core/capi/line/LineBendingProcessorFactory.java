package tests.mynetpcb.core.capi.line;

import com.mynetpcb.circuit.line.CircuitBendingProcessorFactory;
import com.mynetpcb.circuit.shape.SCHWire;
import com.mynetpcb.core.capi.line.AbstractBendingProcessorFactory;
import com.mynetpcb.core.capi.line.DefaultBendingProcessorFactory;
import com.mynetpcb.core.capi.line.DefaultLineBendingProcessor;
import com.mynetpcb.core.capi.line.LineBendingProcessor;
import com.mynetpcb.core.capi.line.RightLineBendingProcessor;
import com.mynetpcb.core.capi.line.TopLineBendingProcessor;
import com.mynetpcb.core.capi.line.Trackable;

import org.junit.Assert;
import org.junit.Test;

public class LineBendingProcessorFactory {

@Test(expected=IllegalStateException.class)
public void createSymbolLineProcessorFactory1(){
   AbstractBendingProcessorFactory f=new DefaultBendingProcessorFactory();
   f.resolve("nonesence",null);
}

@Test
public void createSymbolLineProcessorFactory2(){
       AbstractBendingProcessorFactory f=new DefaultBendingProcessorFactory();
       LineBendingProcessor l1= f.resolve("defaultbend",null);
       LineBendingProcessor l2= f.resolve("defaultbend",l1);
       Assert.assertTrue(l1==l2);
       LineBendingProcessor l3= f.resolve("defaultbend",l2);
       Assert.assertTrue(l2==l3);
}    

    @Test
    public void createCircuitLineProcessorFactory1(){
           AbstractBendingProcessorFactory f=new CircuitBendingProcessorFactory();
           LineBendingProcessor l1= f.resolve("defaultbend",null);
           Trackable line=new SCHWire(0);
           l1.Initialize(line);
           
           LineBendingProcessor l2= f.resolve("topbend", l1);
           Assert.assertTrue(l2.getClass()==TopLineBendingProcessor.class);
           
           LineBendingProcessor l3= f.resolve("rightbend",l2);
           Assert.assertTrue(l3.getClass()==RightLineBendingProcessor.class);
    }   
    
    @Test
    public void createCircuitLineProcessorFactory2(){
           AbstractBendingProcessorFactory f=new CircuitBendingProcessorFactory();
           LineBendingProcessor l1= new DefaultLineBendingProcessor();           
           Trackable line=new SCHWire(0);
           l1.Initialize(line);
           
           LineBendingProcessor l2=f.resolve(l1);
           Assert.assertTrue(l2.getClass()==TopLineBendingProcessor.class);
        
           LineBendingProcessor l3=f.resolve(l2);
           Assert.assertTrue(l3.getClass()==RightLineBendingProcessor.class);
           
           LineBendingProcessor l4=f.resolve(l3);
           Assert.assertTrue(l4.getClass()==DefaultLineBendingProcessor.class);
    }   

}
