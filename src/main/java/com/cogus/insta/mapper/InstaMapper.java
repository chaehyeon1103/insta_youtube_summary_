package com.cogus.insta.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.cogus.insta.vo.AccountVO;
import com.cogus.insta.vo.CommentVO;
import com.cogus.insta.vo.LinkedAccountVO;
import com.cogus.insta.vo.LogVO;
import com.cogus.insta.vo.MediaVO;

@Mapper
public interface InstaMapper {
	public boolean insert(LinkedAccountVO account);
	public int idCheck(String businessId);
	
	public List<LinkedAccountVO> getUserData();
	
	//public boolean insertUser(AccountVO account);
	public boolean insertUser(String filePath);
	public boolean insertMedia(String filePath);
	public boolean insertComment(String filePath);
	
	public int insertLog(LogVO log);
	public boolean updateLog(int seq, int status, String message);
	public LogVO getLog(int seq);
}
