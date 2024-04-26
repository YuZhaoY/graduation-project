package com.scu.stu.common;

import lombok.Data;

@Data
public class Result<T> {
    //响应码
    private int code;
    /**
     * 响应消息
     */
    private String message;
    private T data ;

    public static Result success() {
        return success(null);
    }

    /**
     * 成功
     *
     * @param data
     * @return
     */
    public static<T> Result success(T data) {
        Result rb = new Result();
        rb.setCode(CommonStatusEnum.SUCCESS.getCode());
        rb.setMessage(CommonStatusEnum.SUCCESS.getDesc());
        rb.setData(data);
        return rb;
    }
    /**
     * 失败
     */
    public static Result error(int code, String message) {
        Result rb = new Result();
        rb.setCode(code);
        rb.setMessage(message);
        rb.setData(null);
        return rb;
    }

    /**
     * 失败
     */
    public static Result error(String message) {
        Result rb = new Result();
        rb.setCode(CommonStatusEnum.ERROR.getCode());
        rb.setMessage(message);
        rb.setData(null);
        return rb;
    }

    /**
     * 登出
     */
    public static Result logout() {
        Result rb = new Result();
        rb.setCode(CommonStatusEnum.LOGOUT.getCode());
        rb.setData(null);
        return rb;
    }
}
