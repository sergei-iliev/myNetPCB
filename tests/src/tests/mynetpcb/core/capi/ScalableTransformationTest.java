package tests.mynetpcb.core.capi;


import com.mynetpcb.core.capi.ScalableTransformation;

import java.awt.Point;

import org.junit.Assert;
import org.junit.Test;


public class ScalableTransformationTest {

@Test
public void testScaleableFactor(){
   ScalableTransformation increase=new ScalableTransformation(1.2);
   increase.ScaleIn();
   Point p1=new Point(10,10);
   increase.getCurrentTransformation().transform(p1,p1);
   Assert.assertTrue(p1.x==12);
   Assert.assertTrue(p1.y==12);
   
   ScalableTransformation decrease=new ScalableTransformation(1.2);
   decrease.ScaleIn();
   Point p2=new Point(10,10);
   decrease.getCurrentTransformation().transform(p2,p2);

   Assert.assertTrue(p2.x==p1.x);
   Assert.assertTrue(p2.y==p1.y);
   
   
}
}
