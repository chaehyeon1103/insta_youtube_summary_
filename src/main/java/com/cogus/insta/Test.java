package com.cogus.insta;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVWriter;

public class Test {

	public static void main(String[] args) {
		Map<String, Object> hmap = null;
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
         
        hmap = new HashMap<String, Object>();
        hmap.put("one", 1);
        hmap.put("two", "한글1");
        list.add(hmap);
        
        hmap = new HashMap<String, Object>();
        hmap.put("one", 11);
        hmap.put("two", "한글22");
        list.add(hmap);
         
        hmap = new HashMap<String, Object>();
        hmap.put("one", 111);
        hmap.put("two", "한글3");
        list.add(hmap);
        
        try {
            /**
             * csv 파일을 쓰기위한 설정
             * 설명
             * D:\\test.csv : csv 파일저장할 위치+파일명
             * EUC-KR : 한글깨짐설정을 방지하기위한 인코딩설정(UTF-8로 지정해줄경우 한글깨짐)
             * ',' : 배열을 나눌 문자열
             * '"' : 값을 감싸주기위한 문자
             **/
            CSVWriter cw = new CSVWriter(new OutputStreamWriter(new FileOutputStream("D:\\uploadCSV/test.csv"), "EUC-KR"),',', '"');
            cw.writeNext(new String[] { "one", "two"});
            try {
                for(Map<String, Object> m : list) {
                    //배열을 이용하여 row를 CSVWriter 객체에 write
                    cw.writeNext(new String[] { String.valueOf(m.get("one")),String.valueOf(m.get("two"))});
                }  
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //무조건 CSVWriter 객체 close
                cw.close();
            }  
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
