package com.mynetpcb.core.capi.io;


import java.io.BufferedWriter;
import java.io.IOException;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import java.util.Objects;

import javax.swing.SwingUtilities;


public class WriteUnitLocal extends Command {

    private  String fileName;

    private  String categoryName;

    private  String libraryName;

    private  Path repositoryRoot;

    private  StringBuffer content;

    private boolean override;

    public WriteUnitLocal(CommandListener monitor, StringBuffer content, Path repositoryRoot, String libraryName,
                          String categoryName, String fileName, boolean override, Class receiver) {
        super(monitor,receiver);
        Objects.requireNonNull(fileName);
        if(fileName!=null){
         if (fileName.toLowerCase().endsWith(".xml")) {
            this.fileName = fileName;
         } else {
            this.fileName = fileName+".xml";
         }
        }
        
        this.libraryName=(libraryName==null?"":libraryName);
        this.categoryName=categoryName==null?"":categoryName;
        this.repositoryRoot=repositoryRoot;
        this.override = override;
        this.content = content;
    }
    @Override
    public Void execute() {
        try {
            monitor.onStart(receiver);

            Path category=repositoryRoot.resolve(libraryName).resolve(categoryName);
            if(!Files.exists(category, LinkOption.NOFOLLOW_LINKS)){
               category=Files.createDirectories(category);
            }else{
                //check if file exist and override flag is false
                final Path file=category.resolve(fileName);
                if(Files.exists(file)&&!this.override){
                   this.invokeErrorDialog("File '"+file.toString()+"' exists.\r\nCheck the override flag to override file.");
                   return null;
                }
            }
            
            Charset charset = Charset.forName("UTF-8");
            try (BufferedWriter writer = Files.newBufferedWriter(category.resolve(fileName), charset)) {
                 writer.write(content.toString());
            } 
            /*
             * The dialog is closed in call chain of OnFinish.
             */
            SwingUtilities.invokeLater(new Runnable(){
                    @Override
                    public void run() {
                        monitor.onFinish(receiver);  
                    }
                });
        } catch (IOException e) {
            e.printStackTrace(System.out);
            this.invokeErrorDialog(e.toString());
        } 
        return null;
    }

    public void cancel() {
    }

}
