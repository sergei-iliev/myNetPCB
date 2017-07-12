package com.mynetpcb.gerber.processor.aperture;

import com.mynetpcb.board.shape.PCBCircle;
import com.mynetpcb.board.shape.PCBFootprint;
import com.mynetpcb.board.shape.PCBTrack;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.pad.shape.Arc;
import com.mynetpcb.pad.shape.Circle;

public class ApertureCircleProcessor implements Processor{
    private final ApertureDictionary dictionary;
    
    public ApertureCircleProcessor(ApertureDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public void process(Board board, int layermask) {  
        //circles
        for(PCBCircle circle:board.<PCBCircle>getShapes(PCBCircle.class,layermask)){
            processCircle(circle);
        }
        //circles if footprints
        for(PCBFootprint footprint:board.<PCBFootprint>getShapes(PCBFootprint.class)){
            for(Shape shape:footprint.getShapes() ){
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
