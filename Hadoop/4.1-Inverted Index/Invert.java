import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
        
public class Invert {
        
 public static class Map extends Mapper<LongWritable, Text, Text, Text> {
    private Text word = new Text();
    Text loc = new Text();
        
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        
    	FileSplit fs = (FileSplit) context.getInputSplit();
    	String location = fs.getPath().getName();
    	loc.set(location);
    	String line = value.toString();
        StringTokenizer tokenizer = new StringTokenizer(line.toLowerCase());
        while (tokenizer.hasMoreTokens()) {
            word.set(tokenizer.nextToken().replaceAll("[^A-Za-z0-9]", " "));
            context.write(word, loc);
            
        }
    }
 } 
        
 public static class Reduce extends Reducer<Text, Text, Text, Text> {

    public void reduce(Text key, Iterable<Text> values, Context context) 
      throws IOException, InterruptedException {
        boolean fresh = true;
        StringBuilder sb = new StringBuilder();
        HashMap<String,String> as = new HashMap<String,String>();
        for (Text val : values) {
        	if(!as.containsKey(val.toString())){
            if(!fresh) sb.append(" ");
            else sb.append(" : ");
        	sb.append(val);
        	fresh = false;
        	as.put(val.toString(),"a");
        	}
        }
        context.write(key, new Text(sb.toString()));
    }
 }
        
 public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
        
    Job job = new Job(conf, "Invert");
    job.setJarByClass(Invert.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
        
    job.setMapperClass(Map.class);
    job.setReducerClass(Reduce.class);
        
    job.setInputFormatClass(TextInputFormat.class);
    job.setOutputFormatClass(TextOutputFormat.class);
        
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
    job.waitForCompletion(true);
 }
        
}
