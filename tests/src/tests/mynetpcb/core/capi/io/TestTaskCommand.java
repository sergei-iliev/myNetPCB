package tests.mynetpcb.core.capi.io;


import com.mynetpcb.core.capi.io.Command;
import com.mynetpcb.core.capi.io.CommandExecutor;
import com.mynetpcb.core.capi.io.CommandListener;
import com.mynetpcb.core.capi.io.CommandResult;
import com.mynetpcb.core.capi.io.FutureCommand;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;


public class TestTaskCommand {

@Test
public void testCommandOutsideThread(){
    final Integer a=10,b=23;
    final AtomicInteger c=new AtomicInteger();
    
    Command command=new Command(null,null){

            @Override
            public Void execute() {
              c.set(a+b); 
              return null;
            }

            @Override
            public void cancel() {
            
            }
        };
    
    command.execute();
    Assert.assertTrue(c.get()==33);
    
    command.run();
    Assert.assertTrue(c.get()==33);
}
    final static  String HELLO="HELLO FROM ME";
    @Test
    public void testCommandInsideThread() throws Exception{
        
        CommandListener listner=new CommandListener(){

            @Override
            public void OnStart(Class<?> class1) {
            }

            @Override
            public void OnRecive(String result, Class class1) {
              Assert.assertTrue(result.equals(HELLO));
            }

            @Override
            public void OnFinish(Class<?> class1) {
            }

            @Override
            public void OnError(String string) {
            }
        };
        Command command=new Command(listner,null){

                @Override
                public Void execute() {
                  monitor.OnRecive(HELLO, null);
                  return null;
                }

                @Override
                public void cancel() {
                
                }
            }; 
        
        ExecutorService service= Executors.newSingleThreadExecutor();
        Future<?> task=service.submit(command);
        task.get();
        
    }
    
    
    @Test
    public void testCommandResultOutsideThread(){
        final String a="HELLO";
        final String b=" FROM ME";
        
        CommandResult command=new CommandResult(null,null){

            @Override
            public String execute() {
                return a+b;
            }

            @Override
            public void cancel() {
            }
        };
           
       Assert.assertTrue(command.execute().equals(HELLO));   
           
    }
    
    @Test
    public void testCommandResultInsideThread() throws Exception{
        
        CommandListener listener=Mockito.mock(CommandListener.class);
        
        CommandResult command=new CommandResult(listener,null){

                @Override
                public String execute() {
                  monitor.OnRecive(HELLO,null);
                  return HELLO;
                }

                @Override
                public void cancel() {
                
                }
            }; 
        
        ExecutorService service= Executors.newSingleThreadExecutor();
        Future<String> task=service.submit(command);
        String result=task.get();
        Assert.assertTrue(result.equals(HELLO));
        
        Mockito.verify(listener).OnRecive(HELLO,null);
    }
    
    @Test
    public void testCommandExcutor()throws Exception{
        final int a=34,b=12;
        final AtomicInteger c=new AtomicInteger();
        Command command=new Command(null,null){

            @Override
            public Void execute(){
                //simulate long camputation
                try {
                    Thread.currentThread().sleep(2000);
                } catch (InterruptedException e) {
                }
                c.set(a+b);
                return null;
            }

            @Override
            public void cancel() {
            }
        };
        CommandExecutor.INSTANCE.addTask("test",command);
        FutureTask task=CommandExecutor.INSTANCE.getTaskByName("test");
        task.get();
        Assert.assertTrue(c.get()==46);
        
        task=CommandExecutor.INSTANCE.getTaskByName("test");
        task.get();
        Assert.assertTrue(c.get()==46); 
        
        CommandExecutor.INSTANCE.cancel();
        task=CommandExecutor.INSTANCE.getTaskByName("test");
        Assert.assertNull(task);
        
    }
    
    @Test
    public void testCommandResultExcutor()throws Exception{
        CommandListener listener=Mockito.mock(CommandListener.class);
        
        CommandResult<String> command=new CommandResult<String>(listener,null){

                @Override
                public String execute() {
                  monitor.OnRecive(HELLO,null);
                  return HELLO;
                }

                @Override
                public void cancel() {
                
                }
            };  
        Future<String> task= (Future<String>)CommandExecutor.INSTANCE.submitTask(command);
        String result=task.get();
        Assert.assertTrue(result.equals(HELLO));
    }
    @Test(expected = IllegalStateException.class)
    public void testCommandCancel()throws InterruptedException{
        final int a=34,b=12;
        final AtomicInteger c=new AtomicInteger();
        final AtomicBoolean flag=new AtomicBoolean(false);
        Command command=new Command(null,null){

            @Override
            public Void execute(){
                //simulate long camputation
                flag.getAndSet(true);
                try {
                    Thread.currentThread().sleep(2000);
                } catch (InterruptedException e) {
                   Thread.currentThread().interrupt();
                }
                c.set(a+b);
                return null;
            }

            @Override
            public void cancel() {
               Thread.currentThread().interrupt(); 
               throw new IllegalStateException("Cancel resources"); 
            }
        };
        
        CommandExecutor.INSTANCE.addTask("test",command);
        FutureCommand task=CommandExecutor.INSTANCE.getTaskByName("test");
        //wait until task is running
        while(!flag.get()){}
          task.cancel();
        try{
          task.get();
        }catch(ExecutionException e){
            Assert.assertTrue(2==3);
        }
        catch(InterruptedException e){
            Assert.assertTrue(2==3);
        }
        
    }
    
}
