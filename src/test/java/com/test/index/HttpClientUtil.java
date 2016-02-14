package com.test.index;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;




public class HttpClientUtil {
	
	public static String doPost(String url,String json) {
		String resData = null;
		CloseableHttpResponse response = null;
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url);
			if(!StringUtils.isBlank(json)){
				StringEntity entity = new StringEntity(json,"utf-8");
				entity.setContentType("application/json");
				httpPost.setEntity(entity);
			}
		    response = httpclient.execute(httpPost);
	    	resData = EntityUtils.toString(response.getEntity()); 
	    	System.out.println(resData);
		}catch(Exception ex){
			ex.printStackTrace();
	    } finally {
	    	try {
	    		if(response!=null){
	    			response.close();
	    		}
			} catch (IOException e) {
			}
	    }
	    return resData;
	}
}
