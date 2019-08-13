package tests.mynetpcb.core.capi.unit;


import com.mynetpcb.core.capi.ViewportWindow;

import java.awt.Rectangle;

import org.junit.Assert;
import org.junit.Test;


public class UnitFrameTest {

@Test
public void testUnitFrameRect(){
   Rectangle r=new Rectangle(0,0,10000,20000);
   ViewportWindow vw=new ViewportWindow(0,0,100,200);
   Assert.assertTrue(r.contains(vw));
   Assert.assertTrue(r.intersects(vw));
   vw.x=-1;
   vw.y=-1;
   Assert.assertTrue(!r.contains(vw));
   Assert.assertTrue(!vw.contains(r));
   //smaller
   r.setRect(0,0,10,20);
   Assert.assertTrue(!r.contains(vw));
   Assert.assertTrue(vw.contains(r));
       
}
}
