package com.scu.stu.pojo.DO;

import lombok.Data;

import java.util.Date;

@Data
public class BookingDO {

    /**
     * 预约单ID
     */
    private String bookingId;

    /**
     * 创建时间
     */
    private Date GMTCreate;

    /**
     * 修改时间
     */
    private Date GMTModified;

    /**
     * 预约时间
     */
    private Date bookingTime;

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
    private Date signTime;

    /**
     * 状态
     */
    private int status;

    /**
     * 版本
     */
    private int version;
}
