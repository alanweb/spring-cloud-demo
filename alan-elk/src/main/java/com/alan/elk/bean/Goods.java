package com.alan.elk.bean;

import lombok.Data;

/**
 * @author weiBin
 * @date 2018/7/26
 */

@Data
public class Goods {
    private String id;
    private String name;
    private String brand;
    private String description;
    private Float price;
    private Integer sale;
}
