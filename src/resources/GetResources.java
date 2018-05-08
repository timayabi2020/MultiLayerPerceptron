/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import java.util.ListResourceBundle;
import java.util.ResourceBundle;

/**
 *
 * @author tim
 */
public class GetResources extends ListResourceBundle{
     static ResourceBundle resourceBundle;
     static{
         resourceBundle=ResourceBundle.getBundle("config.HubProps");
     }
     
     public static ResourceBundle getResources(){
         return resourceBundle;
     }
    @Override
    protected Object[][] getContents() {
        
        return null;
    }
    
}
