package com.mynetpcb.gerber.aperture.type;

import com.mynetpcb.core.capi.Grid;

import java.util.Objects;

public class RectangleAperture extends ApertureDefinition {
        
        private int X;
        private int Y;
        private int hole;
        
        public RectangleAperture() {
            super(ApertureDefinition.ApertureShape.RECTANGLE);
        }
       public RectangleAperture(int code,int x,int y){
         this();
         this.X=x;
         this.Y=y;         
         setCode(code);
    }
        public void setX(int X) {
            this.X = X;
        }

        public int getX() {
            return X;
        }

        public void setY(int Y) {
            this.Y = Y;
        }

        public int getY() {
            return Y;
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
            if (!(obj instanceof RectangleAperture)) {
                return false;
            }
            RectangleAperture other = (RectangleAperture)obj;
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
        @Override
        public String print() {
            StringBuffer sb=new StringBuffer();
            sb.append("%ADD");
            sb.append(getCode());
            sb.append("R,"+Grid.COORD_TO_MM(X));            
            sb.append("X");
            sb.append(Grid.COORD_TO_MM(Y));
            //if(hole!=0){
            //    sb.append("X"+Grid.COORD_TO_MM(hole));
            //}
            sb.append("*%");
            return sb.toString();
        }
    

}
