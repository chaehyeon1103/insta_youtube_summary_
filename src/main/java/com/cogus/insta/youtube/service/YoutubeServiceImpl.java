package com.cogus.insta.youtube.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cogus.insta.vo.LinkedAccountVO;
import com.cogus.insta.youtube.vo.LogVO;
import com.cogus.insta.youtube.vo.PlaylistItemVO;
import com.cogus.insta.youtube.mapper.YoutubeMapper;
import com.cogus.insta.youtube.vo.ChannelVO;
import com.cogus.insta.youtube.vo.LinkedChannelVO;

import lombok.Setter;

@Service
public class YoutubeServiceImpl implements YoutubeService{

//	@Setter(onMethod_=@Autowired)
//	private YoutubeMapper mapper;
	@Autowired
	private YoutubeMapper mapper;

	@Override
	public List<LinkedChannelVO> getChannelData() {
		return mapper.getChannelData();
	}
	
	@Override
	public List<String> getPlaylistId() {
		return mapper.getPlaylistId();
	}

	@Override
	public boolean insertChannel(String filePath) {
		return mapper.insertChannel(filePath);
	}

	@Override
	public boolean insertPlaylist(String filePath) {
		return mapper.insertPlaylist(filePath);
	}
	
	@Override
	public boolean insertVideo(String filePath) {
		return mapper.insertVideo(filePath);
	}
	
	@Override
	public boolean insertComment(String filePath) {
		return mapper.insertComment(filePath);
	}

	@Override
	public void alterAutoIncrement(String table) {
		mapper.alterAutoIncrement(table);
	}

	@Override
	public boolean updateLinkedChannelInfo(String channelId, String playlistId) {
		return mapper.updateLinkedChannelInfo(channelId, playlistId);
	}

//	@Override
//	public int getSeq(String datehh, String videoId) {
//		return mapper.getSeq(datehh, videoId);
//	}

	@Override
	public List<PlaylistItemVO> getPlaylist(String date) {
		return mapper.getPlaylist(date);
	}

	@Override
	public List<String> getVideoIdList(String date) {
		return mapper.getVideoIdList(date);
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
}
