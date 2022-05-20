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
public class LinkedChannelVO {
	private int seq;
	private String apiKey;
	private String channelId;
	private String playlistId;
	private String title;
	private String description;
	private Date regdate;
	private String status;
}
