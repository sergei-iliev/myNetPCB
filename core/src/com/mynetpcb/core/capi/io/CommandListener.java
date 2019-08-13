package com.mynetpcb.core.capi.io;

public interface CommandListener {
  
  public void OnStart(Class<?> reciever);
  //****We need to know who is recieving if use common remote connector, to destinguish during event processing
  public void OnRecive(String result,Class<?> reciever);
  
  public void OnFinish(Class<?> receiver);
  
  public void OnError(String error);
  
}
