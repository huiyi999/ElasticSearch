package org.example.test;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.example.utils.ESClient;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * @author : chy
 * @date: 2022-05-19 7:54 p.m.
 */
public class TermDemo {
    String index = "sms_logs_index";
    String type = "sms_logs_type";
    RestHighLevelClient client = ESClient.getClient();

    /**
     * term query
     * term的查询是代表完全匹配，搜索之前不会对你的关键字进行分词
     * #term匹配查询
     * POST /sms_logs_index/_search
     * {
     *   "from": 0,   #limit  from,size
     *   "size": 5,
     *   "query": {
     *     "term": {
     *       "province": {
     *         "value": "河北"
     *       }
     *     }
     *   }
     * }
     * ##不会对term中所匹配的值进行分词查询
     */


    @Test
    public void testQuery() throws IOException {
//        1  create Request
        SearchRequest request = new SearchRequest(index);

//        2 query content
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(0);
        builder.size(5);
        builder.query(QueryBuilders.termQuery("province", "河北"));

        request.source(builder);

//        3 execute
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

//        4  get data from _source
        for (SearchHit hit : response.getHits().getHits()) {
            Map<String, Object> result = hit.getSourceAsMap();
            System.out.println(result);
        }
    }

    /**
     * terms是针对一个字段包含多个值得运用
     *
     * terms: where province = 河北 or province = ? or province = ?
     * #terms 匹配查询
     * POST /sms_logs_index/_search
     * {
     *   "from": 0,
     *   "size": 5,
     *   "query": {
     *     "terms": {
     *       "province": [
     *         "河北",
     *         "河南"
     *       ]
     *     }
     *   }
     * }
     */
    @Test
    public void test_terms() throws IOException {
        SearchRequest request = new SearchRequest(index);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.termsQuery("province","河北","河南"));

        request.source(builder);

        SearchResponse resp = client.search(request, RequestOptions.DEFAULT);

        for (SearchHit hit : resp.getHits().getHits()){
            System.out.println(hit);
        }
    }





}
