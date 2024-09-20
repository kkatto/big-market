package com.kou.trigger.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author KouJY
 * Date: 2024/9/12 16:26
 * Package: com.kou.trigger.api.dto
 *
 * 商品购物车请求对象
 */
@Data
public class SkuProductShopCartRequestDTO implements Serializable {

    /**
     * 用户ID
     */
    private String userId;
    /**
     * sku 商品
     */
    private Long sku;
}
