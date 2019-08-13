package com.mynetpcb.core.capi.io.search;


import com.mynetpcb.core.capi.io.SearchLookup;

import java.io.File;
import java.io.IOException;


public class FileNameLookup extends SearchLookup {                        
    
    public FileNameLookup(SearchLookup searchLookup,String term) {
      super(searchLookup,term);      
    }
    public FileNameLookup(String term) {
      this(null,term);
    }
    
    @Override
    protected boolean search(File file,String term) throws IOException{
        if(file.isFile()){       
            int pos = file.getName().lastIndexOf(".");
            String name = pos > 0 ? file.getName().substring(0, pos) : file.getName();
            if(name.toLowerCase().indexOf(term)>-1)
                return true;
            else        
                return false;            
        }else{
            //directory       
                return false;
        }    
    }
}

