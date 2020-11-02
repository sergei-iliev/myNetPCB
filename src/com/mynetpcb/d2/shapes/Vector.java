package com.mynetpcb.d2.shapes;


public class Vector{
    public double x;
    public double y;
    
    public Vector(double x,double y) {       
       this.set(x, y);
    }
    
    public void set(double x,double y){
        this.x=x;
        this.y=y;        
    }
    public Vector(Point a1,Point a2) {
       this.set((a2.x - a1.x),(a2.y - a1.y));       
    }
    
    public void set(Point a1,Point a2){
        this.set((a2.x - a1.x),(a2.y - a1.y));         
    }
    public void set(double x1,double y1,double x2,double y2){
        this.set((x2 - x1),(y2 - y1));         
    }    
    public Vector clone() {
        return new Vector(this.x, this.y);
    }
    
    public double length() {
        return Math.sqrt(this.dot(this));
    }    
    
    /**
     * Returns scalar product (dot product) of two vectors <br/>
     * <code>dot_product = (this * v)</code>
     */
    public double dot(Vector v) {
        return ( this.x * v.x + this.y * v.y );
    }
    
    /**
     * Returns vector product (cross product) of two vectors <br/>
     * <code>cross_product = (this x v)</code>
     */
    public double cross(Vector v) {
        return ( this.x * v.y - this.y * v.x );
    }     
   
    /**
     * Slope of the vector in degrees from 0 to 360
     */
    public double slope() {
        double angle = Math.atan2(this.y, this.x);
        if (angle<0) angle = 2*Math.PI + angle;
        
        return Utils.degrees(angle);
    }
    
    public void invert() {
        this.x=-this.x;
        this.y=-this.y;
    }
    /**
     * Returns unit vector.<br/>
     */
    public Vector normalize() {            
        return ( new Vector(this.x / this.length(), this.y / this.length()) );            
    }        
    /**
     * Returns new vector rotated by given angle,
     * positive angle defines rotation in counter clockwise direction,
     * negative - in clockwise direction
     */
    public void rotate(double angle) {
        Point point = new Point(this.x, this.y);
        point.rotate(angle);
        this.x=point.x;
        this.y=point.y;
    }        
    /**
     *rotate 90 degrees counter clockwise         
     */
    public void rotate90CCW() {
        double x=this.x;
        double y=this.y;
        this.x=-1*y;
        this.y= x;
    }    
    /**
     * rotate 90 degrees clockwise
     */
    public void rotate90CW() {
        double x=this.x;
        double y=this.y;           
        this.x=y;
        this.y=-1*x;
    }

    /**
     * Return angle between this vector and other vector. <br/>
     * Angle is measured from 0 to 2*PI in the counter clockwise direction
     * from current vector to other.
     */
    public double angleTo(Vector v) {
        Vector norm1 = this.normalize();
        Vector norm2 = v.normalize();
        double angle = Math.atan2(norm1.cross(norm2), norm1.dot(norm2));
        if (angle<0) angle += 2*Math.PI;
        return angle;
    }
    /**
     * Return vector projection of the current vector on another vector
     * @param {Vector} v Another vector
     * @returns {Vector}
     */
    public Vector projectionOn(Vector v){
        Vector  n = v.normalize();
        double d = this.dot(n);
        n.multiply(d);
        return n;
    }
    
    public void multiply(double scalar) {
        this.x=scalar * this.x;
        this.y=scalar * this.y;
    }
    
    public Vector add(Vector v){
       return new Vector(this.x+v.x,this.y+v.y);
    }    
}
