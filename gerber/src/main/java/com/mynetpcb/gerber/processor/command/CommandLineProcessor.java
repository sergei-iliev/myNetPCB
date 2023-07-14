package com.mynetpcb.gerber.processor.command;

import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.attribute.AbstractAttribute;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.GraphicsStateContext;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.gerber.command.AbstractCommand;
import com.mynetpcb.pad.shape.Line;

import java.util.Collection;
import java.util.List;

public class CommandLineProcessor implements Processor{
        private final GraphicsStateContext context;

        public CommandLineProcessor(GraphicsStateContext context) {
            this.context = context;
        }

        @Override
        public void process(GerberServiceContext serviceContext,Unit<? extends Shape>  board, int layermask) {
            //process board lines
            List<Line> lines= board.getShapes(Line.class, layermask);              
            for(Line line:lines){
                processLine(line,board.getHeight());
            }
            
            //process board lines
            if(serviceContext.getParameter(GerberServiceContext.FOOTPRINT_SHAPES_ON_SILKSCREEN, Boolean.class)){        
             List<FootprintShape> footprints= board.getShapes(FootprintShape.class);                   
             for(FootprintShape footprint:footprints){            
                for(Shape shape:footprint.getShapes()){
                    if(!shape.isVisibleOnLayers(layermask)){
                        continue;
                    }
                    if(shape.getClass()== Line.class){
                        processLine((Line)shape,board.getHeight());
                    }
                }
             }
            }

        }
        protected void processLine(Line line,int height){
          this.processLine(line.getLinePoints(),line.getThickness(),height,null,false);
        }
        
        protected void processLine(Collection<? extends Point> points,double thickness,int height,AbstractAttribute.Type attributeType,boolean isRegionMode){
            
                double lastX=-1,lastY=-1;
                boolean firstPoint=true;

                //set linear mode if not set
                context.resetCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION);
                ApertureDefinition aperture;
                if(attributeType==null){            
                   aperture=context.getApertureDictionary().findCircle(thickness);
                }else{
                   aperture=context.getApertureDictionary().findCircle(attributeType,thickness);  
                }
                //set aperture if not same
                if(!isRegionMode) {  //****no apperture change in region mode!
                  context.resetAperture(aperture);
                }
                for(Point point:points){
                    StringBuffer commandLine=new StringBuffer();
                    if (!Utils.EQ(point.x ,lastX)){                   
                        lastX = point.x;
                        commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(point.x)*100000));
                    }
                    if (!Utils.EQ(point.y,lastY))
                      {                   
                        lastY = point.y;
                        commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-point.y)*100000));
                      }
                    
                    if (firstPoint){
                       commandLine.append("D02*");
                    }else{
                       commandLine.append("D01*"); 
                    }
                    
                    context.getOutput().append(commandLine);
                                    
                    firstPoint = false;
                }
                
            
        }
    }
