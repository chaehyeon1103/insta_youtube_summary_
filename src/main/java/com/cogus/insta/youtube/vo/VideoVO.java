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
public class VideoVO {
	private int seq;
	private int forSeq;
	private String date;
	private String hh;
	private String datehh;
	private String id;
	private String tags;
	private String categoryId;
	private String liveBroadcastContent;
	private String defaultLanguage;
	private String defaultAudioLanguage;
	private String viewCount;
	private String likeCount;
	private String favoriteCount;
	private String commentCount;
	private Date regdate;
}
