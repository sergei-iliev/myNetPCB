package com.mynetpcb.gerber;

import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.utils.VersionUtils;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.attribute.AbstractAttribute;
import com.mynetpcb.gerber.attribute.file.CreationDateAttribute;
import com.mynetpcb.gerber.attribute.file.FileFunctionAttribute;
import com.mynetpcb.gerber.attribute.file.GenerationSoftwareAttribute;
import com.mynetpcb.gerber.attribute.file.PartFunctionAttribute;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.Gerberable;
import com.mynetpcb.gerber.capi.GraphicsStateContext;
import com.mynetpcb.gerber.capi.StringBufferExt;
import com.mynetpcb.gerber.command.AbstractCommand;
import com.mynetpcb.gerber.command.CommandDictionary;
import com.mynetpcb.gerber.command.extended.CoordinateResolutionCommand;
import com.mynetpcb.gerber.command.extended.LevelPolarityCommand;
import com.mynetpcb.gerber.command.extended.StepAndRepeatCommand;
import com.mynetpcb.gerber.command.extended.UnitCommand;
import com.mynetpcb.gerber.processor.ApertureProcessor;
import com.mynetpcb.gerber.processor.CommandProcessor;

import java.io.BufferedWriter;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Gerber implements Gerberable{
    private  ApertureDictionary apertureDictionary;
    private final CommandDictionary commandDictionary;
    private final Unit<? extends Shape> board;
    
    public Gerber(Unit<? extends Shape> board){
        this.apertureDictionary=new ApertureDictionary();
        this.commandDictionary=new CommandDictionary();
        this.board=board;
    }

    
    public void build(GerberServiceContext serviceContext, String fileName,int layermask)throws IOException{
        ApertureProcessor apertureProcessor=new ApertureProcessor(apertureDictionary);
        apertureProcessor.process(serviceContext,board, layermask) ;
        
        Path gerberFile = Paths.get(fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(gerberFile,
                        StandardCharsets.UTF_8)) {

           GraphicsStateContext context=new GraphicsStateContext(apertureDictionary, commandDictionary, new StringBufferExt());
           
           writer.write(createHeader(context,layermask));
           writer.write(createCommands(serviceContext,context,layermask));
           writer.write(createFooter(context));                    
       } 
       
        
    }
    
    private String createCommands(GerberServiceContext serviceContext,GraphicsStateContext context,int layermask){
        StringBufferExt sb=new StringBufferExt();
        
        /*Start dark polarity*/
        context.resetPolarity(LevelPolarityCommand.Polarity.DARK);
        
        AbstractCommand stepAndRepeat=new StepAndRepeatCommand
            ();
        sb.append(stepAndRepeat.print());
        
        CommandProcessor processor=new CommandProcessor(context);
        processor.process(serviceContext,board, layermask);
        
        sb.append(context.getOutput());
        return sb.toString();
    }
    private String createHeader(GraphicsStateContext context,int layremask){
        StringBufferExt sb=new StringBufferExt();
        
        AbstractAttribute attribute=new GenerationSoftwareAttribute(VersionUtils.MYNETPCB_NAME, String.valueOf(VersionUtils.MYNETPCB_VERSION));
        sb.append(attribute.print());        
        
        attribute=new FileFunctionAttribute(layremask);        
        sb.append(attribute.print());
        
        attribute=new PartFunctionAttribute();        
        sb.append(attribute.print());
        
        attribute=new CreationDateAttribute();
        sb.append(attribute.print());
        
        sb.append("G04 PCB-Coordinate-Origin: lower left *");
        
        /* Signal Leading zero suppression, Absolute Data, 3.5 format */
        //sb.append("%FSLAX35Y35*%");
        AbstractCommand command=new CoordinateResolutionCommand("35");
        sb.append(command.print());
        
        /* Signal data in mm. */
        //sb.append("%MOMM*%");
        command=new UnitCommand(Grid.Units.MM.getPcbUnit());
        sb.append(command.print());
        
        sb.append("G04 --Define apertures--*");
        sb.append(apertureDictionary.print());
        
        
        return sb.toString();  
    }
    
    


    
    private String createFooter(GraphicsStateContext context){
        StringBufferExt sb=new StringBufferExt();
        sb.append("M02*");
        return sb.toString();  
    }
}
