package tests.mynetpcb.symbols.element;


import com.mynetpcb.core.capi.ScalableTransformation;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.symbol.shape.RoundRect;

import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class TestRoundRect {
@Test
public void testCreateRoundRect(){
   Unit unit=mock(Unit.class);
   RoundRect rr=new RoundRect(10,10,200,200);
   Assert.assertTrue(rr.isControlRectClicked(9,9)!=null);   
   Assert.assertTrue(rr.isControlRectClicked(211,211)!=null);    
}

    @Test
    public void testDrawRoundRect(){
       Graphics2D graphics= mock(Graphics2D.class);
       RoundRect rr=new RoundRect(10,10,200,200);
       ScalableTransformation s=new ScalableTransformation();
       ViewportWindow vw=new ViewportWindow(0,0,500,500);
       rr.Paint(graphics, vw,s.getCurrentTransformation(),0);   
       verify(graphics,times(1)).draw(any(RoundRectangle2D.class));
    
       
    //shift viewport to the middle
       reset(graphics);
       vw.x=150;
       vw.y=150;
       rr.Paint(graphics, vw,s.getCurrentTransformation(),0);   
       verify(graphics,times(1)).draw(any(RoundRectangle2D.class));
    //shift viewport outside rect
       reset(graphics);
        vw.x=250;
        vw.y=250;
        rr.Paint(graphics, vw,s.getCurrentTransformation(),0);   
        verify(graphics,times(0)).draw(any(RoundRectangle2D.class));
    }


}
