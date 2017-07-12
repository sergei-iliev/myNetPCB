package com.mynetpcb.core.capi.config;

import java.io.FilePermission;

import java.nio.file.Path;

import javax.xml.bind.annotation.XmlTransient;

import sun.security.util.SecurityConstants;


@XmlTransient
public abstract class Configuration {
    
    public static final String LIBRARY_FOLDER_NAME="library";
    
    public static final String SYMBOLS_FOLDER_NAME="symbols";
    
    public static final String FOOTPRINTS_FOLDER_NAME="footprints";
    
    public static final String CIRCUITS_FOLDER_NAME="circuits";
    
    public static final String BOARDS_FOLDER_NAME="boards"; 
    
    public static final String WORKSPACE_FOLDER_NAME="workspace"; 
    
    private static Configuration config; 
    
    public static synchronized void Initilize(boolean isapplet){
        if(isapplet){
           config=new RemoteConfig();
        }else{
           config=new LocalConfig();  
        }
    }
    
    public static Configuration get(){
       return config; 
    }

    public abstract void setIsOnline(boolean isOnline);

    public abstract boolean isIsOnline();
    
    public abstract void setLibraryRoot(String libraryRoot);

    public abstract Path getLibraryRoot();
    
    public abstract void setWorkspaceRoot(String workspaceRoot);

    public abstract Path getWorkspaceRoot();
    
    public abstract void setCircuitRoot(String circuitRoot);

    public abstract void setBoardRoot(String boardRoot);
    
    public abstract Path getCircuitsRoot();

    public abstract Path getBoardsRoot();
    
    public abstract Path getFootprintsRoot();
    
    public abstract Path getSymbolsRoot();
    
    public abstract  void setHost(String host);

    public abstract String getHost();

    public abstract  void setPort(int port);


    public abstract int getPort();
        

    public abstract void setIsApplet(boolean isApplet);

    public abstract boolean isIsApplet();
    
    public abstract void read();
    
    public abstract void write();
    
    /*
     * if securoty manager is present then - no access to local files!
     */
    public boolean isLoacalFileAccessAllowed(){ 
        if(System.getSecurityManager()==null){
          return true;  
        }
        try{
            System.getSecurityManager().checkPermission(new FilePermission("*",SecurityConstants.FILE_WRITE_ACTION));  
        }catch(SecurityException e){
            return false;
        }
        return true;
    }
}
