import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class GenerateData
{
	/*public static void main(String[] args) throws FileNotFoundException 
	{
		ArrayList<String> rawAnomData = createAnomalyData();
		//ArrayList<String> rawCleanData = createCleanData();
		
		double[][] parData = parseData(rawAnomData);
		print2dArr(parData);
	}*/
	
	private static void print2dArr(double[][] arr)
	{
		int count = 0;
		for (int r = 0; r < arr.length; r++)
		{
			for (int c = 0; c < arr[0].length; c++)
			{
				System.out.print(arr[r][c] + " ");
			}
			System.out.println();
		}
	}
	
	// parse data into usable information
	// rawData.length must be greater than 10
	private static double[][] parseData(ArrayList<String> rawData)
	{
		double[][] parsed = new double[rawData.size()][4];
		String data;
		String[] prevDatas = new String[10];
		double[][] prevEntries = new double[10][2];
		for (int i = 0; i < 10; i++)
		{
			// one row of data
			data = rawData.get(i);
			// # of entries to follow
			parsed[i][0] = 3;
			//time in minutes
			parsed[i][1] = Double.parseDouble(data.substring(11, 13)) * 60 + Integer.parseInt(data.substring(14, 16));
			// dollar amount
			parsed[i][2] = Double.parseDouble(data.substring(17, 21));
			// place holder for absolute distance differences
			parsed[i][3] = 0;
			prevDatas[i] = data;
		}
		
		for (int i = 10; i < rawData.size(); i++)
		{
			// one row of data
			data = rawData.get(i);
			prevDatas[i%10] = data;
			// # of entries to follow
			parsed[i][0] = 3;
			//time in minutes
			parsed[i][1] = Double.parseDouble(data.substring(11, 13)) * 60 + Integer.parseInt(data.substring(14, 16));
			// dollar amount
			parsed[i][2] = Double.parseDouble(data.substring(17, 21));
			// absolute distance difference average from last 10 entries
			parsed[i][3] = calculateDistance(Double.parseDouble(data.substring(data.length()-18, data.length()-11)), Double.parseDouble(data.substring(data.length()-9, data.length()-1)), prevEntries);
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
	private static ArrayList<String> createAnomalyData() throws FileNotFoundException 
		{
			ArrayList<String> list = new ArrayList<String>();
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
			
			for (int i = 0; i < 100; i++)
			{	
				if (i == 26)
				{
					hour = 2;
					min = 57;
				}
				else if (i == 49)
				{
					hour = 23;
					min = 16;
				}
				else if (i == 27)
				{
					hour = 4;
					min = 12;
				}
				else
				{
					hour = 8 + rand.nextInt(11);
					min = rand.nextInt(59);
				}
				
				if (i == 57)
					amount = 3141;
				else if (i == 83)
					amount = 1500;
				else if (i == 92)
					amount = 5243;
				else
					amount = 5 + rand.nextInt(30);
				
				if (i == 34)
					location = randLocation1;
				else if (i == 63)
					location = randLocation2;
				else if (i != 0 && i % 33 == 0)
					location = sc.nextLine();
				else
					location = "NY-Albany_42.6526N,073.7562W";
				
				//System.out.println(String.format("%02d", mon) + "/" + String.format("%02d", day) + "/" + String.format("%04d", year) + "," + String.format("%02d", hour) + ":" + String.format("%02d", min) + "," + String.format("%04d", amount) + "," + location);
				list.add(String.format("%02d", mon) + "/" + String.format("%02d", day) + "/" + String.format("%04d", year) + "," + String.format("%02d", hour) + ":" + String.format("%02d", min) + "," + String.format("%04d", amount) + "," + location);
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
			return list;
		}
	
	// generate clean fake data for training
	private static ArrayList<String> createCleanData() throws FileNotFoundException 
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
		
		for (int i = 0; i < 100; i++)
		{	
			hour = 8 + rand.nextInt(11);
			min = rand.nextInt(59);
			
			amount = 5 + rand.nextInt(30);
			
			if (i != 0 && i % 33 == 0)
				location = sc.nextLine();
			else
				location = "NY-Albany_42.6526N,073.7562W";
			
			//System.out.println(String.format("%02d", mon) + "/" + String.format("%02d", day) + "/" + String.format("%04d", year) + "," + String.format("%02d", hour) + ":" + String.format("%02d", min) + "," + String.format("%04d", amount) + "," + location);
			list.add(String.format("%02d", mon) + "/" + String.format("%02d", day) + "/" + String.format("%04d", year) + "," + String.format("%02d", hour) + ":" + String.format("%02d", min) + "," + String.format("%04d", amount) + "," + location);
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
		return list;
	}
}
