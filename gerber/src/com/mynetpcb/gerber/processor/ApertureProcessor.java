package com.mynetpcb.gerber.processor;

import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.gerber.processor.aperture.ApertureArcProcessor;
import com.mynetpcb.gerber.processor.aperture.ApertureCircleProcessor;
import com.mynetpcb.gerber.processor.aperture.ApertureFilledContourProcessor;
import com.mynetpcb.gerber.processor.aperture.ApertureLineProcessor;
import com.mynetpcb.gerber.processor.aperture.AperturePadProcessor;
import com.mynetpcb.gerber.processor.aperture.ApertureRectProcessor;
import com.mynetpcb.gerber.processor.aperture.ApertureRegionProcessor;
import com.mynetpcb.gerber.processor.aperture.ApertureTextProcessor;
import com.mynetpcb.gerber.processor.aperture.ApertureTrackProcessor;
import com.mynetpcb.gerber.processor.aperture.ApertureViaProcessor;

import java.util.ArrayList;
import java.util.Collection;


/*
 * Construct aperture dictionary and macro definisions
 */
public class ApertureProcessor implements Processor{
    private final ApertureDictionary dictionary;
    private final Collection<Processor> processors;
    
    public ApertureProcessor(ApertureDictionary dictionary) {
        this.dictionary = dictionary;
        this.dictionary.reset();
        processors=new ArrayList(15);
        processors.add(new ApertureFilledContourProcessor(dictionary)); 
        processors.add(new ApertureRegionProcessor(dictionary)); 
        processors.add(new AperturePadProcessor(dictionary));
        processors.add(new ApertureTrackProcessor(dictionary));
        processors.add(new ApertureViaProcessor(dictionary));   
        processors.add(new ApertureCircleProcessor(dictionary)); 
        processors.add(new ApertureArcProcessor(dictionary)); 
        processors.add(new ApertureLineProcessor(dictionary)); 
        processors.add(new ApertureRectProcessor(dictionary)); 
        processors.add(new ApertureTextProcessor(dictionary)); 
    }

    @Override
    public void process(GerberServiceContext serviceContext,Unit<? extends Shape> board, int layermask) {
       
        for(Processor processor:processors){
            processor.process(serviceContext,board, layermask);
        }
    }
    
    

    

}

