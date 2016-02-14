package com.tomtop.filters;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

/**
 * mutil属性过滤器
 * 
 * @author lijun
 *
 */
@Service
public class MutilIndexFilter implements IIndexFilter {

	@Override
	public void handle(int lang, Map<String, Object> attributes) {
		if (attributes != null && attributes.containsKey(MUTIL_KEY)) {
			JSONArray mutil = (JSONArray) attributes.get(MUTIL_KEY);

			ImmutableList<Object> hits = FluentIterable
					.from(mutil)
					.filter(m -> lang == ((JSONObject) m)
							.getInteger("languageId")).toList();

			if (hits != null && hits.size() > 0) {
				JSONObject hitMutil = (JSONObject) hits.get(0);
//				if (hitMutil.get("productTypes") != null) {
//					Object productTypes = hitMutil.get("productTypes");
//					attributes.put(PRODUCT_TYPES_KEY, productTypes);
//					hitMutil.remove("productTypes");
//				}
				attributes.put(MUTIL_KEY, hitMutil);
			} else {
				attributes.remove(MUTIL_KEY);
			}
		}

	}

	@Override
	public int getPriority() {
		return 0;
	}

}
