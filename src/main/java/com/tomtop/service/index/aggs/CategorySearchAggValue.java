package com.tomtop.service.index.aggs;



import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.tomtop.entity.AggregationEntity;
import com.tomtop.service.index.ISearchAggValue;



public class CategorySearchAggValue implements ISearchAggValue, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7348647203108852949L;
	private Map<Integer, Long> categoryCounts;
	
	private List<AggregationEntity> aggs ;
	
	public CategorySearchAggValue(Map<Integer, Long> categoryCounts) {
		this.categoryCounts = categoryCounts;
	}
	
	public CategorySearchAggValue(List<AggregationEntity> aggs) {
		this.aggs = aggs;
	}

	/**
	 * 
	 * 
	 * @return categoryid,count
	 */
	public Map<Integer, Long> getCategoryCounts() {
		return categoryCounts;
	}

	/**
	 * 聚合多个属性
	 * @return
	 * @author ztiny
	 */
	public List<AggregationEntity> getAggs() {
		return aggs;
	}
}
