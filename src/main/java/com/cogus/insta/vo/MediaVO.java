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
public class MediaVO {
	private int seq;
	private String date;
	private String hh;
	private String datehh;
	private String caption;
	private String comments_count;
	private String id;
	private String is_comment_enabled;
	private String like_count;
	private String media_product_type;
	private String media_type;
	private String media_url;
	private String owner;
	private String permalink;
	private String shortcode;
	private String thumbnail_url;
	private String timestamp;
	private String username;
	private Date regdate;
}
