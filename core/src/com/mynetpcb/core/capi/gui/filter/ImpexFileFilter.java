package com.mynetpcb.core.capi.gui.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;


public class ImpexFileFilter extends FileFilter {
    
    private final String ext;
    
    public ImpexFileFilter(String ext) {
      this.ext=ext;
    }

    public boolean accept(File f) {
        if(f.isDirectory())
          return true;
        else      
          return (f.getName().endsWith(ext));
    }

    public String getDescription() {
        return ext;
    }
}
