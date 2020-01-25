package com.mynetpcb.gerber.processor.aperture;

import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.d2.shapes.Obround;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.attribute.aperture.ComponentPadAttribute;
import com.mynetpcb.gerber.attribute.aperture.SMDPadAttribute;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.pad.shape.Pad;
import com.mynetpcb.pad.shape.pad.CircularShape;

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
        double diameter;
        for(FootprintShape footprint:footprints){
            Collection<Pad> pads=(Collection<Pad>)footprint.getPads();
            for(Pad pad:pads){
                if(pad.isVisibleOnLayers(layermask)){
                    ApertureDefinition apperture=null;
                    switch(pad.getShapeType()){
                    case CIRCULAR:
                        apperture=new CircleAperture();
                        diameter=((CircularShape)pad.getPadDrawing()).getDiameter();
                        ((CircleAperture)apperture).setDiameter(diameter);
                        break;
                    case OVAL:
                        apperture=new CircleAperture();  
                        diameter=((Obround)pad.getPadDrawing().getGeometricFigure()).getDiameter();
                        ((CircleAperture)apperture).setDiameter(diameter);                          
                        break;
                    case RECTANGULAR:case POLYGON:
                            //add default
                            CircleAperture circle=new CircleAperture();
                            circle.setDiameter(Grid.MM_TO_COORD(1));
                            dictionary.add(circle);                                                                        
                    }
                    if(apperture!=null){   //not all pads produce apperture(rect and polygon)
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
}
