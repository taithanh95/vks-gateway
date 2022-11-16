package com.bitsco.vks.gateway.model;

import com.bitsco.vks.common.model.BaseModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class User extends BaseModel {
    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Long groupUserId;
    private Integer type;
    private Integer status;
    private Integer emailVerified;
    private String imageUrl;
    private Date expiredate;
    private String inspcode;
    private String sppid;
    private String departid;
    private String groupid;
    private String sppname;
    private String departname;
    private String inspectname;
    private String groupidname;

    @JsonIgnore
    private String passwordDecode;
}
