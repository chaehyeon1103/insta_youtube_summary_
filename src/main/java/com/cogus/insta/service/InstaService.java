package com.cogus.insta.service;

import java.util.List;
import java.util.Map;

import com.cogus.insta.vo.AccountVO;
import com.cogus.insta.vo.CommentVO;
import com.cogus.insta.vo.LinkedAccountVO;
import com.cogus.insta.vo.LogVO;
import com.cogus.insta.vo.MediaVO;

public interface InstaService {
	//계정 등록
	public boolean insert(LinkedAccountVO account);
	
	//중복된 계정 있는지 확인
	public int idCheck(String businessId);
	
	//등록된 계정 정보 가져옴
	public List<LinkedAccountVO> getUserData();
	
	//account, media, comment, log 정보 저장
//	public boolean insertUser(AccountVO account);
	public boolean insertUser(String filePath);
	public boolean insertMedia(String filePath);
	public boolean insertComment(String filePath);
	
	public int insertLog(LogVO log);
	public boolean updateLog(int seq, int status, String message);
	public LogVO getLog(int seq);
	
	//api 호출해 정보 얻어오는 service
	public AccountVO accountList(int seq, String businessId, String accessToken);
	public List<MediaVO> mediaList(int seq, String businessId, String accessToken);
	public List<String> commentIdList(int seq, String businessId, String accessToken);
	public Map<String, Object> commentList(int seq, String commentId, String accessToken);
	public CommentVO repliesList(int seq, String repliesId, String accessToken);
}
