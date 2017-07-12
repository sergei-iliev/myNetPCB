package com.mynetpcb.core.capi.text;


public class CompositeTextMetrics{
    
    private final Metrics baseTextMetrics;

    private final Metrics scaledTextMetrics;
    
    public CompositeTextMetrics() {
      baseTextMetrics=new TextMetrics();  
      scaledTextMetrics=new TextMetrics();
    }

    public void UpdateMetrics() {
        baseTextMetrics.UpdateMetrics();
        scaledTextMetrics.UpdateMetrics();
    }

    public Metrics getBaseTextMetrics(){
        return baseTextMetrics;    
    }
    
    public Metrics getScaledTextMetrics(){
        return scaledTextMetrics;    
    }    


}

