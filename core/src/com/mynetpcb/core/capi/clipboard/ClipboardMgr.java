package com.mynetpcb.core.capi.clipboard;


import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import java.security.AccessControlException;


public class ClipboardMgr{
    private static ClipboardMgr clipboardMgr;
    
    private final Clipboard clipboard;
    
    public static synchronized ClipboardMgr getInstance() {
        if (clipboardMgr == null) {
            clipboardMgr = new ClipboardMgr();
        }
        return clipboardMgr;
    }

    private ClipboardMgr() {
      clipboard=new Clipboard("myNetPCB");
    }
    
    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }
    
    public void setClipboardContent(Clipboardable.Clipboard type,Transferable transferable){
        switch(type){
        case LOCAL:
            clipboard.setContents(transferable, null); 
            break;
        //case SYSTEM:
        //    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable,null);
        }
    }    

    public Transferable getClipboardContent(Clipboardable.Clipboard type)throws AccessControlException{
        switch(type){
        case LOCAL:
            return clipboard.getContents(this);
        //case SYSTEM:
        //    return Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        }        
        throw new IllegalStateException("Unknown clipboard type");
    }
    
    public boolean isTransferDataAvailable(Clipboardable.Clipboard type){
        try{
            Transferable transferable=getClipboardContent(type);        
            if(transferable==null) 
                return false;        
            else
                return transferable.isDataFlavorSupported(DataFlavor.stringFlavor);       
        }catch(AccessControlException e){
           return false; 
        }
    }
    
    
    
}

