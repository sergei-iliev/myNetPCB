package com.mynetpcb.gerber;

import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.utils.VersionUtils;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.attribute.AbstractAttribute;
import com.mynetpcb.gerber.attribute.file.CreationDateAttribute;
import com.mynetpcb.gerber.attribute.file.GenerationSoftwareAttribute;
import com.mynetpcb.gerber.capi.Gerberable;
import com.mynetpcb.gerber.capi.StringBufferEx;
import com.mynetpcb.gerber.command.AbstractCommand;
import com.mynetpcb.gerber.command.CommandDictionary;
import com.mynetpcb.gerber.command.extended.CoordinateResolutionCommand;
import com.mynetpcb.gerber.command.extended.UnitCommand;
import com.mynetpcb.gerber.processor.aperture.ApertureCutOutProcessor;
import com.mynetpcb.gerber.processor.command.CommandCutOutProcessor;
import com.mynetpcb.gerber.processor.command.GraphicsStateContext;

import java.io.BufferedWriter;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CutOuts implements Gerberable{
    
    private  ApertureDictionary apertureDictionary;
    private final CommandDictionary commandDictionary;
    private final Board board;
    
    public CutOuts(Board board){
        this.apertureDictionary=new ApertureDictionary();
        this.commandDictionary=new CommandDictionary();
        this.board=board;
    }
    
    
    public void build(String fileName,int layermask)throws IOException{
        this.apertureDictionary.Reset();
        ApertureCutOutProcessor apertureProcessor=new ApertureCutOutProcessor(apertureDictionary);
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
        
        CommandCutOutProcessor processor=new CommandCutOutProcessor(context);
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
        
        
        sb.append("G04 Layer name :KeepOutLayer*");
        sb.append("G04 PCB-Coordinate-Origin: lower left *");
        
        AbstractCommand command=new CoordinateResolutionCommand("35");
        sb.append(command.print());
        
        /* Signal data in mm. */
        command=new UnitCommand(Grid.Units.MM.getPcbUnit());
        sb.append(command.print());
    
            
        sb.append("G04 --Define apertures--*");
        
        sb.append(apertureDictionary.print());
        
        
        return sb.toString();  
    }
    
    private String createFooter(GraphicsStateContext context){
        StringBufferEx sb=new StringBufferEx();
        sb.append("M02*");
        return sb.toString();  
    }
}
