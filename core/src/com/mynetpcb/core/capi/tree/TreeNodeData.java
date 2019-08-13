package com.mynetpcb.core.capi.tree;


import java.util.UUID;

public class TreeNodeData {

      private UUID uuid;
            
      private String name;
            
      public TreeNodeData(UUID uuid,String name){
        this.uuid=uuid;
        this.name=name;
      }
      public void setName(String name){
          this.name=name;  
      }
      
      public UUID getUUID(){
        return uuid;  
      }
      
      public String getName(){
        return  name; 
      }
      
      public String toString(){
        return name;  
      }
}

