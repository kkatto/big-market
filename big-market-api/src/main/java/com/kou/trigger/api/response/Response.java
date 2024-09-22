package com.kou.trigger.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author KouJY
 * Date: 2024/7/25 14:49
 * Package: com.kou.types.model
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Response<T> implements Serializable {

    private String code;

    private String info;

    private T data;
}
