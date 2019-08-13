package com.mynetpcb.core.capi.io;


import com.mynetpcb.core.utils.Utilities;

import java.io.File;
import java.io.IOException;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

public class SearchUnitLocal extends Command{
    private final String rootPath;
    
    private final SearchLookup searchLookup;
    
    public SearchUnitLocal(CommandListener monitor,SearchLookup searchLookup,String rootPath,Class receiver) {
      super(monitor,receiver);
      this.rootPath=rootPath;   
      this.searchLookup=searchLookup;
    }

    @Override
    public Void execute() {               
        monitor.OnStart(receiver);
        
        final StringBuffer result = new StringBuffer();
        File allFiles[] = Utilities.getFileDirOrder(new File(rootPath));
        try{
        result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<module>\r\n");    
        for (File aFile : allFiles) {
            if (Thread.currentThread().isInterrupted()) {
               return null;
            }
             
            if(aFile.isDirectory()){
               loopFolder(aFile, result);
            }
        }     
        result.append("</module>");    
        }catch(IOException e){
            e.printStackTrace();
        }
        
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                   if(monitor!=null) 
                     monitor.OnRecive(result.toString(), receiver);
                }
            });
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();  //preserve the interrupt flag
            return null;
        } catch (InvocationTargetException ite) {
            return null;
        }
        
        monitor.OnFinish(receiver);      
        return null;
    }

    private void loopFolder(File libraryPath, StringBuffer result)throws IOException{
        File allFiles[] = Utilities.getFileDirOrder(libraryPath);
        for (File aFile : allFiles){
         if (Thread.currentThread().isInterrupted()) {
            return;
         }   
        if(aFile.isFile()&&!aFile.getName().toLowerCase().endsWith(".xml"))
          continue;    
        
            if(aFile.isDirectory()){
                loopSubFolder(aFile, result);    
            }else{
             if (searchLookup.process(aFile)) {                
                 result.append("<name fullname=\"" + aFile.getName() + "\"  library=\"" + aFile.getParentFile().getName() + "\">" + aFile.getName().substring(0, aFile.getName().lastIndexOf(".")) + "</name>\r\n");
             }
            }                       
        }     
    }
    private void loopSubFolder(File subLibraryPath, StringBuffer result) throws IOException {
        File allFiles[] = subLibraryPath.listFiles();
        for (File aFile : allFiles) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            
            if(aFile.isFile()&&!aFile.getName().toLowerCase().endsWith(".xml"))
               continue;
            
            if(aFile.isDirectory()){
                
            }else{
             if (searchLookup.process(aFile)) {                
                 result.append("<name fullname=\"" + aFile.getName() + "\"  library=\""+subLibraryPath.getParentFile().getName()+"\" category=\"" + subLibraryPath.getName() +
                               "\">" + aFile.getName().substring(0, aFile.getName().lastIndexOf(".")) + "</name>\r\n");                
             }
            }    
        }

    }
    
    @Override
    public void cancel() {
     // listener.cancel
    }
}

