package com.mynetpcb.gerber.processor.aperture;

import com.mynetpcb.board.shape.PCBFootprint;
import com.mynetpcb.board.shape.PCBLine;
import com.mynetpcb.board.shape.PCBTrack;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.attribute.aperture.ConductorAttribute;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.pad.shape.Arc;
import com.mynetpcb.pad.shape.Line;

public class ApertureLineProcessor implements Processor{
    private final ApertureDictionary dictionary;
    
    public ApertureLineProcessor(ApertureDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public void process(Board board, int layermask) {
        //board lines
        for(PCBLine line:board.<PCBLine>getShapes(PCBLine.class,layermask)){
               processLine(line);                               
        }
        //line if footprints
        for(PCBFootprint footprint:board.<PCBFootprint>getShapes(PCBFootprint.class)){
            for(Shape shape:footprint.getShapes() ){
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
