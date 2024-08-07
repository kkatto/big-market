package com.kou.domain.activity.service;

import com.kou.domain.activity.model.valobj.ActivitySkuStockKeyVO;

/**
 * @author KouJY
 * Date: 2024/8/7 17:56
 * Package: com.kou.domain.activity.service
 *
 * 活动sku库存处理接口
 */
public interface ISkuStock {

    /**
     * 获取活动sku库存消耗队列
     *
     * @return 奖品库存Key信息
     * @throws InterruptedException 异常
     */
    ActivitySkuStockKeyVO takeQueueValue() throws InterruptedException;

    /**
     * 清空队列
     */
    void clearQueueValue();

    /**
     * 延迟队列 + 任务趋势更新活动sku库存
     *
     * @param sku 活动商品
     */
    void updateActivitySkuStock(Long sku);

    /**
     * 缓存库存以消耗完毕，清空数据库库存
     *
     * @param sku 活动商品
     */
    void clearActivitySkuStock(Long sku);
}
