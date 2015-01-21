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

public class Prob {
	static int n;
	static int t;

	public static class Map extends Mapper<LongWritable, Text, Text, Text> {
		private Text word = new Text();
		

		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			String[] main = line.split("\t");
			String phrase = main[0];
			String[] ln = phrase.split(" ");
			String last = ln[ln.length - 1];
			Integer count = Integer.parseInt(main[1]);
			if (count >= t) {

				if (ln.length != 1) {
					String prev = getPrevPhrase(ln);
					String val = last +"\t" +Integer.toString(count);
					word.set(prev);
					Text v = new Text(val);
					context.write(word, v);
				}
			}
		}

		public static String getPrevPhrase(String[] ln) {
			int len = ln.length;
			String prev = ln[0];
			int count = 1;
			while (count < ln.length - 1) {
				prev = prev + " ";
				prev = prev + ln[count];
				count++;
			}
			return prev;
		}

	}

	public static class Reduce extends Reducer<Text, Text, Text, Text> {

		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			String minPhrase="";
			
			for (Text val : values) {
				int min = Integer.MAX_VALUE;
				String line  = val.toString();
				String[] ln = line.split("\t");
				int count = Integer.parseInt(ln[1]);
				if (map.size()<n){
					map.put(ln[0], count);
					continue;
				}
				for(String iter : map.keySet()){
					if(map.get(iter) < min){
						min = map.get(iter);
						minPhrase = iter;
					}
				}
				if(count>min){
					map.remove(minPhrase);
					map.put(ln[0], count);
				}
			}
			List<String> s = sortByValue(map);
			StringBuilder sb = new StringBuilder();
			sb.append(s.get(0));
			for(int i=1; i<s.size(); i++) {
				sb.append("\t");
				sb.append(s.get(i));
			}
			
			context.write(key, new Text(sb.toString()));
		}
		
		public static List<String> sortByValue(HashMap<String,Integer> map){
			List<String> list = new ArrayList<String>(map.keySet());
			List<Integer> m =  new ArrayList<Integer>(map.values());
			for(int i=0; i<list.size()-1;i++){
				for(int j=0; j<list.size()-i-1; j++){
					if(m.get(j)<m.get(j+1)){
						String tmp = list.get(j+1);
						list.set(j+1,list.get(j));
						list.set(j, tmp);
						int t = m.get(j+1);
						m.set(j+1,m.get(j));
						m.set(j, t);
					}
				}
			}
			return list;
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		
		
		Job job = new Job(conf, "Invert");
		job.setJarByClass(Prob.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		t = Integer.parseInt(args[2]);
		n = Integer.parseInt(args[3]);
		
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.waitForCompletion(true);
	}

}
