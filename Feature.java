/******************************************************************************
  *  Author:    Daniel Leung
  *  Description: FraudDetection reads in data, creates features, and trains
  * each feature using an anomoly detection algorithm
  ******************************************************************************/

public class Feature {
    private double mean; // mean of the feature
    private double sd; // standard deviation of the feature
    private int n; // number of examples trained with
    
    public Feature(double mean, double sd, int n)
    {
        this.mean = mean;
        this.sd = sd;
        this.n = n;
    }
    public double getMean()
    {
        return mean;
    }
    public double getSD()
    {
        return sd;
    }
    // calculates probability of x in normal distribution
    public double prob(double x)
    {
        double z = (x - mean)/sd;
        double norm = 1/(Math.sqrt(2 * Math.PI)) * Math.exp(-1 * z * z / 2);
        return norm;
    }
    
    // adds new data point
    public void addDataPoint(Double entry)
    {
        updateMean(entry);
        updateSD(entry);
        n++;
    }
    
    // updates mean
    public void updateMean(Double entry)
    {
        mean = (mean * n + entry) / (n + 1);
    }
    
    // updates sd
    public void updateSD(Double entry)
    {
        double undoSD = sd * sd * n;
        undoSD += (entry - mean) * (entry - mean);
        sd = Math.sqrt(undoSD / (n + 1));
    }
}
