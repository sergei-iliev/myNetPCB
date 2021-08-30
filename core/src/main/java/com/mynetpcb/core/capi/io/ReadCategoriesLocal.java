package com.mynetpcb.core.capi.io;

import java.io.IOException;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

public class ReadCategoriesLocal extends Command{
    
    private final  Path repositoryRoot;
    private final String library;
    
    public ReadCategoriesLocal(CommandListener monitor,Path repositoryRoot,String library,Class receiver) {
        super(monitor,receiver);
        this.repositoryRoot=repositoryRoot;
        this.library=library;
    }

    @Override
    public Void execute() {
        StringBuffer content=new StringBuffer(); 
        monitor.onStart(receiver);           
        content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><category>");
        if(repositoryRoot!=null&&Files.exists(repositoryRoot, LinkOption.NOFOLLOW_LINKS) ){ 
            
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(repositoryRoot.resolve(library))) {
                for (Path path : directoryStream) {
                    if(Thread.currentThread().isInterrupted()){ 
                       return null;    
                    }
                    if(Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)){
                       content.append("<name library=\""+library+"\" category=\""+path.getFileName()+"\">"+path.getFileName()+"</name>\r\n"); 
                    }else{
                       //files in the main library?
                       content.append("<name fullname=\"" + path.getFileName() + "\"   library=\""+library+"\">"+path.getFileName().toString().substring(0, path.getFileName().toString().lastIndexOf("."))+"</name>\r\n");   
                    }
                }
            } catch (IOException e) {
                    monitor.onError(e.getMessage());
                    return null;
           }  
          content.append("</category>");   
          monitor.onRecive(content.toString(),receiver);    
        }
        monitor.onFinish(receiver);
        return null;
    }

    @Override
    public void cancel() {

    }
}
