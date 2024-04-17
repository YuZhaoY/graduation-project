package com.scu.stu.controller;

import com.scu.stu.common.Result;
import com.scu.stu.pojo.DTO.RelationDTO;
import com.scu.stu.pojo.VO.RelationVO;
import com.scu.stu.service.RelationService;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class RelationController {

    @Resource
    private RelationService relationService;

    @PostMapping("api/queryRelationByBookingList")
    public Result queryRelationByBookingList(@RequestBody List<String> bookingList) {
        if (bookingList != null && !CollectionUtils.isEmpty(bookingList)) {
            List<RelationDTO> relationDTOList = relationService.queryByBookingList(bookingList);
            if (relationDTOList != null && !CollectionUtils.isEmpty(relationDTOList)) {
                List<RelationVO> relationVOList = relationDTOList.stream().map(relationDTO -> {
                    RelationVO relationVO = new RelationVO();
                    BeanUtils.copyProperties(relationDTO, relationVO);
                    return relationVO;
                }).collect(Collectors.toList());
                return Result.success(relationVOList);
            }
        }
        return Result.success();
    }
}
