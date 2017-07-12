package com.mynetpcb.gerber.capi;


public class StringBufferEx{
    
    private final StringBuffer sb;
    
    public StringBufferEx() {
        this.sb=new StringBuffer();
    }
    
    public void append(String line){
        sb.append(line);
        sb.append(System.lineSeparator());
    }
    public void append(StringBufferEx buffer){
        sb.append(buffer);
    }

    public void append(StringBuffer buffer){
        sb.append(buffer);
        sb.append(System.lineSeparator());
    }
    
    public String toString(){
        return sb.toString();
    }
}
