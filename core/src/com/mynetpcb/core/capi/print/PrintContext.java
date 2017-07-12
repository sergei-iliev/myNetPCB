package com.mynetpcb.core.capi.print;


public class PrintContext {
    
    private int layermaskId;
    
    private boolean isBlackAndWhite;
    
    private boolean isMirrored;

    private int penWidth;
    
    private String tag;
    /**
     * -1 fit to page
     * 1  actual size
     * N  custom ratio
     */
    private double customSizeRatio=1;
    
    public PrintContext(){
        
    }

    public PrintContext(int layermaskId,boolean isBlackAndWhite,boolean isMirrored,int penWidth){
        this.layermaskId=layermaskId;
        this.isBlackAndWhite=isBlackAndWhite;
        this.isMirrored=isMirrored;
        this.penWidth=penWidth;
        
    }

    public void setCustomSizeRatio(double customSizeRatio) {
        this.customSizeRatio = customSizeRatio;
    }

    public double getCustomSizeRatio() {
        return customSizeRatio;
    }

    public String getTag(){
        return tag;
    }
    public void setTag(String tag){
        this.tag=tag;
    }
    public void setLayermaskId(int layermaskId) {
        this.layermaskId = layermaskId;
    }

    public int getLayermaskId() {
        return layermaskId;
    }

    public void setIsBlackAndWhite(boolean isBlackAndWhite) {
        this.isBlackAndWhite = isBlackAndWhite;
    }

    public boolean isBlackAndWhite() {
        return isBlackAndWhite;
    }

    public void setIsMirrored(boolean isMirrored) {
        this.isMirrored = isMirrored;
    }

    public boolean isMirrored() {
        return isMirrored;
    }


    public void setPenWidth(int penWidth) {
        this.penWidth = penWidth;
    }

    public int getPenWidth() {
        return penWidth;
    }


}
