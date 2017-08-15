package com.mynetpcb.gerber;

import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.Reshapeable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.core.utils.VersionUtils;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.attribute.AbstractAttribute;
import com.mynetpcb.gerber.attribute.drill.DrillFunctionAttribute;
import com.mynetpcb.gerber.attribute.drill.DrillToleranceAttribute;
import com.mynetpcb.gerber.attribute.file.CreationDateAttribute;
import com.mynetpcb.gerber.attribute.file.FileFunctionAttribute;
import com.mynetpcb.gerber.attribute.file.GenerationSoftwareAttribute;
import com.mynetpcb.gerber.attribute.file.PartFunctionAttribute;
import com.mynetpcb.gerber.capi.Gerberable;
import com.mynetpcb.gerber.capi.StringBufferEx;
import com.mynetpcb.gerber.command.AbstractCommand;
import com.mynetpcb.gerber.command.CommandDictionary;
import com.mynetpcb.gerber.command.extended.CoordinateResolutionCommand;
import com.mynetpcb.gerber.command.extended.LevelPolarityCommand;
import com.mynetpcb.gerber.command.extended.StepAndRepeatCommand;
import com.mynetpcb.gerber.command.extended.UnitCommand;
import com.mynetpcb.gerber.processor.ApertureProcessor;
import com.mynetpcb.gerber.processor.CommandProcessor;
import com.mynetpcb.gerber.processor.aperture.ApertureDrillProcessor;
import com.mynetpcb.gerber.processor.command.CommandDrillProcessor;
import com.mynetpcb.gerber.processor.command.GraphicsStateContext;

import java.io.BufferedWriter;

import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Excelon implements Gerberable{
    
    private  ApertureDictionary apertureDictionary;
    private final CommandDictionary commandDictionary;
    private final Unit<? extends Shape> board;
    
    public Excelon(Unit<? extends Shape> board){
        this.apertureDictionary=new ApertureDictionary();
        this.commandDictionary=new CommandDictionary();
        this.board=board;
    }
    
    
    public void build(String fileName,int layermask)throws IOException{
        this.apertureDictionary.Reset();
        ApertureDrillProcessor apertureProcessor=new ApertureDrillProcessor(apertureDictionary);
        apertureProcessor.process(board, layermask) ;
        
        Path gerberFile = Paths.get(fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(gerberFile,
                        StandardCharsets.UTF_8)) {

           GraphicsStateContext context=new GraphicsStateContext(apertureDictionary, commandDictionary, new StringBufferEx());
           
           writer.write(createHeader(context,layermask));
           writer.write(createCommands(context,layermask));
           writer.write(createFooter(context));                    
       } 
       
        
    }
    
    private String createCommands(GraphicsStateContext context,int layermask){
        StringBufferEx sb=new StringBufferEx();
        
        /*Start dark polarity*/
        context.resetPolarity(LevelPolarityCommand.Polarity.DARK);
        
        AbstractCommand stepAndRepeat=new StepAndRepeatCommand();
        sb.append(stepAndRepeat.print());
        
        //set lenear interpolation
        context.resetCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION);
        
        CommandDrillProcessor processor=new CommandDrillProcessor(context);
        processor.process(board, layermask);
        sb.append(context.getOutput());
        
        return sb.toString();
    }
    private String createHeader(GraphicsStateContext context,int layremask){
        StringBufferEx sb=new StringBufferEx();
        
        AbstractAttribute attribute=new GenerationSoftwareAttribute(VersionUtils.MYNETPCB_NAME, String.valueOf(VersionUtils.MYNETPCB_VERSION));
        sb.append(attribute.print());        
        
        attribute=new CreationDateAttribute();
        sb.append(attribute.print());
        
        if(layremask==Layer.NPTH_LAYER_DRILL){
          attribute=new DrillFunctionAttribute(DrillFunctionAttribute.Plate.NonPlated,DrillFunctionAttribute.Type.NPTH);
        }else{
          attribute=new DrillFunctionAttribute(DrillFunctionAttribute.Plate.Plated,DrillFunctionAttribute.Type.PTH);  
        }
        sb.append(attribute.print());
        
        attribute=new PartFunctionAttribute();        
        sb.append(attribute.print());
        
        sb.append("G04 PCB-Coordinate-Origin: lower left *");
        
        AbstractCommand command=new CoordinateResolutionCommand("35");
        sb.append(command.print());
        
        /* Signal data in mm. */
        command=new UnitCommand(Grid.Units.MM.getPcbUnit());
        sb.append(command.print());
    
            
        sb.append("G04 --Define apertures--*");
    
        /*default tolerance */
        attribute =new DrillToleranceAttribute("0.05","0.05");
        sb.append(attribute.print());
        
        sb.append(apertureDictionary.print());
        
        
        return sb.toString();  
    }
    
    private String createFooter(GraphicsStateContext context){
        StringBufferEx sb=new StringBufferEx();
        sb.append("M02*");
        return sb.toString();  
    }
}
