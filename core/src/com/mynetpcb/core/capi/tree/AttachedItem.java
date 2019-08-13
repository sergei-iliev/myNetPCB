package com.mynetpcb.core.capi.tree;

import java.util.UUID;


/*
 * Server both Combo Box and Item List
 * Diffrent data holder for UI elements
 */
public class AttachedItem {
    public static class Builder{
     
     private  String displayName;
     private  UUID uuid;   
     private  String fileName;
     private String library;
     private String category;
     
        public Builder(String displayName){
           this.displayName=displayName; 
        }
        
        public Builder setUUID(UUID uuid){
           this.uuid=uuid; 
           return this;
        }
        public Builder setCategory(String category){
           this.category=category;
           return this;
        }        
        
        public Builder setFileName(String fileName){
           this.fileName=fileName;
           return this;
        }

        public Builder setLibrary(String library){
           this.library=library;
           return this;
        }

        
        public AttachedItem build(){
            AttachedItem item=new AttachedItem();         
            item.displayName=displayName;
            item.uuid=uuid;   
            item.fileName=fileName;
            item.library=library;
            item.category=category;
            return item;
        }
    }
    
    private  String displayName;
    
    private  UUID uuid;           
    
    private String fileName;
    
    private String library;
    
    private String category;
    
    private AttachedItem() {
    }
     
    public UUID getUUID(){
      return uuid;  
    }
    
    public String getFileName(){      
      return fileName;
    }
    
    public String getLibrary(){      
      return library;
    }
    
    public String getCategory(){      
      return category;
    }    
    
    public String toString(){
      return displayName;  
    }
    
    /**
     *Compare file names with or without extension
     * @param s1 never null
     * @param s2 never null 
     * @return
     */
    private boolean compare(String s1,String s2){
      int i=Math.min(s1.length(), s2.length());
      for(int j=0;j<i;j++){
          if(s1.charAt(j)!=s2.charAt(j)){
              return false;
          }
      }
      return true;   
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this==obj){
            return true;
        }
        if(!(obj instanceof AttachedItem)){
            return false;
        }
        AttachedItem that=(AttachedItem)obj;
        if((that.library==null&&this.library!=null)||(that.library!=null&&this.library==null))
          return false;
        if((that.library==null&& this.library==null)){
        }else if(!that.library.equals(this.library))
            return false; 
        
        if((that.category==null&&this.category!=null)||(that.category!=null&&this.category==null))
          return false;
        if((that.category==null&& this.category==null)){
        }else if(!that.category.equals(this.category))
            return false;        
        
        if((that.fileName==null&&this.fileName!=null)||(that.fileName!=null&&this.fileName==null))
          return false;
        if((that.fileName==null&& this.fileName==null)){
        }else 
        /**
         * File name could be with .xml suffix or without it
         */
        if(!compare(that.fileName,this.fileName)){
            return false;  
        }    
        
        if((that.uuid==null&&this.uuid!=null)||(that.uuid!=null&&this.uuid==null))
          return false;
        if((that.uuid==null&& this.uuid==null)){
        }else if(!that.uuid.equals(this.uuid))
            return false; 
        
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash=31;
        hash+=library!=null?library.hashCode():0;
        hash+=category!=null?category.hashCode():0;
        hash+=fileName!=null?fileName.hashCode():0;
        hash+=uuid!=null?uuid.hashCode():0;
        return hash;
    }
}

