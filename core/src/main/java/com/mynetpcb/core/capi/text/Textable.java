package com.mynetpcb.core.capi.text;


/**
 *Text shapes like Label/Caption/Multiline must support this interface
 * @author Sergey Iliev
 */
public interface Textable {
    
    /*
     * Get texture by tag
     */
    public default Texture getTextureByTag(String tag){
        return null;
    }
    
    /*
     * Get texture under coordinate (x,y)
     */
    public Texture getClickedTexture(double x,double y);
    
    public boolean isClickedTexture(double x,double y);
}
