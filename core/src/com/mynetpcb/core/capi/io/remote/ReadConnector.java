package com.mynetpcb.core.capi.io.remote;


import com.mynetpcb.core.capi.config.Configuration;
import com.mynetpcb.core.capi.io.Command;
import com.mynetpcb.core.capi.io.CommandListener;
import com.mynetpcb.core.capi.io.remote.rest.RestParameterMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.lang.reflect.InvocationTargetException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;


public class ReadConnector extends Command {
        
    private HttpURLConnection conn;
        
    private final RestParameterMap parameterMap;    
    
        public ReadConnector(CommandListener monitor,RestParameterMap parameterMap,Class receiver) {
            super(monitor,receiver);
            this.parameterMap=parameterMap;
    }
     @Override
    public Void execute(){ 
            BufferedReader in=null;
            URL url;             
         
            try{
                // Create a URLConnection object for a URL
                  url = new URL("http://" + Configuration.get().getHost()+(Configuration.get().getPort()!=80?":" + Configuration.get().getPort():"") +parameterMap.createRestRequest());                             
                }catch(MalformedURLException e){
                  e.printStackTrace(System.out);
                  invokeErrorDialog(e.toString());
                  return null;}
                catch(UnsupportedEncodingException e){
                  e.printStackTrace(System.out);
                  invokeErrorDialog(e.toString());
                  return null;}    
            try{                  
                monitor.OnStart(receiver);                 
                conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("GET");
                conn.setUseCaches(false);
                
                in=new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8")); 
                
                String line;
                String xml="";
                  while ((line= in.readLine()) != null){
                        if(Thread.currentThread().isInterrupted()){ 
                           return null;    
                        }
                                xml+=line;                
                   } 
                //***Unzip stream
                final String response =new String(xml); //StringZipper.unzipStringFromBytes(bos.toByteArray());                      
                /*
                 *process response for error message - mind JList specifics and do it in invokeLater  
                 */                                      
                    try{   
                      SwingUtilities.invokeAndWait(
                          new Runnable(){
                               public void run(){
                                 monitor.OnRecive(response,ReadConnector.this.receiver); 
                               }                        
                          }                  
                          );  
                    }catch(InterruptedException ie){return null;} 
                     catch(InvocationTargetException ite) {return null;}                    
                     
                if(!Thread.currentThread().isInterrupted()){   
                  monitor.OnFinish(receiver);
                } 
            }catch (Exception e)   {
                e.printStackTrace(System.out);
                try{
                    boolean isresterror=false;
                    for(Map.Entry<String,List<String>> entry:conn.getHeaderFields().entrySet()){
                        if(entry.getKey()!=null&& entry.getKey().equals(RestParameterMap.REST_EXCEPTION_KEY)){
                            invokeErrorDialog(entry.getValue().get(0));  
                            isresterror=true;
                        }
                    }   
                    if(!isresterror){
                        invokeErrorDialog(e.getMessage());   
                    }
                }catch(Exception ee){
                    //swallow it up
                    }
                }
             finally{ 
                      if(in!=null){   
                         try{ in.close(); }
                         catch(IOException ioe){}
                      }    
                      if(conn!=null){ 
                        try{
                          conn.getInputStream().close();
                        }catch(IOException e){}                        
                        conn.disconnect();
                      }  
                    } 
            return null;
        
        }
        
        public void cancel() {
          if(conn!=null){
            conn.disconnect();
        }
        }
 
  }



