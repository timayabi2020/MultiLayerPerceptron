/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package propsloader;

import java.util.ListResourceBundle;
import java.util.ResourceBundle;

/**
 *
 * @author nsongok
 */
public class GetResources extends ListResourceBundle {

    static ResourceBundle resourceBundle;

    static {
        resourceBundle = ResourceBundle.getBundle("Config.HubProps");


    }

    public static ResourceBundle getResources() {
        return resourceBundle;
    }

    @Override
    protected Object[][] getContents() {
        // TODO Auto-generated method stub
        return null;
    }
}