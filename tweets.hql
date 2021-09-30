CREATE EXTERNAL TABLE tweets(
	id Bigint,
	username String,
	text String,
	is_retweet boolean,
	ts TIMESTAMP,
	lang String,
	in_reply_to_status_id String,
	hashtags STRING,
	user_loc STRING
)

STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
WITH SERDEPROPERTIES (
	"hbase.columns.mapping" = ":key, ud:usrnm, td:txt, td:isrt, td:ts, td:lng, td:rplyto, td:hstg, ud:uloc"
)

TBLPROPERTIES(
	"hbase.table.name" = "tweets",
	"hbase.mapred.output.outputtable" = "tweets"
);
