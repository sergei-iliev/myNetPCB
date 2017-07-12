package tests.mynetpcb.gerber;

import com.mynetpcb.board.shape.PCBCopperArea;
import com.mynetpcb.board.shape.PCBTrack;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.ApertureMacro;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.aperture.type.ObroundAperture;
import com.mynetpcb.gerber.aperture.type.PolygonAperture;
import com.mynetpcb.gerber.aperture.type.RectangleAperture;
import com.mynetpcb.gerber.attribute.AbstractAttribute;


import com.mynetpcb.gerber.attribute.aperture.ComponentPadAttribute;
import com.mynetpcb.gerber.attribute.aperture.SMDPadAttribute;
import com.mynetpcb.gerber.attribute.aperture.ViaPadAttribute;
import com.mynetpcb.gerber.attribute.drill.DrillFunctionAttribute;

import org.junit.Assert;
import org.junit.Test;

public class ApertureTest {

    @Test
    public void testCircleApperture(){
      PCBTrack track=new PCBTrack(Grid.MM_TO_COORD(0.2),Layer.LAYER_BACK);
      
      CircleAperture circle=new CircleAperture();
      circle.setDiameter(track.getThickness()+track.getClearance());
      
      ApertureDictionary dictionary=new ApertureDictionary();
      dictionary.add(circle);
      
      Assert.assertTrue(dictionary.findCircle(circle.getDiameter()+track.getClearance())!=null);
      
      CircleAperture circle1=new CircleAperture();
      circle1.setDiameter(track.getThickness()+track.getClearance());
      dictionary.add(circle1);
      
      Assert.assertTrue(dictionary.findCircle(track.getThickness()+track.getClearance()) ==circle);  
      
      track=new PCBTrack(Grid.MM_TO_COORD(0.25),Layer.LAYER_BACK);
      
      CircleAperture circle3=new CircleAperture();
      circle3.setDiameter(track.getThickness()+track.getClearance());
      dictionary.add(circle3);
      
      Assert.assertTrue(dictionary.findCircle(track.getThickness()+track.getClearance()) ==circle3);    
    }

    @Test
    public void testCircleApperturePrint(){
        PCBTrack track=new PCBTrack(Grid.MM_TO_COORD(0.5),Layer.LAYER_BACK);
        
        CircleAperture circle=new CircleAperture();
        circle.setDiameter(track.getThickness()+track.getClearance());
        circle.setCode(10);
        Assert.assertTrue("%ADD10C,0.5*%".equals(circle.print()));
        
        //circle.setHole(Grid.MM_TO_COORD(0.25));
        //Assert.assertTrue("%ADD10C,0.5X0.25*%".equals(circle.print()));
    }
    
    @Test
    public void testDrillAttribute(){
        
        DrillFunctionAttribute drill=new DrillFunctionAttribute();        
        Assert.assertTrue("%TF.FileFunction,NonPlated,1,2,NPTH,Drill*%".equals(drill.print()));
    }
    
    @Test
    public void testRectangleApperturePrint(){
                
        RectangleAperture rect=new RectangleAperture();
        rect.setX(Grid.MM_TO_COORD(0.044));
        rect.setY(Grid.MM_TO_COORD(0.025));
        rect.setCode(22);
        Assert.assertTrue("%ADD22R,0.044X0.025*%".equals(rect.print()));
        
//        rect.setHole(Grid.MM_TO_COORD(0.019));
//        Assert.assertTrue("%ADD22R,0.044X0.025X0.019*%".equals(rect.print()));
    }    
    
    @Test
    public void testObroundApperturePrint(){
                
        ObroundAperture rect=new ObroundAperture();
        rect.setX(Grid.MM_TO_COORD(0.046));
        rect.setY(Grid.MM_TO_COORD(0.026));
        rect.setCode(22);
        Assert.assertTrue("%ADD22O,0.046X0.026*%".equals(rect.print()));
        
//        rect.setHole(Grid.MM_TO_COORD(0.019));
//        Assert.assertTrue("%ADD22O,0.046X0.026X0.019*%".equals(rect.print()));
    }  
    @Test
    public void testPoligonApperturePrint(){
                
        PolygonAperture rect=new PolygonAperture();
        rect.setDiameter(Grid.MM_TO_COORD(0.040));
        rect.setVerticesNumber(6);
        rect.setCode(17);
        
        Assert.assertTrue("%ADD17P,0.04X6*%".equals(rect.print()));
        
//        rect.setRotation(0.0);
//        rect.setHole(Grid.MM_TO_COORD(0.019));
//        
//        Assert.assertTrue("%ADD17P,0.04X6X0.0X0.019*%".equals(rect.print()));
    }   
    
    @Test
    public void testAppertureMacro(){
                
        ApertureMacro macro=new ApertureMacro("MACRO",20);        
        Assert.assertTrue("%AMMACRO*\r\n".equals(macro.print()));
    }  
    
    @Test
    public void testAppertureAttribute(){
        ApertureDictionary d=new ApertureDictionary();
        RectangleAperture rect=new RectangleAperture();
        rect.setX(Grid.MM_TO_COORD(0.044));
        rect.setY(Grid.MM_TO_COORD(0.025));
        d.add(rect);
        
        Assert.assertTrue(d.findRectangle(rect.getX(),rect.getY())==rect);
        
        rect=new RectangleAperture();
        rect.setX(Grid.MM_TO_COORD(0.044));
        rect.setY(Grid.MM_TO_COORD(0.025));
        rect.setAttribute(new ComponentPadAttribute());
        d.add(rect);
        
        Assert.assertTrue(d.findRectangle(rect.getX(),rect.getY())!=rect);
        Assert.assertTrue(d.findRectangle(AbstractAttribute.Type.ComponentPad,rect.getX(),rect.getY())==rect);
        
        CircleAperture circle=new CircleAperture();
        circle.setDiameter(Grid.MM_TO_COORD(0.044));
        circle.setAttribute(new ComponentPadAttribute());
        d.add(circle);
        Assert.assertTrue(d.findCircle(AbstractAttribute.Type.ComponentPad,circle.getDiameter())==circle);
        
        rect=new RectangleAperture();
        rect.setX(Grid.MM_TO_COORD(0.044));
        rect.setY(Grid.MM_TO_COORD(0.025));
        rect.setAttribute(new ComponentPadAttribute());
        d.add(rect);
        
        rect=new RectangleAperture();
        rect.setX(Grid.MM_TO_COORD(0.044));
        rect.setY(Grid.MM_TO_COORD(0.125));
        d.add(rect);
        
      
        ObroundAperture obround=new ObroundAperture();
        obround.setX(Grid.MM_TO_COORD(0.046));
        obround.setY(Grid.MM_TO_COORD(0.026));
        obround.setAttribute(new ComponentPadAttribute());
        d.add(obround);
        
        
        
    }  
    @Test
    public void testAppertureDictionary(){
        ApertureDictionary d=new ApertureDictionary();
        RectangleAperture rect=new RectangleAperture();
        rect.setX(Grid.MM_TO_COORD(0.044));
        rect.setY(Grid.MM_TO_COORD(0.025));
        d.add(rect);
        
        rect=new RectangleAperture();
        rect.setX(Grid.MM_TO_COORD(0.044));
        rect.setY(Grid.MM_TO_COORD(0.025));
        rect.setAttribute(new ComponentPadAttribute());
        d.add(rect);
        
        rect=new RectangleAperture();
        rect.setX(Grid.MM_TO_COORD(0.044));
        rect.setY(Grid.MM_TO_COORD(0.025));
        rect.setAttribute(new ComponentPadAttribute());
        d.add(rect);
        
        rect=new RectangleAperture();
        rect.setX(Grid.MM_TO_COORD(0.044));
        rect.setY(Grid.MM_TO_COORD(0.125));
        d.add(rect);
        
        ObroundAperture o=new ObroundAperture();
        o.setX(Grid.MM_TO_COORD(0.046));
        o.setY(Grid.MM_TO_COORD(0.026));
        o.setAttribute(new SMDPadAttribute());
        d.add(o); 
        
        CircleAperture circle=new CircleAperture();
        circle.setDiameter(Grid.MM_TO_COORD(0.044));
        d.add(circle);
        
        circle=new CircleAperture();
        circle.setDiameter(Grid.MM_TO_COORD(0.044));
        circle.setAttribute(new ViaPadAttribute());
        d.add(circle);
        
        circle=new CircleAperture();
        circle.setDiameter(Grid.MM_TO_COORD(0.044));
        circle.setAttribute(new ViaPadAttribute());
        d.add(circle);
        
        System.out.println(d.print());
    }
}
