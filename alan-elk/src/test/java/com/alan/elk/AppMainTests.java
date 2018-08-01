package com.alan.elk;

import com.alan.elk.config.ElasticSearchManager;
import com.alan.elk.service.ElasticSearchService;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppMainTests {
    @Autowired
    private ElasticSearchManager elasticSearchManager;
    @Autowired
    private ElasticSearchService elasticSearchService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testDelete() {
        elasticSearchService.deleteByQueryBuilder("es-test", "goods", QueryBuilders.matchQuery("id", "111"));
    }

    @Test
    public void testHighlightBuilder() {
        HighlightBuilder highlightBuilder = new HighlightBuilder().field("description").requireFieldMatch(false);
        highlightBuilder.preTags("<font style='color:red'>");
        highlightBuilder.postTags("</font>");

        SearchRequestBuilder requestBuilder = elasticSearchManager.client.prepareSearch("es-test")
                .setQuery(QueryBuilders.matchQuery("description", "华为移动"))
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setFrom(0)
                .setSize(20);
        requestBuilder.highlighter(highlightBuilder);
        SearchResponse searchResponse = requestBuilder.get();
        for (SearchHit hit : searchResponse.getHits()) {
            Map<String, Object> source = hit.getSource();
            Map<String, HighlightField> fields = hit.getHighlightFields();
            HighlightField description = fields.get("description");
            if (description != null) {
                Text[] fragments = description.getFragments();
                String content = "";
                for (Text text : fragments) {
                    content += text;
                }
                source.put("description", content);
            }
            Iterator<String> iterator = source.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                System.out.print(key + ":" + source.get(key) + "	");
            }
            System.out.println();
        }
    }

    @Test
    public void testQueryBuilder() {
        QueryBuilder queryBuilder = QueryBuilders.matchQuery("name", "手");
        SearchRequestBuilder searchRequestBuilder = elasticSearchManager.client
                .prepareSearch("es-test")
                .setQuery(queryBuilder)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setFrom(0)
                .setSize(10);
        SearchResponse searchResponse = searchRequestBuilder.get();
        for (SearchHit hit : searchResponse.getHits()) {
            Map<String, Object> source = hit.getSource();
            for (String key : source.keySet()) {
                System.out.print(key + ":" + source.get(key) + "\t");
            }
            System.out.println();
        }
    }

    @Test
    public void testAggregationBuilder() {
        QueryBuilder queryBuilder = QueryBuilders.matchQuery("name", "手机");
        AbstractAggregationBuilder builder = AggregationBuilders.terms(
                "sale_group").field("price");    //es高级查询agg
        AbstractAggregationBuilder sumAgg = AggregationBuilders.sum("sal_sum").field("sace");    //做分组
        SearchRequestBuilder searchRequestBuilder = elasticSearchManager.client
                .prepareSearch("es-test").setTypes("goods")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(queryBuilder).setFrom(0).setSize(20)
                .addAggregation(builder).addAggregation(sumAgg);
        SearchResponse response = searchRequestBuilder.get();
        Terms terms = response.getAggregations().get("sale_group");
        InternalSum internalSum = (InternalSum) response.getAggregations().asMap().get("sal_sum");
        System.out.println(internalSum.getName() + ":" + internalSum.getValue());
        List<? extends Bucket> buckets = terms.getBuckets();
        for (Bucket bucket : buckets) {
            System.out.println(bucket.getKey() + ":" + bucket.getDocCount());
        }
    }

    public static void main(String[] args) throws IOException {
        TransportClient client = null;
        try {
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.142.130"), 9300))
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.142.130"), 8300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String[] pp = {"一加", "三星", "魅族", "锤子", "360手机", "联想", "华为", "荣耀", "小米", "vivo", "OPPO"};
        String[] type = {"青春", "旗舰", "标配", "高配", "拍照", "女性", "性能王"};
        String[] price = {"全网通", "电信", "移动", "联通"};
        String[] pro = {"笔记本", "手机", "Ipad"};

        for (int i = 0; i < 100; i++) {
            String proName = pp[(int) Math.round(Math.random() * (pp.length - 1))] +
                    type[(int) Math.round(Math.random() * (type.length - 1))] +
                    price[(int) Math.round(Math.random() * (price.length - 1))] +
                    Math.round(Math.random() * 9) + Math.round(Math.random() * 9) + Math.round(Math.random() * 9) + Math.round(Math.random() * 9) +
                    pro[(int) Math.round(Math.random() * (pro.length - 1))];
            client.prepareIndex("es-test", "goods", String.valueOf(i + 1)).setSource(
                    XContentFactory.jsonBuilder()
                            .startObject()
                            .field("description", proName)
                            .field("name", proName)
                            .field("price", Math.round(Math.random() * 9) + Math.round(Math.random() * 9) + Math.round(Math.random() * 9) + Math.round(Math.random() * 9))
                            .field("sale", Math.round(Math.random() * 9) + Math.round(Math.random() * 9))
                            .endObject()
            ).get();
        }
        client.close();
    }
}
