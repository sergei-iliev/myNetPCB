package com.mynetpcb.gerber.processor.aperture;

import com.mynetpcb.board.shape.PCBArc;
import com.mynetpcb.board.shape.PCBCircle;
import com.mynetpcb.board.shape.PCBLine;
import com.mynetpcb.board.shape.PCBRoundRect;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.attribute.aperture.CutOutAttribute;
import com.mynetpcb.gerber.capi.Processor;

/*
 * Define board outlines
 */
public class ApertureCutOutProcessor implements Processor{
    
    private final ApertureDictionary dictionary;
    
    public ApertureCutOutProcessor(ApertureDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public void process(Board board, int layermask) {
            //lines
            for(PCBLine line:board.<PCBLine>getShapes(PCBLine.class,layermask)){
                processCutOut(line);        
            }            
            //circle
            for(PCBCircle circle:board.<PCBCircle>getShapes(PCBCircle.class,layermask)){
                processCutOut(circle);
            }
            //arcs
            for(PCBArc arc:board.<PCBArc>getShapes(PCBArc.class,layermask)){
                processCutOut(arc);
            }
            //round rect
            for(PCBRoundRect rect:board.<PCBRoundRect>getShapes(PCBRoundRect.class,layermask)){
                processCutOut(rect);
            }
        }
    
    private void processCutOut(Shape shape){
        CircleAperture circle=new CircleAperture();
        circle.setDiameter(shape.getThickness());
        circle.setAttribute(new CutOutAttribute());
        dictionary.add(circle); 
    }
}
