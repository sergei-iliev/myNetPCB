package com.mynetpcb.gerber.processor.aperture;


import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.pad.shape.Circle;

import java.util.Collection;

public class ApertureCircleProcessor implements Processor{
    private final ApertureDictionary dictionary;
    
    public ApertureCircleProcessor(ApertureDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public void process(Unit<? extends Shape> board, int layermask) {  
        //circles
        for(Circle circle:board.<Circle>getShapes(Circle.class,layermask)){
            processCircle(circle);
        }
        //circles if footprints
        for(FootprintShape footprint:board.<FootprintShape>getShapes(FootprintShape.class)){
            for(Shape shape:footprint.getShapes()){
                if(!shape.isVisibleOnLayers(layermask)){
                    continue;
                }
                if(shape.getClass()== Circle.class){
                    processCircle((Circle)shape);
                }
            }
        }        
        
        
    }
    
    private void processCircle(Circle circle){
        if(circle.getFill()==Shape.Fill.EMPTY){
            CircleAperture aperture=new CircleAperture();
            aperture.setDiameter(circle.getThickness());
            dictionary.add(aperture);
        }else{
            CircleAperture aperture=new CircleAperture();
            aperture.setDiameter(2*circle.getWidth());
            dictionary.add(aperture);                
        } 
    }
}
