package tests.mynetpcb.board;


import com.mynetpcb.core.board.CompositeLayer;
import com.mynetpcb.core.board.CompositeLayerable;
import com.mynetpcb.core.pad.Layer;

import org.junit.Assert;
import org.junit.Test;

public class CompositeLayerTest {

@Test
public void testCompositeLayerCreate(){
     CompositeLayerable cl=new CompositeLayer();      
     Assert.assertTrue(cl.isLayerVisible(Layer.LAYER_FRONT)==true);
     Assert.assertTrue(cl.isLayerVisible(Layer.LAYER_BACK)==true);
     Assert.assertTrue(cl.isLayerVisible(Layer.LAYER_4)==false);
}

    @Test
    public void testLayerVisibility(){
         CompositeLayerable cl=new CompositeLayer();           
         Assert.assertTrue(cl.isLayerVisible(Layer.LAYER_FRONT)==true);
         Assert.assertTrue(cl.isLayerVisible(Layer.LAYER_BACK)==true);
         
         cl.setLayerVisible(Layer.LAYER_FRONT,false);
         Assert.assertTrue(cl.isLayerVisible(Layer.LAYER_FRONT)==false);
         Assert.assertTrue(cl.isLayerVisible(Layer.LAYER_BACK)==true);
        
         cl.setLayerVisible(Layer.LAYER_BACK,false);
         Assert.assertTrue(cl.isLayerVisible(Layer.LAYER_BACK)==false);
         Assert.assertTrue(cl.isLayerVisible(Layer.LAYER_FRONT)==false);
         
        cl.setLayerVisible(Layer.LAYER_BACK,true);
        Assert.assertTrue(cl.isLayerVisible(Layer.LAYER_BACK)==true);
        Assert.assertTrue(cl.isLayerVisible(Layer.LAYER_FRONT)==false);
        
        cl.setLayerVisible(Layer.LAYER_FRONT,true);
        Assert.assertTrue(cl.isLayerVisible(Layer.LAYER_BACK)==true);
        Assert.assertTrue(cl.isLayerVisible(Layer.LAYER_FRONT)==true);
         
         
    }

}
