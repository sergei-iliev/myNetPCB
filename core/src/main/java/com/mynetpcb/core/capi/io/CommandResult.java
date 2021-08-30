package com.mynetpcb.core.capi.io;

import java.util.concurrent.Callable;

import javax.swing.SwingUtilities;


public abstract class CommandResult<V> implements Callable<V>,Commandable{
    protected final CommandListener monitor;
    
    protected final Class receiver;
    
    public CommandResult(CommandListener monitor,Class receiver){
      this.monitor=monitor;  
      this.receiver=receiver;
    }

    @Override
    public V call() {
        return execute();
    }
    /**
     * Queue the error message on the GUI thread stack
     */
    protected void invokeErrorDialog(final String error){
        SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    if(monitor!=null)
                       monitor.onError(error);
                }
            });         
    }
    public abstract V execute();

    public abstract void cancel(); 
}
