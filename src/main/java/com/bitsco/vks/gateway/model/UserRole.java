package com.bitsco.vks.gateway.model;

import com.bitsco.vks.common.model.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRole extends BaseModel {
    private Long userId;
    private Long roleId;
    private Integer status;
}
