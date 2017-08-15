package com.mynetpcb.gerber.processor.aperture;


import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.pad.shape.Line;

import java.util.Collection;

public class ApertureLineProcessor implements Processor{
    private final ApertureDictionary dictionary;
    
    public ApertureLineProcessor(ApertureDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public void process(Unit<? extends Shape> board, int layermask) {
        //board lines
        for(Line line:board.<Line>getShapes(Line.class,layermask)){
               processLine(line);                               
        }
        //line if footprints
        for(FootprintShape footprint:board.<FootprintShape>getShapes(FootprintShape.class)){
            Collection<Shape> shapes=footprint.<Shape>getShapes();
            for(Shape shape:shapes){
                if(!shape.isVisibleOnLayers(layermask)){
                    continue;
                }
                if(shape.getClass()== Line.class){
                    processLine((Line)shape);
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
