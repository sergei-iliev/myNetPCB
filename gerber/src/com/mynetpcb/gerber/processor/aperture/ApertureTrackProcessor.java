package com.mynetpcb.gerber.processor.aperture;

import com.mynetpcb.board.shape.PCBTrack;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.attribute.aperture.ConductorAttribute;
import com.mynetpcb.gerber.capi.Processor;

public class ApertureTrackProcessor implements Processor{
    private final ApertureDictionary dictionary;
    
    public ApertureTrackProcessor(ApertureDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public void process(Board board, int layermask) {     
            //tracks
            for(PCBTrack line:board.<PCBTrack>getShapes(PCBTrack.class,layermask)){
                 CircleAperture circle=new CircleAperture();
                 circle.setDiameter(line.getThickness());
                 circle.setAttribute(new ConductorAttribute());
                 dictionary.add(circle);               
            }
        }
}
