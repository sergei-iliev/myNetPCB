package com.mynetpcb.core.capi;


import com.mynetpcb.core.capi.component.UnitComponent;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;

import java.lang.ref.WeakReference;


public class UnitComponentKeyboardListener implements KeyEventDispatcher{
    
    private WeakReference<UnitComponent> componentRef;
       
    public void setComponent(UnitComponent component){
        if(this.componentRef!=null&&this.componentRef.get()!=null){
            this.componentRef.clear();  
        }
        this.componentRef=new WeakReference<UnitComponent>(component);            
    }
        
    public boolean dispatchKeyEvent(KeyEvent e) {
        if(componentRef!=null&&componentRef.get()!=null){
            if(componentRef.get().processKeyPress(e)){  
             return true;
            }
        }    
        return false;
    }
}

