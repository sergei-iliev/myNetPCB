package com.mynetpcb.gerber.aperture.type;

import com.mynetpcb.core.capi.Grid;

import java.util.Objects;

public class CircleAperture extends ApertureDefinition {
        private int diameter;
        
        private int hole;
        
        public CircleAperture(){
            super(ApertureDefinition.ApertureShape.CIRCLE);
        }
        public CircleAperture(int code,int diameter,int hole){
          this();
          this.diameter=diameter;
          this.hole=hole;
          setCode(code);
        }
        
        public void setDiameter(int diameter) {
            this.diameter = diameter;
        }

        public int getDiameter() {
            return diameter;
        }

        public void setHole(int hole) {
            this.hole = hole;
        }

        public double getHole() {
            return hole;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof CircleAperture)) {
                return false;
            }
            CircleAperture other = (CircleAperture)obj;
            return Objects.equals(this.getAttribute(),other.getAttribute())&&this.getDiameter()==other.getDiameter()&&this.getHole()==other.getHole();
        }
        
        @Override
        public int hashCode() {
            int hash = 31;
            hash += this.getDiameter();
            hash += this.getHole();
            hash+=this.getAttribute()==null?0:this.getAttribute().hashCode();
            return hash;
        }
        
        @Override
        public String print() {
            StringBuilder sb=new StringBuilder();
            sb.append("%ADD");
            sb.append(String.valueOf(code));
            sb.append("C,"+Grid.COORD_TO_MM(diameter));
            //if(hole!=0){
            //    sb.append("X"+Grid.COORD_TO_MM(hole));
            //}
            sb.append("*%");
            return sb.toString();
        }
    

}
