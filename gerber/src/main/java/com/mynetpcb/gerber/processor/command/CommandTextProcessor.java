package com.mynetpcb.gerber.processor.command;

import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.glyph.Glyph;
import com.mynetpcb.core.capi.text.glyph.GlyphTexture;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.GraphicsStateContext;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.gerber.command.AbstractCommand;
import com.mynetpcb.pad.shape.GlyphLabel;

public class CommandTextProcessor implements Processor{
    private final GraphicsStateContext context;

    public CommandTextProcessor(GraphicsStateContext context) {
        this.context = context;
    }

    @Override
    public void process(GerberServiceContext serviceContext,Unit<? extends Shape> board, int layermask) {
        //board text
        for(GlyphLabel label:board.<GlyphLabel>getShapes(GlyphLabel.class,layermask)){
               processTexture(label.getTexture(),board.getHeight());                               
        }
        //text in footprints
        for(FootprintShape footprint:board.<FootprintShape>getShapes(FootprintShape.class)){
            //grab text
            boolean isRefPrintable=serviceContext.getParameter(GerberServiceContext.FOOTPRINT_REFERENCE_ON_SILKSCREEN, Boolean.class);
            if(isRefPrintable){
                GlyphTexture text=(GlyphTexture)footprint.getTextureByTag("reference");
                if(!text.isEmpty()&&((text.getLayermaskId()&layermask)!=0)){
                    processTexture(text,board.getHeight());
                }   
            }
            boolean isValPrintable=serviceContext.getParameter(GerberServiceContext.FOOTPRINT_VALUE_ON_SILKSCREEN, Boolean.class);
            if(isValPrintable){
                GlyphTexture text=(GlyphTexture)footprint.getTextureByTag("value");
                if(!text.isEmpty()&&((text.getLayermaskId()&layermask)!=0)){
                    processTexture(text,board.getHeight());
                }   
            }            
            for(Shape shape:footprint.getShapes()){
                if(!shape.isVisibleOnLayers(layermask)){
                    continue;
                }
                if(shape.getClass()== GlyphLabel.class){
                    processTexture(((GlyphLabel)shape).getTexture(),board.getHeight());
                }
            }
        }
        

    }
    
    private void processTexture(GlyphTexture texture,int height){
    int diameter=texture.getThickness();
    double lastX=-1,lastY=-1;
    
    //set linear mode if not set
    context.resetCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION);
    ApertureDefinition aperture=context.getApertureDictionary().findCircle(diameter);
    //set aperture if not same
    context.resetAperture(aperture);
    
    for(Glyph glyph:texture.getGlyphs()){
            for(int i=0;i<glyph.getSegments().length;i++){    
                 if(glyph.getChar()==' '){
                     continue;
                 } 
                
                StringBuffer commandLine=new StringBuffer();
                //first point
                double x=glyph.getSegments()[i].ps.x;
                double y=glyph.getSegments()[i].ps.y;
                
                if (!Utils.EQ(x ,lastX)){                    
                    lastX = x;
                    commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(x)*100000));
                }
                
                if (!Utils.EQ(y,lastY))
                {                   
                    lastY = y;
                    commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-y)*100000));
                }
                commandLine.append("D02*");
                context.getOutput().append(commandLine);                
                
                //second point
                commandLine.setLength(0);
                
                x=glyph.getSegments()[i].pe.x;
                y=glyph.getSegments()[i].pe.y;
                
                if (!Utils.EQ(x ,lastX)){                    
                    lastX = x;
                    commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(x)*100000));
                }
                
                if (!Utils.EQ(y,lastY))
                {                   
                    lastY = y;
                    commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-y)*100000));
                }
                commandLine.append("D01*");
                context.getOutput().append(commandLine);                     
            }
    }
}
}

