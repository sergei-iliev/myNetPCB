package com.mynetpcb.core.capi;


import java.util.UUID;

/**
 *Track asset{pad,symbol,circuit,board} changes
 * @author Sergey Iliev
 */
public interface Changeable {
/*
 * Is unit within the container changed?
 */
  public boolean isChanged();
  
  /*
   * register initial state for container
   */
  public void registerInitialState();
  /*
   * register initial state for unit
   */
  public void registerInitialState(UUID uuid);
  
}
