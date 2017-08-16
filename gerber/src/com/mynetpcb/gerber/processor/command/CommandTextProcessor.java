package com.mynetpcb.gerber.processor.command;


import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.glyph.Glyph;
import com.mynetpcb.core.capi.text.glyph.GlyphTexture;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.gerber.command.AbstractCommand;
import com.mynetpcb.pad.shape.GlyphLabel;

import java.awt.Rectangle;

public class CommandTextProcessor implements Processor {
    private final GraphicsStateContext context;

    public CommandTextProcessor(GraphicsStateContext context) {
        this.context = context;
    }

    @Override
    public void process(Unit<? extends Shape> board, int layermask) {
        //board text
        for(GlyphLabel label:board.<GlyphLabel>getShapes(GlyphLabel.class,layermask)){
               processTexture(label.getTexture(),board.getHeight());                               
        }
        //text in footprints
        for(FootprintShape footprint:board.<FootprintShape>getShapes(FootprintShape.class)){
            //grab text
            for(Texture text:((Textable)footprint).getChipText().getChildren()){
                if(!text.isEmpty()&&((text.getLayermaskId()&layermask)!=0)){
                    processTexture((GlyphTexture)text,board.getHeight());
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
    int size=texture.getThickness();
    int lastX=-1,lastY=-1;
    
    //set linear mode if not set
    context.resetCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION);
    ApertureDefinition aperture=context.getApertureDictionary().findCircle(size);
    //set aperture if not same
    context.resetAperture(aperture);
    
    
    Rectangle r = texture.getBoundingShape();
    int x,y;
    switch (texture.getAlignment()) {
    case LEFT:
        int xoffset = 0;
        for (Glyph glyph : texture.getGlyphs()) {
            if(glyph.getChar()==' '){
                xoffset += glyph.getDelta();
                continue;
            }
            int j = 0;
            for (int i = 0; i < glyph.getLinesNumber(); i++, j = (j + 2)) {
                StringBuffer commandLine=new StringBuffer();
                
                x=glyph.points[j].x + texture.getAnchorPoint().x + xoffset;
                y=glyph.points[j].y + texture.getAnchorPoint().y - r.height;
                if (x != lastX){                   
                    lastX = x;
                    commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(x)*100000));
                }
                if (y != lastY)
                {                   
                    lastY = y;
                    commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-y)*100000));
                }
                commandLine.append("D02*");
                context.getOutput().append(commandLine);
                
                x=glyph.points[j + 1].x + texture.getAnchorPoint().x + xoffset;
                y=glyph.points[j + 1].y + texture.getAnchorPoint().y - r.height;
                if (x != lastX){                   
                    lastX = x;
                    commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(x)*100000));
                }
                if (y != lastY)
                {                   
                    lastY = y;
                    commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-y)*100000));
                }
                commandLine.append("D01*");
                context.getOutput().append(commandLine);                
            }
            xoffset += glyph.getGlyphWidth() + glyph.getDelta();
        }    
        
         
         break;
    case RIGHT:
        xoffset = 0;
        for (Glyph glyph : texture.getGlyphs()) {
            if(glyph.getChar()==' '){
                xoffset += glyph.getDelta();
                continue;
            }

            int j = 0;
            for (int i = 0; i < glyph.getLinesNumber(); i++, j = (j + 2)) {
                StringBuffer commandLine=new StringBuffer();
                x=glyph.points[j].x + texture.getAnchorPoint().x + xoffset - r.width;
                y=glyph.points[j].y + texture.getAnchorPoint().y - r.height;
                if (x != lastX){                   
                    lastX = x;
                    commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(x)*100000));
                }
                if (y != lastY)
                {                   
                    lastY = y;
                    commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-y)*100000));
                }
                commandLine.append("D02*");
                context.getOutput().append(commandLine);
                
                x=glyph.points[j + 1].x + texture.getAnchorPoint().x + xoffset -r.width;
                y=glyph.points[j + 1].y + texture.getAnchorPoint().y - r.height;
                if (x != lastX){                   
                    lastX = x;
                    commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(x)*100000));
                }
                if (y != lastY)
                {                   
                    lastY = y;
                    commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-y)*100000));
                }
                commandLine.append("D01*");
                context.getOutput().append(commandLine);                  
            }
            xoffset += glyph.getGlyphWidth() + glyph.getDelta();
        }    
    
         break;
    case BOTTOM:
        int yoffset = 0;
        for (Glyph glyph : texture.getGlyphs()) {
            if(glyph.getChar()==' '){
                yoffset += glyph.getDelta();
                continue;
            }

            int j = 0;
            for (int i = 0; i < glyph.getLinesNumber(); i++, j = (j + 2)) {
                StringBuffer commandLine=new StringBuffer();
                x=glyph.points[j].x + texture.getAnchorPoint().x  - r.width;
                y=glyph.points[j].y + texture.getAnchorPoint().y-yoffset;
                if (x != lastX){                   
                    lastX = x;
                    commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(x)*100000));
                }
                if (y != lastY)
                {                   
                    lastY = y;
                    commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-y)*100000));
                }
                commandLine.append("D02*");
                context.getOutput().append(commandLine);
                
                x=glyph.points[j + 1].x + texture.getAnchorPoint().x  - r.width;
                y=glyph.points[j + 1].y + texture.getAnchorPoint().y-yoffset;
                if (x != lastX){                   
                    lastX = x;
                    commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(x)*100000));
                }
                if (y != lastY)
                {                   
                    lastY = y;
                    commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-y)*100000));
                }
                commandLine.append("D01*");
                context.getOutput().append(commandLine); 
            }
            yoffset += glyph.getGlyphHeight() + glyph.getDelta();
        }
        break;
    case TOP:
        yoffset = 0;
        for (Glyph glyph : texture.getGlyphs()) {
            if(glyph.getChar()==' '){
                yoffset += glyph.getDelta();
                continue;
            }
            
            int j = 0;
            for (int i = 0; i < glyph.getLinesNumber(); i++, j = (j + 2)) {
                StringBuffer commandLine=new StringBuffer();
                x=glyph.points[j].x + texture.getAnchorPoint().x  - r.width;
                y=glyph.points[j].y + texture.getAnchorPoint().y-yoffset+r.height;
                if (x != lastX){                   
                    lastX = x;
                    commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(x)*100000));
                }
                if (y != lastY)
                {                   
                    lastY = y;
                    commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-y)*100000));
                }
                commandLine.append("D02*");
                context.getOutput().append(commandLine);
                
                x=glyph.points[j + 1].x + texture.getAnchorPoint().x  - r.width;
                y=glyph.points[j + 1].y + texture.getAnchorPoint().y-yoffset+r.height;
                if (x != lastX){                   
                    lastX = x;
                    commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(x)*100000));
                }
                if (y != lastY)
                {                   
                    lastY = y;
                    commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-y)*100000));
                }
                commandLine.append("D01*");
                context.getOutput().append(commandLine); 
            }
            yoffset += glyph.getGlyphHeight() + glyph.getDelta();
        }            
        break;
    }
}
}
