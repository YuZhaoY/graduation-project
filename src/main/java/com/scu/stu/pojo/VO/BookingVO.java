package com.scu.stu.pojo.VO;

import lombok.Data;

@Data
public class BookingVO {

    /**
     * 预约单ID
     */
    private String bookingId;

    /**
     * 创建时间
     */
    private String GMTCreate;

    /**
     * 预约时间
     */
    private String bookingTime;

    /**
     * 到货仓ID
     */
    private String storeId;

    /**
     * 供应商ID
     */
    private String farmerId;

    /**
     * 车牌号
     */
    private String licensePlate;

    /**
     * 司机姓名
     */
    private String driver;

    /**
     * 签到时间
     */
    private String signTime;

    /**
     * 状态
     */
    private String status;
}
