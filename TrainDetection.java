/******************************************************************************
 *  Author:    Daniel Leung
 *  Description: Trains the threshold for FraudDetection object
 ******************************************************************************/
import java.util.ArrayList;
import java.io.FileNotFoundException;
public class TrainDetection {
    
    // takes in predictor, cv_data, and cv_labels to train threshold
    public static void train(FraudDetection predictor, double[][] cv_data, 
                      double[] cv_labels, double stepSize, double iter)
    {
        double prev_score = 0;
        double f1_score = 0;
        for (int i = 0; i < iter; i++)
        {
            f1_score = f1_score(predictor, cv_data, cv_labels);
            if (f1_score > prev_score)
            {
                prev_score = f1_score;
                // increase epsilon
                predictor.update(predictor.threshold() * (1 + stepSize));
            }
            else 
            {
                // decrease epsilon
                predictor.update(predictor.threshold() * (1 - stepSize));
            }
        }
    }
    
    // calculates f1_score of predictor with cv_data
    public static double f1_score(FraudDetection predictor, double[][] cv_data, double[] cv_labels)
    {
        double precision = 0; // tp/(tp + fp)
        double recall = 0; // tp(tp + fn)
        int tp = 0; // true positives
        int fp = 0; // false positives
        int fn = 0; // false negatives
        
        for (int i = 0; i < cv_data[0].length; i++)
        {
            double[] entry = new double[cv_data.length];
            for (int j = 0; j < cv_data.length; j++)
            {
                entry[j] = cv_data[j][i];
            }
            int prediction = predictor.isFraud(entry) ? 1 : 0;
            if (prediction == cv_labels[i]) tp++;
            else if (prediction == 1 && cv_labels[i] == 0) fp++;
            else if (prediction == 0 && cv_labels[i] == 1) fn++;
        }
        precision = tp / (tp + fp);
        recall = tp / (tp + fn);
        double f1_score = (2 * precision * recall) / (precision + recall);
        return f1_score;
    }
    
    // test client
    public static void main(String[] args) throws FileNotFoundException
    {
        ArrayList<String> raw_data_clean = GenerateData.createCleanData(100);
        ArrayList<String> raw_data_cv = GenerateData.createAnomalyData(100);
        double[][] data = GenerateData.parseData(raw_data_clean);
        double[][] cv_data = GenerateData.parseData(raw_data_cv);
        double[] cv_labels = null;
        FraudDetection predictor = new FraudDetection(data, 0.05);
        train(predictor, cv_data, cv_labels, 0.05, 100);
        System.out.println(f1_score(predictor, cv_data, cv_labels));
    }
}
