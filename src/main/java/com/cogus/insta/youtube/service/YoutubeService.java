package com.cogus.insta.youtube.service;

import java.util.List;

import com.cogus.insta.vo.LinkedAccountVO;
import com.cogus.insta.youtube.vo.LogVO;
import com.cogus.insta.youtube.vo.PlaylistItemVO;
import com.cogus.insta.youtube.vo.ChannelVO;
import com.cogus.insta.youtube.vo.LinkedChannelVO;

public interface YoutubeService {
	//등록된 채널 정보 가져옴
	public List<LinkedChannelVO> getChannelData();
	public List<String> getPlaylistId();
	
	public boolean insertChannel(String filePath);
	public boolean insertPlaylist(String filePath);
	public boolean insertVideo(String filePath);
	public boolean insertComment(String filePath);
	public void alterAutoIncrement(String table);
	
	public boolean updateLinkedChannelInfo(String channelId, String playlistId);

	public List<PlaylistItemVO> getPlaylist(String date);
	public List<String> getVideoIdList(String date);
	
	public int insertLog(LogVO log);
	public boolean updateLog(int seq, String file, int status, String message);
	public LogVO getLog(int seq);

	//이미 수집 진행되었을 때
	public int alreadySummary(String date, String type);
}
