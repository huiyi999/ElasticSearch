package org.example.test;

import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.example.utils.ESClient;
import org.junit.Test;

import java.io.IOException;

/**
 * @author : chy
 * @date: 2022-05-20 1:57 p.m.
 */
public class ScrollDemo {
    String index = "sms_logs_index";
    String type = "sms_logs_type";
    RestHighLevelClient client = ESClient.getClient();

    /**
     * REST API
     * <p>
     * # 执行scroll查询，返回第一页数据，并且将文档id信息存放在ES的上下文中，指定生存时间
     * POST /sms_logs_index/_search?scroll=1m
     * {
     * "query": {
     * "match_all": {}
     * },
     * "size": 2,
     * "sort": [
     * {
     * "fee": {
     * "order": "desc"
     * }
     * }
     * ]
     * }
     * <p>
     * # scroll query next page
     * # "scroll_id": from the result of above query
     * POST /_search/scroll
     * {
     * "scroll_id": "FGluY2x1ZGVfY29udGV4dF91dWlkDnF1ZXJ5VGhlbkZldGNoAxZMRGgxYmQ3M1RGcWhhMnUwdUxPbUJnAAAAAAAAGK4WdnhaSTRXU2NSWXF5cXktUXA3RTdmQRZMRGgxYmQ3M1RGcWhhMnUwdUxPbUJnAAAAAAAAGLAWdnhaSTRXU2NSWXF5cXktUXA3RTdmQRZMRGgxYmQ3M1RGcWhhMnUwdUxPbUJnAAAAAAAAGK8WdnhaSTRXU2NSWXF5cXktUXA3RTdmQQ==",
     * "scroll" :"1m"
     * }
     * <p>
     * <p>
     * # delete scroll data in ES
     * DELETE /_search/scroll/FGluY2x1ZGVfY29udGV4dF91dWlkDnF1ZXJ5VGhlbkZldGNoAxZMRGgxYmQ3M1RGcWhhMnUwdUxPbUJnAAAAAAAAGK4WdnhaSTRXU2NSWXF5cXktUXA3RTdmQRZMRGgxYmQ3M1RGcWhhMnUwdUxPbUJnAAAAAAAAGLAWdnhaSTRXU2NSWXF5cXktUXA3RTdmQRZMRGgxYmQ3M1RGcWhhMnUwdUxPbUJnAAAAAAAAGK8WdnhaSTRXU2NSWXF5cXktUXA3RTdmQQ==
     */
    @Test
    public void test_query_scroll() throws IOException {
//        1   create SearchRequest
        SearchRequest request = new SearchRequest(index);

//        2   create scroll info and time
        request.scroll(TimeValue.timeValueMinutes(1L));

//        3   create query condition
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.size(2);
        builder.sort("fee", SortOrder.DESC);
        builder.query(QueryBuilders.matchAllQuery());

//        4 get result and return scroll_id ,source
        request.source(builder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        String scrollId = response.getScrollId();
        System.out.println(scrollId);

        // ------------- start scroll query -------------
        while (true) {

            //       5  create SearchScrollRequest
            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);

            // 6 set scroll_id existing time, one minute
            scrollRequest.scroll(TimeValue.timeValueMinutes(1L));

//        7 query and return result
            SearchResponse scrollResp = client.scroll(scrollRequest, RequestOptions.DEFAULT);

//        8. check if get result and print
            if (scrollResp.getHits().getHits() != null && scrollResp.getHits().getHits().length > 0) {
                System.out.println("=======next page ========");
                for (SearchHit hit : scrollResp.getHits().getHits()) {
                    System.out.println(hit.getSourceAsMap());
                }
            } else {
                //        9. if no result, break
                System.out.println("no result ");
                break;
            }
        }

        // delete scroll

        // 10  create clearScrollRequest
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();

        // 11 set scroll_id
        clearScrollRequest.addScrollId(scrollId);

        // 12  delete
        client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
    }

    /**
     * delete-by-query
     * <p>
     * POST /sms_logs_index/sms_logs_type/_delete_by_query
     * {
     * "query": {
     * "range": {
     * "relyTotal": {
     * "gte": 2,
     * "lte": 3
     * }
     * }
     * }
     * }
     */

    @Test
    public void test_delete_by_query() throws IOException {
        DeleteByQueryRequest request = new DeleteByQueryRequest(index);

        request.setQuery(QueryBuilders.rangeQuery("relyTotal").gt("2").lt("3"));

        BulkByScrollResponse response = client.deleteByQuery(request, RequestOptions.DEFAULT);

        System.out.println(response.toString());
    }



}
