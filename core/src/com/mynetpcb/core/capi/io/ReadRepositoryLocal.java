package com.mynetpcb.core.capi.io;

import java.io.IOException;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;


public class ReadRepositoryLocal extends Command {

  private final  Path repositoryRoot;
  
    public ReadRepositoryLocal(CommandListener monitor,Path repositoryRoot,Class receiver) {
      super(monitor,receiver);
      this.repositoryRoot=repositoryRoot;      
    }
    @Override
    public Void execute() {
     StringBuffer content=new StringBuffer(); 
     monitor.OnStart(receiver);   
     content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><library>");
     if(repositoryRoot!=null&&Files.exists(repositoryRoot, LinkOption.NOFOLLOW_LINKS) ){ 
         
         try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(repositoryRoot)) {
             for (Path path : directoryStream) {
                 if(Thread.currentThread().isInterrupted()){ 
                    return null;    
                 }
                 if(Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)){
                    content.append("<name>"+path.getFileName()+"</name>"); 
                 }   
             }
         } catch (IOException e) {
                 monitor.OnError(e.getMessage());
                 return null;
        }  
       content.append("</library>");   
       monitor.OnRecive(content.toString(),receiver);    
     }
     monitor.OnFinish(receiver);
     return null;
    }


    public void cancel() {

    }
}
