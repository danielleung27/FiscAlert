/******************************************************************************
 *  Author:    Daniel Leung
 *  Description: FraudDetection reads in data, creates features, and trains
 * each feature using an anomoly detection algorithm
 ******************************************************************************/

public class FraudDetection {
    private Feature[] features; // feature list
    private double threshold; // epsilon bound to mark anomaly
    private int n; // number of examples trained with
    private double median; // median spending
    
    private static class Feature {
        private double mean; // mean of the feature
        private double sd; // standard deviation of the feature
        private int n; // number of examples trained with
        private Feature(double mean, double sd, int n)
        {
            this.mean = mean;
            this.sd = sd;
            this.n = n;
        }
        private double getMean()
        {
            return mean;
        }
        private double getSD()
        {
            return sd;
        }
        // calculates probability of x in normal distribution
        private double prob(double x)
        {
            double norm = 1/(Math.sqrt(2 * Math.PI) * sd) * 
                Math.exp(-1 * (x - mean)*(x - mean)/(2 * sd * sd));
            return norm;
        }
        
        // adds new data point
        private void addDataPoint(Double entry)
        {
            updateMean(entry);
            updateSD(entry);
            n++;
        }
        
        // updates mean
        private void updateMean(Double entry)
        {
            mean = (mean * n + entry) / (n + 1);
        }
        
        // updates sd
        private void updateSD(Double entry)
        {
            double undoSD = sd * sd * n;
            undoSD += (entry - mean) * (entry - mean);
            sd = Math.sqrt(undoSD / (n + 1));
        }
    }
    
    // constructor takes in data and trains FraudDetection object
    public FraudDetection(double[][] data, double threshold)
    {
        this.threshold = threshold;
        features = new Feature[data.length];
        n = data[0].length;
        for (int i = 0; i < data.length; i++)
        {
            double mean = calculateMean(data[i]);
            double sd = calculateSD(mean, data[i]);
            features[i] = new Feature(mean, sd, n);
        }
    }
    
    // flags the entry if detected as fraud
    public int isFraud(double[] entry)
    {
        double prob = 1;
        System.out.println("------------");
        System.out.println(entry[1]);
        System.out.println(features[1].getMean());
        System.out.println(features[1].getSD());
        System.out.println(features[1].prob(entry[1]));
        System.out.println("------------");
        for(int i = 0; i < features.length; i++)
        {
            prob *= features[i].prob(entry[i]);
        }
        
        if (prob < threshold) return 1;
        else return 0;
    }

    // calculates mean of data
    private double calculateMean(double[] entries)
    {
        double sum = 0;
        for (int i = 0; i < entries.length; i++) 
            sum += entries[i];
        return sum / entries.length; 
    }
    
    // calculates standard devation of inputted data
    private double calculateSD(double mean, double[] entries)
    {
        double sum = 0;
        for (int i = 0; i < entries.length; i++)
            sum += (entries[i] - mean) * (entries[i] - mean);
        return Math.sqrt(sum / entries.length);
    }
    
    // updates epsilon
    public void update(double epsilon)
    {
        this.threshold = epsilon;
    }
    
    // returns threshold
    public double threshold()
    {
        return threshold;
    }
}
