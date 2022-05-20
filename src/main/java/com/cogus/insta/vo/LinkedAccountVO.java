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
public class LinkedAccountVO {
	private int seq;
	private String accessToken;
	private String pageToken;
	private String businessId;
	private String pageId;
	private String fbName;
	private Date regdate;
	private String status;
}
