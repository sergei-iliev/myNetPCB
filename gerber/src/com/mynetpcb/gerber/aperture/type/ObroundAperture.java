package com.mynetpcb.gerber.aperture.type;


public class ObroundAperture extends ApertureDefinition{
        
    public ObroundAperture(int code,int x,int y){
      super(ApertureDefinition.ApertureShape.OBROUND);

    }
    @Override
    public String print() {
            StringBuffer sb=new StringBuffer();

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
//            ObroundAperture other = (ObroundAperture)obj;
//            return Objects.equals(this.getAttribute(),other.getAttribute())&&this.getX()==other.getX()&&this.getY()==other.getY()&&this.getHole()==other.getHole();
            return true;
        }
        
        @Override
        public int hashCode() {
            int hash = 31;
//            hash += this.getX()+getY();
//            hash += this.getHole();
//            hash+=this.getAttribute()==null?0:this.getAttribute().hashCode();
            return hash;
        }        
        
    
}

