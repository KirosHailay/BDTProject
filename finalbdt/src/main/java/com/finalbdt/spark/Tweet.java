package com.finalbdt.spark;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;


public class Tweet {
	private String id;
	private String username;
	private String text;
	private boolean isRetweet;
	private String timestamp;
	private String lang;
	private String inReplyToStatusId;
	private List<String> hashtags;
	private String uLoc;
	
	private Tweet() {
		hashtags = new ArrayList<String>();
	}
	
	public static Tweet parseJson(JsonObject o) {
		Tweet t = new Tweet();

		o.get("entities").getAsJsonObject().get("hashtags").getAsJsonArray().forEach(h -> {
			t.hashtags.add(h.getAsJsonObject().get("text").getAsString());
		});

		t.setId(o.get("id").getAsString());
		t.setUsername(o.getAsJsonObject("user").get("screen_name").getAsString());
		t.setText(o.get("text").getAsString());
		t.setLang(o.get("lang").getAsString());
		t.setIsRetweet(t.getText().startsWith("RT @"));
		t.setTimestamp(o.get("timestamp_ms").getAsString());
		t.setInReplyToStatusId(o.get("in_reply_to_status_id").toString());
		t.setULoc(o.getAsJsonObject("user").get("location").toString());
		
		return t;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isRetweet() {
		return isRetweet;
	}
	public void setIsRetweet(boolean is_retweet) {
		this.isRetweet = is_retweet;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	
	public List<String> getHashtags() {
		return hashtags;
	}
	
	public void setULoc(String uloc) {
		if (uloc != null && uloc.equals("null"))
			uloc = null;
		
		this.uLoc = uloc;
	}
	
	public String getULoc() {
		return this.uLoc;
	}

	public byte[] getHashtagsBytes() {
		return String.join(",", hashtags).getBytes();
	}

	public String getInReplyToStatusId() {
		return inReplyToStatusId;
	}
	public void setInReplyToStatusId(String inReplyToStatusId) {
		if (inReplyToStatusId != null && inReplyToStatusId.equals("null"))
			inReplyToStatusId = null;

		this.inReplyToStatusId = inReplyToStatusId;
	}
	public boolean isReply() {
		return inReplyToStatusId != null;
	}
	public String toString() {
		return String.format("@%s tweeted: %s\n (Is Retweet? %s / Is reply? %s) \n Lang: %s, ts: %s\n======================================\n",
				username, text, isRetweet, isReply(), lang, timestamp, inReplyToStatusId);
	}
}
