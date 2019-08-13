package com.mynetpcb.core.capi.text;


/**
 *Text shapes like Label/Caption/Multiline must support this interface
 * @author Sergey Iliev
 */
public interface Textable {
    
    @Deprecated
    public ChipText  getChipText();

    /*
     * Get texture by tag
     */
    //public Texture getTextureByTag(String tag);
    
    /*
     * Get texture under coordinate (x,y)
     */
    //public Texture getClickedTexture(int x,int y);
}
