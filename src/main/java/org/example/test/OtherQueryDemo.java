package org.example.test;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.example.utils.ESClient;
import org.junit.Test;

import java.io.IOException;

/**
 * @author : chy
 * @date: 2022-05-19 8:21 p.m.
 */
public class OtherQueryDemo {

    String index = "sms_logs_index";
    String type = "sms_logs_type";
    RestHighLevelClient client = ESClient.getClient();

    /**
     * # id query
     * GET /sms_logs_index/0001
     * GET /索引名/id
     */

    @Test
    public void test_multi_match() throws IOException {
        GetRequest request = new GetRequest(index, "0001");
        GetResponse resp = client.get(request, RequestOptions.DEFAULT);
        System.out.println(resp.getSourceAsMap());
    }

    /**
     * # ids query
     * 根据多个id进行查询，类似MySql中的where Id in (id1,id2,id3….)
     * <p>
     * POST /sms_logs_index/_search
     * {
     * "query": {
     * "ids": {
     * "values": [1,2,3]  #id值
     * }
     * }
     * }
     */
    @Test
    public void test_query_ids() throws IOException {
        SearchRequest request = new SearchRequest(index);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.idsQuery().addIds("1", "2", "3"));
        request.source(builder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    /**
     * prefix query
     * 前缀查询，可以通过一个关键字去指定一个Field的前缀，从而查询到指定的文档
     * POST /sms_logs_index/_search
     * {
     * "query": {
     * "prefix": {
     * "smsContext": {
     * "value": "河"
     * }
     * }
     * }
     * }
     * #与 match查询的不同在于，prefix类似mysql中的模糊查询。而match的查询类似于严格匹配查询
     * # 针对不可分割词
     */
    @Test
    public void test_query_prefix() throws IOException {
        SearchRequest request = new SearchRequest(index);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.prefixQuery("smsContext", "河"));
        request.source(builder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    /**
     * fuzzy query
     * 输入一个字符的大概，ES就可以根据输入的内容大概去匹配一下结果，eg.你可以存在一些错别字
     * <p>
     * POST /sms_logs_index/_search
     * {
     * "query": {
     * "fuzzy": {
     * "corpName": {
     * "value": "盒马生鲜",
     * "prefix_length": 2  # 指定前几个字符要严格匹配
     * }
     * }
     * }
     * }
     * <p>
     * #不稳定，查询字段差太多也可能查不到
     * .prefixLength() :指定前几个字符严格匹配
     */
    @Test
    public void test_query_fuzzy() throws IOException {
        SearchRequest request = new SearchRequest(index);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.fuzzyQuery("corpName", "盒马生鲜").prefixLength(2));
        request.source(builder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    /**
     * wildcard query
     * 与mysql中的like查询是一样的，可以在查询时，在字符串中指定通配符*和占位符？
     * <p>
     * POST /sms_logs_index/_search
     * {
     * "query": {
     * "wildcard": {
     * "corpName": {
     * "value": "*车"
     * }
     * }
     * }
     * }
     * ?代表一个占位符
     * ??代表两个占位符
     */

    @Test
    public void test_query_wildcard() throws IOException {
        SearchRequest request = new SearchRequest(index);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.wildcardQuery("corpName", "*车"));
        request.source(builder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    /**
     * range  query
     * 范围查询，只针对数值类型，对某一个Field进行大于或者小于的范围指定
     * <p>
     * POST /sms_logs_index/_search
     * {
     * "query": {
     * "range": {
     * "relyTotal": {
     * "gte": 0,
     * "lte": 3
     * }
     * }
     * }
     * }
     */
    @Test
    public void test_query_range() throws IOException {
        SearchRequest request = new SearchRequest(index);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.rangeQuery("fee").lt(5).gt(2));
        request.source(builder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    /**
     * regexp query
     * 正则查询，通过你编写的正则表达式去匹配内容
     * <p>
     * PS: prefix,fuzzy,wildcard和regexp查询效率相对比较低,在对效率要求比较高时，避免去使用
     * <p>
     * POST /sms_logs_index/_search
     * {
     * "query": {
     * "regexp": {
     * "moible": "109[0-8]{7}"
     * }
     * }
     * }
     */
    @Test
    public void test_query_regexp() throws IOException {
        SearchRequest request = new SearchRequest(index);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.regexpQuery("moible", "106[0-9]{8}"));
        request.source(builder);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }
}
