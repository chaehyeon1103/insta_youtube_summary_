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
public class LogVO {
	private int seq;
	private String date;
	private String hh;
	private String datehh;
	private String type;
	private int status;
	private String message;
	private Date regdate;
}
