package com.mynetpcb.core.capi.io;


import com.mynetpcb.core.utils.concurrent.OwnThreadPoolExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


public enum CommandExecutor{
    INSTANCE;
    
    private final  ExecutorService executor;
    
    private final Map<String,FutureCommand> commands;
    
    private CommandExecutor(){
        executor=  new OwnThreadPoolExecutor(1,1,
                                      0L, TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>());        
        commands=new HashMap<String,FutureCommand>(3); 
    }
    
    public void addTask(String taskName,Command task){
        FutureCommand future=new FutureCommand(task);
        commands.put(taskName,future);
        executor.execute(future);
    }
    
    public void addTask(String taskName,CommandResult task){
        FutureCommand future=new FutureCommand(task);
        commands.put(taskName,future);
        executor.execute(future);
    }
    /**
     *Fire and wait method.
     * @param task
     * @return
     */
    public Future<?> submitTask(CommandResult task){
        return executor.submit(task);
    }
    
    public FutureCommand getTaskByName(String taskName){
       return commands.get(taskName);                         
    }
    
    public void cancel(){
        for(FutureCommand command:commands.values()){
          command.cancel();  
        }        
        commands.clear();
    }
}
