/******************************************************************************
 *  Author:    Daniel Leung
 *  Description: FraudDetection reads in data, creates features, and trains
 * each feature using an anomoly detection algorithm
 ******************************************************************************/

public class FraudDetection {
    public Feature[] features; // feature list
    private double threshold; // epsilon bound to mark anomaly
    private int n; // number of examples trained with
    private RunningMedian median; // median spending
    
    // constructor takes in data and trains FraudDetection object
    public FraudDetection(double[][] data, double threshold)
    {
        median = new RunningMedian();
        this.threshold = threshold;
        features = new Feature[data.length];
        n = data[0].length;
        for (int i = 0; i < data.length; i++)
        {
            double mean = calculateMean(data[i]);
            double sd = calculateSD(mean, data[i]);
            features[i] = new Feature(mean, sd, n);
            if (i == 1) calculateMedian(data[i]);
        }
    }
    
    // add a new data point
    public boolean addData(double time, double price, double distance)
    {
        double[] entry = new double[3];
        entry[0] = time;
        entry[1] = price;
        entry[2] = distance;
        features[0].addDataPoint(time);
        features[1].addDataPoint(price);
        features[2].addDataPoint(distance);
        return isFraud(entry) == 1;
    }
    
    // flags the entry if detected as fraud
    public int isFraud(double[] entry)
    {
        double prob = 1;
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
        {
            sum += entries[i];
        }
        return sum / entries.length; 
    }
    
    // calculates median of data
    private void calculateMedian(double[] entries)
    {
        for (int i = 0; i < entries.length; i++) 
        {
            median.add(entries[i]);
        }
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
    
    // returns median
    public double getMedian()
    {
        return median.median();
    }
}
