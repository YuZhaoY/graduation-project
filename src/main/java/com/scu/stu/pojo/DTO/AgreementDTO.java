package com.scu.stu.pojo.DTO;

import lombok.Data;

@Data
public class AgreementDTO {

    /**
     * 协议ID
     */
    private String agreementId;

    /**
     * 创建时间
     */
    private String GMTCreate;

    /**
     * 修改时间
     */
    private String GMTModified;

    /**
     * 协议内容
     */
    private String content;

    /**
     * 协议生效时间
     */
    private String effectStartTime;

    /**
     * 协议结束时间
     */
    private String effectEndTime;

    /**
     * 供应商签署时间
     */
    private String GMTSign;

    /**
     * 采购小二ID
     */
    private String purchaseId;

    /**
     * 供应商ID
     */
    private String farmerId;

    /**
     * 协议状态
     */
    private int status;

    /**
     * 版本号
     */
    private int version;
}
