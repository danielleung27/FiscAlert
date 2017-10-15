import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.io.Writer;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateData
{
    // constants
    //============================================================================
    private static int[] cv_labels;
    private static final int ANOMALY = 1;
    private static final int NORMAL = 0;
    private static final int NUM_CATEGORIES = 3;
    private static final int NUM_PREVIOUS_ENTRIES = 10;
    private static final int MIN_IN_HOUR = 60;
    private static final int ZERO_PLACEHOLDER = 0;
    private static final int DEFAULT_SEED = 3;
    private static String location = "NY-Albany_42.6526N,073.7562W";
    private static String randLocation1 = "CA-Sacramento_38.5816N,121.4944W";
    private static String randLocation2 = "AL-Montgomery_32.3668N,086.3000W";
    //=============================================================================
    
    public static void main(String[] args) throws FileNotFoundException
    {
    }
    
    public static void outputToFile(ArrayList<String> raw_data, String fileName) throws IOException
    {
        Writer out = new FileWriter(fileName);
        for (int i = 0; i < raw_data.size(); i++)
        {
            out.write(raw_data.get(i));
            out.write("\n");
        }
        out.close();
    }

    private static void print2dArr(double[][] arr)
    {
        for (int c = 0; c < arr[0].length; c++)
        {
            for (int r = 0; r < arr.length; r++)
            {
                System.out.print(arr[r][c] + " ");
            }
            System.out.println();
        }
    }
    
    private static int[] createCvLabels(ArrayList<Integer> list)
    {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        int size = list.remove(0), num;
        Collections.sort(list);
        //System.out.println(list.size() + " " + Arrays.toString(list.toArray()));
        int a=1;
        for (int i = 0; i < list.size(); i++)
        {
            num = list.get(i);
            for (; a < num; a++)
            {
                //System.out.println(0 + " ");
                ret.add(NORMAL);
            }
            //System.out.println(1 + " ");
            ret.add(ANOMALY);
            a++;
        }
        for (int i = list.get(list.size()-1); i < size; i++)
        {
            //System.out.println(0 + " ");
            ret.add(NORMAL);
        }
        int[] retArr = new int[ret.size()];
        for (int i = 0; i < ret.size(); i++)
            retArr[i] = ret.get(i);
        return retArr;
    }
    
    // parse data into usable information
    // rawData.length must be greater than 10
    public static double[][] parseData(ArrayList<String> rawData)
    {
        double[][] parsed = new double[NUM_CATEGORIES][rawData.size()];
        String data;
        String[] prevDatas = new String[NUM_PREVIOUS_ENTRIES];
        double[][] prevEntries = new double[NUM_PREVIOUS_ENTRIES][2];
        for (int i = 0; i < 10; i++)
        {
            // one row of data
            data = rawData.get(i);
            // put into better format
            data = data.replaceAll(",", "_");
            String[] dataAr = data.split("_");
            
            //time in minutes
            parsed[0][i] = Integer.parseInt(dataAr[1].split(":")[0]) * MIN_IN_HOUR + Integer.parseInt(dataAr[1].split(":")[1]);
            // dollar amount
            parsed[1][i] = Double.parseDouble(dataAr[2]);
            
            // place holder for absolute distance differences
            parsed[2][i] = ZERO_PLACEHOLDER;
            prevDatas[i] = data;
        }
        
        for (int i = 10; i < rawData.size(); i++)
        {
            // one row of data
            data = rawData.get(i);
            prevDatas[i%10] = data;
            // put into better format
            data = data.replaceAll(",", "_");
            String[] dataAr = data.split("_");
            
            //time in minutes
            parsed[0][i] = Integer.parseInt(dataAr[1].split(":")[0]) * MIN_IN_HOUR + Integer.parseInt(dataAr[1].split(":")[1]);
            // dollar amount
            parsed[1][i] = Double.parseDouble(dataAr[2]);
            
            double lat = Double.parseDouble(dataAr[4].substring(0, dataAr[4].length()-1));
            double lon = Double.parseDouble(dataAr[5].substring(0, dataAr[5].length()-1));
            
            // absolute distance difference average from last 10 entries
            parsed[2][i] = calculateDistance(lat, lon, prevEntries);
        }
        
        return parsed;
    }
    
    // calculate average distance between previous locations 
    private static double calculateDistance(double lat, double lon, double[][] prevEntries)
    {
        double sum = 0;
        double latDif, lonDif;
        
        for (int i = 0; i < prevEntries.length; i++)
        {
            latDif = lat - prevEntries[i][0];
            lonDif = lon - prevEntries[i][1];
            sum += Math.sqrt(latDif*latDif + lonDif*lonDif);
        }
        
        return (sum/prevEntries.length);
    }
    
    // shift the guassian shift to return a positive value
    private static double gaussShift(Random rand, int seed)
    {
        return (((rand.nextGaussian()%seed)+seed)/(2*seed));
    }
    
    // generate fake data with a few anomalies
    public static ArrayList<String> createAnomalyData(int numEntries, double anomsPerc, int seed) throws FileNotFoundException 
    {
        ArrayList<String> list = new ArrayList<String>();
        cv_labels = new int[numEntries];
        Scanner sc = new Scanner(new File("Locations.txt"));
        // irrelevant initializations for these variables
        double amount = 0;
        int year = 2012, mon=1, day=1;
        int hour = 8, min = 0;
        
        Random rand = new Random(DEFAULT_SEED);
        Random randAnoms = new Random(seed);
        
        for (int i = 0; i < numEntries; i++)
        { 
            if (randAnoms.nextInt(100) <= anomsPerc)
            {
                cv_labels[i] = ANOMALY;
                hour = (int)(gaussShift(rand, DEFAULT_SEED) * 8);
                min = (int)(gaussShift(rand, DEFAULT_SEED) * 59);
            }
            else
            {
                hour = 8 + (int)(gaussShift(rand, DEFAULT_SEED) * 15);
                min = (int)(gaussShift(rand, DEFAULT_SEED) * 59);
            }
            
            if (randAnoms.nextInt(100) <= anomsPerc)
            {
                cv_labels[i] = ANOMALY;
                amount = 1000 + (gaussShift(rand, DEFAULT_SEED) * 1000);
            }
            else
            {
                amount = (gaussShift(rand, DEFAULT_SEED) * 100);
            }
            
            if (randAnoms.nextInt(100) <= anomsPerc)
            {
                cv_labels[i] = ANOMALY;
                if (i%2 == 0)
                    location = randLocation1;
                else
                    location = randLocation2;
            }
            else if (i != 0 && i % (numEntries/3) == 0)
            {
                location = sc.nextLine();
            }
            else
            {
                location = "NY-Albany_42.6526N,073.7562W";
            }
            list.add(String.format("%02d", mon) + "/" + String.format("%02d", day) 
                         + "/" + String.format("%04d", year) + "," + String.format("%02d", hour) 
                         + ":" + String.format("%02d", min) + "," + String.format("%04f", amount) 
                         + "," + location);
            //System.out.println(list.get(i));
            day++;
            if (mon == 2 && day == 28)
            {
                mon++; day = 1;
            }
            else if (day == 30)
            {
                mon++; day = 1;
            }
            if (mon == 13)
            {
                year++; mon = 1;
            }
        }
        sc.close();
        return list;
    }
    
    public static int[] get_cv_labels()
    {
        return cv_labels;
    }
    
    // generate clean fake data for training
    public static ArrayList<String> createCleanData(int numEntries) throws FileNotFoundException 
    {
        ArrayList<String> list = new ArrayList<String>();
        Scanner sc = new Scanner(new File("Locations.txt"));
        double amount = 0;
        int year = 2012, mon=1, day=1;
        int hour = 8, min = 0;
        
        Random rand = new Random(DEFAULT_SEED);
        
        for (int i = 0; i < numEntries; i++)
        { 
            hour = 8 + (int)(gaussShift(rand, DEFAULT_SEED) * 15);
            min = (int)(gaussShift(rand, DEFAULT_SEED) * 59);
            
            amount = gaussShift(rand, DEFAULT_SEED) * 100;
            
            if (i != 0 && i % (numEntries/3) == 0)
                location = sc.nextLine();
            else
                location = "NY-Albany_42.6526N,073.7562W";
            
            list.add(String.format("%02d", mon) + "/" + String.format("%02d", day) 
                         + "/" + String.format("%04d", year) + "," 
                         + String.format("%02d", hour) + ":" + String.format("%02d", min) 
                         + "," + String.format("%04f", amount) + "," + location);
            day++; 
            if (mon == 2 && day == 28)
            {
                mon++; day = 1;
            }
            else if (day == 30)
            {
                mon++; day = 1;
            }
            if (mon == 13)
            {
                year++; mon = 1;
            }
        }
        sc.close();
        return list;
    }
}