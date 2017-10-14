/******************************************************************************
 *  Author:    Daniel Leung
 *  Description: Trains the threshold for FraudDetection object
 ******************************************************************************/
import java.util.ArrayList;
import java.io.FileNotFoundException;
public class TrainDetection {
    
    // takes in predictor, cv_data, and cv_labels to train threshold
    public static void train(FraudDetection predictor, double[][] cv_data, 
                      int[] cv_labels, double stepSize, int iter)
    {
        double prev_score = 0;
        double f1_score = 0;
        for (int i = 0; i < iter; i++)
        {
            prev_score = f1_score(predictor, cv_data, cv_labels);
            predictor.update(predictor.threshold() * (1 + stepSize));
            f1_score = f1_score(predictor, cv_data, cv_labels);
            if (f1_score > prev_score)
            {
                // epsilon already increased
                continue;
            }
            else 
            {
                // decrease epsilon (first revert the increase
                predictor.update(predictor.threshold() / (1 + stepSize));
                predictor.update(predictor.threshold() * (1 - stepSize));
            }
        }
    }
    
    // calculates f1_score of predictor with cv_data
    public static double f1_score(FraudDetection predictor, double[][] cv_data, int[] cv_labels)
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
            int prediction = predictor.isFraud(entry);
            if (prediction == cv_labels[i]) tp++;
            else if (prediction == 1 && cv_labels[i] == 0) fp++;
            else if (prediction == 0 && cv_labels[i] == 1) fn++;
        }
        precision = ((double)tp) / (tp + fp);
        recall = ((double)tp) / (tp + fn);
        double f1_score = (2 * precision * recall) / (precision + recall);
        return f1_score;
    }
    
    // computes accuracy and f_1 score on test data
    public static void test(FraudDetection predictor, double[][] test_data, int[] test_labels)
    {
        int correct = 0;
        double f1_score = f1_score(predictor, test_data, test_labels);
        System.out.println("F1_score on test data is: " + f1_score);
        for (int i = 0; i < test_data[0].length; i++)
        {
            double[] entry = new double[test_data.length];
            for (int j = 0; j < test_data.length; j++)
            {
                entry[j] = test_data[j][i];
            }
            int prediction = predictor.isFraud(entry);
            if (prediction == test_labels[i]) correct++;
        }
        System.out.println("Accuracy: " + ((double)correct) / test_labels.length);
    }
    
    // test client
    public static void main(String[] args) throws FileNotFoundException
    {
        ArrayList<String> raw_data_clean = GenerateData.createCleanData(1000);
        ArrayList<String> raw_data_cv = GenerateData.createAnomalyData(1000, 30, 5);
        int[] cv_labels = GenerateData.get_cv_labels();
        ArrayList<String> raw_data_test = GenerateData.createAnomalyData(1000, 30, 4);
        int[] test_labels = GenerateData.get_cv_labels();
        double[][] data = GenerateData.parseData(raw_data_clean);
        double[][] cv_data = GenerateData.parseData(raw_data_cv);
        double[][] test_data = GenerateData.parseData(raw_data_test);
        FraudDetection predictor = new FraudDetection(data, 0.05);
        train(predictor, cv_data, cv_labels, 0.05, 1000);
        System.out.println(predictor.threshold());
        test(predictor, test_data, test_labels);
    }
}
