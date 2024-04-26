package com.scu.stu.service;

import com.scu.stu.pojo.DO.AgreementDO;
import com.scu.stu.pojo.DTO.AgreementDTO;
import org.springframework.stereotype.Service;

import java.util.List;
public interface AgreementService {

    AgreementDTO query(String agreementId);

    boolean insert(AgreementDO agreementDO);

    List<AgreementDTO> queryAgreementList(String userId, int pageNow, int pageSize);

    int countByPurchaseId(String purchaseId);

    boolean updateAgreement(AgreementDTO  agreementDTO);
}
