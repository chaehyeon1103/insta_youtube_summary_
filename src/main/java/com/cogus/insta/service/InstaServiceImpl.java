package com.cogus.insta.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cogus.insta.mapper.InstaMapper;
import com.cogus.insta.vo.AccountVO;
import com.cogus.insta.vo.CommentVO;
import com.cogus.insta.vo.LinkedAccountVO;
import com.cogus.insta.vo.LogVO;
import com.cogus.insta.vo.MediaVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import lombok.Setter;

@Service
public class InstaServiceImpl implements InstaService{

	@Setter(onMethod_=@Autowired)
	private InstaMapper mapper;
	
	@Override
	public boolean insert(LinkedAccountVO account) {
		return mapper.insert(account);
	}

	@Override
	public int idCheck(String businessId) {
		return mapper.idCheck(businessId);
	}

	@Override
	public List<LinkedAccountVO> getUserData() {
		return mapper.getUserData();
	}

	@Override
	public boolean insertUser(String filePath) {
		return mapper.insertUser(filePath);
	}

	@Override
	public boolean insertMedia(String filePath) {
		return mapper.insertMedia(filePath);
	}
	
	@Override
	public boolean insertComment(String filePath) {
		return mapper.insertComment(filePath);
	}
	
	@Override
	public int insertLog(LogVO log) {
		return mapper.insertLog(log);
	}
	
	@Override
	public boolean updateLog(int seq, int status, String message) {
		return mapper.updateLog(seq, status, message);
	}
	
	@Override
	public LogVO getLog(int seq) {
		return mapper.getLog(seq);
	}

	//account 정보 가져오기
	@Override
	public AccountVO accountList(int seq, String businessId, String accessToken) {
		AccountVO account = new AccountVO();
		
		String line = null;
		String uri = "https://graph.facebook.com/v13.0/";
		uri += businessId;
		uri += "?fields=id,biography,followers_count,follows_count,media_count,name,profile_picture_url,username,website&access_token=";
		uri += accessToken;
		
		try {
			//uri 실행
			URL url = new URL(uri);
			URLConnection conn = url.openConnection();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			
			line = br.readLine();
			
			ObjectMapper mapper = new ObjectMapper();
			account = mapper.readValue(line, AccountVO.class);
			
			System.out.println(account);
			
		} catch (Exception e) { 
			//err 메세지 저장
			String err = "url로부터 account 정보를 가져오지 못했습니다.";
			mapper.updateLog(seq, 9, err);
			e.printStackTrace(); 
		} 
		return account;
	}

	//media 정보 가져오기
	@Override
	public List<MediaVO> mediaList(int seq, String businessId, String accessToken) {
		List<MediaVO> mediaList = new ArrayList<MediaVO>();
		MediaVO media = new MediaVO();
		
		String line = null;
		String uri = "https://graph.facebook.com/v13.0/";
		uri += businessId;
		uri += "/media?fields=caption,comments_count,id,is_comment_enabled,like_count,media_product_type,media_type,media_url,owner,permalink,shortcode,thumbnail_url,timestamp,username&access_token=";
		uri += accessToken;
		
		try {
			//uri 실행
			URL url = new URL(uri);
			URLConnection conn = url.openConnection();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			line = br.readLine();
			System.out.println(line);

			JSONParser parser = new JSONParser(); 
			JSONObject obj = (JSONObject) parser.parse(line); 
			
			JSONArray response = (JSONArray) obj.get("data");
			
			for(int i=0; i<response.size(); i++) {
				System.out.println(response.get(i));
				
				JSONObject mmedia = (JSONObject) response.get(i);
				
				JSONObject owner = (JSONObject) mmedia.get("owner");
				mmedia.replace("owner", owner.get("id"));
				
				System.out.println(mmedia);
				
				Gson gson = new Gson();
				media = gson.fromJson(mmedia.toString(), MediaVO.class);
				
				mediaList.add(media);
			}
			
		} catch (Exception e) { 
			//err 메세지 저장
			String err = "url로부터 media 정보를 가져오지 못했습니다.";
			mapper.updateLog(seq, 9, err);
			e.printStackTrace(); 
		} 
		return mediaList;
	}

	//commentId 가져오기
	@Override
	public List<String> commentIdList(int seq, String businessId, String accessToken) {
		List<String> commentIdList = new ArrayList<>();
		
		String line = null;
		String uri = "https://graph.facebook.com/v13.0/";
		uri += businessId;
		uri += "/media?fields=comments&access_token=";
		uri += accessToken;
		
		try {
			//uri 실행
			URL url = new URL(uri);
			URLConnection conn = url.openConnection();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			line = br.readLine();
			System.out.println(line);

			JSONParser parser = new JSONParser(); 
			JSONObject obj = (JSONObject) parser.parse(line); 
			
			JSONArray response = (JSONArray) obj.get("data");
			
			//media 수 만큼 for문 실행
			for(int i=0; i<response.size(); i++) {
				System.out.println(response.get(i));
				
				JSONObject media = (JSONObject) response.get(i);
				JSONObject media2 = (JSONObject) media.get("comments");
				JSONArray media3 = (JSONArray) media2.get("data");
				
				//댓글 수 만큼 for문 실행
				for(int j=0; j<media3.size(); j++) {
					System.out.println(media3.get(j));
					
					//댓글 id 추출
					JSONObject comment2 = (JSONObject) media3.get(j);
					String id = (String) comment2.get("id");
					
					commentIdList.add(id);
				}
			}
			
		} catch (Exception e) { 
			//err 메세지 저장
			String err = "url로부터 commentId 정보를 가져오지 못했습니다.";
			mapper.updateLog(seq, 9, err);
			e.printStackTrace(); 
		}
		return commentIdList;
	}

	//comment 정보 및 replies id (대댓글 id) 가져오기
	@Override
	public Map<String, Object> commentList(int seq, String commentId, String accessToken) {
		Map<String, Object> map = new HashMap<>();
		CommentVO comment = new CommentVO();
		List<String> repliesList = new ArrayList<>();
		
		String line = null;
		String uri = "https://graph.facebook.com/v13.0/";
		uri += commentId;
		uri += "?fields=from,id,parent_id,replies,text,timestamp,username,media&access_token=";
		uri += accessToken;
		
		try {
			//uri 실행
			URL url = new URL(uri);
			URLConnection conn = url.openConnection();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			line = br.readLine();
			System.out.println(line);

			JSONParser parser = new JSONParser(); 
			JSONObject obj = (JSONObject) parser.parse(line); 
			
			JSONObject from = (JSONObject) obj.get("from");
			obj.put("fromId", from.get("id"));
			
			obj.put("isReplies", 0);

			JSONObject mediaId = (JSONObject) obj.get("media");
			obj.put("mediaId", mediaId.get("id"));

			Gson gson = new Gson();
			comment = gson.fromJson(obj.toString(), CommentVO.class);
			
			//commentVO map에 저장
			System.out.println(comment);
			map.put("comment", comment);
			
			//대댓글이 있을 경우(replies)
			if(obj.keySet().contains("replies")) {
				JSONObject replies = (JSONObject) obj.get("replies");
				JSONArray replies1 = (JSONArray) replies.get("data");
				
				for(int i=0; i<replies1.size(); i++) {
					JSONObject replies2 = (JSONObject) replies1.get(i);
					repliesList.add((String) replies2.get("id"));
				}
				
				//replies id map에 저장
				map.put("repliesId", repliesList);
			}
			
		} catch (Exception e) { 
			//err 메세지 저장
			String err = "url로부터 comment 정보를 가져오지 못했습니다.";
			mapper.updateLog(seq, 9, err);
			e.printStackTrace(); 
		} 
		return map;
	}

	//replies 정보 가져오기
	@Override
	public CommentVO repliesList(int seq, String repliesId, String accessToken) {
		CommentVO comment = new CommentVO();
		
		String line = null;
		String uri = "https://graph.facebook.com/v13.0/";
//		String uri = "1";
		uri += repliesId;
		uri += "?fields=from,id,parent_id,text,timestamp,username,media&access_token=";
		uri += accessToken;
		
		try {
			//uri 실행
			URL url = new URL(uri);
			URLConnection conn = url.openConnection();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			line = br.readLine();
			System.out.println(line);

			JSONParser parser = new JSONParser(); 
			JSONObject obj = (JSONObject) parser.parse(line); 
			
			JSONObject from = (JSONObject) obj.get("from");
			obj.put("fromId", from.get("id"));
			
			obj.put("isReplies", 1);

			JSONObject mediaId = (JSONObject) obj.get("media");
			obj.put("mediaId", mediaId.get("id"));
			
			Gson gson = new Gson();
			comment = gson.fromJson(obj.toString(), CommentVO.class);
		}  catch (Exception e) {
			//error 메세지 저장
			String err = "url로부터 replies 정보를 가져오지 못했습니다.";
			mapper.updateLog(seq, 9, err);
			e.printStackTrace();
		}
		return comment;
	}

	
}
