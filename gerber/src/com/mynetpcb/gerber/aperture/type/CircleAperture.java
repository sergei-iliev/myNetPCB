package com.mynetpcb.gerber.aperture.type;

import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.d2.shapes.Utils;

import java.util.Objects;

public class CircleAperture extends ApertureDefinition {
        private double diameter;
        
        public CircleAperture(){
            super(ApertureDefinition.ApertureShape.CIRCLE);
        }
        
        public CircleAperture(int code,double diameter){
          super(ApertureDefinition.ApertureShape.CIRCLE);
          this.diameter=diameter;
          setCode(code);
        }
        
        public void setDiameter(double diameter) {
            this.diameter = diameter;
        }

        public double getDiameter() {
            return diameter;
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
            return Objects.equals(this.getAttribute(),other.getAttribute())&&Utils.EQ(this.getDiameter(),other.getDiameter());
        }
        
        @Override
        public int hashCode() {
            int hash = 31;
            hash += Double.hashCode(this.getDiameter());
            hash+=this.getAttribute()==null?0:this.getAttribute().hashCode();
            return hash;
        }
        
        @Override
        public String print() {
            StringBuilder sb=new StringBuilder();
            sb.append("%ADD");
            sb.append(String.valueOf(code));
            sb.append("C,"+Grid.COORD_TO_MM(diameter));
            sb.append("*%");
            return sb.toString();
        }
    

}