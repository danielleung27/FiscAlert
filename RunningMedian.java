/******************************************************************************
  *  Author:    Daniel Leung
  *  Description: FraudDetection reads in data, creates features, and trains
  * each feature using an anomoly detection algorithm
  ******************************************************************************/
import java.util.PriorityQueue;
import java.util.Collections;

public class RunningMedian {
    PriorityQueue<Double> upperQueue;
    PriorityQueue<Double> lowerQueue;
    
    public RunningMedian()
    {
        lowerQueue = new PriorityQueue<Double>(20, Collections.reverseOrder());
        upperQueue = new PriorityQueue<Double>();
        upperQueue.add(Double.POSITIVE_INFINITY);
        lowerQueue.add(Double.NEGATIVE_INFINITY);
    }
    public void add(double num)
    {
        //adding the number to proper heap
        if (num >= upperQueue.peek())
            upperQueue.add(num);
        else
            lowerQueue.add(num);
        //balancing the heaps
        if (upperQueue.size() - lowerQueue.size() == 2)
            lowerQueue.add(upperQueue.poll());
        else if (lowerQueue.size() - upperQueue.size() == 2)
            upperQueue.add(lowerQueue.poll());
    }
    public double median()
    {
        //returning the median
        if(upperQueue.size() == lowerQueue.size())
            return(upperQueue.peek() + lowerQueue.peek())/2.0;
        else if (upperQueue.size() > lowerQueue.size())
            return upperQueue.peek();
        else
            return lowerQueue.peek();
    }
}
