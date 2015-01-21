package project.sputtasw;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;

public class Functions {

	
//Check if aggregate of 3600 has been achieved

	public static boolean checkEnd(String loadGenDns){
		
		try {
			URL url = new URL("http://"+loadGenDns+"/view-logs?name=result_sputtasw_Pizza.txt");
			 BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		String finline="";
		String line;
		while ((line = in.readLine()) != null) {
			finline += line+"\n";
		}
		
		String[] arr= finline.split("minute");
		String last = arr[arr.length-1];
		String[] sp= last.split("----------------------");
		String[] s =sp[0].split("\n");
		float sum =0;
		for (int i=1;i<s.length;i++){
			sum += Float.parseFloat(s[i].split("\\s+")[4].trim());
		}
		System.out.println("RPS Aggreagte: "+ sum);
		in.close();
		if (sum>3600)
			return true;
		else
			return false;
		}catch (IOException ex){
			ex.printStackTrace();
			return true;
		}
	}
	
//Register data instance on the load generator
	public static void genLoad(String loadDns, String dataDns){
	try{
		String req = "http://"+loadDns+"/part/one/i/want/more?dns="+dataDns+"/&testId=Pizza";
		 URL url = new URL(req);
		 HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		 connection.setRequestMethod("POST");
		 System.out.println("Http response code to add next data centre: " + connection.getResponseCode());
		    connection.connect();
     } catch (Exception ex) {
         ex.printStackTrace();
     }
	}
	
	
	
//Activate the machine
	public static void enableMachine(String dns){
		 try {
	            String req= "http://"+dns+"/username?username=sputtasw";
	            System.out.println("URL for machine activation: " + req);
			    URL url = new URL(req);
			    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			    connection.setRequestMethod("POST");
			    System.out.println("Http response code for activation: " + connection.getResponseCode());
			    connection.connect();
			    
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	}
	
//Delay around two minutes	
	public static void delayTwo(){
		long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis()-startTime)< 135*1000){}
	}
	
//Delay thirty seconds	
	public static void delayThirtySec(){
		long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis()-startTime)< 30*1000){}
	}
	
	
//Get the dns name 
	public static String getInstancePublicDnsName(String instanceId,AmazonEC2Client ec2) {
	    DescribeInstancesResult describeInstancesRequest = ec2.describeInstances();
	    List<Reservation> reservations = describeInstancesRequest.getReservations();
	    Set<Instance> allInstances = new HashSet<Instance>();
	    for (Reservation reservation : reservations) {
	      for (Instance instance : reservation.getInstances()) {
	        if (instance.getInstanceId().equals(instanceId))
	          return instance.getPublicDnsName();
	      }
	    }
	    return null;
	}

	
	
//End of process and note down code	
public static void getCode(String loadDns){
		
		try{
			URL url = new URL("http://"+loadDns+"/view-logs?name=result_sputtasw_Pizza.txt");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			
			 String finline="";
				String line;
				while ((line = in.readLine()) != null) {
					finline += line+"\n";
				}
				String[] sp= finline.split("----------------------");
				String sec= sp[sp.length-2];
				
				
				String[] time = sec.split("\n");
				
				
				String[] fin =sec.split("\\s+");
				System.out.println(time[1]);
				String code =fin[19];
				System.out.println("The code is "+ code);
		    }
		    catch(Exception e){
		    }
		}
	
	
}
