package com.mynetpcb.core.capi.impex;


import com.mynetpcb.core.capi.container.UnitContainer;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Map;


public class XMLExporter implements Impex<UnitContainer>{        

    @Override
    public void process(UnitContainer source,Map<String,?> context) throws IOException{
        PrintWriter ps=null;
        try{                                         
            ps=new PrintWriter((String)context.get("target.file"),"UTF-8");
            ps.print(source.Format()); 
        }finally{
            if(ps!=null){ 
              ps.close();
            }
        }
    }

  
}
