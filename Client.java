import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.Charset;
import java.util.Scanner;

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
    public void addEntry(FraudDetection predictor, double[][] cv_data, 
                      int[] cv_labels, double stepSize, int iter, double[] newEntry, String original)
    {
        boolean ret = predictor.addData(newEntry);
        if(newEntry[1] > predictor.getMedian() * limit)
            System.out.println("You are spending more than usual!");
        for (String a : triggers)
        {
            if(original != null && original.contains(a))
                System.out.println("Trigger Keyword: Warning, transaction made is suspicious!");
        }
        for (String a : safelist)
        {
            if(original != null && original.contains(a))
                System.out.println("Whitelist Keyword: Transaction may seem suspcious but should be safe!");
        }
        TrainDetection.train(predictor, cv_data, cv_labels, stepSize, iter);
        if (ret)
        {
            System.out.println("Transaction may be fraudulent!");
        }
        else 
            System.out.println("Transaction appears to be safe!");
    }

    // test client
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        // load user data
        ArrayList<String> raw_data_clean = GenerateData.createCleanData(1000);
        ArrayList<String> raw_data_cv = GenerateData.createAnomalyData(1000, 2, 5);
        int[] cv_labels = GenerateData.get_cv_labels();
        ArrayList<String> raw_data_test = GenerateData.createAnomalyData(1000, 2, 4);
        int[] test_labels = GenerateData.get_cv_labels();
        
        GenerateData.outputToFile(raw_data_clean, "raw_data_clean.txt");
        GenerateData.outputToFile(raw_data_cv, "raw_data_cv.txt");
        GenerateData.outputToFile(raw_data_test, "raw_data_test.txt");
        
        double[][] data = GenerateData.parseData((ArrayList<String>)Files.readAllLines(Paths.get("raw_data_clean.txt"),  Charset.defaultCharset()));
        double[][] cv_data = GenerateData.parseData((ArrayList<String>)Files.readAllLines(Paths.get("raw_data_cv.txt"),  Charset.defaultCharset()));
        double[][] test_data = GenerateData.parseData((ArrayList<String>)Files.readAllLines(Paths.get("raw_data_test.txt"),  Charset.defaultCharset()));
        
        FraudDetection predictor = new FraudDetection(data, 0.05);
        TrainDetection.train(predictor, cv_data, cv_labels, 0.05, 1000);
        TrainDetection.test(predictor, test_data, test_labels);
        
        ArrayList<String> triggers = new ArrayList<String>();
        triggers.add("alcohol");
        ArrayList<String> safelist = new ArrayList<String>();
        safelist.add("online");
        Client newClient = new Client(2, triggers, safelist);
        double[] newEntry = {643, 50, 85};
        System.out.println("Add new entires with this format: Item Time Cost Distance");
        Scanner input = new Scanner(System.in);
        while(true)
        {
            String string = input.next();
            double a = Double.parseDouble(input.next());
            double b = Double.parseDouble(input.next());
            double c = Double.parseDouble(input.next());
            double[] nextEntry = {a, b, c};
            newClient.addEntry(predictor, cv_data, cv_labels, 0.5, 1000, nextEntry, string);
            TrainDetection.test(predictor, test_data, test_labels);
        }
    }
}