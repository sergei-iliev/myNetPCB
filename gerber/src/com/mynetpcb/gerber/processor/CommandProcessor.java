package com.mynetpcb.gerber.processor;

import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.GraphicsStateContext;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.gerber.processor.command.CommandArcProcessor;
import com.mynetpcb.gerber.processor.command.CommandCircleProcessor;
import com.mynetpcb.gerber.processor.command.CommandFilledContourProcessor;
import com.mynetpcb.gerber.processor.command.CommandPadProcessor;
import com.mynetpcb.gerber.processor.command.CommandRectProcessor;
import com.mynetpcb.gerber.processor.command.CommandTextProcessor;
import com.mynetpcb.gerber.processor.command.CommandTrackProcessor;
import com.mynetpcb.gerber.processor.command.CommandViaProcessor;

import java.util.ArrayList;
import java.util.Collection;


public class CommandProcessor implements Processor{

   private final  GraphicsStateContext context;
   private final Collection<Processor> processors; 
   
   public CommandProcessor(GraphicsStateContext context){
      this.context=context;
      this.processors=new ArrayList(15);
       this.processors.add(new CommandFilledContourProcessor(context));
      //this.processors.add(new CommandRegionProcessor(context));
      this.processors.add(new CommandTrackProcessor(context));
      this.processors.add(new CommandViaProcessor(context));
      this.processors.add(new CommandPadProcessor(context));
      this.processors.add(new CommandCircleProcessor(context));
      this.processors.add(new CommandArcProcessor(context));
      //this.processors.add(new CommandLineProcessor(context));
      this.processors.add(new CommandRectProcessor(context));
      this.processors.add(new CommandTextProcessor(context));
   }
   
   
   public void process(GerberServiceContext serviceContext,Unit<? extends Shape> board,int layermask){
       for(Processor processor:processors){
           processor.process(serviceContext, board, layermask);
       }
   }

}

