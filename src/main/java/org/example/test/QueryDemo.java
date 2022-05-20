package org.example.test;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.example.utils.ESClient;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * @author : chy
 * @date: 2022-05-20 2:55 p.m.
 */
public class QueryDemo {
    String index = "sms_logs_index";
    String type = "sms_logs_type";
    RestHighLevelClient client = ESClient.getClient();

    /**
     * POST /sms_logs_index/_search
     * {
     * "query": {
     * "match": {
     * "smsContent": "车"
     * }
     * },
     * "highlight": {
     * "fields": {
     * "smsContent": {}
     * },
     * "pre_tags": "<font color='red'>",
     * "post_tags": "</font>",
     * "fragmenter": "simple"
     * }
     * }
     */
    @Test
    public void testHighlightQuery() throws IOException {
        SearchRequest request = new SearchRequest(index);

        SearchSourceBuilder builder = new SearchSourceBuilder();

        builder.query(QueryBuilders.matchQuery("smsContent", "团队"));

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("smsContent", 10).preTags("<font color='red'>").postTags("</font>");

        builder.highlighter(highlightBuilder);
        request.source(builder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        SearchHit[] hits = response.getHits().getHits();
        System.out.println(hits.length);
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //System.out.println(sourceAsMap);
            HighlightField smsContent = hit.getHighlightFields().get("smsContent");
            System.out.println(smsContent);
        }
    }

    /**
     * POST /sms_logs_index/_search
     * {
     *   "aggs": {
     *     "my-agg-name": {
     *       "terms": {
     *         "field": "my-field"
     *       }
     *     }
     *   }
     * }
     */
    @Test
    public void testAggregationQuery() {


    }


}
