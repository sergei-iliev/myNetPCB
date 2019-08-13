package com.mynetpcb.core.capi.print;

import java.awt.print.Printable;

/**
 * Need to prepare shapes,execute rules, validate integrity before printing
 */
public interface PrintCallable extends Printable{
     public void prepare(PrintContext printContext);
     
     public int getNumberOfPages();
     
     public void finish();
}
