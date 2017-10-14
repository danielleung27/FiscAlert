/******************************************************************************
 *  Author:    Daniel Leung
 *  Description: FraudDetection reads in data, creates features, and trains
 * each feature using an anomoly detection algorithm
 ******************************************************************************/

public class FraudDetection {
    private Feature[] features; // feature list
    private double threshold; // epsilon bound to mark anomaly
    
    private static class Feature {
        private double mean; // mean of the feature
        private double sd; // standard deviation of the feature
        private Feature(double mean, double sd)
        {
            this.mean = mean;
            this.sd = sd;
        }
        // calculates probability of x in normal distribution
        private double prob(double x)
        {
            double norm = 1/(Math.sqrt(2 * Math.PI) * sd) * 
                Math.exp(-1 * (x - mean)*(x - mean)/(2 * sd * sd));
            return norm;
        }
    }
    
    // constructor takes in data and trains FraudDetection object
    public FraudDetection(double[][] data, double threshold)
    {
        features = new Feature[data.length];
        for (int i = 0; i < data.length; i++)
        {
            double mean = calculateMean(data[i]);
            double sd = calculateSD(mean, data[i]);
            features[i] = new Feature(mean, sd);
        }
    }
    
    // flags the entry if detected as fraud
    public boolean isFraud(double[] entry)
    {
        double prob = 1;
        for(int i = 0; i < features.length; i++)
        {
            prob *= features[i].prob(entry[i]);
        }
        return prob < threshold;
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
