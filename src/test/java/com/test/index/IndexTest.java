package com.test.index;

import java.io.File;
public class IndexTest {

	
	public static void main(String args[]) throws Exception{
		
		IndexTest test = new IndexTest();
		File file = new File("/Users/zting/Documents/workspace2/search_engine/src/test/resources/templates/test_data.json");
		String content = org.apache.commons.io.FileUtils.readFileToString(file);
		System.out.println(content);
		
	}
}
