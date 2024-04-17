package com.scu.stu.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scu.stu.common.RoleEnum;
import com.scu.stu.mapper.AgreementMapper;
import com.scu.stu.mapper.UserMapper;
import com.scu.stu.pojo.DO.AgreementDO;
import com.scu.stu.pojo.DO.UserInfoDO;
import com.scu.stu.pojo.DTO.AgreementDTO;
import com.scu.stu.service.AgreementService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgreementServiceImpl implements AgreementService {

    @Resource
    private AgreementMapper agreementMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public AgreementDTO query(String agreementId) {
        AgreementDO result = agreementMapper.query(agreementId);
        AgreementDTO agreementDTO = new AgreementDTO();
        BeanUtils.copyProperties(result, agreementDTO);
        return agreementDTO;
    }

    @Override
    public boolean insert(AgreementDO agreementDO) {
        return agreementMapper.insertAgreement(agreementDO);
    }

    @Override
    public List<AgreementDTO> queryAgreementList(String userId, int pageNow, int pageSize) {
        PageHelper.startPage(pageNow,pageSize);
        UserInfoDO userInfoDO = userMapper.queryUserInfo(userId);
        List<AgreementDO> agreementDOS = userInfoDO.getRole() == RoleEnum.ADMIN.getRole()? agreementMapper.queryByXiaoer(userId) : agreementMapper.queryBySupplier(userId);
        List<AgreementDTO> agreementDTOS = agreementDOS.stream().map(agreementDO -> {
            AgreementDTO agreementDTO = new AgreementDTO();
            BeanUtils.copyProperties(agreementDO, agreementDTO);
            return agreementDTO;
        }).collect(Collectors.toList());
        PageInfo<AgreementDTO> agreementDTOList = new PageInfo<>(agreementDTOS);
        return agreementDTOList.getList();
    }

    @Override
    public int countByPurchaseId(String purchaseId) {
        return agreementMapper.countByPurchaseId(purchaseId);
    }

    @Override
    public boolean updateAgreement(AgreementDTO agreementDTO) {
        AgreementDO agreementDO = new AgreementDO();
        BeanUtils.copyProperties(agreementDTO,agreementDO);
        return agreementMapper.update(agreementDO);
    }
}
