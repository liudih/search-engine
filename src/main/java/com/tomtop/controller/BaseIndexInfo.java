package com.tomtop.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSONObject;
import com.tomtop.common.BaseServiceUtil;
import com.tomtop.common.Config;
import com.tomtop.entity.AttributeItem;
import com.tomtop.entity.Constant;
import com.tomtop.entity.IndexEntity;
import com.tomtop.entity.Language;
import com.tomtop.entity.MutilLanguage;
import com.tomtop.entity.ProductEntity;
import com.tomtop.entity.ProductTypeEntity;

/**
 * 索引基础信息
 * 
 * @author ztiny
 */
public abstract class BaseIndexInfo {

	Logger logger = Logger.getLogger(BaseIndexInfo.class);
	
	// 局部更新索引时会用到
	public static final JSONObject ROUTE;
	public static final String[] INDEX_ALL;
	static {
		String routes = Config.getValue("routes");
		ROUTE = JSONObject.parseObject(routes);

		String indexAllStr = Config.getValue("product.all");
		INDEX_ALL = indexAllStr.split(",");
	}

	/**
	 * 获取索引名称
	 * 
	 * @param product
	 * @return
	 */
	public List<String> getIndexNames() {
		List<String> languages = new ArrayList<String>();
		for (String index : BaseIndexInfo.INDEX_ALL) {
			String indexName = Constant.ES_INDEX_PREFIX + index;
			languages.add(indexName);
		}
		return languages;
	}

	/**
	 * 根据语言解析出不同版本的产品对象
	 * 
	 * @param product
	 * @return Map<Stirng,IndexEntity> 域名为键，值IndexEntity实体类
	 */
	public Map<String, IndexEntity> getMutilLanguagesProduct(
			ProductEntity product) throws Exception {
		if (product == null || product.getMutil() == null
				|| product.getMutil().size() < 1) {
			return null;
		}
		Map<String, Language> languageMap = BaseServiceUtil.getLanguageMap();
		// 解析product已经国际化的语言
		Map<String, MutilLanguage> domainsMap = getDomains(product);
		Map<String, IndexEntity> map = new HashMap<String, IndexEntity>();
		for (int i = 0; i < INDEX_ALL.length; i++) {
			MutilLanguage mutil = null;
			IndexEntity indexModel = new IndexEntity();
			BeanUtils.copyProperties(product, indexModel, "mutil");
			// 判断产品是否已经国际化了该语言
			if (domainsMap.get(INDEX_ALL[i]) != null) {

				// 如果已经国际化了该语言，则取当前对象复制给索引对象
				mutil = domainsMap.get(INDEX_ALL[i]);
				// 和属性匹配语言
				mutil = getNewMutilLanguageByItem(mutil, null);
				// 和类目匹配
				mutil = getNewMutilLanguageByType(mutil, null);
				indexModel.setMutil(mutil);
			} else {
				// 如果没有则取英文站点国际化的属性
				mutil = domainsMap.get("en");
				if (mutil != null) {
					MutilLanguage newMutil = (MutilLanguage) mutil.clone();
					// 设置当前语言
					newMutil.setLanguageName(INDEX_ALL[i]);
					// 和属性匹配语言
					newMutil = getNewMutilLanguageByItem(newMutil, languageMap);
					// 和类目匹配
					newMutil = getNewMutilLanguageByType(newMutil, languageMap);
					indexModel.setMutil(newMutil);
				}
			}
			
			// add by lijun,always use en url
//			mutil = domainsMap.get("en");
//			String enUrl = (mutil != null ? mutil.getUrl() : null);
////			if (StringUtils.isEmpty(enUrl)) {
////				throw new NullPointerException("can not get en url");
////			}
//			indexModel.getMutil().setUrl(enUrl);
			String indexName = Constant.ES_INDEX_PREFIX + INDEX_ALL[i];
			map.put(indexName, indexModel);
		}
		return map;
	}

	/**
	 * 根据语言解析出不同版本的产品对象
	 * 
	 * @param products
	 * @return
	 */
	public Map<String, List<IndexEntity>> getMutilLanguagesProducts(List<ProductEntity> products) throws Exception {
		if (products == null || products.size() < 1) {
			return null;
		}
		Map<String, Language> languageMap = BaseServiceUtil.getLanguageMap();
		Map<String, List<IndexEntity>> map = new HashMap<String, List<IndexEntity>>();
		for (ProductEntity productEntity : products) {
			FileUtils.writeStringToFile(Constant.file,productEntity.getListingId()+"\r\n",true);
			// 解析product已经国际化的语言
			Map<String, MutilLanguage> domainsMap = getDomains(productEntity);
			for (int i = 0; i < INDEX_ALL.length; i++) {
				IndexEntity indexModel = new IndexEntity();
				BeanUtils.copyProperties(productEntity, indexModel, "mutil");
				MutilLanguage mutil = null;
				// 判断是否有该语言的国际化属性
				if (domainsMap.get(INDEX_ALL[i]) != null) {
					// 如果已经国际化了该语言，则取当前对象复制给索引对象
					mutil = domainsMap.get(INDEX_ALL[i]);
					// 和属性匹配语言
					mutil = getNewMutilLanguageByItem(mutil, null);
					// 和类目匹配
					mutil = getNewMutilLanguageByType(mutil, null);
					indexModel.setMutil(mutil);
				}
				else {
					// 如果没有则取英文站点国际化的属性
					mutil = domainsMap.get("en");
					if (mutil != null) {
						MutilLanguage newMutil = (MutilLanguage) mutil.clone();
						// 设置当前语言
						newMutil.setLanguageName(INDEX_ALL[i]);
						// 和属性匹配语言
						newMutil = getNewMutilLanguageByItem(newMutil,languageMap);
						// 和类目匹配
						newMutil = getNewMutilLanguageByType(newMutil,languageMap);
						indexModel.setMutil(newMutil);
					}
				}
				// add by lijun,always use en url
				mutil = domainsMap.get("en");
//				String enUrl = (mutil != null ? mutil.getUrl() : null);
//				if (StringUtils.isEmpty(enUrl)) {
//					throw new NullPointerException("can not get en url");
//				}
//				indexModel.getMutil().setUrl(enUrl);

				String indexName = Constant.ES_INDEX_PREFIX + INDEX_ALL[i];
				// 同类型索引存放在同一集合里面
				List<IndexEntity> oldIndexs = map.get(indexName);
				if (oldIndexs == null) {
					oldIndexs = new ArrayList<IndexEntity>();
				}
				oldIndexs.add(indexModel);
				if(StringUtils.isNotBlank(indexName)){
					map.put(indexName, oldIndexs);
				}
			}
		}
		return map;
	}

	/**
	 * 国际化对象匹配类目的国际化对象
	 * 
	 * @param mutil
	 * @return
	 */
	public MutilLanguage getNewMutilLanguageByType(MutilLanguage mutil,
			Map<String, Language> languageMap) {
		if (mutil == null) {
			return null;
		}
		List<ProductTypeEntity> types = mutil.getProductTypes();
		Language language = null;
		if (languageMap != null) {
			languageMap.get(mutil.getLanguageName());
		}
		List<ProductTypeEntity> models = new ArrayList<ProductTypeEntity>();
		for (ProductTypeEntity model : types) {
			if (language != null) {
				mutil.setLanguageId(language.getId());
			}
			if (mutil.getLanguageId() == model.getLanguageId()) {
				models.add(model);
			}
			mutil.setProductTypes(models);
		}
		return mutil;
	}

	/**
	 * 国际化对象匹配多属性的国际化对象
	 * 
	 * @param mutil
	 * @return
	 */
	public MutilLanguage getNewMutilLanguageByItem(MutilLanguage mutil,
			Map<String, Language> languageMap) {
		if (mutil == null) {
			return null;
		}
		List<AttributeItem> items = mutil.getItems();
		List<AttributeItem> newItems = new ArrayList<AttributeItem>();
		Language language = null;
		if (languageMap != null) {
			languageMap.get(mutil.getLanguageName());
		}
		for (AttributeItem attributeItem : items) {
			if (language != null) {
				mutil.setLanguageId(language.getId());
			}
			if (mutil.getLanguageId() == attributeItem.getLanguageId()) {
				newItems.add(attributeItem);
			}
			mutil.setItems(newItems);
		}
		return mutil;
	}

	/**
	 * 解析产品所有已经国际化的语言
	 * 
	 * @param product
	 * @return
	 */
	public Map<String, MutilLanguage> getDomains(ProductEntity product) {
		if (product == null || product.getMutil() == null) {
			return null;
		}
		List<MutilLanguage> mutils = product.getMutil();
		Map<String, MutilLanguage> map = new HashMap<String, MutilLanguage>();

		for (MutilLanguage mutilLanguage : mutils) {
			String domainName = mutilLanguage.getLanguageName();
			map.put(domainName, mutilLanguage);
		}
		return map;
	}

	/**
	 * 获取索引名称
	 * 
	 * @param languageName
	 *            国家域名缩写
	 * @return
	 */
	public String getIndexName(String languageName) {
		return Constant.ES_INDEX_PREFIX + languageName;
	}

	/**
	 * 获取索引名称
	 * 
	 * @param languageNames
	 *            国家域名缩写集合
	 * @return
	 */
	public List<String> getIndexName(List<String> languageNames) {
		if (languageNames == null || languageNames.size() < 1) {
			return null;
		}
		List<String> languages = new ArrayList<String>();
		for (String languageName : languageNames) {
			String indexName = getIndexName(languageName);
			languages.add(indexName);
		}
		return languages;
	}

	/**
	 * 字符串格式化之后，组装索引名称
	 * 
	 * @param languageName
	 *            语言名称,多个的话以逗号(,)号隔开
	 * @return
	 */
	public List<String> getDefaultIndexName(String languageName) {
		String[] languageNames = languageName.split(",");
		List<String> languages = new ArrayList<String>();
		for (String language : languageNames) {
			String indexName = getIndexName(language);
			languages.add(indexName);
		}
		return languages;
	}

}
