package tests.mynetpcb.symbols.element;


import com.mynetpcb.symbol.container.SymbolContainer;
import com.mynetpcb.symbol.shape.Pin;

import org.junit.Assert;
import org.junit.Test;

public class TestPin {
    private final String module="<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"+
"<modules identity=\"Module\" type=\"SYMBOL\" version=\"1.3\">"+
"<module width=\"508\" height=\"530\">"+
"<name>at90s2313</name>"+
"<reference>U,192,313,LEFT,PLAIN</reference>"+
"<unit>at90s2313,192,165,LEFT,PLAIN</unit>"+
"<elements>"+
"<rectangle>192,168,88,136,1,1,0</rectangle>"+
"<pin type=\"1\">"+
"<a>192,176,true,2,0</a>"+
"<name>RESET,198,178,LEFT,PLAIN</name>"+
"<number>1,185,175,RIGHT,PLAIN</number>"+
"</pin>"+
"</elements>"+
"</module>"+
"</modules>";    
     
     
    @Test
    public void testCreatePin()throws Exception{
      Pin pin=new Pin();
      Assert.assertTrue(pin.getStyle()==Pin.Style.LINE);
      pin.setStyle(Pin.Style.CLOCK);
      Assert.assertTrue(pin.getStyle()==Pin.Style.CLOCK);
      Pin copy=pin.clone();
      Assert.assertTrue(copy.getStyle()==Pin.Style.CLOCK);
    }
    
    @Test
    public void testModuleXML()throws Exception{
      SymbolContainer sc=new SymbolContainer();
      sc.Parse(module);
      Assert.assertNotNull(sc.getUnit());
      Assert.assertTrue(sc.getUnit().getShapes(Pin.class).size()==1);
      Pin pin=(Pin)sc.getUnit().getShapes(Pin.class).get(0);
      Assert.assertTrue(pin.getOrientation()==Pin.Orientation.WEST);
      
    }
}
