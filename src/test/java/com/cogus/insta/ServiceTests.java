package com.cogus.insta;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cogus.insta.service.InstaService;
import com.cogus.insta.youtube.service.YoutubeService;

import lombok.Setter;

@SpringBootTest
public class ServiceTests {
	@Setter(onMethod_=@Autowired)
	InstaService service;
	
	@Setter(onMethod_=@Autowired)
	YoutubeService yservice;
	
	@Test
	public void test() {
		//service.commentList("17916932801308336", "EAAsA0f7SxZBQBAM5BvxwrE6UHgdiZChl7WZAxhYR1ieGy6qcpte9Ucu8uwwxmZBu4JiqytabUUGukVVeiZBefrAy9DfH2us0BqsSLlPqjOCgoMmDKZCtwlRrYZA37fXlIqaSmAgcYR5wm88Vddh9skJZAFztA3Tt2dysTBQfnnfAoqgMXevWopzh");
	}
	
	@Test
	public void test2() {
		service.updateLog(76, 2, "");
	}
	
	@Test
	public void test3() {
		yservice.insertComment("D:\\uploadYoutubeCsv/220504_1721commentData.csv");
	}
}
