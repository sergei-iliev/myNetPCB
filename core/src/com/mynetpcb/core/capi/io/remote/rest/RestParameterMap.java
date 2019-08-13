package com.mynetpcb.core.capi.io.remote.rest;

import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class RestParameterMap{
    private static final String REST_PREFIX="/rest";
    
    public static final  String REST_EXCEPTION_KEY="rest-exception"; 
    private final String root;
    
    private final Map<String,String> parameterMap;
    
    private List<String> uriSet;
    
    public static class ParameterBuilder{
        private String root;     
        
        private  Map<String,String> parameterMap=new LinkedHashMap<String,String>();
        
        private List<String> uriSet=new ArrayList<String>();
        
        public ParameterBuilder(String root){
           this.root=root;           
        }
        
        public ParameterBuilder addURI(String uri){
            uriSet.add(uri);
            return this;
        }
        public ParameterBuilder addAttribute(String attribute,String value){
          parameterMap.put(attribute,value);  
          return this;  
        }
        
        public RestParameterMap build(){
           RestParameterMap m=new RestParameterMap(root);
           for(Entry<String,String> entry:this.parameterMap.entrySet()){
               if(entry.getValue()!=null){ 
                 m.parameterMap.put(entry.getKey(),entry.getValue());
               }
           } 
           m.uriSet.addAll(this.uriSet);
           return m;
        }
        
        
    }
    
    
    private RestParameterMap(String root) {  
       this.root=root; 
       this.parameterMap=new LinkedHashMap<String,String>();
       this.uriSet=new ArrayList<String>();
    }
    
     
    public String createRestRequest()  throws UnsupportedEncodingException {
        String parameters="";
        for (Entry<String,String> entry :parameterMap.entrySet()) {              
            parameters +=(parameters!=""?"&":"") + entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8");                
        }
        String uri="";
        for(String item:uriSet){
            uri+="/"+URLEncoder.encode(item,"UTF-8");
        }
        return REST_PREFIX+(root==null?"":root)+uri+(parameters!=""?"?"+parameters:"");
    }
   
}

