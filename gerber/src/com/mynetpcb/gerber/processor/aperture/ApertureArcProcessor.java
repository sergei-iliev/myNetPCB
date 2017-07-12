package com.mynetpcb.gerber.processor.aperture;

import com.mynetpcb.board.shape.PCBArc;
import com.mynetpcb.board.shape.PCBFootprint;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.pad.shape.Arc;
import com.mynetpcb.pad.shape.Circle;

public class ApertureArcProcessor implements Processor{
    private final ApertureDictionary dictionary;
    
    public ApertureArcProcessor(ApertureDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public void process(Board board, int layermask) {          
        //arcs
        for(PCBArc arc:board.<PCBArc>getShapes(PCBArc.class,layermask)){                        
            processArc(arc);
        }
        //arcs in footprints
        for(PCBFootprint footprint:board.<PCBFootprint>getShapes(PCBFootprint.class)){
            for(Shape shape:footprint.getShapes() ){
                if(!shape.isVisibleOnLayers(layermask)){
                    continue;
                }
                if(shape.getClass()== Arc.class){
                    processArc((Arc)shape);
                }
            }
        }
    }
    
    private void processArc(Arc arc){
        CircleAperture aperture=new CircleAperture();
        aperture.setDiameter(arc.getThickness());
        dictionary.add(aperture);
    }
}
