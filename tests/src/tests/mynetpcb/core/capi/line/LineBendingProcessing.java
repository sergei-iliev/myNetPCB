package tests.mynetpcb.core.capi.line;

import com.mynetpcb.board.shape.PCBTrack;
import com.mynetpcb.core.capi.line.LineBendingProcessor;
import com.mynetpcb.core.capi.line.SlopeLineBendingProcessor;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.pad.Layer;

import java.awt.Point;

import org.junit.Assert;
import org.junit.Test;
/*
 * TDD of bending processor slope/line first
 */
public class LineBendingProcessing {

    @Test
    public void testProcessorCreateFirstForthQuadrant(){
      Trackable wire=new PCBTrack(8,Layer.LAYER_BACK);   
      LineBendingProcessor processor=new SlopeLineBendingProcessor();  
      processor.Initialize(wire);
      
      Point first=new Point(10,10);
      wire.addPoint(first);
      wire.Reset(first);
      processor.moveLinePoint(20,10);
      //mid point overlaps with start point
      Assert.assertEquals(wire.getFloatingMidPoint(),wire.getFloatingStartPoint());
      
      processor.moveLinePoint(20,8);
      Assert.assertEquals(wire.getFloatingMidPoint().y,wire.getFloatingEndPoint().y);
      Assert.assertTrue(wire.getFloatingMidPoint().x>wire.getFloatingStartPoint().x);
      
      processor.moveLinePoint(20,20);
      Assert.assertEquals(wire.getFloatingMidPoint(),wire.getFloatingEndPoint());
      
      processor.moveLinePoint(20,15);
      Assert.assertEquals(wire.getFloatingMidPoint().y,wire.getFloatingEndPoint().y);
      Assert.assertTrue(wire.getFloatingMidPoint().x>wire.getFloatingStartPoint().x);
    }
    
    @Test
    public void testProcessorCreateFirstForthQuadrantMultiPoint(){
      Trackable wire=new PCBTrack(8,Layer.LAYER_BACK);   
      LineBendingProcessor processor=new SlopeLineBendingProcessor();  
      processor.Initialize(wire);
      
      Point first=new Point(10,10);
      processor.addLinePoint(first);
      
      processor.moveLinePoint(20,10);
      processor.addLinePoint(new Point(20,10));
      Assert.assertEquals(wire.getFloatingMidPoint(),new Point(20,10));      
        
      //first  
      processor.moveLinePoint(25,8);
      Assert.assertEquals(wire.getFloatingMidPoint(),new Point(22,8));      
      //forth
      processor.moveLinePoint(25,14);
      Assert.assertEquals(wire.getFloatingMidPoint(),new Point(24,14));
      
      processor.addLinePoint(new Point(25,14));
      Assert.assertEquals(wire.getFloatingStartPoint(),new Point(24,14));
      Assert.assertEquals(wire.getFloatingMidPoint(),new Point(25,14));
      
      processor.moveLinePoint(25,14);
      
      
    }
    
    @Test
    public void testProcessorCreateSecondThirdQuadrant(){
      Trackable wire=new PCBTrack(8,Layer.LAYER_BACK);   
      LineBendingProcessor processor=new SlopeLineBendingProcessor();  
      processor.Initialize(wire);
      
      Point first=new Point(10,10);
      wire.addPoint(first);
      wire.Reset(first);
      processor.moveLinePoint(7,10);
      //mid point overlaps with start point
      Assert.assertEquals(wire.getFloatingMidPoint(),wire.getFloatingStartPoint());
      
      processor.moveLinePoint(7,8);
      Assert.assertEquals(wire.getFloatingMidPoint().y,wire.getFloatingEndPoint().y);
      Assert.assertTrue(wire.getFloatingMidPoint().x<wire.getFloatingStartPoint().x);
      
      processor.moveLinePoint(5,5);
      Assert.assertEquals(wire.getFloatingMidPoint(),wire.getFloatingEndPoint());
      
      processor.moveLinePoint(5,7);
      Assert.assertEquals(wire.getFloatingMidPoint().y,wire.getFloatingEndPoint().y);
      Assert.assertTrue(wire.getFloatingMidPoint().x<wire.getFloatingStartPoint().x);
    }    
}
