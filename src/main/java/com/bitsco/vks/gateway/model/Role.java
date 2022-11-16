package com.bitsco.vks.gateway.model;

import com.bitsco.vks.common.model.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Role extends BaseModel {
    private Long parentId;
    private String name;
    private String description;
    private String url;
    private String icon;
    private Integer type;
    private Integer status;
}
