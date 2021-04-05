package com.mynetpcb.core.capi.io;


public interface CommandListener {
  
  public void onStart(Class<?> reciever);
  //****We need to know who is recieving if use common remote connector, to destinguish during event processing
  public void onRecive(String result,Class<?> reciever);
  
  public void onFinish(Class<?> receiver);
  
  public void onError(String error);
  
}
