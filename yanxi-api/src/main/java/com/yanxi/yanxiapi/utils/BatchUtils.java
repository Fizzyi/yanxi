package com.yanxi.yanxiapi.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 批量操作工具类 - 优化数据库查询性能
 */
@Component
@Slf4j
public class BatchUtils {

    @Value("${app.performance.batch-size:100}")
    private int defaultBatchSize;

    /**
     * 批量处理集合，避免一次性处理过多数据
     * @param collection 要处理的集合
     * @param batchSize 批次大小
     * @param processor 处理函数
     * @param <T> 集合元素类型
     * @param <R> 处理结果类型
     * @return 处理结果列表
     */
    public <T, R> List<R> processBatch(Collection<T> collection, int batchSize, Function<List<T>, List<R>> processor) {
        if (collection == null || collection.isEmpty()) {
            return new ArrayList<>();
        }

        List<T> list = new ArrayList<>(collection);
        List<R> results = new ArrayList<>();
        
        for (int i = 0; i < list.size(); i += batchSize) {
            int end = Math.min(i + batchSize, list.size());
            List<T> batch = list.subList(i, end);
            
            try {
                List<R> batchResults = processor.apply(batch);
                if (batchResults != null) {
                    results.addAll(batchResults);
                }
            } catch (Exception e) {
                log.error("Error processing batch [{}-{}]", i, end, e);
                // 继续处理下一批，不中断整个流程
            }
        }
        
        return results;
    }

    /**
     * 使用默认批次大小进行批量处理
     */
    public <T, R> List<R> processBatch(Collection<T> collection, Function<List<T>, List<R>> processor) {
        return processBatch(collection, defaultBatchSize, processor);
    }

    /**
     * 将集合按指定大小分割成多个批次
     * @param collection 要分割的集合
     * @param batchSize 批次大小
     * @param <T> 集合元素类型
     * @return 分割后的批次列表
     */
    public <T> List<List<T>> partition(Collection<T> collection, int batchSize) {
        if (collection == null || collection.isEmpty()) {
            return new ArrayList<>();
        }

        List<T> list = new ArrayList<>(collection);
        List<List<T>> partitions = new ArrayList<>();
        
        for (int i = 0; i < list.size(); i += batchSize) {
            int end = Math.min(i + batchSize, list.size());
            partitions.add(new ArrayList<>(list.subList(i, end)));
        }
        
        return partitions;
    }

    /**
     * 批量查询并构建映射关系
     * @param keys 查询键集合
     * @param keyExtractor 从结果中提取键的函数
     * @param queryFunction 查询函数
     * @param <K> 键类型
     * @param <V> 值类型
     * @return 键值映射
     */
    public <K, V> Map<K, V> batchQueryToMap(Collection<K> keys, Function<V, K> keyExtractor, Function<List<K>, List<V>> queryFunction) {
        if (keys == null || keys.isEmpty()) {
            return new HashMap<>();
        }

        List<K> uniqueKeys = keys.stream().distinct().collect(Collectors.toList());
        List<V> results = processBatch(uniqueKeys, queryFunction);
        
        return results.stream()
                .collect(Collectors.toMap(keyExtractor, Function.identity(), (existing, replacement) -> existing));
    }

    /**
     * 批量查询并构建分组映射关系
     * @param keys 查询键集合
     * @param keyExtractor 从结果中提取键的函数
     * @param queryFunction 查询函数
     * @param <K> 键类型
     * @param <V> 值类型
     * @return 键到值列表的映射
     */
    public <K, V> Map<K, List<V>> batchQueryToGroupMap(Collection<K> keys, Function<V, K> keyExtractor, Function<List<K>, List<V>> queryFunction) {
        if (keys == null || keys.isEmpty()) {
            return new HashMap<>();
        }

        List<K> uniqueKeys = keys.stream().distinct().collect(Collectors.toList());
        List<V> results = processBatch(uniqueKeys, queryFunction);
        
        return results.stream()
                .collect(Collectors.groupingBy(keyExtractor));
    }

    /**
     * 去重并保持顺序
     * @param collection 原始集合
     * @param <T> 元素类型
     * @return 去重后的列表
     */
    public <T> List<T> distinctPreserveOrder(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return new ArrayList<>();
        }

        return collection.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 安全的集合大小检查
     * @param collection 集合
     * @return 集合大小，null时返回0
     */
    public int safeSize(Collection<?> collection) {
        return collection == null ? 0 : collection.size();
    }

    /**
     * 检查集合是否为空或null
     * @param collection 集合
     * @return 是否为空
     */
    public boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 检查集合是否非空
     * @param collection 集合
     * @return 是否非空
     */
    public boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }
} 