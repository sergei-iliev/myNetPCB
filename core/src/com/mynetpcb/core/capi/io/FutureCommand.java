package com.mynetpcb.core.capi.io;


import java.util.concurrent.FutureTask;


public class FutureCommand extends FutureTask{
    
    private final Commandable command;
    

    public FutureCommand(Command command) {
       super(command,null);
       this.command=command;
    }
    public FutureCommand(CommandResult command) {
       super(command);
       this.command=command;
    }
    public boolean cancel() {
         //***set interrupted flag first
         super.cancel(true);
         
         //***cancel socket communication
         if(command!=null){
           command.cancel();
         }             
         return true;
    }      
} 
