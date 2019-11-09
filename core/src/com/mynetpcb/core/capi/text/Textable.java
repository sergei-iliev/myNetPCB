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
    public Texture getClickedTexture(int x,int y);
    
    public boolean isClickedTexture(int x,int y);
}
