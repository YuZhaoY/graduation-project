package com.scu.stu.service;

import com.scu.stu.pojo.DO.queryParam.ReplenishmentQuery;
import com.scu.stu.pojo.DO.queryParam.ReplenishmentSubQuery;
import com.scu.stu.pojo.DTO.ReplenishmentDTO;
import com.scu.stu.pojo.DTO.ReplenishmentSubDTO;

import java.text.ParseException;
import java.util.List;

public interface ReplenishmentService {

    /**
     * 查询补货计划列表
     * @param query
     * @return
     */
    List<ReplenishmentDTO> query(ReplenishmentQuery query);

    /**
     * 查询补货计划总量
     * @param query
     * @return
     */
    int total(ReplenishmentQuery query);

    /**
     * 创建补货计划
     * @param replenishmentDTO
     * @return
     */
    boolean createReplenishment(ReplenishmentDTO replenishmentDTO, List<ReplenishmentSubDTO> replenishmentSubDTOS) throws ParseException;

    /**
     * 更新补货计划
     * @param replenishmentDTO
     * @return
     */
    boolean updateReplenishment(
            ReplenishmentDTO replenishmentDTO,
            List<ReplenishmentSubDTO> updateSubDTOS,
            List<ReplenishmentSubDTO> createSubDTOS,
            List<ReplenishmentSubDTO> deleteSubDTOS
    ) throws ParseException;


    /**
     * 更新补货计划状态
     * @param replenishmentDTO
     * @return
     */
    boolean updateReplenishment(ReplenishmentDTO replenishmentDTO) throws ParseException;

    /**
     * 更新过期计划
     * @return
     */
    boolean updateInvalid();

    /**
     * 更新有效计划
     * @return
     */
    boolean updateValid();

    /**
     * 查找补货计划子表
     * @param query
     * @return
     */
    List<ReplenishmentSubDTO> querySub(ReplenishmentSubQuery query);

    /**
     * 批量查找补货计划子表
     * @param replenishmentIdList
     * @return
     */
    List<ReplenishmentSubDTO> batchQuerySub(List<String> replenishmentIdList);

    /**
     * 创建补货计划子表
     * @param replenishmentSubDTOS
     * @return
     */
    boolean createReplenishmentSub(List<ReplenishmentSubDTO> replenishmentSubDTOS);

    /**
     * 更新补货计划子表
     * @param replenishmentSubDTOS
     * @return
     */
    boolean updateReplenishmentSub(List<ReplenishmentSubDTO> replenishmentSubDTOS);

    /**
     * 删除补货计划子表数据
     * @param replenishmentSubDTOS
     * @return
     */
    boolean deleteReplenishmentSub(List<ReplenishmentSubDTO> replenishmentSubDTOS);
}
