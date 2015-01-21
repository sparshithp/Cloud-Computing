package sparsh.project2.two;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.model.CreateAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.CreateLaunchConfigurationRequest;
import com.amazonaws.services.autoscaling.model.CreateOrUpdateTagsRequest;
import com.amazonaws.services.autoscaling.model.DeleteAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.DeleteLaunchConfigurationRequest;
import com.amazonaws.services.autoscaling.model.PutScalingPolicyRequest;
import com.amazonaws.services.autoscaling.model.PutScalingPolicyResult;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.ComparisonOperator;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.PutMetricAlarmRequest;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.amazonaws.services.cloudwatch.model.Statistic;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.AddTagsRequest;
import com.amazonaws.services.elasticloadbalancing.model.ConfigureHealthCheckRequest;
import com.amazonaws.services.elasticloadbalancing.model.ConfigureHealthCheckResult;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.DeleteLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.HealthCheck;
import com.amazonaws.services.elasticloadbalancing.model.Listener;

public class RunProject2 {

	public static void main(String[] args) throws Exception {
		
		//Get the Account ID and secret key
				Properties properties = new Properties();
				
					properties.load(RunProject2.class.getResourceAsStream("/AwsCredentials.properties"));
				
				
				BasicAWSCredentials sec = new BasicAWSCredentials(properties.getProperty("accessKey"), 
											properties.getProperty("secretKey"));
		
				
				AmazonElasticLoadBalancingClient elb = new AmazonElasticLoadBalancingClient(sec);
				CreateLoadBalancerRequest lbRequest = new CreateLoadBalancerRequest();
				lbRequest.setLoadBalancerName("LoadBalancer");
				List<Listener> listeners = new ArrayList<Listener>(1);
		        listeners.add(new Listener("HTTP", 80, 80));
				lbRequest.setListeners(listeners);
				lbRequest.withSecurityGroups("sg-09e6c96c")
						 .withAvailabilityZones("us-east-1d");
				
				CreateLoadBalancerResult lbResult = elb.createLoadBalancer(lbRequest);
				
				com.amazonaws.services.elasticloadbalancing.model.Tag tag = new com.amazonaws.services.elasticloadbalancing.model.Tag();
                tag.setKey("Project");
                tag.setValue("2.2");
                AddTagsRequest tagReq =  new AddTagsRequest();
                tagReq.withLoadBalancerNames("LoadBalancer")
                	  .withTags(tag);
                
				elb.addTags(tagReq);
				
				Functions.delayThirtySec();
				String elbDns = lbResult.getDNSName();
				System.out.println(elbDns);
				
				
				
				HealthCheck hc =new HealthCheck();
				hc.setTarget("HTTP:80/heartbeat?username=sputtasw");
				hc.setHealthyThreshold(10);
				hc.setInterval(30);
				hc.setTimeout(5);
				hc.setUnhealthyThreshold(2);
				ConfigureHealthCheckRequest hcr = new ConfigureHealthCheckRequest();
				hcr.withHealthCheck(hc)
				   .withLoadBalancerName("LoadBalancer");
				elb.configureHealthCheck(hcr);
				
				
				
				
				AmazonAutoScalingClient asc = new AmazonAutoScalingClient(sec);

				CreateLaunchConfigurationRequest lcRequest = new CreateLaunchConfigurationRequest();
				lcRequest.withInstanceType("m3.medium")
				         .withImageId("ami-ec14ba84")
				         .withKeyName("sp")
				         .withSecurityGroups("MySecurity")
				         .withLaunchConfigurationName("launch");
				 asc.createLaunchConfiguration(lcRequest);
				
				
				 CreateAutoScalingGroupRequest asgRequest = new CreateAutoScalingGroupRequest();
				 asgRequest.withLaunchConfigurationName("launch")
				           .withLoadBalancerNames("LoadBalancer")
				 		   .withHealthCheckType("ELB")
				 		   .withAutoScalingGroupName("autoscaling")
				 		   .withDesiredCapacity(2)
				 		   .withMinSize(1)
				 		   .withMaxSize(5)
				 		   .withAvailabilityZones("us-east-1d")
				 		   .withHealthCheckGracePeriod(300);
				 asc.createAutoScalingGroup(asgRequest);
				
				 com.amazonaws.services.autoscaling.model.Tag tagasc= new com.amazonaws.services.autoscaling.model.Tag();
				 tagasc.setKey("Project");
				 tagasc.setValue("2.2");
				 tagasc.withPropagateAtLaunch(true)
				       .withResourceId("autoscaling")
				       .withResourceType("auto-scaling-group");
				 
				 CreateOrUpdateTagsRequest tagAscReq = new CreateOrUpdateTagsRequest() ;
				 tagAscReq.withTags(tagasc);
				 asc.createOrUpdateTags(tagAscReq);
				
				 
				
				 
				 AmazonCloudWatchClient cloudWatchClient = new AmazonCloudWatchClient(sec); 
				 
				 PutScalingPolicyRequest request = new PutScalingPolicyRequest();
			        request.setAutoScalingGroupName("autoscaling");
			        request.setPolicyName("ScaleUp"); // This scales up so I've put up at the end. 
			        request.setScalingAdjustment(1); // scale up by one
			        request.setAdjustmentType("ChangeInCapacity");
			        request.setCooldown(60);

			        PutScalingPolicyResult result = asc.putScalingPolicy(request);
			        String arn = result.getPolicyARN();
				 
				 
			        
			        String upArn = "arn:aws:autoscaling:xxx"; // from the policy request

			        // Scale Up
			        PutMetricAlarmRequest upRequest = new PutMetricAlarmRequest();
			        upRequest.setAlarmName("AlarmNameup");
			        upRequest.setMetricName("CPUUtilization");
			        

			        List dimensions = new ArrayList();
			        Dimension dimension = new Dimension();
			        dimension.setName("autoscaling");
			        dimension.setValue("autoscaling");
			        upRequest.setDimensions(dimensions);

			        upRequest.setNamespace("AWS/EC2");
			        upRequest.setComparisonOperator(ComparisonOperator.GreaterThanThreshold);
			        upRequest.setStatistic(Statistic.Average);
			        upRequest.setUnit(StandardUnit.Percent);
			        upRequest.setThreshold(80d);
			        upRequest.setPeriod(60);
			        upRequest.setEvaluationPeriods(1);
			       
			        List actions = new ArrayList();
			        actions.add(arn); // This is the value returned by the ScalingPolicy request
			        upRequest.setAlarmActions(actions);
			    
			        cloudWatchClient.putMetricAlarm(upRequest);
			        
			        
			        PutScalingPolicyRequest drequest = new PutScalingPolicyRequest();
			        drequest.setAutoScalingGroupName("autoscaling");
			        drequest.setPolicyName("scaledown"); // This scales up so I've put up at the end. 
			        drequest.setScalingAdjustment(-1); // scale up by one
			        drequest.setAdjustmentType("ChangeInCapacity");
			        drequest.setCooldown(60);

			        PutScalingPolicyResult dresult = asc.putScalingPolicy(drequest);
			        String darn = dresult.getPolicyARN();
			        System.out.println(darn);

			        String downArn = "darn:aws:autoscaling:xxx"; // from the policy request

			        // Scale Up
			        PutMetricAlarmRequest downRequest = new PutMetricAlarmRequest();
			        downRequest.setAlarmName("AlarmName-down");
			        downRequest.setMetricName("CPUUtilization");

			        List ddimensions = new ArrayList();
			        Dimension ddimension = new Dimension();
			        ddimension.setName("autoscaling");
			        ddimension.setValue("autoscaling");
			        downRequest.setDimensions(ddimensions);

			        downRequest.setNamespace("AWS/EC2");
			        downRequest.setComparisonOperator(ComparisonOperator.LessThanThreshold);
			        downRequest.setStatistic(Statistic.Average);
			        downRequest.setUnit(StandardUnit.Percent);
			        downRequest.setThreshold(50d);
			        downRequest.setPeriod(60);
			        downRequest.setEvaluationPeriods(1);
			        
			        
			        
			        List downactions = new ArrayList();
			        downactions.add(darn); // This is the value returned by the ScalingPolicy request
			        downRequest.setAlarmActions(downactions);
			        

			        cloudWatchClient.putMetricAlarm(downRequest);
			        
					 
			        AmazonEC2Client ec2 = new AmazonEC2Client(sec); 
					
					RunInstancesRequest runInst = new RunInstancesRequest();
					
					//Configure Instance Request
					runInst.withImageId("ami-562d853e")
							.withInstanceType("m3.medium")
							.withMinCount(new Integer(1))
							.withMaxCount(new Integer(1))
							.withKeyName("sp")
							.withSecurityGroups("MySecurity");
			
					
					RunInstancesResult inst = ec2.runInstances(runInst); 
					Functions.delayThirtySec();
					
					Instance loadInstance = inst.getReservation().getInstances().get(0);
					String loadGenid= loadInstance.getInstanceId();
					String loadGenDns = Functions.getInstancePublicDnsName(loadGenid,ec2);
					Functions.delayThirtySec();
					Functions.enableMachine(loadGenDns);
					Functions.delayThirtySec();
					Functions.delayThirtySec();
					 
					List<Reservation> reservations = ec2.describeInstances().getReservations();
			        int reservationCount = reservations.size();
					
					for(int i = 0; i < reservationCount; i++) {
				            List<Instance> instances = reservations.get(i).getInstances();
				            int instanceCount = instances.size();
				            
				            for(int j = 0; j < instanceCount; j++) {
				                Instance instance = instances.get(j);
				                ArrayList<Tag> requestTags = new ArrayList<Tag>();
				                requestTags.add(new Tag("Project","2.2"));
				                
				                ArrayList<String> strarr= new ArrayList<String>();
				                strarr.add(instance.getInstanceId());
				                
				                CreateTagsRequest createTagsRequest_requests = new CreateTagsRequest();
				                createTagsRequest_requests.setResources(strarr);
				                createTagsRequest_requests.setTags(requestTags);
				                ec2.createTags(createTagsRequest_requests);
				                           

				            }
				        }
						
					
					Functions.delayThirtySec();
					Functions.delayThirtySec();
					System.out.println("one min");
					Functions.delayThirtySec();
					Functions.delayThirtySec();
					System.out.println("two min");
					Functions.delayThirtySec();
					Functions.delayThirtySec();
					System.out.println("three min");
					Functions.delayThirtySec();
					Functions.delayThirtySec();
					System.out.println("four min");
					Functions.delayThirtySec();
					Functions.delayThirtySec();
					Functions.warmup(loadGenDns, elbDns);
					Functions.delaySixMin();
					Functions.warmup(loadGenDns, elbDns);
					Functions.delaySixMin();
					Functions.warmup(loadGenDns, elbDns);
					Functions.delaySixMin();
					Functions.warmup(loadGenDns, elbDns);
					Functions.delaySixMin();
					Functions.begin(loadGenDns, elbDns);	
					
					Functions.delayFortyFiveMin();
					
					DeleteAutoScalingGroupRequest deleteAutoScalingGroupRequest = new DeleteAutoScalingGroupRequest();
					deleteAutoScalingGroupRequest.setAutoScalingGroupName("autoscaling");
					asc.deleteAutoScalingGroup(deleteAutoScalingGroupRequest);
					
					
					DeleteLaunchConfigurationRequest delLc = new DeleteLaunchConfigurationRequest();
					delLc.setLaunchConfigurationName("launch");
					asc.deleteLaunchConfiguration(delLc);
					
					
					
					
					List<Reservation> term = ec2.describeInstances().getReservations();
			        int termCount = term.size();
			        List<Instance> terminstances = new ArrayList<Instance>() ;
					for(int i = 0; i < termCount; i++) {
				            terminstances = reservations.get(i).getInstances();
				            }
					List<String> instids = new ArrayList<String>();
					for (Instance i : terminstances )
					{
						while (i.getInstanceId()!=loadGenid)
						instids.add(i.getInstanceId());
					}
					
					
					
					TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest(instids);
					ec2.terminateInstances(terminateInstancesRequest);
					
	}

}
