package com.mynetpcb.core.capi.io;


import com.mynetpcb.core.capi.verification.VerificationException;
import com.mynetpcb.core.capi.verification.Verifier;
import com.mynetpcb.core.capi.verification.VerifierFactory;

import java.io.IOException;

import java.lang.reflect.InvocationTargetException;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import javax.swing.SwingUtilities;


/****Read assets under specific library,project(folder),category folder
1.Modules
2.Circuits
3.Footprints
 */
public class ReadUnitsLocal extends Command {
    private final Path libraryPath;

    private final String tagvalue;

    public ReadUnitsLocal(CommandListener monitor, Path libraryPath, String tagvalue, Class receiver) {
        super(monitor,receiver);
        this.libraryPath = libraryPath;
        this.tagvalue = tagvalue;

    }
    @Override 
    public Void execute() {
        monitor.OnStart(receiver);

        final StringBuffer result = new StringBuffer();
        result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><units>\r\n");

        //***iterate through folders - library names - construct xml
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(libraryPath)) {
            for (Path entry: stream) {
                if (Thread.currentThread().isInterrupted()) {
                  return null;
                }
                if(!Files.isDirectory(entry, LinkOption.NOFOLLOW_LINKS)){
                    //***file
                    Verifier v = VerifierFactory.createXMLRootTagVerifier(entry.toFile(), tagvalue);
                    if (v.check()) {
                        result.append("<name fullname=\"" + entry.getFileName() + "\" category=\"" + entry.getParent().getFileName() +"\"  library=\"" + entry.getParent().getParent().getFileName() + "\">" + entry.getFileName().toString().substring(0, entry.getFileName().toString().lastIndexOf(".")) + "</name>\r\n");
                    }                    
                }
            }
        } catch (IOException e) {
             monitor.OnError(e.getMessage());
             return null;
        }catch(VerificationException e){
              monitor.OnError(e.getMessage());
              return null;    
        }
        result.append("</units>");
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    monitor.OnRecive(result.toString(), receiver);
                }
            });
            
            monitor.OnFinish(receiver);    
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException ite) {
        }
        return null;
    }

//    private void loopSubFolder(Path subLibraryPath, StringBuffer result) throws IOException,VerificationException {
//        PathMatcher matcher =
//            FileSystems.getDefault().getPathMatcher("glob:*.{xml}");
//        //create category even thogh may be empty
//            result.append("<name fullname=\"\"  library=\""+subLibraryPath.getParent().getFileName()+"\" category=\"" + subLibraryPath.getFileName() +
//                      "\"></name>\r\n");
//
//        try (DirectoryStream<Path> stream = Files.newDirectoryStream(subLibraryPath)) {
//            for (Path entry: stream) {
//                if(Files.isDirectory(entry, LinkOption.NOFOLLOW_LINKS)||(!matcher.matches(entry.getFileName()))){
//                   continue;
//                }else{
//                    Verifier v = VerifierFactory.createXMLRootTagVerifier(entry.toFile(), tagvalue);
//                    if (v.check()) {
//                        result.append("<name fullname=\"" + entry.getFileName() + "\"  library=\""+subLibraryPath.getParent().getFileName()+"\" category=\"" + subLibraryPath.getFileName() +
//                                      "\">" + entry.getFileName().toString().substring(0, entry.getFileName().toString().lastIndexOf(".")) + "</name>\r\n");
//                    }                    
//                }
//            }
//        }
//    }

    public void cancel() {
    }
}
