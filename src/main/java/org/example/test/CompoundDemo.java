package org.example.test;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.BoostingQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.example.utils.ESClient;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * @author : chy
 * @date: 2022-05-20 2:19 p.m.
 */
public class CompoundDemo {
    String index = "sms_logs_index";
    String type = "sms_logs_type";
    RestHighLevelClient client = ESClient.getClient();

    /**
     * #查询省份为河北或者河南的
     * #并且公司名不是河马生鲜的
     * #并且smsContext中包含软件两个字
     * POST /sms_logs_index/_search
     * {
     * "query": {
     * "bool": {
     * "should": [
     * {
     * "term": {
     * "province": {
     * "value": "河北"
     * }
     * }
     * },
     * {
     * "term": {
     * "province": {
     * "value": "河南"
     * }
     * }
     * }
     * ],
     * "must_not": [
     * {
     * "term": {
     * "corpName": {
     * "value": "河马生鲜"
     * }
     * }
     * }
     * ],
     * "must": [
     * {
     * "match": {
     * "smsContext": "软件"
     * }
     * }
     * ]
     * }
     * }
     * }
     */
    @Test
    public void test_bool_query() throws IOException {
        SearchRequest request = new SearchRequest(index);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        boolQuery.should(QueryBuilders.termQuery("province", "wuhan"));
        boolQuery.should(QueryBuilders.termQuery("province", "beijing"));

        boolQuery.mustNot(QueryBuilders.termQuery("operatorId", 2));

        boolQuery.must(QueryBuilders.matchQuery("smsContext", "che"));
        boolQuery.must(QueryBuilders.matchQuery("smsContext", "software"));

        builder.query(boolQuery);
        request.source(builder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    /**
     * POST /sms_logs_index/_search
     * {
     * "query": {
     * "boosting": {
     * "positive": {
     * "match": {
     * "smsContent": "TEXT1"
     * }
     * },
     * "negative": {
     * "match": {
     * "smsContent": "TEXT2"
     * }
     * },
     * "negative_boost": 0.5
     * }
     * }
     * }
     */
    @Test
    public void test_boosting_query() throws IOException {

        SearchRequest request = new SearchRequest(index);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoostingQueryBuilder boostingQueryBuilder = QueryBuilders.boostingQuery(
                QueryBuilders.matchQuery("smsContent", "孩童"),
                QueryBuilders.matchQuery("smsContent", "希尔曼")
        );
        boostingQueryBuilder.negativeBoost(0.5F);

        builder.query(boostingQueryBuilder);
        request.source(builder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        SearchHit[] hits = response.getHits().getHits();
        System.out.println(hits.length);
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            System.out.println(sourceAsMap);
        }
    }

    /**
     * POST /sms_logs_index/_search
     * {
     * "query": {
     * "bool": {
     * "filter": [
     * {
     * "term": {
     * "smsContent": "TEXT1"
     * }
     * },
     * {
     * "range": {
     * "fee": {
     * "lte": 20
     * }
     * }
     * }
     * <p>
     * ]
     * }
     * }
     * }
     */
    @Test
    public void test_filter_query() throws IOException {
        SearchRequest request = new SearchRequest(index);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.filter(QueryBuilders.termQuery("corpName", "格力汽车"));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("fee").gt(20));

        builder.query(boolQueryBuilder);
        request.source(builder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        SearchHit[] hits = response.getHits().getHits();
        System.out.println(hits.length);
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            System.out.println(sourceAsMap);
        }
    }

}
