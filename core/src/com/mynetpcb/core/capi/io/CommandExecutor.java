package com.mynetpcb.core.capi.io;




import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public enum CommandExecutor{
    INSTANCE;
    
    private final  ExecutorService executor;
    
    private final Map<String,FutureCommand> commands;
    
    private CommandExecutor(){
        executor=  Executors.newFixedThreadPool(1);      
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
