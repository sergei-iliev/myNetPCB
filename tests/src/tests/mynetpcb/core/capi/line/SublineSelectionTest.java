package tests.mynetpcb.core.capi.line;


import com.mynetpcb.core.capi.line.LinePoint;

import org.junit.Assert;
import org.junit.Test;

public class SublineSelectionTest {

@Test
public void testLinePointClone()throws Exception{
    LinePoint point=new LinePoint(20,20);
    LinePoint copy=(LinePoint)point.clone();
    Assert.assertTrue(point!=copy);
    Assert.assertTrue(point.x==copy.x&&point.y==copy.y);
}



}
