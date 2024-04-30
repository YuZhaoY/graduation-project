package com.scu.stu.pojo.VO.dashboard;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Line {

    /**
     * 去年按月划分的总金额
     */
    List<BigDecimal> last;


    /**
     * 今年年按月划分的总金额
     */
    List<BigDecimal> now;
}
