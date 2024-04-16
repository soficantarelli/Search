package ar.edu.ubp.das.beans;

import java.util.ArrayList;
import java.util.List;

public class ResultBean {
	private Long resultsAmount;
	private Double time;
	private List<MetadataBean> results;
	
	public ResultBean() {
		this.results = new ArrayList<MetadataBean>();
	}

	public Long getResultsAmount() {
		return resultsAmount;
	}

	public Double getTime() {
		return time;
	}

	public List<MetadataBean> getResults() {
		return results;
	}

	public void setResultsAmount(Long resultsAmount) {
		this.resultsAmount = resultsAmount;
	}

	public void setTime(Double time) {
		this.time = time;
	}

	public void setResults(List<MetadataBean> results) {
		this.results = results;
	}
	
	public void addResult(MetadataBean result) {
		this.results.add(result);
	}
}