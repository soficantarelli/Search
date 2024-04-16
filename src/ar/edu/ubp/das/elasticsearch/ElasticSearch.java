package ar.edu.ubp.das.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpHost;
import org.elasticsearch.script.Script;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.significant.ParsedSignificantStringTerms;
import org.elasticsearch.search.aggregations.bucket.significant.SignificantTerms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;

import com.google.gson.Gson;

import ar.edu.ubp.das.beans.MetadataBean;
import ar.edu.ubp.das.beans.ResultBean;
import ar.edu.ubp.das.beans.SearchBean;
import ar.edu.ubp.das.db.Dao;
import ar.edu.ubp.das.db.DaoFactory;
import ar.edu.ubp.das.logger.MyLogger;

public class ElasticSearch {
	private MyLogger logger;
	RestHighLevelClient client;
	

    public ElasticSearch() {
    	this.logger = new MyLogger(this.getClass().getSimpleName());
    	this.client = new RestHighLevelClient(
							RestClient.builder(
				    		    new HttpHost("localhost", 9200, "http"),
				    		    new HttpHost("localhost", 9201, "http")));   	
    }
    
	//SEARCH
    public ResultBean search(SearchBean search) throws Exception {
    	Dao<SearchBean, SearchBean> dao = DaoFactory.getDao("Search", "ar.edu.ubp.das");
		List<MetadataBean> metadataList = new ArrayList<MetadataBean>();
		MetadataBean metadata;
		Gson gson = new Gson();
		
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		
		SearchRequest searchRequest = new SearchRequest("metadata");
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		
		HighlightBuilder highlightBuilder = new HighlightBuilder().postTags("").preTags("").field("text");
		//.fragmentSize(200).noMatchSize(200)
		//noMatchSize: The amount of text you want to return from the beginning of the field if there are no matching 
		//fragments to highlight. Defaults to 0 (nothing is returned).
		
		if (search.getType() != null && !search.getType().isEmpty()) {
			boolQueryBuilder.must(QueryBuilders.termQuery("type", search.getType()));
		}
		
		if (search.getDateFrom() != null && !search.getDateFrom().isEmpty()) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("date").gte(search.getDateFrom()));
		}
		if (search.getDateTo() != null && !search.getDateTo().isEmpty()) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("date").lte(search.getDateTo()));
		}
		
		if (search.getPopularity() != null) {
			System.out.print(search.getPopularity());
			boolQueryBuilder.must(QueryBuilders.termQuery("popularity", search.getPopularity()));
		}
		
		
		boolQueryBuilder
				.must(QueryBuilders.termQuery("userId", search.getIdUser()))
				.must(QueryBuilders.termQuery("approved", true))
				.must(QueryBuilders.multiMatchQuery(search.getQuery(), "title", "url", "text", "tags").field("tags", 10)
						.field("text", 1).field("title", 2))
				.mustNot(QueryBuilders.matchQuery("filters", search.getQuery()));
		
		QueryBuilder query = boolQueryBuilder;
		
		//The method also accepts an array of one or more wildcard patterns to control 
		//which fields get included or excluded in a more fine grained way:
		String[] includeFields = new String[] { };
		String[] excludeFields = new String[] {"text", "topWords", "approved", "extension", "filters", "tags",
				"textLength", "userId", "websiteId"};
		
		sourceBuilder.query(query);
		
		sourceBuilder.highlighter(highlightBuilder);
		sourceBuilder.fetchSource(includeFields, excludeFields);
		
		sourceBuilder.trackTotalHits(true);
		
		
		if (search.getSortBy() != null && !search.getSortBy().isEmpty()) {
			if (search.getSortBy().equals("asc")) {
				FieldSortBuilder sortBuilder = SortBuilders.fieldSort("popularity").order(SortOrder.ASC);
				sourceBuilder.sort(sortBuilder);
			} else {
				FieldSortBuilder sortBuilder = SortBuilders.fieldSort("popularity").order(SortOrder.DESC);
				sourceBuilder.sort(sortBuilder);
			}
		}
		
		
		searchRequest.source(sourceBuilder);
		
		System.out.println(searchRequest);
	
		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		
		this.logger.log(MyLogger.INFO, "ELASTIC: Get Metadatos. Respuesta: " + searchResponse.getHits().getTotalHits());
		
		ResultBean results = new ResultBean();
		results.setResultsAmount(searchResponse.getHits().getTotalHits().value);
		results.setTime(searchResponse.getTook().getMillisFrac());
		
		//no hits
		if (searchResponse.getHits().getTotalHits().value == 0) {
			search.setResults((long) 0);
			dao.insert(search);
			return null;
		}
		
        for (SearchHit hit : searchResponse.getHits().getHits()) {
        	metadata = new MetadataBean();
        	metadata = gson.fromJson(hit.getSourceAsString(), MetadataBean.class);
        	metadata.setId(hit.getId());
        	
        	Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField highlight = highlightFields.get("text"); 
            try {
            Text[] fragments = highlight.fragments();  
            String fragmentString = fragments[0].string();
            metadata.setText(fragmentString);
            } catch (Exception e) {
				System.out.println("No fragment text");
			}
            
        	metadataList.add(metadata);
        	
        	results.addResult(metadata);
		}
        
        
        	AggregationBuilder aggregation = AggregationBuilders.significantText("keywords", "text");
    		
    		SearchRequest searchRequestInsert = new SearchRequest("metadata");
    		sourceBuilder.aggregation(aggregation);
    		searchRequestInsert.source(sourceBuilder);
    		
    		SearchResponse searchResponseInsert;
    		try {
    			searchResponseInsert = client.search(searchRequestInsert, RequestOptions.DEFAULT);
    			
    			search.setResults(searchResponseInsert.getHits().getTotalHits().value);
    			
    			//INSERT BSUQUEDA
    			
    			dao.insert(search);

    		} catch (IOException e) {
    			e.printStackTrace();
    		}
               
		return results;
	}	

	public void increasePopularity(String id) throws IOException {
		UpdateRequest request = new UpdateRequest("metadata", id);
		Script inline = new Script("ctx._source.popularity += 1");
		request.script(inline);
		UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);
		System.out.print(updateResponse);
		logger.log(MyLogger.INFO, updateResponse.status().toString());
	}

    
	//METADATA
	public List<MetadataBean> getMetadata(Integer idUser, Boolean indexed) throws Exception {
		QueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("userId", idUser))
				.must(QueryBuilders.termQuery("approved", indexed));
		HighlightBuilder highlightBuilder = new HighlightBuilder().postTags("").preTags("").fragmentSize(500)
				.noMatchSize(500).field("text");
		String[] includeFields = new String[] {};
		String[] excludeFields = new String[] { "text" };
		SearchRequest searchRequest = new SearchRequest("metadata");
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.size(10000);
		sourceBuilder.query(query);
		sourceBuilder.highlighter(highlightBuilder);
		sourceBuilder.fetchSource(includeFields, excludeFields);
		searchRequest.source(sourceBuilder);

		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		List<MetadataBean> metadataList = new ArrayList<MetadataBean>();
		MetadataBean metadata;
		Gson gson = new Gson();
		System.out.println("Hit count: " + searchResponse.getHits().getTotalHits());
		
		
		for (SearchHit hit : searchResponse.getHits().getHits()) {
			System.out.println(hit);
			metadata = new MetadataBean();
			metadata = gson.fromJson(hit.getSourceAsString(), MetadataBean.class);
			metadata.setId(hit.getId());
			try {
				Text[] fragments = hit.getHighlightFields().get("text").fragments();
				System.out.println(fragments[0]);
				metadata.setText(fragments[0].string());
			} catch (Exception e) {
				System.out.println("No fragment text");
			}
			metadataList.add(metadata);
		}
		return metadataList;
	}
	
	
    public void update(MetadataBean metadata, Integer time) throws IOException { 
    	String id = (metadata.getId()).toString();
    	
		UpdateRequest request = new UpdateRequest("metadata", id)
					.doc("approved", true, "title", "metadata.getTitle()", 
							"tags", metadata.getTags(), "filters", metadata.getFilters())
					.setRefreshPolicy("wait_for"); //timeout();
					/*This option ensures the indexing operation waits for a periodic refresh before running the search.*/
		try {
			UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);
			this.logger.log(MyLogger.INFO, "ELASTIC: Metadata " + id + "Respuesta: " + updateResponse.status().toString());
		} catch (ElasticsearchException e) {
		    if (e.status() == RestStatus.NOT_FOUND) {
		    	this.logger.log(MyLogger.ERROR, "ELASTIC: Not Found " + id + "Respuesta: " + e.status());
		    }
		    
		    if (e.status() == RestStatus.CONFLICT) {
		    	this.logger.log(MyLogger.ERROR, "ELASTIC: Conflict " + id + "Respuesta: " + e.status());
		    }
		}
    }
	
	public void updateBatch(List<MetadataBean> metadataList, Integer time) throws IOException { 
		BulkRequest request = new BulkRequest();
		for (MetadataBean metadata : metadataList) {
			System.out.println("entre");
			String id = (metadata.getId()).toString();
			request.add(new UpdateRequest("metadata", id)
					.doc("approved", true, "title", "metadata.getTitle()", 
							"tags", metadata.getTags(), "filters", metadata.getFilters()));
		}
		request.setRefreshPolicy("wait_for");
		request.timeout((TimeValue.timeValueSeconds(time))); //default 10seconds, si supera el tiempo da error
		
		try {
			BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);
			this.logger.log(MyLogger.INFO, "ELASTIC: Update Metadatos. Respuesta: " + bulkResponse.status().toString());
		} catch (ElasticsearchException e) {
		    if (e.status() == RestStatus.NOT_FOUND) {
		    	this.logger.log(MyLogger.ERROR, "ELASTIC: Not Found. Respuesta: " + e.status());
		    }
		    
		    if (e.status() == RestStatus.CONFLICT) {
		    	this.logger.log(MyLogger.ERROR, "ELASTIC: Conflict. Respuesta: " + e.status());
		    }
		}
    }
	
	
	public void deleteByIdMetadato(String id) throws IOException { 
		DeleteRequest request = new DeleteRequest("metadata", id);
		request.setRefreshPolicy("wait_for");
		try {
			DeleteResponse deleteResponse = client.delete(request, RequestOptions.DEFAULT);
			this.logger.log(MyLogger.INFO, "ELASTIC: Delete Metadata " + id + "Respuesta: " + deleteResponse.status().toString());
		} catch (ElasticsearchException e) {
		    if (e.status() == RestStatus.NOT_FOUND) {
		    	this.logger.log(MyLogger.ERROR, "ELASTIC: Delete Not Found " + id + "Respuesta: " + e.status());
		    }
		    
		    if (e.status() == RestStatus.CONFLICT) {
		    	this.logger.log(MyLogger.ERROR, "ELASTIC: Delete Conflict " + id + "Respuesta: " + e.status());
		    }
		}
	}
	
	public void deleteBatch(List<MetadataBean> metadataList) throws IOException { 
		BulkRequest request = new BulkRequest();
		
		for (MetadataBean metadata : metadataList) {
			
			String id = (metadata.getId()).toString();
			
			request.add(new DeleteRequest("metadata", id));
		}
		request.setRefreshPolicy("wait_for");
		
		try {
			BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);
			this.logger.log(MyLogger.INFO, "ELASTIC: Delete Metadatos. Respuesta: " + bulkResponse.status().toString());
		} catch (ElasticsearchException e) {
		    if (e.status() == RestStatus.NOT_FOUND) {
		    	this.logger.log(MyLogger.ERROR, "ELASTIC: Delete Not Found. Respuesta: " + e.status());
		    }
		    
		    if (e.status() == RestStatus.CONFLICT) {
		    	this.logger.log(MyLogger.ERROR, "ELASTIC: Delete Conflict. Respuesta: " + e.status());
		    }
		}
    }
	
	
	//QUEDA CONFIGURAR
	public void deleteByWebsiteId(Integer id) throws IOException {
		DeleteByQueryRequest request = new DeleteByQueryRequest("metadata"); 
		request.setQuery(new TermQueryBuilder("idWebSite", id)); 
		//request.setMaxDocs(10); limit the number of processed documents
		//request.setBatchSize(100); default DeleteByQueryRequest uses batches of 1000.
		request.setRefresh(true); //Refresh index after calling delete by query
		try {
			BulkByScrollResponse bulkResponse = client.deleteByQuery(request, RequestOptions.DEFAULT);
			this.logger.log(MyLogger.INFO, "ELASTIC: Delete Metadatos. Respuesta: " + bulkResponse.getStatus().toString());
		} catch (ElasticsearchException e) {
		    if (e.status() == RestStatus.NOT_FOUND) {
		    	this.logger.log(MyLogger.ERROR, "ELASTIC: Delete Not Found. Respuesta: " + e.status());
		    }
		    
		    if (e.status() == RestStatus.CONFLICT) {
		    	this.logger.log(MyLogger.ERROR, "ELASTIC: Delete Conflict. Respuesta: " + e.status());
		    }
		}
	} 
	
	public void deleteByUserId(Integer id) throws IOException {
		DeleteByQueryRequest request = new DeleteByQueryRequest("metadata"); 
		request.setQuery(new TermQueryBuilder("idUser", id)); 
		//request.setMaxDocs(10); limit the number of processed documents
		//request.setBatchSize(100); default DeleteByQueryRequest uses batches of 1000.
		request.setRefresh(true); //Refresh index after calling delete by query
		try {
			BulkByScrollResponse bulkResponse = client.deleteByQuery(request, RequestOptions.DEFAULT);
			this.logger.log(MyLogger.INFO, "ELASTIC: Delete Metadatos. Respuesta: " + bulkResponse.getStatus().toString());
		} catch (ElasticsearchException e) {
		    if (e.status() == RestStatus.NOT_FOUND) {
		    	this.logger.log(MyLogger.ERROR, "ELASTIC: Delete Not Found. Respuesta: " + e.status());
		    }
		    
		    if (e.status() == RestStatus.CONFLICT) {
		    	this.logger.log(MyLogger.ERROR, "ELASTIC: Delete Conflict. Respuesta: " + e.status());
		    }
		}
	} 

}
