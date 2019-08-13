package com.mynetpcb.core.board;


/**
 * Add clearance capabilities
 */
public interface Clearanceaware extends Layerable{
  
  public void setClearance(int clearance);
  
  public int getClearance();

}
