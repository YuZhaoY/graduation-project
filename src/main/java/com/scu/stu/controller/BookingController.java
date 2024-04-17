package com.scu.stu.controller;

import com.alibaba.fastjson.JSON;
import com.scu.stu.common.BookingStatus;
import com.scu.stu.common.Result;
import com.scu.stu.common.RoleEnum;
import com.scu.stu.pojo.DO.LoginInfoDO;
import com.scu.stu.pojo.DO.queryParam.BookingQuery;
import com.scu.stu.pojo.DTO.BookingDTO;
import com.scu.stu.pojo.VO.BookingVO;
import com.scu.stu.pojo.VO.param.BookingParam;
import com.scu.stu.service.BookingService;
import com.scu.stu.service.UserService;
import com.scu.stu.utils.DateUtils;
import com.scu.stu.utils.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import wiki.xsx.core.snowflake.config.Snowflake;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class BookingController {

    @Resource
    private BookingService bookingService;

    @Autowired
    private Snowflake snowflake;

    @Resource
    private UserService userService;

    @GetMapping("api/getBookingList")
    public Result getList(@RequestParam(value = "token") String token, @RequestParam(value = "data") String data) {
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            LoginInfoDO loginInfoDO = userService.queryLoginInfo(userId);
            BookingQuery query = JSON.parseObject(data, BookingQuery.class);
            if (loginInfoDO.getRole() == RoleEnum.EDITOR.getRole()) {
                query.setFarmerId(userId);
            }
            List<BookingDTO> bookingDTOList = bookingService.query(query);
            if (bookingDTOList != null && !CollectionUtils.isEmpty(bookingDTOList)) {
                List<BookingVO> bookingVOList = bookingDTOList.stream().map(bookingDTO -> {
                    BookingVO bookingVO = new BookingVO();
                    BeanUtils.copyProperties(bookingDTO, bookingVO);
                    bookingVO.setGMTCreate(DateUtils.format(bookingDTO.getGMTCreate()));
                    bookingVO.setBookingTime(DateUtils.format(bookingDTO.getBookingTime()));
                    if(bookingDTO.getSignTime() != null) {
                        bookingVO.setSignTime(DateUtils.format(bookingDTO.getSignTime()));
                    }
                    bookingVO.setStatus(BookingStatus.getDescByCode(bookingDTO.getStatus()));
                    return bookingVO;
                }).collect(Collectors.toList());
                return Result.success(bookingVOList);
            }
            return Result.success();
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @GetMapping("api/getBookingTotal")
    public Result total(@RequestParam(value = "token") String token, @RequestParam(value = "data") String data){
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            LoginInfoDO loginInfoDO = userService.queryLoginInfo(userId);
            BookingQuery query = JSON.parseObject(data, BookingQuery.class);
            if (loginInfoDO.getRole() == RoleEnum.EDITOR.getRole()) {
                query.setFarmerId(userId);
            }
            int total = bookingService.total(query);
            return Result.success(total);
        } else {
            return Result.error("未查到身份信息");
        }
    }
    @PostMapping("api/createBooking")
    public Result create(@RequestParam(value = "token") String token, @RequestParam(value = "data") String data) throws ParseException {
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            BookingParam param = JSON.parseObject(data, BookingParam.class);
            BookingDTO bookingDTO = new BookingDTO();
            BeanUtils.copyProperties(param, bookingDTO);
            bookingDTO.setBookingId(snowflake.nextIdStr());
            bookingDTO.setBookingTime(DateUtils.parse(param.getBookingTime()));
            bookingDTO.setStatus(BookingStatus.VALID.getCode());
            bookingDTO.setFarmerId(userId);
            if (bookingService.create(bookingDTO, param.getSaleId())) {
                return Result.success();
            } else {
                return Result.error("创建失败");
            }
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @PostMapping("api/cancelBooking")
    public Result cancel(@RequestParam(value = "token") String token, @RequestParam(value = "data") String bookingId){
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            BookingQuery query = new BookingQuery();
            query.setBookingId(bookingId);
            query.setFarmerId(userId);
            List<BookingDTO> bookingDTOS = bookingService.query(query);
            if (bookingDTOS != null && !CollectionUtils.isEmpty(bookingDTOS)) {
                BookingDTO old = bookingDTOS.get(0);
                if(old.getStatus() == BookingStatus.INVALID.getCode()){
                    return Result.success();
                }
                if(old.getStatus() == BookingStatus.SIGN.getCode()){
                    return Result.error("已签到，无法取消");
                }
                old.setStatus(BookingStatus.INVALID.getCode());
                if (bookingService.update(old)) {
                    return Result.success();
                } else {
                    return Result.error("取消失败");
                }
            } else {
                return Result.error("未查到预约单");
            }
        } else {
            return Result.error("未查到身份信息");
        }
    }
    @PostMapping("api/updateBooking")
    public Result update(@RequestBody BookingParam param) throws ParseException {
        BookingQuery query = new BookingQuery();
        query.setBookingId(param.getBookingId());
        List<BookingDTO> bookingDTOS = bookingService.query(query);
        if (bookingDTOS != null && !CollectionUtils.isEmpty(bookingDTOS)) {
            BookingDTO old = bookingDTOS.get(0);
            if(old.getStatus() == BookingStatus.SIGN.getCode()){
                return Result.error("已签到，无法取消");
            }
            if (old.getVersion() >= 3) {
                return Result.error("修改超过三次，无法继续修改");
            }
            BookingDTO bookingDTO = new BookingDTO();
            BeanUtils.copyProperties(param, bookingDTO);
            bookingDTO.setBookingTime(DateUtils.parse(param.getBookingTime()));
            bookingDTO.setStatus(BookingStatus.VALID.getCode());
            if (bookingService.update(bookingDTO)) {
                return Result.success();
            } else {
                return Result.error("更新失败");
            }
        } else {
            return Result.error("未查到预约单");
        }
    }

    /**
     * 签到
     * @param token
     * @param bookingId
     * @return
     */
    @PostMapping("api/signBooking")
    public Result sign(@RequestParam(value = "token") String token, @RequestParam(value = "data") String bookingId){
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            LoginInfoDO loginInfoDO = userService.queryLoginInfo(userId);
            if (loginInfoDO.getRole() == RoleEnum.EDITOR.getRole()) {
                return Result.error("供应商无法进行签到,请联系仓库小二签到");
            }
            BookingQuery query = new BookingQuery();
            query.setBookingId(bookingId);
            List<BookingDTO> bookingDTOS = bookingService.query(query);
            if (bookingDTOS != null && !CollectionUtils.isEmpty(bookingDTOS)) {
                BookingDTO old = bookingDTOS.get(0);
                if(old.getStatus() == BookingStatus.INVALID.getCode()){
                    return Result.error("预约单无效，无法签到");
                }
                if(old.getStatus() == BookingStatus.SIGN.getCode()){
                    return Result.error("已签到，请勿重复签到");
                }
                old.setStatus(BookingStatus.SIGN.getCode());
                old.setSignTime(new Date());
                if (bookingService.update(old)) {
                    return Result.success();
                } else {
                    return Result.error("签到失败");
                }
            } else {
                return Result.error("未查到预约单");
            }
        } else {
            return Result.error("未查到身份信息");
        }
    }
}
