/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuroph.contrib.samples.stockmarket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.nnet.learning.BackPropagation;

/**
 *
 * @author tmwamalwa
 */
public class DAO {
        private int maxCounter;
    private String[] valuesRow;
     private double normolizer = 100.0D;
    private double minlevel = 0.0D;
    private TrainingSet trainingSet = new TrainingSet();
    public String[] getValuesRow() {
        return valuesRow;
    }

    public void setValuesRow(String[] valuesRow) {
        this.valuesRow = valuesRow;
    }

    public int getMaxCounter() {
        return maxCounter;
    }

    public void setMaxCounter(int maxCounter) {
        this.maxCounter = maxCounter;
    }
    //percentage of training to be done
    public DAO() {
        //this.setMaxCounter(145);
    }

    public DAO(int maxCounter) {
        this.setMaxCounter(maxCounter);
    }
  
  
    public TrainingSet readNRBData() {
        
        
        HashMap hm = new HashMap();
       Statement stmt = null; 
        
        PreparedStatement preparedstatement = null;
        String values="";
        String id = "";
        Connection connection = null;
        
        int counter = 0;
        try {
          
             connection = GetDatabaseConnection.getMysqlConnection();
            stmt =  connection.createStatement();
            String query ="SELECT ID,DATA FROM NAIROBI";
             preparedstatement = (PreparedStatement) connection.prepareStatement(query);
           
           
            
            ResultSet result = preparedstatement.executeQuery();
            while(result.next()){
               
                values=result.getString("DATA");
                id = String.valueOf(result.getInt("ID"));
                hm.put(id, values);
               counter = counter + 1;
            }
            
             connection.close();
        } catch (Exception ioe) {
            System.out.println("Oops- an IOException happened.");
            ioe.printStackTrace();
            System.exit(1);
        }
        int maxCount = (int)(70*counter); 
        System.out.println("full number of values = " + counter + " Number of training data"+ maxCount/100); 
        setMaxCounter(maxCount/100);
        Set s = hm.keySet();
        Iterator i = s.iterator();
        valuesRow = new String[this.getMaxCounter()];
        
        int n = 0;
        while (i.hasNext()) {
            String key = (String) i.next();
            String value = (String) hm.get(key);
            //System.out.println(key + "->" + value);
            n = n + 1;
            //configurable percentage of
            if (counter - n < this.getMaxCounter()) {
                valuesRow[counter - n] = value;
                //System.out.println("Get values "+ value);
               //System.out.println(valuesRow[counter - n] );
            }
        }
        System.out.println("valuesRow.length=" + valuesRow.length);
         if (valuesRow.length < 5) {
            //System.out.println("valuesRow.length < 5");
            return null;
        }
        
         for (int j = 0; j + 4 < valuesRow.length; j++) {
                String s1 = valuesRow[j];
                String s2 = valuesRow[j + 1];
                String s3 = valuesRow[j + 2];
                String s4 = valuesRow[j + 3];
                String s5 = valuesRow[j + 4];
                double d1 = (Double.parseDouble(s1) - minlevel) / normolizer;
               // System.out.println("D1 "+d1 *normolizer + " S1 "+ s1);
                double d2 = (Double.parseDouble(s2) - minlevel) / normolizer;
                double d3 = (Double.parseDouble(s3) - minlevel) / normolizer;
                double d4 = (Double.parseDouble(s4) - minlevel) / normolizer;
                double d5 = (Double.parseDouble(s5) - minlevel) / normolizer;
                //System.out.println(i + " " + d1 + " " + d2 + " " + d3 + " " + d4 + " ->" + d5);
                trainingSet.addElement(new SupervisedTrainingElement(new double[]{d1, d2, d3, d4}, new double[]{d5}));
               
            }
         return trainingSet;
    }

    public TrainingSet getTrainingSet() {
        return trainingSet;
    }

    public void setTrainingSet(TrainingSet trainingSet) {
        this.trainingSet = trainingSet;
    }
    

    public DAO(String[] valuesRow) {
        this.setValuesRow(valuesRow);
    }
    
}
