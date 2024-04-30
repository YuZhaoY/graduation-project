package com.scu.stu.pojo.VO.dashboard;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Bar {

    /**
     * 按月划分的已支付金额
     */
    List<BigDecimal> pay;

    /**
     * 按月划分的已冻结金额
     */
    List<BigDecimal> freeze;

    /**
     * 按月划分退供金额
     */
    List<BigDecimal> refund;

    /**
     * 按月划分未入库金额
     */
    List<BigDecimal> noInbound;
}
