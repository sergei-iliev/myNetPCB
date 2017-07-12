package com.mynetpcb.gerber.aperture.type;

import com.mynetpcb.core.capi.Grid;

import java.util.Objects;

public class ObroundAperture extends RectangleAperture{
        public ObroundAperture() {
            setShape(ApertureDefinition.ApertureShape.OBROUND);
        }
        
    public ObroundAperture(int code,int x,int y){
      this();
      setX(x);
      setY(y);         
      setCode(code);
    }
        @Override
        public String print() {
            StringBuffer sb=new StringBuffer();
            sb.append("%ADD");
            sb.append(getCode());
            sb.append("O,"+Grid.COORD_TO_MM(getX()));            
            sb.append("X");
            sb.append(Grid.COORD_TO_MM(getY()));
            //if(getHole()!=0){
            //    sb.append("X"+Grid.COORD_TO_MM(getHole()));
            //}
            sb.append("*%");
            return sb.toString();
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof ObroundAperture)) {
                return false;
            }
            ObroundAperture other = (ObroundAperture)obj;
            return Objects.equals(this.getAttribute(),other.getAttribute())&&this.getX()==other.getX()&&this.getY()==other.getY()&&this.getHole()==other.getHole();
        }
        
        @Override
        public int hashCode() {
            int hash = 31;
            hash += this.getX()+getY();
            hash += this.getHole();
            hash+=this.getAttribute()==null?0:this.getAttribute().hashCode();
            return hash;
        }        
        
    
}
