package com.scu.stu.pojo.VO.param;

import lombok.Data;

import java.util.List;

@Data
public class AgreementParam {
    /**
     * 协议ID
     */
    private String agreementId;

    /**
     * 协议内容
     */
    private String avatar;

    /**
     * 协议生效时间
     */
    private List<String> effectTime;

    /**
     * 供应商ID
     */
    private String farmerId;
}
