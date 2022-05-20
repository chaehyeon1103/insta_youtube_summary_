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
public class PlaylistItemVO {
	private int seq;
	private String date;
	private String hh;
	private String datehh;
	private String id;
	private String publishedAt;
	private String channelId;
	private String title;
	private String description;
	private String thumbnails;
	private String channelTitle;
	private String playlistId;
	private String position;
	private String kind;
	private String videoId;
	private Date regdate;
}
