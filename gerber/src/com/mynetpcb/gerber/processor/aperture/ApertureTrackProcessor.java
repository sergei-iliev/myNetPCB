package com.mynetpcb.gerber.processor.aperture;


import com.mynetpcb.core.board.shape.TrackShape;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.attribute.aperture.ConductorAttribute;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.Processor;

public class ApertureTrackProcessor implements Processor{
    private final ApertureDictionary dictionary;
    
    public ApertureTrackProcessor(ApertureDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public void process(GerberServiceContext serviceContext,Unit<? extends Shape> board, int layermask) {     
            //tracks
            for(TrackShape line:board.<TrackShape>getShapes(TrackShape.class,layermask)){
                 CircleAperture circle=new CircleAperture();
                 circle.setDiameter(line.getThickness());
                 circle.setAttribute(new ConductorAttribute());
                 dictionary.add(circle);               
            }
        }
}
