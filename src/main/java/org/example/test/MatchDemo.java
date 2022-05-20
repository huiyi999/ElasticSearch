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

/**
 * @author : chy
 * @date: 2022-05-19 8:12 p.m.
 */
public class MatchDemo {
    String index = "sms_logs_index";
    // String type = "sms_logs_type";
    RestHighLevelClient client = ESClient.getClient();

    /**
     * match查询属于高层查询，它会根据你查询字段类型不一样，采用不同的查询方式
     * <p>
     * match查询，实际底层就是多个term查询，将多个term查询的结果进行了封装
     * <p>
     * 1. 查询的如果是日期或者是数值的话，它会根据你的字符串查询内容转换为日期或者是数值对等
     * <p>
     * 2. 如果查询的内容是一个不可被分的内容（keyword），match查询不会对你的查询的关键字进行分词   == term
     * <p>
     * 3. 如果查询的内容是一个可被分的内容（text）,match则会根据指定的查询内容按照一定的分词规则去分词进行查询
     * <p>
     * POST /sms_logs_index/_search
     * {
     * "query": {
     * "match_all": {}
     * }
     * }
     */
    @Test
    public void test_match_all() throws IOException {

        SearchRequest request = new SearchRequest(index);
        // request.types(type);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.size(20);
        //es默认查询结果只展示10条，这里可以指定展示的条数

        builder.query(QueryBuilders.matchAllQuery());
        request.source(builder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit);
        }
    }

    /**
     * POST /sms_logs_index/_search
     * {
     * "query": {
     * "match": {
     * "smsContent": "车"
     * }
     * }
     * }
     */
    @Test
    public void test_match_field() throws IOException {
        SearchRequest request = new SearchRequest(index);
        // request.types(type);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchQuery("smsContext", "打车"));
        request.source(builder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit);
        }
    }

    /**
     * 基于一个Filed匹配的内容，采用and或者or的方式进行连接
     * <p>
     * 布尔match查询
     * POST /sms_logs_index/_search
     * {
     * "query": {
     * "match": {
     * "smsContext": {
     * "query": "打车 女士",
     * "operator": "and"
     * }
     * }
     * }
     * }
     */

    @Test
    public void test_match_boolean() throws IOException {
        SearchRequest request = new SearchRequest(index);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchQuery("smsContext", "打车 女士").operator(Operator.AND));
        request.source(builder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit);
        }
    }

    /**
     * match针对一个field做检索，multi_match针对多个field进行检索，多个key对应一个text
     *
     * POST /sms_logs_index/_search
     * {
     *   "query": {
     *     "multi_match": {
     *       "query": "河北",  #指定text
     *       "fields": ["province","smsContext"] #指定field
     *     }
     *   }
     * }
     */
    @Test
    public void test_multi_match() throws IOException {
        SearchRequest request = new SearchRequest(index);

        SearchSourceBuilder builder = new SearchSourceBuilder();

        //query content:  field1, field2, field3
        builder.query(QueryBuilders.multiMatchQuery("河北", "province", "smsContext"));
        request.source(builder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit);
        }
    }
}
