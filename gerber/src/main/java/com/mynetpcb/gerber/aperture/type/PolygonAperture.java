package com.mynetpcb.gerber.aperture.type;

import com.mynetpcb.core.capi.Grid;

import java.util.Objects;

@Deprecated
/*
 * Use contour instead
 */
public  class PolygonAperture extends ApertureDefinition{
        
        private int diameter;  //outer diameter
        private int verticesNumber;
        private double rotation;
        private int hole;
        
        public PolygonAperture() {
            super(ApertureDefinition.ApertureShape.POLYGON);
        }


        public void setDiameter(int diameter) {
            this.diameter = diameter;
        }

        public int getDiameter() {
            return diameter;
        }

        public void setVerticesNumber(int verticesNumber) {
            this.verticesNumber = verticesNumber;
        }

        public int getVerticesNumber() {
            return verticesNumber;
        }

        public void setRotation(double rotation) {
            this.rotation = rotation;
        }

        public double getRotation() {
            return rotation;
        }

        public void setHole(int hole) {
            this.hole = hole;
        }

        public int getHole() {
            return hole;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof PolygonAperture)) {
                return false;
            }
            PolygonAperture other = (PolygonAperture)obj;
            return Objects.equals(this.getAttribute(),other.getAttribute())&&Double.compare(this.rotation,other.getRotation())==0&&this.getDiameter()==other.getDiameter()&&this.getVerticesNumber()==other.getVerticesNumber()&&this.getHole()==other.getHole();
        }
        
        @Override
        public int hashCode() {
            int hash = 31;
            hash += this.getDiameter()+getVerticesNumber();
            hash += this.getHole()+Double.hashCode(rotation);
            hash+=this.getAttribute()==null?0:this.getAttribute().hashCode();
            return hash;
        }
        
        @Override
        public String print() {
            StringBuffer sb=new StringBuffer();
            sb.append("%ADD");
            sb.append(getCode());
            sb.append("P,"+Grid.COORD_TO_MM(diameter));            
            sb.append("X");
            sb.append(verticesNumber);
            //if(rotation!=null){
            //  if(hole!=0){
            //    sb.append("X"+rotation);
            //    sb.append("X"+Grid.COORD_TO_MM(hole));
            //  }
            //}
            sb.append("*%");
            return sb.toString();
        }
    }

