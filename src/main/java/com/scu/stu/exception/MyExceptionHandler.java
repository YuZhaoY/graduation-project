package com.scu.stu.exception;

import com.scu.stu.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

@ControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(value = ParseException.class)
    @ResponseBody
    public Result handler(ParseException e){
        return Result.error("日期数据格式转换错误");
    }

    @ExceptionHandler(value = SQLException.class)
    @ResponseBody
    public Result handler(SQLException e){
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(value = RuntimeException.class)
    @ResponseBody
    public Result handler(RuntimeException e){
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(value = IOException.class)
    @ResponseBody
    public Result handler(IOException e){
        return Result.error("验证token失败");
    }

    @ExceptionHandler(value = ServletException.class)
    @ResponseBody
    public Result handler(ServletException e){
        return Result.error("请求错误");
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result handler(Exception e){
        return Result.error("系统错误");
    }
}
