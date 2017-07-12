package com.mynetpcb.gerber.processor.command;

import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.aperture.type.RectangleAperture;
import com.mynetpcb.gerber.capi.StringBufferEx;
import com.mynetpcb.gerber.command.AbstractCommand;
import com.mynetpcb.gerber.command.CommandDictionary;
import com.mynetpcb.gerber.command.extended.LevelPolarityCommand;
import com.mynetpcb.gerber.command.function.FunctionCommand;

import com.mynetpcb.gerber.command.function.SetApertureCodeCommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/*
 * Command rendering context
 */
public final class GraphicsStateContext {
    
    private final ApertureDictionary apertureDictionary;
    private final CommandDictionary commandDictionary;
    private ApertureDefinition currentAperture;
    private FunctionCommand interpolationCommand;
    private final LevelPolarityCommand levelPolarityCommand;
    private final NumberFormat formatter;
    private final StringBufferEx sb;
    private final SetApertureCodeCommand currentApertureCommand;
    
    public GraphicsStateContext(ApertureDictionary apertureDictionary, CommandDictionary commandDictionary,StringBufferEx sb){
        this.apertureDictionary = apertureDictionary;
        this.commandDictionary = commandDictionary;
        this.currentAperture=new RectangleAperture();
        this.levelPolarityCommand=new LevelPolarityCommand();
        this.levelPolarityCommand.setPolarity(LevelPolarityCommand.Polarity.CLEAR);
        //default comments
        this.interpolationCommand=commandDictionary.get(AbstractCommand.Type.COMMENTS,FunctionCommand.class);
        this.formatter = new DecimalFormat("########"); 
        this.sb=sb;
        this.currentApertureCommand =this.commandDictionary.get(AbstractCommand.Type.SET_CURRENT_APERTURE, SetApertureCodeCommand.class);
    }


    public StringBufferEx getOutput(){
      return sb;  
    }
    public CommandDictionary getCommandDictionary(){
        return commandDictionary;        
    }


    public ApertureDictionary getApertureDictionary() {
        return apertureDictionary;
    }

    public ApertureDefinition getCurrentAperture() {
        return currentAperture;
    }

    public void setCurrentAperture(ApertureDefinition current) {
        currentAperture=current;
    }
    
    public FunctionCommand getInterpolationCommand() {
        return interpolationCommand;
    }

    public void setInterpolationCommand(FunctionCommand interpolationCommand) {        
        this.interpolationCommand=interpolationCommand;
    }
    
    public void resetPolarity(LevelPolarityCommand.Polarity polarity){
        if(levelPolarityCommand.getPolarity()!=polarity){
            levelPolarityCommand.setPolarity(polarity);
            sb.append(levelPolarityCommand.print());
        }
    }
    /*
     * Change and print command if different from current one
     */
    public void resetCommand(AbstractCommand.Type type){
        if (interpolationCommand.getType() != type) {
            interpolationCommand=this.getCommandDictionary()
                                            .get(type,FunctionCommand.class);
            sb.append(this.interpolationCommand.print());
        }
    }
    
    /*
     * Change and print apperture if different from current one
     */    
    public void resetAperture(ApertureDefinition aperture){
        if (!currentAperture.equals(aperture)) {
            currentAperture=aperture;
            currentApertureCommand.setDCode(currentAperture.getCode());
            sb.append(currentApertureCommand.print());
        }    
    }
    
    public NumberFormat getFormatter() {
        return formatter;
    }

}
