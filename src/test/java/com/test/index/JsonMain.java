package com.test.index;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.tomtop.BaseClient;
import com.tomtop.entity.DepotEntity;
import com.tomtop.entity.Filter;
import com.tomtop.entity.IndexEntity;
import com.tomtop.entity.OrderEntity;
import com.tomtop.entity.PageBean;

public class JsonMain {
	
	public static void main(String args[]) throws Exception{
		JsonMain main = new JsonMain();
		
//		main.insert(main);
//		main.delete("1450834046481_listingId","cn");
//		main.update("1450862500005_listingId");
		main.testHot();
//		main.testMlike();
//		main.testQuery();
//		main.update(null);
//		ProductEntity bean = main.getBean();
//		IndexEntity indexModel = new IndexEntity();
//		indexModel.setSku("1111111");
//		BeanUtils.copyProperties(indexModel, bean);
//		System.out.println(bean.getSku());
		
//		main.getProperties("visible");
//		main.getProperties("isOnSale");
	}
	
	//判断属性是否存在
	public void getProperties(String propertyName){
		IndexEntity model = new IndexEntity();
		String json = JSONObject.toJSONString(model);
		System.out.println(json);
	}
	
	
	public void testQuery(){
		PageBean bean = new PageBean();
		bean.setEndNum(10);
		Filter filter = new Filter("mutil.productTypes.productTypeId",1,"&&",true,true);
		bean.getFilters().add(filter);
//		Filter brand = new Filter("brand",null,"&&",true,false);
//		bean.getFilters().add(brand);
		
		List<OrderEntity> orders = new ArrayList<OrderEntity>();
		OrderEntity order = new OrderEntity();
		order.setOrder(-999);
		order.setPropetyName("mutil.productTypes.sort");
		order.setType("asc");
		orders.add(order);
		
//		OrderEntity orderPrice = new OrderEntity();
//		orderPrice.setPropetyName("yjPrice");
//		orderPrice.setType("asc");
//		orders.add(orderPrice);
		
		bean.setOrders(orders);
		
		Filter tagsFilter = new Filter("tagsName.tagName","Hot,Special","&&",true,false);
		bean.getFilters().add(tagsFilter);
		Filter onSellFilter = new Filter("onSale","","&&",true,false);
		bean.getFilters().add(onSellFilter);
		
		
		
		
		bean.setLanguageName("en");
		bean.setWebSites("1");
//		bean.setKeyword("MINI");
//		bean.setOrderName("yjPrice");
//		bean.setOrderValue("asc");
//		RangeAggregation range = new RangeAggregation(new Double(103.00d),null,"yjPrice");
//		range.setAliasName("$0.01 to $1.0");
//		bean.getRangeAgg().add(range);
		
		String json = JSONObject.toJSONString(bean);
		File file = new File("/Users/zting/Documents/workspace2/search_engine/src/test/resources/templates/test_data.json");
		try {
//			String content = org.apache.commons.io.FileUtils.readFileToString(file);
//			String[] str = content.split(",");
//			List<String> list = new ArrayList<String>();
//			for (String string : str) {
//				list.add(string);
//			}
//			String json = JSONObject.toJSONString(list);
//			String url ="http://localhost:8080/search-engine/search/qhome/en/1/";
			String url ="http://localhost:8080/search-engine/search/query";
			String resData = HttpClientUtil.doPost(url,json);
			System.out.println(resData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void testMlike(){
//		String url = "http://localhost:8080/search-engine/search/qmlike/en/1/00ba6fb5-d914-1004-874c-d371c9ab96c0";
		String url ="http://localhost:8080/search-engine/search/qlistingid/en/1/G0547B-L";
		String resData = HttpClientUtil.doPost(url,null);
		System.out.println(resData);
	}
	
	
	public void testQueryListingId(){
		String url ="http://localhost:8080/search-engine/search/qlistingid/en/1/G0547B-L";
		String resData = HttpClientUtil.doPost(url,null);
		System.out.println(resData);
	}
	
	public void testHot(){
		try {
			
			List<String> list = new ArrayList<String>();
			list.add("d65dbdc0-d929-1004-835b-90389054983d");
			list.add("37346468-d944-1004-89c8-fe4acbfc908f");
			list.add("007eb32c-d914-1004-874c-d371c9ab96c0");
			list.add("d5469808-d929-1004-835b-90389054983d");
			list.add("00a6bf0d-d914-1004-874c-d371c9ab96c0,");
			list.add("d7d0fb42-d929-1004-835b-90389054983d");
			list.add("00657557-d914-1004-874c-d371c9ab96c0");
			list.add("1b6c8466-6020-479d-ac84-666020079de7");
			list.add("dfe5ca67-2ac5-45bd-a5ca-672ac5a5bd0a");
			list.add("00b8ccd9-d914-1004-874c-d371c9ab96c0");
			list.add("ac31d11a-e6f3-4c03-b1d1-1ae6f35c03f3");
			
			String json = JSONObject.toJSONString(list);
			String url ="http://localhost:8080/search-engine/search/qbyids/en/1";
//			String url = "http://localhost:8080/search-engine/search/qmlike/en/1";
//			String url = "http://localhost:8080/search-engine/search/qlistingid/en/1/006309cc-d914-1004-874c-d371c9ab96c0,009ac42f-d914-1004-874c-d371c9ab96c0";
//			String url = "http://localhost:8080/search-engine/search/qhome/en/1";
//			String url = "http://192.168.220.55:8009/search-engine/search/qhot/en/1";
			HttpClientUtil.doPost(url,json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void insert(JsonMain main){
//		String url = "http://localhost:8080/index/insert";
//		ProductEntity product = main.getBean();
//		String json = JSONObject.toJSONString(product);
//		System.out.println(json);
		
//		HttpClientUtil.doPost(url,json);
		
	}
	
	
	public void update(String listingid) throws Exception{
		listingid = "293522a9-b31a-423d-b522-a9b31ad23d03";
		String url = "http://localhost:8080/search-engine/index/updateIndexPart/293522a9-b31a-423d-b522-a9b31ad23d03";
		String path = BaseClient.class.getClassLoader().getResource("").toURI().getPath()+"/templates/test_data.json";
		File file = new File(path);
		String content = org.apache.commons.io.FileUtils.readFileToString(file);
		System.out.println(content);
		HttpClientUtil.doPost(url, content);
	}
	
	
	public void delete(String listingid,String param){
		String url = "http://localhost:8080/index/delete/20/"+listingid+"/"+param;
		HttpClientUtil.doPost(url,null);
		
	}
	
	
	
	public Map<String,Object> getJson(List<DepotEntity> list){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("depots", list);
		return map;
	}
}
