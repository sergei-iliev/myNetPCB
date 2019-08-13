package com.mynetpcb.core.capi.config;

import java.nio.file.Path;


public class RemoteConfig extends Configuration{
    private String host="www.bitslib.net";
    
    private int port=80;

    private boolean isApplet=true;
    
    private boolean isOnline=true;
    
    public RemoteConfig() {
    }

    public void setIsOnline(boolean isOnline) {
    }

    public boolean isIsOnline() {
        return true;
    }

    public void setLibraryRoot(String libraryRoot) {
    }



    public void setCircuitRoot(String circuitRoot) {
    }

    public void read() {
 
    }

    public void write() {
    
    }

    @Override
    public Path getLibraryRoot() {
        return null;
    }

    @Override
    public void setWorkspaceRoot(String workspaceRoot) {

    }

    @Override
    public Path getWorkspaceRoot() {
        return null;
    }

    @Override
    public Path getCircuitsRoot() {
        return null;
    }

    @Override
    public Path getFootprintsRoot() {
        return null;
    }

    @Override
    public Path getSymbolsRoot() {
        return null;
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

    @Override
    public void setBoardRoot(String boardRoot) {
    }

    @Override
    public Path getBoardsRoot() {
        return null;
    }
}
