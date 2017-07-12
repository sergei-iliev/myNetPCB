package com.mynetpcb.gerber.processor.aperture;

import com.mynetpcb.board.shape.PCBFootprint;
import com.mynetpcb.board.shape.PCBLine;
import com.mynetpcb.board.shape.PCBRoundRect;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.attribute.aperture.ConductorAttribute;
import com.mynetpcb.gerber.capi.Processor;

import com.mynetpcb.pad.shape.Pad;

import com.mynetpcb.pad.shape.RoundRect;

import java.util.Collection;
import java.util.List;

public class ApertureRectProcessor implements Processor{
    private final ApertureDictionary dictionary;
    
    public ApertureRectProcessor(ApertureDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public void process(Board board, int layermask) {
        List<PCBFootprint> footprints= board.getShapes(PCBFootprint.class, layermask);   
        for(PCBFootprint footrpint:footprints){
            Collection<Shape> shapes=footrpint.getShapes();
            for(Shape shape:shapes){
                if(shape.getClass()==RoundRect.class){
                    processRect((RoundRect)shape);
                }
            }
        }
        
        //board lines
        for(PCBRoundRect rect:board.<PCBRoundRect>getShapes(PCBRoundRect.class,layermask)){
               processRect(rect);                               
        }
    }
    
    private void processRect(RoundRect rect){
        CircleAperture circle=new CircleAperture();
        circle.setDiameter(rect.getThickness());
        dictionary.add(circle);
    }
}
