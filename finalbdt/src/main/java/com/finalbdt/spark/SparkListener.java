package com.finalbdt.spark;

import java.io.IOException;
import java.util.*;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.*;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
//import org.apache.spark.streaming.kafka.KafkaUtils;

import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import com.finalbdt.kafka.KafkaConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.media.jfxmedia.logging.Logger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import scala.Tuple2;

public class SparkListener {
	static Map<String, Object> kafkaParams = new HashMap<String, Object>() {{
		put("bootstrap.servers", "127.0.0.1:9092");
		put("key.deserializer", StringDeserializer.class);
		put("value.deserializer", StringDeserializer.class);
		put("group.id", "use_a_separate_group_id_for_each_stream");
		put("auto.offset.reset", "latest");
		put("enable.auto.commit", false);
	}};
	
	static Set<String> topics = Collections.singleton(KafkaConfig.TOPIC);
	
//	static HBaseTweets hbaseTweets;
//	static{
//		try {
//			hbaseTweets= HBaseTweets.getInstance();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		}
	
	public static void main(String[] args) throws InterruptedException, IOException {
		HBaseTweets hbaseTweets = HBaseTweets.getInstance();
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("FinalBDT");
		JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(10));
		JavaInputDStream<ConsumerRecord<String, String>> stream =

                KafkaUtils.createDirectStream(

                  jssc,
                  LocationStrategies.PreferConsistent(),

                  ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams)

                );
		
		// get the stream of data and save it to hbase
		JavaDStream<String> jdd = stream.map(s -> s.value());
				jdd.map(s -> new Gson().fromJson(s, JsonObject.class))
				.filter(s -> ! s.has("delete"))
				.map(Tweet::parseJson).foreachRDD(rdd -> {
					   rdd.foreach(t -> {
						   System.out.println(t);
						   hbaseTweets.addTweet(t);
					   });
				});
		
		
		
		
		jssc.start();
		jssc.awaitTermination();
	}
}
