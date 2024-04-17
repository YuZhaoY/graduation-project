package com.scu.stu.pojo.VO.param;

import lombok.Data;

@Data
public class BookingParam {

    /**
     * 采购单ID
     */
    private String saleId;

    /**
     * 预约单ID
     */
    private String bookingId;

    /**
     * 预约时间
     */
    private String bookingTime;

    /**
     * 到货仓ID
     */
    private String storeId;

    /**
     * 车牌号
     */
    private String licensePlate;

    /**
     * 司机姓名
     */
    private String driver;
}
