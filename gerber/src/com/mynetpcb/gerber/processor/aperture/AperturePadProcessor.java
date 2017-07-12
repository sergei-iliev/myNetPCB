package com.mynetpcb.gerber.processor.aperture;

import com.mynetpcb.board.shape.PCBFootprint;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.aperture.type.ObroundAperture;
import com.mynetpcb.gerber.aperture.type.PolygonAperture;
import com.mynetpcb.gerber.aperture.type.RectangleAperture;
import com.mynetpcb.gerber.attribute.drill.ComponentDrillAttribute;
import com.mynetpcb.gerber.attribute.aperture.ComponentPadAttribute;
import com.mynetpcb.gerber.attribute.aperture.SMDPadAttribute;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.pad.shape.Pad;

import java.util.Collection;
import java.util.List;

public class AperturePadProcessor implements Processor{
    
    private final ApertureDictionary dictionary;
    
    public AperturePadProcessor(ApertureDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public void process(Board board, int layermask) {       
        List<PCBFootprint> footprints= board.getShapes(PCBFootprint.class);              
        for(PCBFootprint footrpint:footprints){
            Collection<Pad> pads=footrpint.getPins();
            for(Pad pad:pads){
                if(pad.isVisibleOnLayers(layermask)){
                    ApertureDefinition apperture=null;
                    switch(pad.getShape()){
                    case CIRCULAR:
                        apperture=new CircleAperture();
                        ((CircleAperture)apperture).setDiameter(pad.getWidth());
                        break;
                    case OVAL:
                         apperture=new ObroundAperture();
                        ((ObroundAperture)apperture).setX(pad.getWidth());
                        ((ObroundAperture)apperture).setY(pad.getHeight()); 
                        break;
                    case RECTANGULAR:
                        apperture=new RectangleAperture();
                        ((RectangleAperture)apperture).setX(pad.getWidth());
                        ((RectangleAperture)apperture).setY(pad.getHeight()); 
                        break;
                    case POLYGON:
                        apperture= new PolygonAperture();
                        ((PolygonAperture)apperture).setDiameter(pad.getWidth());
                        ((PolygonAperture)apperture).setVerticesNumber(6);
                    }
                    
                    if(pad.getType()==Pad.Type.SMD){
                        apperture.setAttribute(new SMDPadAttribute());  
                    }else{
                        apperture.setAttribute(new ComponentPadAttribute());                                                
                    }                    
                    dictionary.add(apperture);                    
                }
            }
        
        }
    }
}
