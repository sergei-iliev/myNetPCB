package com.mynetpcb.gerber.processor.aperture;


import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.aperture.type.ObroundAperture;
import com.mynetpcb.gerber.aperture.type.PolygonAperture;
import com.mynetpcb.gerber.aperture.type.RectangleAperture;
import com.mynetpcb.gerber.attribute.aperture.ComponentPadAttribute;
import com.mynetpcb.gerber.attribute.aperture.SMDPadAttribute;
import com.mynetpcb.gerber.capi.GerberServiceContext;
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
    public void process(GerberServiceContext serviceContext,Unit<? extends Shape> board, int layermask) {       
        List<FootprintShape> footprints= board.getShapes(FootprintShape.class);              
        for(FootprintShape footrpint:footprints){
            Collection<Pad> pads=(footrpint).getPins();
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
