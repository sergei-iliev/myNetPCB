package com.mynetpcb.gerber.processor.aperture;

import com.mynetpcb.board.shape.PCBVia;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.attribute.aperture.ViaPadAttribute;
import com.mynetpcb.gerber.capi.Processor;

public class ApertureViaProcessor implements Processor{
    
    private final ApertureDictionary dictionary;
    
    public ApertureViaProcessor(ApertureDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public void process(Board board, int layermask) {
            
            for(PCBVia via:board.<PCBVia>getShapes(PCBVia.class,layermask)){
                CircleAperture circle=new CircleAperture();
                circle.setDiameter(via.getWidth());
                circle.setAttribute(new ViaPadAttribute());
                dictionary.add(circle);                         
            }
        }
    
}
