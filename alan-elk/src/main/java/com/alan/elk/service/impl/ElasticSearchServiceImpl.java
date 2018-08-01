package com.alan.elk.service.impl;

import com.alan.common.pojo.CommonPage;
import com.alan.common.util.ExceptionUtil;
import com.alan.elk.config.ElasticSearchManager;
import com.alan.elk.service.ElasticSearchService;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * @author weiBin
 * @date 2018/7/26
 */
@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ElasticSearchManager elasticSearchManager;

    @Override
    public String add(Object o, String index, String type) {
        if (o == null) {
            return null;
        }
        Map<String, Object> doc = new HashMap<>();
        Field[] fields = o.getClass().getDeclaredFields();
        List<String> fieldNames = new ArrayList<>();
        for (Field field : fields) {
            fieldNames.add(field.getName());
        }
        for (String fieldName : fieldNames) {
            String first = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + first + fieldName.substring(1);
            try {
                Method method = o.getClass().getMethod(getter, new Class[]{});
                Object invoke = method.invoke(o, new Class[]{});
                doc.put(fieldName, invoke);
            } catch (Exception e) {
                logger.error(ExceptionUtil.getStackTrace(e));
                return null;
            }
        }
        return add(doc, index, type);
    }

    @Override
    public String add(Map<String, Object> doc, String index, String type) {
        if (doc == null) {
            return null;
        }
        XContentBuilder jsonBuilder = null;
        try {
            jsonBuilder = jsonBuilder();
            fullJsonBuilder(doc, jsonBuilder);
            IndexResponse response = elasticSearchManager.client.prepareIndex(index, type).setSource(jsonBuilder).get();
            return response.getId();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void fullJsonBuilder(Map<String, Object> doc, XContentBuilder jsonBuilder) throws IOException {
        jsonBuilder.startObject();
        for (String key : doc.keySet()) {
            Object object = doc.get(key);
            if (object instanceof Integer) {
                jsonBuilder.field(key, Integer.valueOf(object.toString()));
            } else if (object instanceof Long) {
                jsonBuilder.field(key, Long.valueOf(object.toString()));
            } else if (object instanceof String) {
                jsonBuilder.field(key, object.toString());
            } else {
                jsonBuilder.field(key, object);
            }
        }
        jsonBuilder.endObject();
    }

    @Override
    public Map<String, Object> get(String index, String type, String id) {
        GetResponse response = elasticSearchManager.client.prepareGet(index, type, id).get();
        Map<String, Object> source = response.getSource();
        return source;
    }

    @Override
    public boolean delete(String index, String type, String id) {
        try {
            elasticSearchManager.client.prepareDelete(index, type, id).get();
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
            return false;
        }
        return true;
    }

    @Override
    public void deleteByQueryBuilder(String index, String type, QueryBuilder queryBuilder) {
        DeleteByQueryAction.INSTANCE.newRequestBuilder(elasticSearchManager.client)
                .filter(queryBuilder)
                .source(index)
                .execute(new ActionListener<BulkByScrollResponse>() {
                    //回调监听
                    @Override
                    public void onResponse(BulkByScrollResponse response) {
                        //删除文档的数量
                        long deleted = response.getDeleted();
                        logger.info("批量删除{}条记录", deleted);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        logger.info("批量删除失败!");
                    }
                });
    }

    @Override
    public boolean update(Map<String, Object> doc, String id, String index, String type) {
        if (doc == null) {
            return false;
        }
        try {
            XContentBuilder jsonBuilder = jsonBuilder();
            fullJsonBuilder(doc, jsonBuilder);
            UpdateRequest updateRequest = new UpdateRequest(index, type, id).doc(jsonBuilder);
            elasticSearchManager.client.update(updateRequest).get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public CommonPage page(CommonPage commonPage, String index, String type, List<QueryBuilder> queryBuilders) {
        if (queryBuilders == null) {
            return commonPage;
        }
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        for (QueryBuilder qb : queryBuilders) {
            queryBuilder.must(qb);
        }
        SearchRequestBuilder searchRequestBuilder = elasticSearchManager.client
                .prepareSearch(index)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(queryBuilder)
                .setFrom(commonPage.getStartNo())
                .setSize(commonPage.getPageSize());
        SearchResponse searchResponse = searchRequestBuilder.get();
        commonPage.setTotalCount(searchResponse.getHits().getTotalHits());
        List<Map> list = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits()) {
            Map<String, Object> map = hit.getSource();
            list.add(map);
        }
        commonPage.setDatas(list);
        return commonPage;
    }
}
