package com.mynetpcb.gerber.processor.aperture;

import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.pad.shape.RoundRect;

import java.util.Collection;
import java.util.List;

public class ApertureRectProcessor implements Processor{
    private final ApertureDictionary dictionary;
    
    public ApertureRectProcessor(ApertureDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public void process(GerberServiceContext serviceContext,Unit<? extends Shape> board, int layermask) {
        //board rects
        for(RoundRect rect:board.<RoundRect>getShapes(RoundRect.class,layermask)){
               processRect(rect);                               
        }
        if(serviceContext.getParameter(GerberServiceContext.FOOTPRINT_SHAPES_ON_SILKSCREEN, Boolean.class)){        
         List<FootprintShape> footprints= board.getShapes(FootprintShape.class);   
         for(FootprintShape footrpint:footprints){
            Collection<? extends Shape> shapes=footrpint.getShapes();
            for(Shape shape:shapes){
                if(shape.getClass()==RoundRect.class){
                    processRect((RoundRect)shape);
                }
            }
         }
        }
    }
    
    private void processRect(RoundRect rect){
        CircleAperture circle=new CircleAperture();
        circle.setDiameter(rect.getThickness());
        dictionary.add(circle);
    }
    }