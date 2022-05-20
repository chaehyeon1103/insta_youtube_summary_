package com.cogus.insta.vo;

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
	private String mediaId;
	private String fromId;
	private String id;
	private String text;
	private String timestamp;
	private String username;
	private String isReplies;
	private String parent_id;
	private Date regdate;
}
