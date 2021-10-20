package tests.mynetpcb.circuit.shape;

//import org.junit.Assert;


import com.mynetpcb.circuit.shape.SCHNoConnector;
import com.mynetpcb.circuit.unit.Circuit;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;


public class SCHNoConnectorTest {

@Test
public void testCreateSCHNoConnector()throws Exception{
   Circuit circuit=Mockito.mock(Circuit.class);
   SCHNoConnector shape=new SCHNoConnector();
   Assert.assertNotNull(shape.getOwningUnit());
   SCHNoConnector copy=shape.clone();
   Assert.assertNull(copy.getOwningUnit());
}


}
