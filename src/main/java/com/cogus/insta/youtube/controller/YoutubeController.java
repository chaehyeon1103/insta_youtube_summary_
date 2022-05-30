package com.cogus.insta.youtube.controller;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cogus.insta.youtube.vo.LogVO;
import com.cogus.insta.youtube.service.YoutubeService;
import com.cogus.insta.youtube.vo.ChannelVO;
import com.cogus.insta.youtube.vo.CommentVO;
import com.cogus.insta.youtube.vo.LinkedChannelVO;
import com.cogus.insta.youtube.vo.PlaylistItemVO;
import com.cogus.insta.youtube.vo.VideoVO;

import au.com.bytecode.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/youtube")
@RequiredArgsConstructor
public class YoutubeController {

	@Setter(onMethod_=@Autowired)
	YoutubeService service;
	
	@GetMapping("/getData")
	public void getData() {
	}

	//오늘 날짜 get하는 함수 yyMMdd_HHmm
	private String getDate() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd_HHmm");
		return now.format(formatter);
	}

	//오늘 날짜 get하는 함수 yyyyMMdd
	private String getDate2() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		return now.format(formatter);
	}

	//url connection하는 함수
	private JSONObject urlConnection(String uri) throws IOException, ParseException {
		String line;

		//uri 실행
		URL url = new URL(uri);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");

		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
		StringBuilder response = new StringBuilder();

		while((line = br.readLine()) != null) {
			response.append(line);
		}
		br.close();

		JSONParser parser = new JSONParser();

		return (JSONObject) parser.parse(response.toString());
	}

	@ResponseBody
	@GetMapping("/getChannels")
	public boolean getChannels() {
		//오늘의 날짜 get
		String formatedNow = getDate();
		String date = getDate2();

		//등록된 채널 가져옴
		List<LinkedChannelVO> linkedChannel = service.getChannelData();
		List<ChannelVO> channelList = new ArrayList<>();
		
		//logVO 생성
		LogVO log = new LogVO();
		log.setType("channel");
		service.insertLog(log);
		int seq = log.getSeq();
		
		for (LinkedChannelVO linkChannel : linkedChannel) {
			String channelId = linkChannel.getChannelId();
			String apiKey = linkChannel.getApiKey();
			
			ChannelVO channel = new ChannelVO();
			
			//channel 정보 가져오기
			try {
				//String line = null;
				String uri = "https://www.googleapis.com/youtube/v3/channels?part=id,snippet,statistics,contentDetails&id=";
				uri += channelId;
				uri += "&key=";
				uri += apiKey;

				//url connect
				JSONObject obj = urlConnection(uri);

				JSONArray items = (JSONArray) obj.get("items");
				JSONObject item = (JSONObject) items.get(0);

				//id get
				channel.setId(item.get("id").toString());
				
				//snippet get
				JSONObject snippet = (JSONObject) item.get("snippet");
				channel.setTitle(snippet.get("title").toString());
				channel.setDescription(snippet.get("description").toString());
				channel.setPublishedAt(snippet.get("publishedAt").toString());
				channel.setCountry(snippet.get("country").toString());
				
				//thumbnail get
				JSONObject thumbnail = (JSONObject) snippet.get("thumbnails");
				JSONObject medium = (JSONObject) thumbnail.get("medium");
				channel.setThumbnails(medium.get("url").toString());
				
				//contentDetails get
				JSONObject contentDetails = (JSONObject) item.get("contentDetails");
				JSONObject relatedPlaylists = (JSONObject) contentDetails.get("relatedPlaylists");
				channel.setUploads(relatedPlaylists.get("uploads").toString());
				
				//statistics get
				JSONObject statistics = (JSONObject) item.get("statistics");
				channel.setViewCount(statistics.get("viewCount").toString());
				channel.setSubscriberCount(statistics.get("subscriberCount").toString());
				channel.setHiddenSubscriberCount(statistics.get("hiddenSubscriberCount").toString());
				channel.setVideoCount(statistics.get("videoCount").toString());
				
			} catch (Exception e) {
				//err 메세지 저장
				service.updateLog(seq, "", 9, "url로부터 channel 정보를 가져오지 못했습니다.");
				return false;
			}
			channelList.add(channel);
			service.updateLinkedChannelInfo(channel.getId(), channel.getUploads());
		}
		
		String filePath = "D:\\uploadYoutubeCsv/"+formatedNow+"channelData.csv";
		
		//csv 파일 변환
		try {
            CSVWriter cw = new CSVWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8),',', CSVWriter.NO_QUOTE_CHARACTER);
			try (cw) {
				cw.writeNext(new String[]{"\ufeff"});
				cw.writeNext(new String[]{"id", "title", "description", "publishedAt", "thumbnails", "country", "uploads", "viewCount", "subscriberCount", "hiddenSubscriberCount", "videoCount"});
				for (ChannelVO channel : channelList) {
					channel.setDescription(channel.getDescription().replace("\n", " "));
					channel.setDescription(channel.getDescription().replace(",", " "));
					cw.writeNext(new String[]{channel.getId(), channel.getTitle(), channel.getDescription(), channel.getPublishedAt(), channel.getThumbnails(),
							channel.getCountry(), channel.getUploads(), channel.getViewCount(), channel.getSubscriberCount(), channel.getHiddenSubscriberCount(), channel.getVideoCount()});
				}
			} catch (Exception e) {
				//err 메세지 저장
				service.updateLog(seq, "", 9, "channel 정보를 가져와 csv를 만드는 중에 문제가 생겼습니다.");
				return false;
			}
		} catch (Exception e) {
			//err 메세지 저장
			service.updateLog(seq, "", 9, "channel.csv 파일 변환에 문제가 있습니다.");
			return false;
        }

		//이미 수집 진행되었을 때는 false
		if(service.alreadySummary(date, "channel") < 1) {
			//channel 정보 db 저장
			if(service.insertChannel(filePath)) {
				service.updateLog(seq, formatedNow+"channelData.csv", 1, "");
			} else {
				//err 메세지 저장
				service.updateLog(seq, formatedNow+"channelData.csv", 9, "channel.csv 파일을 db에 저장하지 못했습니다.");
				return false;
			}
		} else {
			//err 메세지 저장
			service.updateLog(seq, formatedNow+"channelData.csv", 9, "이미 오늘의 데이터가 정상적으로 수집되었습니다.");
			return false;
		}
		return true;
	}
	
	@ResponseBody
	@GetMapping("/getVideos")
	public boolean getPlaylistItems() {
		//오늘의 날짜 get
		String formatedNow = getDate();
		String date = getDate2();

		//등록된 channelInfo 가져오기
		List<LinkedChannelVO> linkedChannel = service.getChannelData();
		List<PlaylistItemVO> playlistList = new ArrayList<>();
		String apiKey = null;
		
		//logVO 생성
		LogVO log = new LogVO();
		log.setType("playlist");
		service.insertLog(log);
		int seqq = log.getSeq();
		
		for (LinkedChannelVO linkChannel : linkedChannel) {
			String playlistId = linkChannel.getPlaylistId();
			apiKey = linkChannel.getApiKey();
			String pageToken = null;
			int playlistCnt = 0;

			//pageToken 이용해 100개만 수집
			while(true) {
				//playlistItem 정보 가져오기
				try {
					String uri = "https://www.googleapis.com/youtube/v3/playlistItems?part=id,snippet,contentDetails&playlistId=";
					uri += playlistId;
					if (pageToken == null) {
						uri += "&maxResults=50&key=";
					} else {
						uri += "&maxResults=50&pageToken=";
						uri += pageToken;
						uri += "&key=";
					}
					uri += apiKey;

					//url connect
					JSONObject obj = urlConnection(uri);

					JSONArray items = (JSONArray) obj.get("items");

					if (obj.containsKey("nextPageToken")) {
						pageToken = obj.get("nextPageToken").toString();
					} else {
						pageToken = null;
					}

					for (Object itemm : items) {
						playlistCnt++;

						PlaylistItemVO playlist = new PlaylistItemVO();

						JSONObject item = (JSONObject) itemm;

						//id get
						playlist.setId(item.get("id").toString());

						//snippet get
						JSONObject snippet = (JSONObject) item.get("snippet");

						playlist.setPublishedAt(snippet.get("publishedAt").toString());
						playlist.setChannelId(snippet.get("channelId").toString());
						playlist.setTitle(snippet.get("title").toString());
						playlist.setDescription(snippet.get("description").toString());
						playlist.setChannelTitle(snippet.get("channelTitle").toString());
						playlist.setPlaylistId(snippet.get("playlistId").toString());
						playlist.setPosition(snippet.get("position").toString());

						//thumbnail get
						JSONObject thumbnail = (JSONObject) snippet.get("thumbnails");
						JSONObject medium = (JSONObject) thumbnail.get("medium");
						playlist.setThumbnails(medium.get("url").toString());

						//resourceId get
						JSONObject resourceId = (JSONObject) snippet.get("resourceId");
						playlist.setKind(resourceId.get("kind").toString());
						playlist.setVideoId(resourceId.get("videoId").toString());

						playlistList.add(playlist);
					}
				} catch (Exception e) {
					//err 메세지 저장
					service.updateLog(seqq, "", 9, "url로부터 playlist 정보를 가져오지 못했습니다.");
					return false;
				}
				//갯수가 100개 이상이면 while문 탈출
				if(playlistCnt == 100 || pageToken == null) {
					break;
				}
			}
		}
		
		String filePath = "D:\\uploadYoutubeCsv/"+formatedNow+"playlistData.csv";
		
		//csv 파일 변환
		try {
			CSVWriter cw = new CSVWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8),',', CSVWriter.NO_QUOTE_CHARACTER);
			try (cw) {
				cw.writeNext(new String[]{"\ufeff"});
				cw.writeNext(new String[]{"id", "publishedAt", "channelId", "title", "description", "thumbnails", "channelTitle", "playlistId", "position", "kind", "videoId"});
				for (PlaylistItemVO playlist : playlistList) {
					playlist.setDescription(playlist.getDescription().replace("\n", " "));
					playlist.setDescription(playlist.getDescription().replace(",", " "));
					playlist.setTitle(playlist.getTitle().replace(",", " "));
					cw.writeNext(new String[]{playlist.getId(), playlist.getPublishedAt(), playlist.getChannelId(), playlist.getTitle(), playlist.getDescription(),
							playlist.getThumbnails(), playlist.getChannelTitle(), playlist.getPlaylistId(), playlist.getPosition(), playlist.getKind(), playlist.getVideoId()});
				}
			} catch (Exception e) {
				//err 메세지 저장
				service.updateLog(seqq, "", 9, "playlist 정보를 가져와 csv를 만드는 중에 문제가 생겼습니다.");
				return false;
			}
		} catch (Exception e) {
			//err 메세지 저장
			service.updateLog(seqq, "", 9, "playlist.csv 파일 변환에 문제가 있습니다.");
			return false;
		}

		//이미 수집 진행되었다면 false
		//상위 데이터 수집이 안되었다면 false
		if(service.alreadySummary(date, "playlist") < 1) {
			service.alterAutoIncrement("playlistItem");

			//playlist 정보 db 저장
			if(service.insertPlaylist(filePath)) {
				service.updateLog(seqq, formatedNow+"playlistData.csv", 1, "");
			} else {
				//err 메세지 저장
				service.updateLog(seqq, formatedNow+"playlistData.csv", 9, "playlist.csv 파일을 db에 저장하지 못했습니다.");
				return false;
			}
		} else if(service.alreadySummary(date, "playlist") >= 1) {
			//err 메세지 저장
			service.updateLog(seqq, formatedNow+"playlistData.csv", 9, "이미 오늘의 데이터가 정상적으로 수집되었습니다.");
			return false;
		} else if(service.alreadySummary(date, "channel") < 1) {
			//err 메세지 저장
			service.updateLog(seqq, formatedNow+"playlistData.csv", 9, "상위 channel의 데이터가 아직 수집되지 않았습니다.");
			return false;
		}


		//-----------------------video 저장------------------------

		log = new LogVO();
		log.setType("video");
		service.insertLog(log);
		seqq = log.getSeq();

//		videoId 50개 한 번에 api로 보내기 위해 문자열 생성
//		StringBuilder video1 = new StringBuilder();
//		StringBuilder video2 = new StringBuilder();
//		List<Integer> seqList = new ArrayList<>();
//
//		for (int i=0; i<100; i++) {
//			if(i<49) video1.append(playlistList.get(i).getVideoId()).append(",");
//			else if(i<50) video1.append(playlistList.get(i).getVideoId());
//			else if(i<99) video2.append(playlistList.get(i).getVideoId()).append(",");
//			else video2.append(playlistList.get(i).getVideoId());
//
//			//seq list에 따로 저장
//			seqList.add(playlistList.get(i).getSeq());
//		}
//		List<String> videoIdList = new ArrayList<>();
//		videoIdList.add(video1.toString());
//		videoIdList.add(video2.toString());

		//video 정보 가져오기
		List<VideoVO> videoList = new ArrayList<>();
		playlistList = service.getPlaylist(date);

		//videoId 50개 한 번에 api로 보내기 위해 문자열 생성
		List<Integer> seqList = new ArrayList<>();
		List<String> videoIdList = new ArrayList<>();
		int cnt = 0;

		for(int i=0; i<(playlistList.size()/50); i++) {
			StringBuilder video = new StringBuilder();
			for(int j=1; j<=50; j++) {
				if(j < 50) {
					video.append(playlistList.get(cnt).getVideoId()).append(",");
				} else {
					video.append(playlistList.get(cnt).getVideoId());
				}
				seqList.add(playlistList.get(cnt).getSeq());
				cnt++;
			}
			videoIdList.add(video.toString());
		}
		int seqNo = 0;

		//video id 문자열이 들은 list size만큼 for문 실행
		for (String videoIdStr : videoIdList) {
			try {
				String uri = "https://www.googleapis.com/youtube/v3/videos?part=id,snippet,statistics&id=";
				uri += videoIdStr;
				uri += "&key=";
				uri += apiKey;

				//url connect
				JSONObject obj = urlConnection(uri);

				JSONArray items = (JSONArray) obj.get("items");

				for (Object itemm : items) {
					VideoVO video = new VideoVO();
					video.setForSeq(seqList.get(seqNo++));

					JSONObject item = (JSONObject) itemm;

					//id get
					video.setId(item.get("id").toString());

					//snippet get
					JSONObject snippet = (JSONObject) item.get("snippet");
					video.setCategoryId(snippet.get("categoryId").toString());
					video.setLiveBroadcastContent(snippet.get("liveBroadcastContent").toString());
					video.setDefaultLanguage(snippet.get("defaultLanguage").toString());
					video.setDefaultAudioLanguage(snippet.get("defaultAudioLanguage").toString());

					//tags get
					if(snippet.containsKey("tags")) {
						StringBuilder tagss = new StringBuilder();
						JSONArray tags = (JSONArray) snippet.get("tags");
						for (Object tag : tags) {
							tagss.append(tag.toString()).append(" ");
						}
						video.setTags(tagss.toString());
					}

					//statistics get
					JSONObject statistics = (JSONObject) item.get("statistics");
					video.setViewCount(statistics.get("viewCount").toString());
					video.setLikeCount(statistics.get("likeCount").toString());
					video.setFavoriteCount(statistics.get("favoriteCount").toString());
					video.setCommentCount(statistics.get("commentCount").toString());

					videoList.add(video);
				}
			} catch (Exception e) {
				e.printStackTrace();
				//err 메세지 저장
				service.updateLog(seqq, "", 9, "url로부터 video 정보를 가져오지 못했습니다.");
				return false;
			}
		}

		filePath = "D:\\uploadYoutubeCsv/"+formatedNow+"videoData.csv";

		//csv 파일 변환
		try {
			CSVWriter cw = new CSVWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8),',', CSVWriter.NO_QUOTE_CHARACTER);
			try (cw) {
				cw.writeNext(new String[]{"\ufeff"});
				cw.writeNext(new String[]{"forSeq", "id", "tags", "categoryId", "liveBroadcastContent", "defaultLanguage", "defaultAudioLanguage", "viewCount", "likeCount", "favoriteCount", "commentCount"});
				for (VideoVO video : videoList) {

					if(video.getTags() != null) {
						video.setTags(video.getTags().replace("\n", " "));
						video.setTags(video.getTags().replace(",", " "));
					}
					cw.writeNext(new String[]{Integer.toString(video.getForSeq()), video.getId(), video.getTags(), video.getCategoryId(), video.getLiveBroadcastContent(), video.getDefaultLanguage(),
							video.getDefaultAudioLanguage(), video.getViewCount(), video.getLikeCount(), video.getFavoriteCount(), video.getCommentCount()});
				}
			} catch (Exception e) {
				//err 메세지 저장
				service.updateLog(seqq, "", 9, "video 정보를 가져와 csv를 만드는 중에 문제가 생겼습니다.");
				return false;
			}
		} catch (Exception e) {
			//err 메세지 저장
			service.updateLog(seqq, "", 9, "video.csv 파일 변환에 문제가 있습니다.");
			return false;
		}

		//이미 수집 진행되었다면 false
		if(service.alreadySummary(date, "video") < 1) {
			service.alterAutoIncrement("video");

			//video 정보 db 저장
			if(service.insertVideo(filePath)) {
				service.updateLog(seqq, formatedNow+"videoData.csv", 1, "");
			} else {
				//err 메세지 저장
				service.updateLog(seqq, formatedNow+"videoData.csv", 9, "video.csv 파일을 db에 저장하지 못했습니다.");
				return false;
			}
		} else if(service.alreadySummary(date, "video") >= 1) {
			//err 메세지 저장
			service.updateLog(seqq, formatedNow+"videoData.csv", 9, "이미 오늘의 데이터가 정상적으로 수집되었습니다.");
			return false;
		} else if(service.alreadySummary(date, "playlist") < 1) {
			//err 메세지 저장
			service.updateLog(seqq, formatedNow+"videoData.csv", 9, "상위 playlistitem의 데이터가 아직 수집되지 않았습니다.");
			return false;
		}
		return true;
	}
	
	@ResponseBody
	@GetMapping("/getComments")
	public boolean getComment() {
		//오늘의 날짜 get
		String formatedNow = getDate();
		String date = getDate2();

		//logVO 생성
		LogVO log = new LogVO();
		log.setType("comment");
		service.insertLog(log);
		int seq = log.getSeq();

		String apiKey = null;

		//등록된 channelInfo 가져오기
		List<LinkedChannelVO> linkedChannel = service.getChannelData();
		for (LinkedChannelVO linkChannel : linkedChannel) {
			apiKey = linkChannel.getApiKey();
		}

		//videoId list 생성
		List<String> videoIdList = service.getVideoIdList(date);
		System.out.println(videoIdList);

		//commentList 생성
		List<CommentVO> commentList = new ArrayList<>();
		
		//comment 정보 가져오기
		for (String videoId : videoIdList) {
			try {
				String uri = "https://www.googleapis.com/youtube/v3/commentThreads?part=id,snippet&videoId=";
				uri += videoId;
				uri += "&maxResults=100&key=";
				uri += apiKey;

				//url connect
				JSONObject obj = urlConnection(uri);

				JSONArray items = (JSONArray) obj.get("items");

				for (Object itemm : items) {
					CommentVO comment = new CommentVO();

					JSONObject item = (JSONObject) itemm;

					//id get
					comment.setId(item.get("id").toString());

					//snippet get
					JSONObject snippet = (JSONObject) item.get("snippet");
					comment.setVideoId(snippet.get("videoId").toString());
					comment.setCanReply(snippet.get("canReply").toString());
					comment.setTotalReplyCount(snippet.get("totalReplyCount").toString());
					comment.setIsPublic(snippet.get("isPublic").toString());

					//topLevelComment get
					JSONObject topLevelComment = (JSONObject) snippet.get("topLevelComment");
					comment.setKind(topLevelComment.get("kind").toString());

					//snippet2 get
					JSONObject snippet2 = (JSONObject) topLevelComment.get("snippet");
					comment.setTextOriginal(snippet2.get("textOriginal").toString());
					comment.setAuthorDisplayName(snippet2.get("authorDisplayName").toString());
					comment.setAuthorProfileImageUrl(snippet2.get("authorProfileImageUrl").toString());
					comment.setAuthorChannelUrl(snippet2.get("authorChannelUrl").toString());
					comment.setCanRate(snippet2.get("canRate").toString());
					comment.setViewerRating(snippet2.get("viewerRating").toString());
					comment.setLikeCount(snippet2.get("likeCount").toString());
					comment.setPublishedAt(snippet2.get("publishedAt").toString());
					comment.setUpdatedAt(snippet2.get("updatedAt").toString());

					//authorChannelId get
					if(snippet2.containsKey("authorChannelId")) {
						JSONObject authorChannelId = (JSONObject) snippet2.get("authorChannelId");
						comment.setAuthorChannelId(authorChannelId.get("value").toString());
					}
					commentList.add(comment);
				}
			} catch (Exception e) {
				e.printStackTrace();
				//err 메세지 저장
				service.updateLog(seq, "", 9, "url로부터 comment 정보를 가져오지 못했습니다.");
				return false;
			}
		}

		System.out.println(formatedNow);
		String filePath = "D:\\uploadYoutubeCsv/"+formatedNow+"commentData.csv";
		
		//csv 파일 변환
		try {
			CSVWriter cw = new CSVWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8),',', CSVWriter.NO_QUOTE_CHARACTER);
			try (cw) {
				cw.writeNext(new String[]{"\ufeff"});
				cw.writeNext(new String[]{"id", "videoId", "kind", "textOriginal", "authorDisplayName", "authorProfileImageUrl", "authorChannelUrl", "authorChannelId", "canRate", "viewerRating", "likeCount",
						"publishedAt", "updatedAt", "canReply", "totalReplyCount", "isPublic"});
				for (CommentVO comment : commentList) {
					comment.setTextOriginal(comment.getTextOriginal().replace("\n", " "));
					comment.setTextOriginal(comment.getTextOriginal().replace("\r", " "));
					comment.setTextOriginal(comment.getTextOriginal().replace("\\", " "));
					comment.setTextOriginal(comment.getTextOriginal().replace(",", " "));

					if (comment.getTextOriginal().length() > 20000) {
						comment.setTextOriginal(comment.getTextOriginal().substring(0, 20000));
					}
					comment.setAuthorDisplayName(comment.getAuthorDisplayName().replace(",", " "));

					cw.writeNext(new String[]{comment.getId(), comment.getVideoId(), comment.getKind(), comment.getTextOriginal(), comment.getAuthorDisplayName(), comment.getAuthorProfileImageUrl(),
							comment.getAuthorChannelUrl(), comment.getAuthorChannelId(), comment.getCanRate(), comment.getViewerRating(), comment.getLikeCount(), comment.getPublishedAt(),
							comment.getUpdatedAt(), comment.getCanReply(), comment.getTotalReplyCount(), comment.getIsPublic()});
				}
			} catch (Exception e) {
				//err 메세지 저장
				service.updateLog(seq, "", 9, "comment 정보를 가져와 csv를 만드는 중에 문제가 생겼습니다.");
				return false;
			}
		} catch (Exception e) {
			//err 메세지 저장
			service.updateLog(seq, "", 9, "comment.csv 파일 변환에 문제가 있습니다.");
			return false;
        }

		//이미 수집 진행되었다면 false
		if(service.alreadySummary(date, "comment") < 1) {
			service.alterAutoIncrement("comment");

			//comment 정보 db 저장
			if(service.insertComment(filePath)) {
				service.updateLog(seq, formatedNow+"commentData.csv", 1, "");
			} else {
				//err 메세지 저장
				service.updateLog(seq, formatedNow+"commentData.csv", 9, "comment.csv 파일을 db에 저장하지 못했습니다.");
				return false;
			}
		} else if(service.alreadySummary(date, "comment") >= 1){
			//err 메세지 저장
			service.updateLog(seq, formatedNow+"commentData.csv", 9, "이미 오늘의 데이터가 정상적으로 수집되었습니다.");
			return false;
		} else if(service.alreadySummary(date, "video") < 1) {
			//err 메세지 저장
			service.updateLog(seq, formatedNow+"commentData.csv", 9, "상위 video의 데이터가 아직 수집되지 않았습니다.");
			return false;
		}
		return true;
	}
}
