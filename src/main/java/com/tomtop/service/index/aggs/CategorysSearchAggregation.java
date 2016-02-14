package com.tomtop.service.index.aggs;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tomtop.common.Config;
import com.tomtop.common.HttpClientUtil;
import com.tomtop.entity.AggregationEntity;
import com.tomtop.service.index.ISearchAggValue;
import com.tomtop.service.index.ISearchAggregation;

public class CategorysSearchAggregation implements ISearchAggregation, Serializable {

	private static final long serialVersionUID = 1L;
	
	final String KEYVAL = "categorys";

	@Override
	public AbstractAggregationBuilder getAggBuilder() {
		return AggregationBuilders.terms(KEYVAL).field("mutil.productTypes.productTypeId");
	}

	@Override
	public ISearchAggValue getAggValue(SearchResponse response) {
		List<AggregationEntity> aggs = new ArrayList<AggregationEntity>();
		CategorySearchAggValue cvalue = new CategorySearchAggValue(aggs);
		if (response.getAggregations() != null) {
			Terms t = response.getAggregations().get(KEYVAL);
			t.getBuckets().forEach(p -> {
				AggregationEntity agg = new AggregationEntity();
				agg.setCount(p.getDocCount());
//				String url = StringUtils.replace(Constant.ES_GET_PRODUCT_TYPES_URL,"{id}",p.getKeyAsString());
				String url = Config.getValue("base.productType")+p.getKeyAsString();
				String result = HttpClientUtil.doGet(url);
				JSONObject obj = JSON.parseObject(result);
				JSONObject o = (JSONObject) obj.get("data");
				String typeName = o.get("cname").toString();
				agg.setName(typeName);
				agg.setId(p.getKeyAsNumber().intValue());
				aggs.add(agg);
			});
		}
		return cvalue;
	}

	@Override
	public List<AbstractAggregationBuilder> getAggBuilders(List<String> list) {
		
		return null;
	}
}
