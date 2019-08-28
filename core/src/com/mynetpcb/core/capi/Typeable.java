package com.mynetpcb.core.capi;

public interface Typeable {
    public enum Type{
       SYMBOL,
       GROUND,
       POWER
    }
   public Type getType();

   public void setType(Type type);
}
