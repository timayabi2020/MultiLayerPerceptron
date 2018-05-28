
package org.neuroph.contrib.samples.stockmarket;

import static java.lang.Math.sqrt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
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
import org.neuroph.util.TransferFunctionType;


/**
 *
 * @author Tim Mayabi
 */
public class Main {
    private int maxCounter;
    private String[] valuesRow;
     private double normolizer = 100000.0D;
    private double minlevel = 0.0D;
    private static DecimalFormat df2 = new DecimalFormat(".######");
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

        int maxIterations = 10000;
        NeuralNetwork neuralNet = new MultiLayerPerceptron(TransferFunctionType.SIGMOID,5,22,22, 1);
        ((LMS) neuralNet.getLearningRule()).setMaxError(0.001);//0-1
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
          
           
        System.out.println("Neural Total network Error " + df2.format(((LMS)neuralNet.getLearningRule()).getTotalNetworkError()));
        //System.out.println("Neural Total network Error " + neuralNet.getLearningRule());
        // save trained neural network
          neuralNet.save("nrbPerceptron.nnet");
          // load saved neural network
         NeuralNetwork loadedMlPerceptron = NeuralNetwork.load("nrbPerceptron.nnet");
         HashMap hm = new HashMap();
         Statement stmt = null; 
        
        PreparedStatement preparedstatement = null;
        String values="";
        String id = "";
        Connection connection = null;
        String riceprice = "";
        String wheatprice="";
        String maizeprice="";
        String maizeproduction="";
        String rainfall="";
        String inflation ="";
         int rowcount = 0;
        int counter = 0;
          double error = 0.0;
         double rmse = 0.0;
         double sqrtrmse=0.0;
         double mape = 0.0;
         double getsum  =0.0;
         int maxCount=0;
         TrainingSet testSet = new TrainingSet();
        try {
          
              connection = GetDatabaseConnection.getMysqlConnection();
            stmt =  connection.createStatement();
            String query ="SELECT ID,MAIZEPRICE,INFLATION,MAIZEPRODUCTION,RAINFALL,RICEPRICE,WHEATPRICE FROM RESEARCH ORDER BY 1 DESC";
             preparedstatement = (PreparedStatement) connection.prepareStatement(query);
           
           
            
            ResultSet result = preparedstatement.executeQuery();
            
            if (result.last()) {
            rowcount = result.getRow();
            result.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
            }
            maxCount = (int)(30*rowcount); 
           System.out.println("full number of values = " + counter + " Number of training data"+ maxCount/100); 
            setMaxCounter(maxCount/100);
            for(int i =0; i<maxCount; i++){
                while(result.next()){
                riceprice=result.getString("RICEPRICE");
                inflation=result.getString("INFLATION");
                rainfall=result.getString("RAINFALL");
                maizeproduction=result.getString("MAIZEPRODUCTION");
                wheatprice=result.getString("WHEATPRICE");
                maizeprice=result.getString("MAIZEPRICE");
                id = String.valueOf(result.getInt("ID"));
                //for(int a=0; a<31;a ++){
                  
              double d1 = (Double.parseDouble(riceprice) - minlevel) / normolizer;
               //System.out.println("NORMALIZED "+d1 *normolizer + " REAL "+ riceprice);
                double d2 = (Double.parseDouble(wheatprice) - minlevel) / normolizer;
                double d3 = (Double.parseDouble(maizeprice) - minlevel) / normolizer;
                double d4 = (Double.parseDouble(inflation) - minlevel) / 100;
                double d5 = (Double.parseDouble(rainfall) - minlevel) / 1000;
                double d6 = (Double.parseDouble(maizeproduction) - minlevel) / normolizer;
               
              System.out.print( "Actual"  + df2.format((d3*normolizer)));
                 //testSet.addElement(new TrainingElement(new double[]{d1}));
                 loadedMlPerceptron.setInput(d1,d2,d4,d5,d6);
                  loadedMlPerceptron.calculate();
                 System.out.print(" Predicted "+ df2.format((loadedMlPerceptron.getOutput().firstElement())*normolizer));
                  error = ((loadedMlPerceptron.getOutput().firstElement())*normolizer)-(d3*normolizer);
                 System.out.println(" Error "+ df2.format(error));
                 //getsum =+error;
                 System.out.println("Adding RMSE "+getsum);
                 rmse =+ (error*error);
                      
                      
                }
               
            }
            
             connection.close();
        } catch (Exception ioe) {
            System.out.println("Oops- an IOException happened.");
            ioe.printStackTrace();
            System.exit(1);
        }
//        int maxCount = (int)(30*counter); 
//        System.out.println("full number of values = " + counter + " Percentage "+ maxCount/100); 
//        setMaxCounter(maxCount/100);
//        Set s = hm.keySet();
//        Iterator i = s.iterator();
//        valuesRow = new String[this.getMaxCounter()];
//        
//        int n = 0;
//        while (i.hasNext()) {
//            String key = (String) i.next();
//            String value = (String) hm.get(key);
//            //System.out.println(key + "->" + value);
//            n = n + 1;
//            //configurable percentage of
//            if (counter - n < this.getMaxCounter()) {
//                valuesRow[counter - n] = value;
//                //System.out.println("Get values "+ value);
//              // System.out.println(valuesRow[counter - n] );
//            }
//        }
//        System.out.println("valuesRow.length=" + valuesRow.length);
//         if (valuesRow.length < 5) {
//            System.out.println("valuesRow.length < 5");
//            
//        }
//         double error = 0.0;
//         double rmse = 0.0;
//         double sqrtrmse=0.0;
//         double mape = 0.0;
//         double getsum  =0.0;
//         TrainingSet testSet = new TrainingSet();
//        
//         for (int j = 0; j + 4 < valuesRow.length; j++) {
//                String s1 = valuesRow[j];
//                String s2 = valuesRow[j + 1];
//                String s3 = valuesRow[j + 2];
//                String s4 = valuesRow[j + 3];
//                String s5 = valuesRow[j + 4];
//                double d1 = (Double.parseDouble(s1) - minlevel) / normolizer;
//                //System.out.println("D1 "+d1 *normolizer + " S1 "+ s1);
//                double d2 = (Double.parseDouble(s2) - minlevel) / normolizer;
//                double d3 = (Double.parseDouble(s3) - minlevel) / normolizer;
//                double d4 = (Double.parseDouble(s4) - minlevel) / normolizer;
//                double d5 = (Double.parseDouble(s5) - minlevel) / normolizer;
//                System.out.print( "Actual"  + df2.format((d5*normolizer)));
//                 testSet.addElement(new TrainingElement(new double[]{d1}));
//                 loadedMlPerceptron.setInput(d1,d2,d3,d4);
//                  loadedMlPerceptron.calculate();
//                 System.out.print(" Predicted "+ df2.format((loadedMlPerceptron.getOutput().firstElement())*normolizer));
//                  error = ((loadedMlPerceptron.getOutput().firstElement())*normolizer)-(d5*normolizer);
//                 System.out.println(" Error "+ df2.format(error));
//                 //getsum =+error;
//                 System.out.println("Adding RMSE "+getsum);
//                 rmse =+ (error*error);
//            }
              
             System.out.println(" Total RMSE COUNT  "+ df2.format(rmse));
             sqrtrmse=sqrt((rmse/maxCount));
              System.out.println(" Total RMSE  "+ df2.format(sqrtrmse));
              
              mape = (rmse/maxCount);

              System.out.println(" MAD  "+ df2.format(mape));
       }
    

     
      public Main(String[] valuesRow) {
        this.setValuesRow(valuesRow);
    }
}
