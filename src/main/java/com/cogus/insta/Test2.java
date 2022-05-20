package com.cogus.insta;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Test2 {
	public static void main(String[] args) {
		String filePath = "D:/uploadCSV/test2.csv";
		
		File file = null;
		BufferedWriter bw = null;
		String NEWLINE = System.lineSeparator(); //줄바꿈
		
		try {
			file = new File(filePath);
			bw = new BufferedWriter(new FileWriter(file));
			
			bw.write("번호, 이름, 지역");
			bw.write(NEWLINE);
			
			bw.write("1, 김철수, 서울");
			bw.write("\n");
			bw.write("2, 김영희, 경기");
			bw.write("\n");
			bw.write("3, 이철희, 경북");
			
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
