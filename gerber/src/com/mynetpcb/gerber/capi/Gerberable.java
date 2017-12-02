package com.mynetpcb.gerber.capi;

import java.io.IOException;

public interface Gerberable {

    public void build(GerberServiceContext serviceContext,String fileName,int layermask)throws IOException;
        
}
