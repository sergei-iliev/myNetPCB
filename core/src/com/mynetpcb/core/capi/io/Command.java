package com.mynetpcb.core.capi.io;


import javax.swing.SwingUtilities;


//***All IO Commands will be executed in a thread
public abstract class Command implements Runnable,Commandable<Void>{    
    
    protected final CommandListener monitor;
    
    protected final Class receiver;
    
    public Command(CommandListener monitor,Class receiver){
      this.monitor=monitor;  
      this.receiver=receiver;
    }
   
    public abstract Void execute();
   
   //*****thread oriented 
    public void run() {
    try{
      execute();
    }catch(Exception e){
        e.printStackTrace();
        invokeErrorDialog(e.getMessage());     
    }
    }
    /**
     * Queue the error message on the GUI thread stack
     */
    protected void invokeErrorDialog(final String error){
        SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    if(monitor!=null)
                       monitor.OnError(error);
                }
            });         
    }
    public abstract void cancel(); 

}
