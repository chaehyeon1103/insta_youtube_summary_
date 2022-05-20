package com.cogus.insta.youtube.mapper;

import java.util.List;

import com.cogus.insta.youtube.vo.*;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface YoutubeMapper {
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
	public boolean updateLog(int seq, int status, String message);
	public LogVO getLog(int seq);
	//public  getLogStatus
}
