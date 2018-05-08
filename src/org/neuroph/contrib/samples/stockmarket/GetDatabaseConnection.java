/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuroph.contrib.samples.stockmarket;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import propsloader.GetResources;

/**
 *
 * @author tim
 */
public class GetDatabaseConnection {
   
    static ResourceBundle resources;
    static String driver_class;
    static String driver_url;
    static String db_user;
    static String db_password;
    static String expiry;
    private String expiryDate;
    
    public static Connection getMysqlConnection(){
       
            Connection conn = null;
             try {
            resources = GetResources.getResources();
            
            driver_class = resources.getString("db_driver_class");
            driver_url = resources.getString("db_connection_url");
            db_user = resources.getString("db_user_name");
            db_password = resources.getString("db_user_password");
             System.out.println(driver_class);
            
            Class.forName("com.mysql.jdbc.Driver");
           
            conn = DriverManager.getConnection(driver_url,db_user,db_password);
            
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GetDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GetDatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }

    public String getExpiryDate() {
        resources = GetResources.getResources();
        expiry = resources.getString("test");
        expiryDate = expiry;
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
   
    
    
}
