package com.mynetpcb.gerber.command;

import com.mynetpcb.gerber.command.function.FunctionCommand;
import com.mynetpcb.gerber.command.function.SetApertureCodeCommand;

import java.util.HashMap;

public class CommandDictionary extends HashMap<AbstractCommand.Type,AbstractCommand>{


    public CommandDictionary(){
       init(); 
    }
    
    private void init(){
        this.put(AbstractCommand.Type.CLOCKWISE_CICULAR_INTERPOLATION ,new FunctionCommand(AbstractCommand.Type.CLOCKWISE_CICULAR_INTERPOLATION ));
        this.put(AbstractCommand.Type.COUNTER_CLOCKWISE_CIRCULAR_INTERPOLATION  ,new FunctionCommand(AbstractCommand.Type.COUNTER_CLOCKWISE_CIRCULAR_INTERPOLATION ));
        this.put(AbstractCommand.Type.INTERPOLATE_OPERATION  ,new FunctionCommand(AbstractCommand.Type.INTERPOLATE_OPERATION));
        this.put(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION ,new FunctionCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION));
        this.put(AbstractCommand.Type.SET_CURRENT_APERTURE, new SetApertureCodeCommand());
        this.put(AbstractCommand.Type.MULTI_QUADRENT_MODE, new FunctionCommand(AbstractCommand.Type.MULTI_QUADRENT_MODE));
        this.put(AbstractCommand.Type.COMMENTS, new FunctionCommand(AbstractCommand.Type.COMMENTS));
        this.put(AbstractCommand.Type.SINGLE_QUADRENT_MODE, new FunctionCommand(AbstractCommand.Type.SINGLE_QUADRENT_MODE));
        this.put(AbstractCommand.Type.REGION_MODE_ON, new FunctionCommand(AbstractCommand.Type.REGION_MODE_ON));
        this.put(AbstractCommand.Type.REGION_MODE_OFF, new FunctionCommand(AbstractCommand.Type.REGION_MODE_OFF));
    }

    public <T extends AbstractCommand> T get(AbstractCommand.Type id,Class<T> clazz){
        Object o=this.get(id);
        if(o==null){
            throw new IllegalStateException("Unknown type:"+id);
        }
        return clazz.cast(o);
    }

}
