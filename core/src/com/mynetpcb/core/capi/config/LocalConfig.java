package com.mynetpcb.core.capi.config;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.URI;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


@XmlRootElement(name = "localconfig")
    @XmlAccessorType(XmlAccessType.FIELD)
    public  class LocalConfig extends Configuration{
        
        private static final String CONFIG_FILE_NAME="configuration.xml";

        private URI libraryRoot;
        
        private URI workspaceRoot;
        
        @XmlTransient
        private URI circuitRoot;
        
        @XmlTransient
        private URI boardRoot;
    
        @XmlTransient
        private URI symbolsRoot;
        
        @XmlTransient
        private URI footprintsRoot;
        
        private String host="www.bitslib.net";
        
        private int port=80;
        
        @XmlTransient
        private boolean isApplet=false;
        
        private boolean isOnline=false;

        public LocalConfig(){
                                 
            Class clazz = this.getClass();
            File root = new File(clazz.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile().getParentFile();
            //File r=new File("C:\\sergei\\java\\myNetPCB\\deploy");
            
            File workspace =new File(root,this.WORKSPACE_FOLDER_NAME); 
            workspaceRoot=workspace.toURI();
            
            circuitRoot=new File(workspace,this.CIRCUITS_FOLDER_NAME).toURI();      
            
            boardRoot=new File(workspace,this.BOARDS_FOLDER_NAME).toURI(); 
            
            File library=new File(root,this.LIBRARY_FOLDER_NAME);  
            libraryRoot  =library.toURI();

            symbolsRoot  =new File(library,this.SYMBOLS_FOLDER_NAME).toURI();
            
            footprintsRoot=new File(library,this.FOOTPRINTS_FOLDER_NAME).toURI();            
        }
        
        public void setIsOnline(boolean isOnline) {
            this.isOnline = isOnline;
        }

        public boolean isIsOnline() {
            return isOnline;
        }

    
        @Override
        public  void setWorkspaceRoot(String workspaceRoot){
         File file=new File(workspaceRoot); 
         this.workspaceRoot = file.toURI();     
        }
        public void setLibraryRoot(String libraryRoot) {
            File file=new File(libraryRoot); 
            this.libraryRoot = file.toURI();
        }
        public void setCircuitRoot(String circuitRoot) {
            File file=new File(circuitRoot);         
            this.circuitRoot = file.toURI();
        }
        public void setBoardRoot(String boardRoot) {
            File file=new File(boardRoot);         
            this.boardRoot = file.toURI();
        }
        public void setHost(String host) {
            this.host = host;
        }

        public String getHost() {
            return host;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public int getPort() {
            return port;
        }

        public void setIsApplet(boolean isApplet) {
            this.isApplet = isApplet;
        }

        public boolean isIsApplet() {
            return isApplet||isOnline;
        }
        
        public void read(){
                try{
                          File file = new File(CONFIG_FILE_NAME);
                          JAXBContext context = JAXBContext.newInstance(LocalConfig.class); 
                          Unmarshaller u=context.createUnmarshaller();
                          if (file.exists()) {
                           LocalConfig that=(LocalConfig)u.unmarshal(file);
                           this.libraryRoot=that.libraryRoot;
                           this.symbolsRoot=this.libraryRoot.resolve(SYMBOLS_FOLDER_NAME);
                           this.footprintsRoot=this.libraryRoot.resolve(FOOTPRINTS_FOLDER_NAME);
                           
                           this.workspaceRoot=that.workspaceRoot;
                           this.circuitRoot= this.workspaceRoot.resolve(CIRCUITS_FOLDER_NAME);
                           this.boardRoot= this.workspaceRoot.resolve(BOARDS_FOLDER_NAME);
                           
                           this.port=that.port;
                           this.host=that.host;
                           this.isOnline=that.isOnline;
                          }      
                    
                }catch(JAXBException e){
                          throw new IllegalStateException(e);   
                }
        }   
        
        public void write(){
            try
            {
                FileOutputStream fos;
                fos = new FileOutputStream(new File(CONFIG_FILE_NAME));
                JAXBContext c = JAXBContext.newInstance(LocalConfig.class); 
                Marshaller marshaller = c.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.marshal(this, fos);
                fos.flush();
            }
            catch(JAXBException ex)
            {
                throw new RuntimeException(ex);
            }
            catch(FileNotFoundException ex){
                throw new RuntimeException(ex);
            }        
            catch(IOException ex)
            {
                throw new RuntimeException(ex);
            }
        }

    @Override
    public  Path getWorkspaceRoot(){
      return Paths.get(workspaceRoot);  
    }
    
    @Override
    public Path getLibraryRoot() {
        return Paths.get(libraryRoot);
    }

    @Override
    public Path getCircuitsRoot() {
      return Paths.get(circuitRoot);
    }
    
    @Override
    public Path getBoardsRoot() {
      return Paths.get(boardRoot);
    }
    
    @Override
    public Path getFootprintsRoot() {
        return Paths.get(footprintsRoot);
    }
    
    @Override
    public Path getSymbolsRoot() {
        return Paths.get(symbolsRoot);
    }
}
