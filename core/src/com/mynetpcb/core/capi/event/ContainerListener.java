package com.mynetpcb.core.capi.event;

import java.util.EventListener;

public interface ContainerListener  extends EventListener{
  public void selectContainerEvent(ContainerEvent e);
  
  public void renameContainerEvent(ContainerEvent e);
  
  public void deleteContainerEvent(ContainerEvent e);
}
