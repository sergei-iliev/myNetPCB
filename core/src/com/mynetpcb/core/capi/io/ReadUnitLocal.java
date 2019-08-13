package com.mynetpcb.core.capi.io;


import com.mynetpcb.core.utils.Utilities;

import java.io.BufferedReader;

import java.lang.reflect.InvocationTargetException;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Objects;

import javax.swing.SwingUtilities;


public class ReadUnitLocal extends Command {

    private final String fileName;

    private final String libraryName;
    
    private final String categoryName;        
    
    private final Path repositoryRoot;
    
    
    public ReadUnitLocal(CommandListener monitor,Path repositoryRoot,String libraryName,String categoryName,String fileName,Class receiver) {
      super(monitor,receiver);   
      this.libraryName=libraryName==null?"":libraryName;
      this.categoryName=categoryName==null?"":categoryName;
      this.fileName=Objects.requireNonNull(fileName);
      this.repositoryRoot=repositoryRoot;
    }
    @Override
    public Void execute() {
        if(monitor!=null){
          monitor.OnStart(this.getClass());  
        }
        
        Charset charset=Charset.forName("UTF-8");
        try (BufferedReader reader = Files.newBufferedReader(repositoryRoot.resolve(libraryName).resolve(categoryName).resolve(fileName), charset)) {
            String line;
            String xml="";
            while ((line = reader.readLine()) != null) {
               if(Thread.currentThread().isInterrupted()){ 
                 return null;    
               }
                xml+=line;  
            }
            xml=Utilities.addNode(xml,"filename",fileName);
            xml=Utilities.addNode(xml,"library",libraryName);
            xml=Utilities.addNode(xml,"category",categoryName);                 
            
            final String response=xml; 
            try{   
              SwingUtilities.invokeAndWait(
                  new Runnable(){
                       public void run(){
                           if(monitor!=null){
                             monitor.OnRecive(response,receiver);                               
                           }
                       }                        
                  }                  
                  );  
            }catch(InterruptedException ie){} 
             catch(InvocationTargetException ite) {}    
 
               if(monitor!=null){
                 monitor.OnFinish(receiver);
               }
            
        } catch(Exception e) {
            e.printStackTrace();
            invokeErrorDialog(e.getMessage());
        } 
        return null;
    }
    


    public void cancel() {
       //futuretask's cancel method will invoke interrupt()
    }
}
