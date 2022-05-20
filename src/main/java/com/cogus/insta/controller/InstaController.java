package com.cogus.insta.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.cogus.insta.service.InstaService;
import com.cogus.insta.vo.AccountVO;
import com.cogus.insta.vo.CommentVO;
import com.cogus.insta.vo.LinkedAccountVO;
import com.cogus.insta.vo.LogVO;
import com.cogus.insta.vo.MediaVO;

import au.com.bytecode.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Component
@Controller
@RequestMapping("/insta")
@RequiredArgsConstructor
public class InstaController {

	private static final Logger LOGGER = LoggerFactory.getLogger(InstaController.class);
	
	@Setter(onMethod_=@Autowired)
	InstaService service;
	
	//로그인 페이지 이동
	@GetMapping("/login")
	public String eventGet() {
		System.out.println("로그인 페이지 get");
		return "insta/login";
	}
	
	//계정 등록
	@PostMapping("/add")
	@ResponseBody
	public boolean add(LinkedAccountVO account) {
		boolean result = service.insert(account);
		return result;
	}
	
	//중복된 계정 확인
	@GetMapping("/idCheck")
	@ResponseBody
	public int idCheck(String businessId) {
		return service.idCheck(businessId);
	}
	
	//instagram 정보 수집
	//3hours = 10800000
//	@Scheduled(fixedDelay = 1000)
	@ResponseBody
	@GetMapping("/getData")
	public void getData() throws IOException {
		LOGGER.info("잘 돌아가나");
		
		//logVO 생성
		LogVO log = new LogVO();
		
		
		//등록된 계정들 가져옴
		List<LinkedAccountVO> linkedAccount = service.getUserData();
		
		for (LinkedAccountVO linkAccount : linkedAccount) {
			String businessId = linkAccount.getBusinessId();
			String accessToken = linkAccount.getAccessToken();
			
			//account 정보 read 후 insert
			log.setType("account");
			service.insertLog(log);
			int seq = log.getSeq();
			
			AccountVO account = service.accountList(seq, businessId, accessToken);
			
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd_HHmm");
			String formatedNow = now.format(formatter);
			System.out.println(formatedNow);
			
			String filePath = "D:\\uploadInstaCsv/"+formatedNow+"accountData.csv";
			
			//csv 파일 변환
			try {
	            CSVWriter cw = new CSVWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"),',', CSVWriter.NO_QUOTE_CHARACTER);
	            cw.writeNext(new String[] {"\ufeff"});
	            cw.writeNext(new String[] {"id", "biography", "followers_count", "follows_count", "media_count", "name", "profile_picture_url", "username", "website"});
	            try {
	            	account.setBiography(account.getBiography().replace("\n", " "));
	            	account.setBiography(account.getBiography().replace(",", " "));
	            	cw.writeNext(new String[] { account.getId(), account.getBiography(), account.getFollowers_count(), account.getFollows_count()
	            			, account.getMedia_count(), account.getName(), account.getProfile_picture_url(), account.getUsername(), account.getWebsite()});
	            } catch (Exception e) {
	                log = service.getLog(seq);
	                if(log.getMessage().equals("")) {
	                	service.updateLog(seq, 9, "account 정보를 가져와 csv를 만드는 중에 문제가 생겼습니다.");
	                }
	                e.printStackTrace();
	            } finally {
	                cw.close();
	            }  
	        } catch (Exception e) {
                log = service.getLog(seq);
                if(log.getMessage().equals("")) {
                	service.updateLog(seq, 9, "account.csv 파일 변환에 문제가 있습니다.");
                }
                e.printStackTrace();
	        }
			//데이터가 잘 들어갔다면 log 테이블 status 숫자 변경
			if(service.insertUser(filePath) == true) {
				System.out.println(seq);
				service.updateLog(seq, 2, "");
			} else {
				log = service.getLog(seq);
                if(log.getMessage().equals("")) {
                	service.updateLog(seq, 9, "account.csv 파일을 db에 저장하지 못했습니다.");
                }
			}
			
			//media 정보 read 후 insert
			log.setType("media");
			service.insertLog(log);
			seq = log.getSeq();
			
			List<MediaVO> mediaList = service.mediaList(seq, businessId, accessToken);
			
			filePath = "D:\\uploadInstaCsv/"+formatedNow+"mediaData.csv";
			
			//csv 파일 변환
			try {
	            CSVWriter cw = new CSVWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"),',', CSVWriter.NO_QUOTE_CHARACTER);
	            cw.writeNext(new String[] {"\ufeff"});
	            cw.writeNext(new String[] {"caption", "comments_count", "id", "is_comment_enabled", "like_count", "media_product_type", "media_type", "media_url", "owner", "permalink", "shortcode", "thumbnail_url", "timestamp", "username"});
	            try {
	            	for (MediaVO media : mediaList) {
	            		media.setCaption(media.getCaption().replace("\n", " "));
	            		media.setCaption(media.getCaption().replace(",", " "));
	            		System.out.println(media.getCaption());
	            		cw.writeNext(new String[] { media.getCaption(), media.getComments_count(), media.getId(), media.getIs_comment_enabled(), media.getLike_count(), media.getMedia_product_type(), media.getMedia_type(), media.getMedia_url(), media.getOwner(), media.getPermalink(), media.getShortcode(), media.getThumbnail_url(), media.getTimestamp(), media.getUsername()});
	            	}
	            } catch (Exception e) {
	            	e.getMessage();
	                log = service.getLog(seq);
	                if(log.getMessage().equals("")) {
	                	service.updateLog(seq, 9, "media 정보를 가져와 csv를 만드는 중에 문제가 생겼습니다.");
	                }
	            } finally {
	                cw.close();
	            }  
	        } catch (Exception e) {
	        	e.getMessage();
                log = service.getLog(seq);
                if(log.getMessage().equals("")) {
                	service.updateLog(seq, 9, "media.csv 파일 변환에 문제가 있습니다.");
                }
	        }
			//데이터가 잘 들어갔다면 log 테이블 status 숫자 변경
			if(service.insertMedia(filePath) == true) {
				System.out.println(seq);
				service.updateLog(seq, 2, "");
			} else {
				log = service.getLog(seq);
                if(log.getMessage().equals("")) {
                	service.updateLog(seq, 9, "media.csv 파일을 db에 저장하지 못했습니다.");
                }
			}
			
			
			//comment 정보 read 후 insert
			//comment id 가져오기
			log.setType("comment");
			service.insertLog(log);
			seq = log.getSeq();
			
			List<String> commentIdList = service.commentIdList(seq, businessId, accessToken);
			
			filePath = "D:\\uploadInstaCsv/"+formatedNow+"commentData.csv";
			
			//csv 파일 변환
			try {
				CSVWriter cw = new CSVWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"),',', CSVWriter.NO_QUOTE_CHARACTER);
	            cw.writeNext(new String[] {"\ufeff"});
				cw.writeNext(new String[] {"mediaId", "fromId", "id", "text", "timestamp", "username", "isReplies", "parent_id"});
				try {
					for (String commentId : commentIdList) {
						Map<String, Object> map = new HashMap<>();
						CommentVO comment = new CommentVO();
						List<String> repliesIdList = new ArrayList<>();
						
						//comment 정보 가져오기
						map = service.commentList(seq, commentId, accessToken);
						comment = (CommentVO) map.get("comment");
						
						comment.setText(comment.getText().replace("\n", " "));
						comment.setText(comment.getText().replace(",", " "));
						cw.writeNext(new String[] { comment.getMediaId(), comment.getFromId(), comment.getId(), comment.getText(), comment.getTimestamp(), comment.getUsername(), comment.getIsReplies(), comment.getParent_id()});
						
						//replies 대댓글 정보 read 후 insert
						//replies id 가져오기
						if(map.keySet().contains("repliesId")) {
							repliesIdList = (List<String>) map.get("repliesId");
							for (String repliesId : repliesIdList) {
								//replies 정보 가져오기
								if(service.repliesList(seq, repliesId, accessToken) != null) {
									comment = service.repliesList(seq, repliesId, accessToken);
									comment.setText(comment.getText().replace("\n", " "));
									cw.writeNext(new String[] { comment.getMediaId(), comment.getFromId(), comment.getId(), comment.getText(), comment.getTimestamp(), comment.getUsername(), comment.getIsReplies(), comment.getParent_id()});
								} 
							}
						}
					}
	            } catch (Exception e) {
	                e.getMessage();
	                log = service.getLog(seq);
	                if(log.getMessage().equals("")) {
	                	service.updateLog(seq, 9, "comment 정보를 가져와 csv를 만드는 중에 문제가 생겼습니다.");
	                }
	            } finally {
	                cw.close();
	            }  
	        } catch (Exception e) {
	            e.getMessage();
	            log = service.getLog(seq);
                if(log.getMessage().equals("")) {
                	service.updateLog(seq, 9, "comment.csv 파일 변환에 문제가 있습니다.");
                }
	        }
			//데이터가 잘 들어갔다면 log 테이블 status 숫자 변경
			if(service.insertComment(filePath) == true) {
				System.out.println(seq);
				service.updateLog(seq, 2, "");
			} else {
				log = service.getLog(seq);
                if(log.getMessage().equals("")) {
                	service.updateLog(seq, 9, "comment.csv 파일을 db에 저장하지 못했습니다.");
                }
			}
		}
	}
}
