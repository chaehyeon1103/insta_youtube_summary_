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
public class CommentVO {
	private int seq;
	private String date;
	private String hh;
	private String datehh;
	private String id;
	private String videoId;
	private String kind;
	private String textOriginal;
	private String authorDisplayName;
	private String authorProfileImageUrl;
	private String authorChannelUrl;
	private String authorChannelId;
	private String canRate;
	private String viewerRating;
	private String likeCount;
	private String publishedAt;
	private String updatedAt;
	private String canReply;
	private String totalReplyCount;
	private String isPublic;
	private Date regdate;
}
