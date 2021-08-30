package com.mynetpcb.core.capi.verification;


import java.io.File;

public abstract class VerifierFactory {
    public static final  Verifier createXMLVersionVerifier(final File xmlfile,final double version){
      return new XMLVersionVerifier(xmlfile,version);  
    }
    
    public static final  Verifier createXMLRootTagVerifier(final File xmlfile,final String tagvalue){
      return new XMLRootTagVerifier(xmlfile,tagvalue);  
    }

}
