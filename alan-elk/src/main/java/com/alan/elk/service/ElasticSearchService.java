package com.alan.elk.service;

import com.alan.common.pojo.CommonPage;
import org.elasticsearch.index.query.QueryBuilder;

import java.util.List;
import java.util.Map;

/**
 * elasticsearch 操作接口
 *
 * @author weiBin
 * @date 2018/7/26
 */
public interface ElasticSearchService {
    /**
     * 往es添加文档 返回 _id
     *
     * @param o     对象
     * @param index es索引
     * @param type  es类型
     * @return
     */
    String add(Object o, String index, String type);

    /**
     * 往es添加文档 返回 _id
     *
     * @param doc   属性集合
     * @param index es索引
     * @param type  es类型
     * @return
     */
    String add(Map<String, Object> doc, String index, String type);

    /**
     * 通过_id 获取es 文档
     *
     * @param index es索引
     * @param type  es类型
     * @param id    _id
     * @return
     */
    Map<String, Object> get(String index, String type, String id);

    /**
     * 通过_id 删除es文档
     *
     * @param index es索引
     * @param type  es类型
     * @param id    _id
     * @return
     */
    boolean delete(String index, String type, String id);


    /**
     * 根据条件异步批量删除文档
     *
     * @param index        es索引
     * @param type         es类型
     * @param queryBuilder 查询条件
     */
    void deleteByQueryBuilder(String index, String type, QueryBuilder queryBuilder);

    /**
     * 更新es文档
     *
     * @param doc   更新文档内容
     * @param id    es _id
     * @param index es索引
     * @param type  es类型
     * @return
     */
    boolean update(Map<String, Object> doc, String id, String index, String type);


    /**
     * 根据条件分页查询es 文档
     *
     * @param commonPage    分页对象
     * @param index         es索引
     * @param type          es类型
     * @param queryBuilders 查询条件集合
     * @return
     */
    CommonPage page(CommonPage commonPage, String index, String type, List<QueryBuilder> queryBuilders);


}
