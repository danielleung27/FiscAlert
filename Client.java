import java.io.FileNotFoundException;
import java.util.ArrayList;

// 0=time, 1=price, 2=distance
public class Client {
    double limit; // if exceeds this factor, output warning
    ArrayList<String> triggers; // if entry contains any of these words, trigger warning
    ArrayList<String> safelist; // if entry contains these, dont mark as fraud
    
    // constructor
    public Client(double limit, ArrayList<String> triggers, ArrayList<String> safelist)
    {
        this.limit = limit;
        this.triggers = triggers;
        this.safelist = safelist;
    }
    
    // add entry and update predictor
    public boolean addEntry(FraudDetection predictor, double[][] cv_data, 
                      int[] cv_labels, double stepSize, int iter, double[] newEntry)
    {
        if(newEntry[1] > predictor.getMedian() * limit)
            System.out.println("You are spending more than usual!");
        TrainDetection.train(predictor, cv_data, cv_labels, stepSize, iter);
        return predictor.addData(newEntry);
    }
    
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
        
        ArrayList<String> triggers = new ArrayList<String>();
        triggers.add("alcohol");
        ArrayList<String> safelist = new ArrayList<String>();
        safelist.add("online");
        Client newClient = new Client(2, triggers, safelist);
        double[] newEntry = {643, 50, 85};
        System.out.println(newClient.addEntry(predictor, cv_data, cv_labels, 0.05, 1000, newEntry));
    }
}