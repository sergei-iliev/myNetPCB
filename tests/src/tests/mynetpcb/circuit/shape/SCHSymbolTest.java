package tests.mynetpcb.circuit.shape;


import com.mynetpcb.circuit.shape.SCHSymbol;
import com.mynetpcb.circuit.unit.Circuit;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Text;
import com.mynetpcb.core.capi.text.font.FontTexture;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.symbol.shape.Pin;
import com.mynetpcb.symbol.shape.RoundRect;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class SCHSymbolTest {

@Test    
public void testCreate() {
  SCHSymbol symbol=new SCHSymbol();
  Shape shape=new RoundRect(10,10,100,100);
  symbol.Add(shape);
  Assert.assertTrue(symbol.calculateShape().getWidth()==100);
  Assert.assertTrue(symbol.calculateShape().getHeight()==100);
  
  symbol.Clear();
  Assert.assertTrue(symbol.calculateShape().getWidth()==0);
  Assert.assertTrue(symbol.calculateShape().getHeight()==0);
}


@Test    
public void testClone()throws Exception {
    Circuit unit=mock(Circuit.class);
    SCHSymbol symbol=new SCHSymbol();
    Assert.assertNotNull(symbol.getOwningUnit());
    Shape shape=new RoundRect(10,10,100,100);
    symbol.Add(shape);
    
    SCHSymbol copy =symbol.clone();
    Assert.assertNull(copy.getOwningUnit());
    Assert.assertTrue(copy.calculateShape().getWidth()==100);
    Assert.assertTrue(copy.calculateShape().getHeight()==100);
    
    Assert.assertTrue(copy.getChipText()!=symbol.getChipText());
}


    @Test    
    public void testPaintNoScale()throws Exception {
        ViewportWindow vw=new ViewportWindow(0,0,500,500);
        Graphics2D g2=mock(Graphics2D.class);
        Circuit unit=mock(Circuit.class);
        AffineTransform at=AffineTransform.getScaleInstance(1, 1);
        //AffineTransform at=mock(AffineTransform.class);
        //when(at.getScaleX()).thenReturn(1.0);
        
        SCHSymbol symbol=new SCHSymbol();
        
        symbol.Paint(g2,vw,at,0);
        verify(g2,times(0)).draw(any(java.awt.Shape.class));
        
    }
    
    @Test    
    public void testPaintShapeNoScale()throws Exception {
        ViewportWindow vw=new ViewportWindow(0,0,500,500);
        Graphics2D g2=mock(Graphics2D.class);
        
        Circuit unit=mock(Circuit.class);
        AffineTransform at=AffineTransform.getScaleInstance(1, 1);
        
        class FontMetricsImpl extends FontMetrics{
            public FontMetricsImpl(Font f){
                super(f);
            }
        }
        Font font=new Font(Text.FONT_NAME,Font.BOLD,8);
        
        when(g2.getFont()).thenReturn(font);
        when(g2.getFontRenderContext()).thenReturn(new FontRenderContext(at,true,true));
        when(g2.getFontMetrics()).thenReturn(new FontMetricsImpl(font));
        
        SCHSymbol symbol=new SCHSymbol();
        symbol.Add(new RoundRect(10,10,100,100));
        symbol.Paint(g2,vw,at,0);
        verify(g2,times(1)).draw(any(java.awt.Shape.class));
        
        //shift viewport outside
        vw.x=400;
        vw.x=400;
        reset(g2);
        when(g2.getFont()).thenReturn(font);
        when(g2.getFontRenderContext()).thenReturn(new FontRenderContext(at,true,true));
        when(g2.getFontMetrics()).thenReturn(new FontMetricsImpl(font));
        
        symbol.Paint(g2,vw,at,0);
        verify(g2,times(0)).draw(any(java.awt.Shape.class));
    }
    
    @Test
    public void testMementoCreate(){
        Circuit unit=mock(Circuit.class);
        SCHSymbol symbol=new SCHSymbol();
        symbol.Add(new RoundRect(10,10,14,14));
        symbol.Add(new Pin(10,10));
        AbstractMemento memento1=symbol.getState(MementoType.CREATE_MEMENTO);
        AbstractMemento memento2=symbol.getState(MementoType.CREATE_MEMENTO);
        Assert.assertEquals(memento1,memento2);
        
        symbol.Move(20,20);
        AbstractMemento memento3=symbol.getState(MementoType.CREATE_MEMENTO);
        Assert.assertTrue(!memento1.equals(memento3));          
    }

    @Test
    public void testMementoChange(){
        Circuit unit=mock(Circuit.class);
        SCHSymbol symbol=new SCHSymbol();
        symbol.Add(new RoundRect(10,10,14,14));
        symbol.getChipText().Add(new FontTexture("pinnumber", "0", 8, 8, Text.Alignment.RIGHT, 8));
        symbol.Add(new Pin(10,10));
        AbstractMemento memento1=symbol.getState(MementoType.CREATE_MEMENTO);

        
        symbol.getChipText().get(0).Move(200, 100);
        AbstractMemento memento2=symbol.getState(MementoType.CREATE_MEMENTO);
        Assert.assertTrue(!memento1.equals(memento2));          
    }  
    
    
    
    
}

