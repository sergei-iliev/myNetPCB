package com.mynetpcb.core.capi.io;

import java.io.File;
import java.io.IOException;

/**
 * Chain of responsibility pattern for IO lookup
 */
public abstract class SearchLookup {
    private final SearchLookup next;
    private final String term;
    
    public SearchLookup(SearchLookup next,String term){
      this.next=next;  
      this.term=term.toLowerCase();
    }
    
    public boolean process(File file)throws IOException{
        if(this.search(file,term)){
          return true;  
        }else{
          return next!=null?next.search(file,term):false;   
        }
    }
    
    protected abstract boolean search(File file,String term)throws IOException;
}
