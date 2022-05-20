package com.cogus.insta.youtube.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChannelVO {
	private int seq;
	private String date;
	private String hh;
	private String datehh;
	private String id;
	private String title;
	private String description;
	private String publishedAt;
	private String thumbnails;
	private String country;
	private String uploads;
	private String viewCount;
	private String subscriberCount;
	private String hiddenSubscriberCount;
	private String videoCount;
	private Date regdate;
}
