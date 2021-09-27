package com.finalbdt.spark;

import java.io.IOException;
import java.io.Serializable;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;
 
@SuppressWarnings("serial")
public class HBaseTweets implements Serializable  {

	private static final String TABLE_NAME = "tweets";
	private static final String TWEET_FAM = "td";
	private static final String USER_FAM = "ud";
	
	private static final byte[] TWEET_FAM_BYTES = TWEET_FAM.getBytes();
	private static final byte[] USER_FAM_BYTES = USER_FAM.getBytes();
	private static final byte[] COL_TXT = "txt".getBytes();
	private static final byte[] COL_USRNM = "usrnm".getBytes();
	private static final byte[] COL_ISRT = "isrt".getBytes();
	private static final byte[] COL_LNG = "lng".getBytes();
	private static final byte[] COL_RPLYTO = "rplyto".getBytes();
	private static final byte[] COL_TS = "ts".getBytes();
	private static final byte[] COL_HSTG = "hstg".getBytes();
	private static final byte[] COL_ULOC = "uloc".getBytes();
	
	private static HBaseTweets instance;
	private static Table tweetsTable;
	
	public static HBaseTweets getInstance() throws IOException {
		if (instance != null)
			return instance;
		
		return new HBaseTweets();
	}
	
	private HBaseTweets() throws IOException {
		createTable();
	}
	
	private void createTable() throws IOException {
		Configuration config = HBaseConfiguration.create();

		try
		{
			Connection connection = ConnectionFactory.createConnection(config);
			Admin admin = connection.getAdmin();

			HTableDescriptor table = new HTableDescriptor(TableName.valueOf(TABLE_NAME));
			table.addFamily(new HColumnDescriptor(TWEET_FAM).setCompressionType(Algorithm.NONE));
			table.addFamily(new HColumnDescriptor(USER_FAM).setCompressionType(Algorithm.NONE));

			System.out.println("Checking if table Exists");

			if (admin.tableExists(table.getTableName()))
			{
				System.out.println("Tweets table already exists.... ");
				//return;
				//admin.disableTable(table.getTableName());
				//admin.deleteTable(table.getTableName());
			}
			else {
				System.out.println("Creating tweets table.... ");
				admin.createTable(table);
			}

			System.out.println("Done!");
			
			tweetsTable = connection.getTable(TableName.valueOf(TABLE_NAME));
		} finally {
			//
		}
	}
	
	public void addTweet(Tweet tweet) throws IOException {
		Put p = new Put(tweet.getId().getBytes());
		
		p.addColumn(USER_FAM_BYTES, COL_USRNM, tweet.getUsername().getBytes());
		p.addColumn(TWEET_FAM_BYTES, COL_TXT, tweet.getText().getBytes());
		p.addColumn(TWEET_FAM_BYTES, COL_ISRT, new Boolean(tweet.isRetweet()).toString().getBytes());
		p.addColumn(TWEET_FAM_BYTES, COL_TS, tweet.getTimestamp().getBytes());
		p.addColumn(TWEET_FAM_BYTES, COL_LNG, tweet.getLang().getBytes());
		p.addColumn(TWEET_FAM_BYTES, COL_HSTG, tweet.getHashtagsBytes());
		if (tweet.getULoc() != null)
			p.addColumn(USER_FAM_BYTES, COL_ULOC, tweet.getULoc().getBytes());
		if (tweet.getInReplyToStatusId() != null)
			p.addColumn(TWEET_FAM_BYTES, COL_RPLYTO, tweet.getInReplyToStatusId().getBytes());
		
		tweetsTable.put(p);
	}
}
