package com.mynetpcb.gerber.processor.aperture;

import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.pad.shape.Line;

public class ApertureLineProcessor implements Processor{
    private final ApertureDictionary dictionary;
    
    public ApertureLineProcessor(ApertureDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public void process(GerberServiceContext serviceContext,Unit<? extends Shape> board, int layermask) {
        //board lines
        for(Line line:board.<Line>getShapes(Line.class,layermask)){
               processLine(line);                               
        }
        //line if footprints
        if(serviceContext.getParameter(GerberServiceContext.FOOTPRINT_SHAPES_ON_SILKSCREEN, Boolean.class)){        
         for(FootprintShape footprint:board.<FootprintShape>getShapes(FootprintShape.class)){

            for(Shape shape:footprint.getShapes()){
                if(!shape.isVisibleOnLayers(layermask)){
                    continue;
                }
                if(shape.getClass()== Line.class){
                    processLine((Line)shape);
                }
            }
         }
        }
    }
    
    private void processLine(Line line){
        CircleAperture circle=new CircleAperture();
        circle.setDiameter(line.getThickness());
        dictionary.add(circle); 
    }
}

