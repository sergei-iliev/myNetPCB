package com.mynetpcb.gerber.capi;


public class StringBufferExt{
    private final StringBuffer sb;
    
    public StringBufferExt() {
        this.sb=new StringBuffer();
    }
    
    public void append(String line){
        sb.append(line);
        sb.append(System.lineSeparator());
    }
    public void append(StringBufferExt buffer){
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
