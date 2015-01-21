package project.two.three;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;

public class Functions {

	//Delay thirty seconds	
		public static void delayThirtySec(){
			long startTime = System.currentTimeMillis();
	        while ((System.currentTimeMillis()-startTime)< 30*1000){}
		}
		
		public static void delaySixMin(){
			long startTime = System.currentTimeMillis();
	        while ((System.currentTimeMillis()-startTime)< 6*60*1000){}
		}
		
		public static void delayHundredFiveMin(){
			long startTime = System.currentTimeMillis();
	        while ((System.currentTimeMillis()-startTime)< 105*60*1000){}
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
				    BufferedReader br = new BufferedReader(
	                        new InputStreamReader(connection.getInputStream()));

			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				System.out.println(inputLine);
			}
				    
		        } catch (Exception ex) {
		            ex.printStackTrace();
		        }
		}
	
		
		public static void warmup(String loadGen, String elbDns){
			try {
	            String req= "http://"+loadGen+"/warmup?dns="+elbDns+"&testId=qwer";
	            System.out.println("URL for warmup: " + req);
			    URL url = new URL(req);
			    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			    connection.setRequestMethod("POST");
			    System.out.println("Http response code for warmup: " + connection.getResponseCode());
			    connection.connect();
			    BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

		String inputLine;
		while ((inputLine = br.readLine()) != null) {
			System.out.println(inputLine);
		}
			    
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
		}
		
		public static void begin(String loadGen, String elbDns){
			try {
	            String req= "http://"+loadGen+"/begin-phase-3?dns="+elbDns+"&testId=test";
	            System.out.println("URL for test: " + req);
			    URL url = new URL(req);
			    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			    connection.setRequestMethod("POST");
			    System.out.println("Http response code for activation: " + connection.getResponseCode());
			    connection.connect();
			    BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

		String inputLine;
		while ((inputLine = br.readLine()) != null) {
			System.out.println(inputLine);
		}
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
		}
		
		
}
