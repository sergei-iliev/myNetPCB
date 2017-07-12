package com.mynetpcb.gerber.processor.command;

import com.mynetpcb.board.shape.PCBArc;
import com.mynetpcb.board.shape.PCBCircle;
import com.mynetpcb.board.shape.PCBLine;
import com.mynetpcb.board.shape.PCBRoundRect;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.attribute.AbstractAttribute;
import com.mynetpcb.gerber.capi.Processor;

import com.mynetpcb.gerber.command.AbstractCommand;
import com.mynetpcb.pad.shape.Arc;
import com.mynetpcb.pad.shape.Circle;
import com.mynetpcb.pad.shape.Line;

import com.mynetpcb.pad.shape.RoundRect;

import java.awt.Point;

import java.awt.geom.Point2D;

import java.util.List;

public class CommandCutOutProcessor implements Processor {

    private final GraphicsStateContext context;

    public CommandCutOutProcessor(GraphicsStateContext context) {
        this.context = context;
    }

    @Override
    public void process(Board board, int layermask) {
        //lines
                     
        for(PCBLine line:board.<PCBLine>getShapes(PCBLine.class, layermask)){
            processLine(line,board.getHeight());
        } 
        //round rect
        for(PCBRoundRect rect:board.<PCBRoundRect>getShapes(PCBRoundRect.class,layermask)){
            processRect(rect,board.getHeight());
        }
        //circle
        for(PCBCircle circle:board.<PCBCircle>getShapes(PCBCircle.class,layermask)){
            processCircle(circle,board.getHeight());
        }
        //arc
        for(PCBArc circle:board.<PCBArc>getShapes(PCBArc.class,layermask)){
            processArc(circle,board.getHeight());
        }
        
        
        
    }
    
    private void processArc(Arc arc,int height){
        CommandArcProcessor arcProcessor=new CommandArcProcessor(context);  
        arcProcessor.processArc(arc, height,AbstractAttribute.Type.CutOut); 
    }
    
    private void processCircle(Circle circle,int height){
        CommandCircleProcessor circleProcessor=new CommandCircleProcessor(context);  
        circleProcessor.processCircle(circle, height, AbstractAttribute.Type.CutOut);        
    }
    
    private void processRect(RoundRect rect,int height){        
        if(rect.getArc()==0){
           //rect is 4 point line
           CommandLineProcessor lineProcessor=new CommandLineProcessor(context);          
           int x=rect.getX();
           int y=rect.getY();
           int w=rect.getWidth();
           int h=rect.getHeight();
           
           Line line =new Line(rect.getThickness(),0);
           line.add(x, y);
           line.add(x+w, y);
           line.add(x+w, y+h);
           line.add(x, y+h);
           line.add(x, y); 
           lineProcessor.processLine(line, height,AbstractAttribute.Type.CutOut); 
        }else{
           //draw 4 arcs and a line                
           CommandArcProcessor arcProcessor=new CommandArcProcessor(context);           
           Arc arc=new Arc(rect.getX()+rect.getArc(),rect.getY()+rect.getArc(),rect.getArc(),rect.getThickness(),rect.getCopper().getLayerMaskID());
           arc.setStartAngle(90);
           arc.setExtendAngle(90); 
           arcProcessor.processArc(arc, height,AbstractAttribute.Type.CutOut); 
           Point2D tlStart=arc.getStartPoint();
           Point2D tlEnd=arc.getEndPoint();
            
           arc.setX((rect.getX()+rect.getWidth())-rect.getArc());
           arc.setStartAngle(0);
           arc.setExtendAngle(90); 
           arcProcessor.processArc(arc, height,AbstractAttribute.Type.CutOut); 
           Point2D trStart=arc.getStartPoint();
           Point2D trEnd=arc.getEndPoint(); 
           
           arc.setX((rect.getX()+rect.getWidth())-rect.getArc());
           arc.setY((rect.getY()+rect.getHeight())-rect.getArc()); 
           arc.setStartAngle(270);
           arc.setExtendAngle(90); 
           arcProcessor.processArc(arc, height,AbstractAttribute.Type.CutOut); 
           Point2D brStart=arc.getStartPoint();
           Point2D brEnd=arc.getEndPoint(); 
           
           arc.setX((rect.getX())+rect.getArc());
           arc.setY((rect.getY()+rect.getHeight())-rect.getArc()); 
           arc.setStartAngle(180);
           arc.setExtendAngle(90); 
           arcProcessor.processArc(arc, height,AbstractAttribute.Type.CutOut); 
           Point2D blStart=arc.getStartPoint();
           Point2D blEnd=arc.getEndPoint(); 
           
           CommandLineProcessor lineProcessor=new CommandLineProcessor(context);   
           Line line=new Line(rect.getThickness(),rect.getCopper().getLayerMaskID()); 
           line.add((int)tlStart.getX(),(int)tlStart.getY()); 
           line.add((int)trEnd.getX(),(int)trEnd.getY()); 
           lineProcessor.processLine(line, height,AbstractAttribute.Type.CutOut);
            
           line.Clear();
           line.add((int)trStart.getX(),(int)trStart.getY()); 
           line.add((int)brEnd.getX(),(int)brEnd.getY()); 
           lineProcessor.processLine(line, height,AbstractAttribute.Type.CutOut);
            
           line.Clear();
           line.add((int)brStart.getX(),(int)brStart.getY()); 
           line.add((int)blEnd.getX(),(int)blEnd.getY()); 
           lineProcessor.processLine(line, height,AbstractAttribute.Type.CutOut);  
            
           line.Clear();
           line.add((int)blStart.getX(),(int)blStart.getY()); 
           line.add((int)tlEnd.getX(),(int)tlEnd.getY()); 
           lineProcessor.processLine(line, height,AbstractAttribute.Type.CutOut);  
            
        }        
                    
    }
    
    protected void processLine(Line line,int height){
            CommandLineProcessor processor=new CommandLineProcessor(context);
            processor.processLine(line, height, AbstractAttribute.Type.CutOut);                                
    }    
    
}
