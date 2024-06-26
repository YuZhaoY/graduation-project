package com.scu.stu.mapper;

import com.scu.stu.pojo.DO.AgreementDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AgreementMapper {

    AgreementDO query(String agreementId);

    boolean insertAgreement(AgreementDO agreementDO);

    List<AgreementDO> queryByXiaoer(String userId);

    List<AgreementDO> queryBySupplier(String userId);

    int countByPurchaseId(String purchaseId);

    boolean update(AgreementDO agreementDO);
}
