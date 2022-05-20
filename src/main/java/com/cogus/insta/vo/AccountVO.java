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
public class AccountVO {
	private int seq;
	private String date;
	private String hh;
	private String datehh;
	private String id;
	private String biography;
	private String followers_count;
	private String follows_count;
	private String media_count;
	private String name;
	private String profile_picture_url;
	private String username;
	private String website;
	private Date regdate;
}
