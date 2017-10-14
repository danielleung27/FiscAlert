/******************************************************************************
 *  Author:    Daniel Leung
 *  Description: FraudDetection reads in data, creates features, and trains
 * each feature using an anomoly detection algorithm
 ******************************************************************************/
import java.util.ArrayList;
import java.io.FileNotFoundException;
public class Client {
    // test client
    public static void main(String[] args) throws FileNotFoundException
    {
        // load user data
        ArrayList<String> raw_data_clean = GenerateData.createCleanData(1000);
        ArrayList<String> raw_data_cv = GenerateData.createAnomalyData(1000, 2, 5);
        int[] cv_labels = GenerateData.get_cv_labels();
        ArrayList<String> raw_data_test = GenerateData.createAnomalyData(1000, 2, 4);
        int[] test_labels = GenerateData.get_cv_labels();
        double[][] data = GenerateData.parseData(raw_data_clean);
        double[][] cv_data = GenerateData.parseData(raw_data_cv);
        double[][] test_data = GenerateData.parseData(raw_data_test);

        FraudDetection predictor = new FraudDetection(data, 0.05);
        TrainDetection.train(predictor, cv_data, cv_labels, 0.05, 1000);
        TrainDetection.test(predictor, test_data, test_labels);
        System.out.println(predictor.addData(643, 50, 85));
    }
}
