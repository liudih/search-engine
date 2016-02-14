package com.tomtop;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.springframework.util.Assert;

import com.tomtop.common.Config;
import com.tomtop.entity.Constant;

/**
 * 获取查询、创建索引的客户端
 * @author ztiny
 * @Date 2015-12-19
 */
public class BaseClient {

	private BaseClient(){
		
	}
	private static TransportClient indexClient ;
	
	private static TransportClient	transportClient;
	
	static Logger logger = Logger.getLogger(BaseClient.class);
	
	/**
	 * 获取elastic search 客户端,后续参数会从配置文件读取,单机可用
	 * @param ip
	 * @return
	 */
	public Client getClient(String ip){
		Client client = null;
		try {
			client = TransportClient.builder().build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ip), 9300));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return client;
	}
	
	
	/**
	 * 静态索引客户端
	 */
	static{
		try{
			if(indexClient==null){
				Settings settings = Settings.settingsBuilder()
						.put("number_of_shards",Config.getValue("number_of_shards")).put("number_of_replicas",Config.getValue("number_of_replicas"))
						.put("client.transport.sniff", Config.getValue("client.transport.sniff"))
						.put("cluster.name",Config.getValue("cluster.name"))
						.put("client.transport.ping_timeout",Config.getValue("client.transport.ping_timeout")).build();
				
				indexClient = TransportClient.builder().settings(settings).build();
				String ip = Config.getValue("client.nodes.ip");
				String port =Config.getValue("client.nodes.port");
				
				if(ip==null || port==null || ip.split(",").length!=port.split(",").length){
					logger.info("====================>>>>>config.properties file had error<<<<<============");
				}
				String ips[] = ip.substring(1, ip.length()-1).split(",");
				String ports[] = port.substring(1, port.length()-1).split(",");
				for(int i=0;i<ips.length;i++){
					InetAddress host = InetAddress.getByName(ips[i]);
					int portNo = Integer.parseInt(ports[i]);
					InetSocketTransportAddress transportAddress = new InetSocketTransportAddress(host,portNo);
					indexClient.addTransportAddress(transportAddress);
				}
				
			} 
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * 静态化查询客户端
	 */
	static {
		try {
			Settings settings = Settings.settingsBuilder()
					.put("client.transport.sniff", Config.getValue("client.transport.sniff"))
					.put("cluster.name",Config.getValue("cluster.name"))
					.put("client.transport.ping_timeout",Config.getValue("client.transport.ping_timeout")).build();

			transportClient = TransportClient.builder().settings(settings).build();
			
			String ip = Config.getValue("client.nodes.ip");
			String port =Config.getValue("client.nodes.port");
			
			if(ip==null || port==null || ip.split(",").length!=port.split(",").length){
				logger.info("====================>>>>>config.properties file had error<<<<<============");
			}
			String ips[] = ip.substring(1, ip.length()-1).split(",");
			String ports[] = port.substring(1, port.length()-1).split(",");
			for(int i=0;i<ips.length;i++){
				InetAddress host = InetAddress.getByName(ips[i]);
				int portNo = Integer.parseInt(ports[i]);
				TransportAddress transportAddress = new InetSocketTransportAddress(host,portNo);
				transportClient.addTransportAddress(transportAddress);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized Client getSearchClient(){
		return transportClient;
	}
	
	
	/**
	 * 获取客户端
	 * @param indexName
	 * @param indexType
	 * @param client
	 * @return
	 */
	public static synchronized Client getIndexClient(String indexName,String indexType){
		try{
			Assert.notNull(indexName, "indexName为空");
			Assert.notNull(indexType, "indexType为空");
			String indexKey = indexName+"_"+indexType;
			//判断索引映射文件是否已经存在
			Boolean exists = Constant.indexMappingFlagCache.get(indexKey);
		    if (exists==null || !exists) {
		    	String path = BaseClient.class.getClassLoader().getResource("").toURI().getPath()+"/templates/mapping_product.json";
		    	String mapping = insertMapping(path);
		    	exists = indexClient.admin().indices().prepareExists(indexName).execute().actionGet().isExists();
		    	if(!exists){
			    	indexClient.admin().indices().prepareCreate(indexName).execute().actionGet();
			    	indexClient.admin().indices().preparePutMapping(indexName).setType(indexType).setSource(mapping).execute().actionGet();
		    	}
		    	Constant.indexMappingFlagCache.put(indexKey, exists);
		    }
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return indexClient;
	}
	
	/**
	 * 文件名称
	 * @param filepath
	 * @return
	 */
	public  static String insertMapping(String path){
		String mapping = null;
		try {
			File file = new File(path);
			mapping = org.apache.commons.io.FileUtils.readFileToString(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapping;
	}
}
