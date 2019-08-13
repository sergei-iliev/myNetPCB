package tests.mynetpcb.symbols.element;

import com.mynetpcb.board.shape.PCBCopperArea;
import com.mynetpcb.board.shape.Polygonal;
import com.mynetpcb.core.capi.ScalableTransformation;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.pad.Layer;

import java.awt.Graphics2D;
import java.awt.Point;

import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class TestCopperArea {

    @Test
    public void testPolygonalOp(){
      Polygonal polygonal=new Polygonal();
      Point p1=new Point(1000,1000);
      polygonal.addPoint(p1);
      Assert.assertTrue(polygonal.getLinePoints().size()==1);
      
      Point p2=new Point(1100,1000);
      polygonal.addPoint(p2);
      
      Point p3=new Point(1200,1000);
      polygonal.addPoint(p3);
      
      Assert.assertTrue(polygonal.getLinePoints().size()==3);
      Assert.assertTrue(polygonal.npoints==3);
        
      polygonal.removePoint(p2);
      Assert.assertTrue(polygonal.getLinePoints().size()==2);
      Assert.assertTrue(polygonal.npoints==2);
      
      polygonal.removePoint(p2);
      Assert.assertTrue(polygonal.getLinePoints().size()==2);
      Assert.assertTrue(polygonal.npoints==2);
      
      
      polygonal.removePoint(p1);
      polygonal.removePoint(p3);
      Assert.assertTrue(polygonal.getLinePoints().size()==0);
      Assert.assertTrue(polygonal.npoints==0);
      
    }
    
    @Test
    public void testCopperAreaPaint(){   
        Graphics2D g2= mock(Graphics2D.class);
        ScalableTransformation scale = new ScalableTransformation();
        scale.Reset(0.5,10,3,13);

        PCBCopperArea area=new PCBCopperArea(Layer.Copper.FCu.getLayerMaskID());
        area.addPoint(new Point(4000,4000));
        area.addPoint(new Point(8000,4000));
        area.Paint(g2,new ViewportWindow(0,0,700,600),scale.getCurrentTransformation(),Layer.Copper.All.getLayerMaskID());
        
        
    }
}
