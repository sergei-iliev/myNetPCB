package com.mynetpcb.core.capi.io.remote;


import com.mynetpcb.core.capi.config.Configuration;
import com.mynetpcb.core.capi.io.Command;
import com.mynetpcb.core.capi.io.CommandListener;
import com.mynetpcb.core.capi.io.remote.rest.RestParameterMap;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.SwingUtilities;


public class WriteConnector extends Command {
        
    private HttpURLConnection conn;    
    
    private final RestParameterMap parameterMap;  
    
    private final StringBuffer content;         
    
    public WriteConnector(CommandListener monitor,StringBuffer content,RestParameterMap parameterMap,Class receiver) {
        super(monitor,receiver);
        this.parameterMap=parameterMap;     
        this.content=content;
    }
    @Override
    public Void execute() {
        URL url;
        OutputStream   out;
        BufferedInputStream bins=null;
        
        try{                      
            url = new URL("http://" + Configuration.get().getHost()+(Configuration.get().getPort()!=80?":" + Configuration.get().getPort():"") +parameterMap.createRestRequest());               
        }catch(MalformedURLException mue){
            invokeErrorDialog(mue.toString());
            return null;}
         catch(UnsupportedEncodingException uee){
            invokeErrorDialog(uee.toString());
            return null;}    

        monitor.OnStart(receiver); 
        try{          
            conn = (HttpURLConnection)url.openConnection();   
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-type","application/xml");   
            conn.setRequestProperty("User-Agent","myNetPCB"); 
            out=conn.getOutputStream();

            int n;
            byte[] buff=new byte[1024];
            
            //***send compressed content            
              ByteArrayInputStream ais=new ByteArrayInputStream(content.toString().getBytes("UTF-8"));                              
              while((n=ais.read(buff))>0){
                out.write(buff,0,n);                
                  if(Thread.currentThread().isInterrupted()){
                   return null;
                  }                           
              }                
              ais.close();          
              out.close();   
            
            if( conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                bins=new BufferedInputStream(conn.getInputStream());
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                String response="";
            
                byte[] buf = new byte[1024];
                int len;            
                while( (len = bins.read(buf)) > 0 )
                {
                  if(Thread.currentThread().isInterrupted()){
                   return null;
                  } 
                  bos.write(buf, 0, len);
                }             
                bins.close();
            
                response=bos.toString();

//****process response for error message             
                 monitor.OnRecive(response,receiver);                                   
                                  
            }else if(conn.getResponseCode()==HttpURLConnection.HTTP_NO_CONTENT){
                //no content
            }else{
                for (int i = 0;; i++) {
                      String headerName = conn.getHeaderFieldKey(i);
                      String headerValue = conn.getHeaderField(i);
                    if(headerName==null&&headerValue==null){
                      invokeErrorDialog(conn.getResponseMessage()); 
                      return null;  
                    }
                    
                    if (headerName!= null && headerName.equalsIgnoreCase(RestParameterMap.REST_EXCEPTION_KEY)) {
                      invokeErrorDialog(headerValue);            
                      return null;
                    }
                      
                }
            }
            /*
             * The dialog is closed in call chain of OnFinish.
             */
            SwingUtilities.invokeLater(new Runnable(){
                    @Override
                    public void run() {
                        monitor.OnFinish(receiver);  
                    }
                }); 
        }catch (Exception e)   {
                        e.printStackTrace(System.out);
                        invokeErrorDialog(e.getMessage());}
         finally{ 
                       if(bins!=null)   
                         try{ bins.close(); }catch(IOException ioe){}
                       if(conn!=null){
                         try {conn.getInputStream().close();} catch (IOException e1) {}
                         try {conn.getOutputStream().close();} catch (IOException e2) {}                 
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

