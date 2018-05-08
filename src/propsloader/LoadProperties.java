/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package propsloader;

import java.util.ResourceBundle;
import propsloader.GetResources;

/**
 *
 * @author tim
 */
public class LoadProperties {
    static ResourceBundle resources;
  
    private static String url;
    
    private static String img;
    
    
    
   
    

    public static String getURL() {
       resources = GetResources.getResources();
      url = resources.getString("url");
        
        return url;
    }

    public static String getImg() {
        resources = GetResources.getResources();
      img = resources.getString("logourl");
        return img;
    }

   
    
}
