package com.mynetpcb.gerber.aperture;

import java.util.HashSet;
import java.util.Set;


public class ApertureMacro {
    
    private final String name;
    private final int code;
    private final Set<Integer> points;
    
    public ApertureMacro(String name,int code) {
       this.name=name;
       this.code=code;
       this.points=new HashSet<>();
    }
    
    public void addPoint(int point){
        points.add(point);
    }
    
    public String print(){
        StringBuilder sb=new StringBuilder();
        sb.append("%AM");
        sb.append(name+"*\r\n");
        
        return sb.toString();    
    }
}
