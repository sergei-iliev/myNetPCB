package com.mynetpcb.core.pad;


/**
 * Enable composition for Symbol and SCHSymbol to avoid code cluter
 */
public class Packaging{
    
    private String footprintLibrary;
    private String footprintCategory;
    private String footprintFileName;
    private String footprintName;

    public void copy(Packaging packaging){
        this.footprintLibrary=packaging.footprintLibrary;
        this.footprintCategory=packaging.footprintCategory;
        this.footprintFileName=packaging.footprintFileName;
        this.footprintName=packaging.footprintName;
    }
    
    public String getFootprintLibrary() {
        return footprintLibrary;
    }

    public String getFootprintCategory() {
        return footprintCategory;
    }

    public String getFootprintFileName() {
        return footprintFileName;
    }

    public String getFootprintName() {
        return footprintName;
    }
    
    public void setFootprintLibrary(String name){
        this.footprintLibrary=(name==""?null:name);
    }
    
    public void setFootprintCategory(String name){
       this.footprintCategory=(name==""?null:name); 
    }
    
    public void setFootprintFileName(String name){
       this.footprintFileName=(name==""?null:name); 
    }
    
    public void setFootprintName(String name){
      this.footprintName=(name==""?null:name);
    }
    
    @Override
    public String toString() {
        StringBuffer sb=new StringBuffer();
        sb.append(footprintLibrary+":");
        sb.append(footprintCategory+":");
        sb.append(footprintFileName+":");
        sb.append(footprintName);
        return sb.toString();
    }
    
}
