import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class GenerateData
{
    public static void main(String[] args) throws FileNotFoundException 
    {
        ArrayList<String> rawAnomData = createAnomalyData(100, 3, 5);
        //ArrayList<String> rawCleanData = createCleanData(100);
        
        double[][] parData = parseData(rawAnomData);
        print2dArr(parData);
    }
    
    private static void print2dArr(double[][] arr)
    {
        for (int r = 0; r < arr.length; r++)
        {
            for (int c = 0; c < arr[0].length; c++)
            {
                System.out.print(arr[r][c] + " ");
            }
            System.out.println();
        }
    }
    
    private static void printCvLabels(ArrayList<Integer> list)
    {
    	ArrayList<Integer> ret = new ArrayList<Integer>();
    	int size = list.remove(0), num;
    	Collections.sort(list);
    	System.out.println(list.size() + " " + Arrays.toString(list.toArray()));
    	int a=1;
    	for (int i = 0; i < list.size(); i++)
    	{
    		num = list.get(i);
    		for (; a < num; a++)
    		{
    			System.out.println(0 + " ");
    			ret.add(0);
    		}
    		System.out.println(1 + " ");
			ret.add(1);
			a++;
    	}
    	for (int i = list.get(list.size()-1); i < size; i++)
    	{
			System.out.println(0 + " ");
			ret.add(0);
    	}
    }
    
    // parse data into usable information
    // rawData.length must be greater than 10
    public static double[][] parseData(ArrayList<String> rawData)
    {
        double[][] parsed = new double[rawData.size()][3];
        String data;
        String[] prevDatas = new String[10];
        double[][] prevEntries = new double[10][2];
        for (int i = 0; i < 10; i++)
        {
            // one row of data
            data = rawData.get(i);
            //time in minutes
            parsed[i][0] = Double.parseDouble(data.substring(11, 13)) * 60 + Integer.parseInt(data.substring(14, 16));
            // dollar amount
            parsed[i][1] = Double.parseDouble(data.substring(17, 21));
            // place holder for absolute distance differences
            parsed[i][2] = 0;
            prevDatas[i] = data;
        }
        
        for (int i = 10; i < rawData.size(); i++)
        {
            // one row of data
            data = rawData.get(i);
            prevDatas[i%10] = data;
            //time in minutes
            parsed[i][0] = Double.parseDouble(data.substring(11, 13)) * 60 + Integer.parseInt(data.substring(14, 16));
            // dollar amount
            parsed[i][1] = Double.parseDouble(data.substring(17, 21));
            // absolute distance difference average from last 10 entries
            parsed[i][2] = calculateDistance(Double.parseDouble(data.substring(data.length()-18, data.length()-11)), Double.parseDouble(data.substring(data.length()-9, data.length()-1)), prevEntries);
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
    
    // generate fake data with a few anomalies
    public static ArrayList<String> createAnomalyData(int numEntries, double anomsPerc, int seed) throws FileNotFoundException 
    {
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<Integer> allAnoms = new ArrayList<Integer>();
        Scanner sc = new Scanner(new File("Locations.txt"));
        String location = "NY-Albany_42.6526N,073.7562W";
        String randLocation1 = "CA-Sacramento_38.5816N,121.4944W";
        String randLocation2 = "AL-Montgomery_32.3668N,086.3000W";
        int amount = 0;
        int year = 2012, mon=1, day=1;
        //Date date = Calendar.set(112, 7, 25);
        int hour = 8;
        int min = 0;
        Random rand = new Random(3);
        Random randAnoms = new Random(seed);
        
        for (int i = 0; i < numEntries; i++)
        { 
            if (randAnoms.nextInt(100) <= anomsPerc)
            {
            	allAnoms.add(i);
            	hour = rand.nextInt(8);
                min = rand.nextInt(59);
            }
            else
            {
                hour = 8 + rand.nextInt(15);
                min = rand.nextInt(59);
            }
            
            if (randAnoms.nextInt(100) <= anomsPerc)
            {
            	allAnoms.add(i);
                amount = 1000 + rand.nextInt(1000);
            }
            else
            {
                amount = rand.nextInt(100);
            }
            
            if (randAnoms.nextInt(100) <= anomsPerc)
            {
            	allAnoms.add(i);
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
                         + ":" + String.format("%02d", min) + "," + String.format("%04d", amount) 
                         + "," + location);
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

        allAnoms.add(numEntries);
        printCvLabels(allAnoms);
        return list;
    }
    
    // generate clean fake data for training
    public static ArrayList<String> createCleanData(int numEntries) throws FileNotFoundException 
    {
        ArrayList<String> list = new ArrayList<String>();
        Scanner sc = new Scanner(new File("Locations.txt"));
        String location = "NY-Albany_42.6526N,073.7562W";
        int amount = 0;
        int year = 2012, mon=1, day=1;
        //Date date = Calendar.set(112, 7, 25);
        int hour = 8;
        int min = 0;
        Random rand = new Random(3);
        
        for (int i = 0; i < numEntries; i++)
        { 
            hour = 8 + rand.nextInt(15);
            min = rand.nextInt(59);
            
            amount = rand.nextInt(1000);
            
            if (i != 0 && i % (numEntries/3) == 0)
                location = sc.nextLine();
            else
                location = "NY-Albany_42.6526N,073.7562W";
            
           list.add(String.format("%02d", mon) + "/" + String.format("%02d", day) 
                         + "/" + String.format("%04d", year) + "," 
                         + String.format("%02d", hour) + ":" + String.format("%02d", min) 
                         + "," + String.format("%04d", amount) + "," + location);
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
