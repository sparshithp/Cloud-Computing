package project.sputtasw;


import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;

public class RunProject {

	public static void main(String[] args) throws Exception {
		//Get the Account ID and secret key
		Properties properties = new Properties();
		properties.load(RunProject.class.getResourceAsStream("/AwsCredentials.properties"));
		
		BasicAWSCredentials bawsc = new BasicAWSCredentials(properties.getProperty("accessKey"), 
									properties.getProperty("secretKey"));

		AmazonEC2Client ec2 = new AmazonEC2Client(bawsc); 
		
		RunInstancesRequest runInst = new RunInstancesRequest();
		
		//Configure Instance Request
		runInst.withImageId("ami-1810b270")
				.withInstanceType("m3.medium")
				.withMinCount(new Integer(1))
				.withMaxCount(new Integer(1))
				.withKeyName("sp")
				.withSecurityGroups("MySecurity");
		
		
		RunInstancesResult inst = ec2.runInstances(runInst); 
		Functions.delayThirtySec();
		
		
		Instance loadInstance = inst.getReservation().getInstances().get(0);
		String id= loadInstance.getInstanceId();
		String loadGenDns = Functions.getInstancePublicDnsName(id,ec2);
		Functions.delayThirtySec();
		Functions.enableMachine(loadGenDns);
		
		RunInstancesRequest runInst1 = new RunInstancesRequest();
		
		
		
		runInst1.withImageId("ami-324ae85a")
		.withInstanceType("m3.medium")
		.withMinCount(new Integer(1))
		.withMaxCount(new Integer(1))
		.withKeyName("sp")
		.withSecurityGroups("MySecurity");

        
        RunInstancesResult inst1 = ec2.runInstances(runInst1);
        Functions.delayThirtySec();
        Instance dataInstance = inst1.getReservation().getInstances().get(0);
        id = dataInstance.getInstanceId();
		String dataDns = Functions.getInstancePublicDnsName(id, ec2);
		Functions.delayThirtySec();
		Functions.enableMachine(dataDns);
		Functions.delayThirtySec();
		Functions.genLoad(loadGenDns, dataDns);
		Functions.delayTwo();
		
		// Scale up
		while (!(Functions.checkEnd(loadGenDns))){
			runInst.withImageId("ami-324ae85a")
			.withInstanceType("m3.medium")
			.withMinCount(new Integer(1))
			.withMaxCount(new Integer(1))
			.withKeyName("sp")
			.withSecurityGroups("MySecurity");

	        
	        inst = ec2.runInstances(runInst);
	        Functions.delayThirtySec();
	        dataInstance = inst.getReservation().getInstances().get(0);
	        id = dataInstance.getInstanceId();
			dataDns = Functions.getInstancePublicDnsName(id, ec2);
			Functions.delayThirtySec();
			Functions.enableMachine(dataDns);
			Functions.delayThirtySec();
			Functions.genLoad(loadGenDns, dataDns);
			Functions.delayTwo();
		}
		
		
		
		//Print out the code
		Functions.getCode(loadGenDns);
		
		
		
        List<Reservation> reservations = ec2.describeInstances().getReservations();
        int reservationCount = reservations.size();

        //Tages for every instance
        for(int i = 0; i < reservationCount; i++) {
            List<Instance> instances = reservations.get(i).getInstances();
            int instanceCount = instances.size();
            
            for(int j = 0; j < instanceCount; j++) {
                Instance instance = instances.get(j);
                ArrayList<Tag> requestTags = new ArrayList<Tag>();
                requestTags.add(new Tag("Project","2.1"));
                
                ArrayList<String> strarr= new ArrayList<String>();
                strarr.add(instance.getInstanceId());
                
                CreateTagsRequest createTagsRequest_requests = new CreateTagsRequest();
                createTagsRequest_requests.setResources(strarr);
                createTagsRequest_requests.setTags(requestTags);
                ec2.createTags(createTagsRequest_requests);
                           

            }
        }
		
		

		
	}
	
	
	
	
	
}
