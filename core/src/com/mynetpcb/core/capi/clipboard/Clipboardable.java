package com.mynetpcb.core.capi.clipboard;

import java.awt.datatransfer.Transferable;

public interface Clipboardable {
    public enum Clipboard{
        LOCAL,
        SYSTEM    
    }
    
    public  Transferable createClipboardContent();   
    
    public  void realizeClipboardContent(Transferable transferable);
}
