package com.example.im.vo;

import lombok.Data;

@Data
public class ImTo {

    private Long id;
    private String username;
    private String type; //群类型
    private String avatar;
    private Integer members;

}
