
package org.neuroph.contrib.samples.stockmarket;

import static java.lang.Math.sqrt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.LMS;


/**
 *
 * @author Tim Mayabi
 */
public class Main {
    private int maxCounter;
    private String[] valuesRow;
     private double normolizer = 100000.0D;
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
     public Main() {
        //this.setMaxCounter(145);
    }

    public Main(int maxCounter) {
        this.setMaxCounter(maxCounter);
    }
    public static void main(String[] args) {
//        System.out.println("Time stamp N1:" + new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss:MM").format(new Date()));
       Main model = new Main();
       model.Testing();
    }
    
    public void Testing(){
          DAO dao = new DAO();
          dao.readNRBData();

        int maxIterations = 200000;
        NeuralNetwork neuralNet = new MultiLayerPerceptron(4,10,10 ,1);
        ((LMS) neuralNet.getLearningRule()).setMaxError(0.1);//0-1
        ((LMS) neuralNet.getLearningRule()).setLearningRate(0.5);//0-1
        ((LMS) neuralNet.getLearningRule()).setMaxIterations(maxIterations);//0-1
        //TrainingSet trainingSet = new TrainingSet();
        TrainingSet trainingSet = dao.getTrainingSet();
        
         BackPropagation backPropagation = new BackPropagation();
                 backPropagation.setMaxIterations(maxIterations);
                
        
        
        //System.out.println("MAIN CLASS "+trainingSet.getLabel());
//      
        double daxmax = 100.0D;
           //neuralNet.learnInSameThread(backPropagation);
          neuralNet.learnInSameThread(trainingSet, backPropagation);
          

        System.out.println("Neural Total network Error " + ((LMS)neuralNet.getLearningRule()).getTotalNetworkError());
        //System.out.println("Neural Total network Error " + neuralNet.getLearningRule());
        
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
        int maxCount = (int)(30*counter); 
        System.out.println("full number of values = " + counter + " Percentage "+ maxCount/100); 
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
              // System.out.println(valuesRow[counter - n] );
            }
        }
        System.out.println("valuesRow.length=" + valuesRow.length);
         if (valuesRow.length < 5) {
            System.out.println("valuesRow.length < 5");
            
        }
         double error = 0.0;
         double rmse = 0.0;
         double sqrtrmse=0.0;
         double mape = 0.0;
         TrainingSet testSet = new TrainingSet();
         for (int j = 0; j + 4 < valuesRow.length; j++) {
                String s1 = valuesRow[j];
                String s2 = valuesRow[j + 1];
                String s3 = valuesRow[j + 2];
                String s4 = valuesRow[j + 3];
                String s5 = valuesRow[j + 4];
                double d1 = (Double.parseDouble(s1) - minlevel) / normolizer;
                //System.out.println("D1 "+d1 *normolizer + " S1 "+ s1);
                double d2 = (Double.parseDouble(s2) - minlevel) / normolizer;
                double d3 = (Double.parseDouble(s3) - minlevel) / normolizer;
                double d4 = (Double.parseDouble(s4) - minlevel) / normolizer;
                double d5 = (Double.parseDouble(s5) - minlevel) / normolizer;
                System.out.print( "Actual"  + (d5*normolizer));
                 testSet.addElement(new TrainingElement(new double[]{d1}));
                 neuralNet.setInput(d1,d2,d3,d4);
                  neuralNet.calculate();
                 System.out.print(" Predicted "+ (neuralNet.getOutput().firstElement())*normolizer);
                  error = (d5*normolizer) - ((neuralNet.getOutput().firstElement())*normolizer);
                 System.out.println(" Error "+ error);
                
                 rmse =+ (error*error);
            }
         
             sqrtrmse=sqrt((rmse/valuesRow.length));
              System.out.println(" Total RMSE  "+ sqrtrmse);
              
              mape = (rmse/valuesRow.length);

              System.out.println(" MAD  "+ mape);
       }
    

     
      public Main(String[] valuesRow) {
        this.setValuesRow(valuesRow);
    }
}
