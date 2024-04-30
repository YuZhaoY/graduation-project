package com.scu.stu.controller;

import com.scu.stu.common.ReplenishmentStatus;
import com.scu.stu.common.Result;
import com.scu.stu.common.SaleStatus;
import com.scu.stu.pojo.DO.queryParam.ReplenishmentQuery;
import com.scu.stu.pojo.DO.queryParam.SaleQuery;
import com.scu.stu.pojo.DTO.SaleDTO;
import com.scu.stu.pojo.VO.dashboard.Bar;
import com.scu.stu.pojo.VO.dashboard.Line;
import com.scu.stu.pojo.VO.dashboard.Panel;
import com.scu.stu.service.ReplenishmentService;
import com.scu.stu.service.SaleService;
import com.scu.stu.utils.DateUtils;
import com.scu.stu.utils.JwtUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class DashBoardController {

    @Resource
    private ReplenishmentService replenishmentService;

    @Resource
    private SaleService saleService;

    @GetMapping("api/dashboardPanel")
    public Result panel(@RequestParam(value = "token") String token) {
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            Panel panel = new Panel();
            //生效中的补货计划总数
            ReplenishmentQuery replenishmentQuery = new ReplenishmentQuery();
            replenishmentQuery.setPurchaseId(userId);
            replenishmentQuery.setStatus(ReplenishmentStatus.CREATE.getCode());
            int totalReplenishment = replenishmentService.total(replenishmentQuery);
            replenishmentQuery.setStatus(ReplenishmentStatus.VALID.getCode());
            totalReplenishment += replenishmentService.total(replenishmentQuery);
            panel.setReplenishment(totalReplenishment);
            //生效中的采购单总数
            SaleQuery saleQuery = new SaleQuery();
            saleQuery.setPurchaseId(userId);
            saleQuery.setStatus(SaleStatus.CREATE.getCode());
            int totalSale = saleService.total(saleQuery);
            saleQuery.setStatus(SaleStatus.CONFIRM.getCode());
            totalSale += saleService.total(saleQuery);
            panel.setSale(totalSale);

            saleQuery.setStatus(0);
            List<SaleDTO> saleDTOS = saleService.getList(saleQuery);
            BigDecimal payAmount = new BigDecimal(0);
            BigDecimal freezeAmount = new BigDecimal(0);
            for (SaleDTO saleDTO : saleDTOS) {
                if (DateUtils.isThisTime(saleDTO.getGMTCreate(), "yyyy")) {
                    //采购单已付款金额
                    if (saleDTO.getPayAmount().compareTo(new BigDecimal(0)) != 0) {
                        payAmount = payAmount.add(saleDTO.getPayAmount());
                    }
                    //采购单未付款金额
                    if (saleDTO.getFreezeAmount().compareTo(new BigDecimal(0)) != 0) {
                        freezeAmount = freezeAmount.add(saleDTO.getFreezeAmount());
                    }
                }
            }
            panel.setFreezeAmount(freezeAmount);
            panel.setPayAmount(payAmount);
            return Result.success(panel);
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @GetMapping("api/dashboardLine")
    public Result line(@RequestParam(value = "token") String token) {
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            Line line = new Line();
            SaleQuery query = new SaleQuery();
            List<SaleDTO> saleDTOS = saleService.getList(query);
            List<SaleDTO> lastYear = saleDTOS.stream().filter(saleDTO -> DateUtils.isLastYear(saleDTO.getGMTCreate())).collect(Collectors.toList());
            List<SaleDTO> nowYear = saleDTOS.stream().filter(saleDTO -> DateUtils.isThisTime(saleDTO.getGMTCreate(), "yyyy")).collect(Collectors.toList());
            Map<Integer, List<SaleDTO>> lastMap = lastYear.stream().collect(Collectors.groupingBy(saleDTO -> DateUtils.getMonth(saleDTO.getGMTCreate())));
            Map<Integer, List<SaleDTO>> nowMap = nowYear.stream().collect(Collectors.groupingBy(saleDTO -> DateUtils.getMonth(saleDTO.getGMTCreate())));
            List<BigDecimal> last = new ArrayList<>();
            List<BigDecimal> now = new ArrayList<>();
            for (int i = 1; i <= 12; i++) {
                BigDecimal lastMonth = new BigDecimal(0);
                if(lastMap.get(i) != null) {
                    for (SaleDTO saleDTO : lastMap.get(i)) {
                        lastMonth = lastMonth.add(saleDTO.getTotalAmount());
                    }
                }
                last.add(lastMonth);
            }
            line.setLast(last);
            for (int i = 1; i <= DateUtils.getMonth(new Date()); i++) {
                BigDecimal nowMonth = new BigDecimal(0);
                if(nowMap.get(i) != null) {
                    for (SaleDTO saleDTO : nowMap.get(i)) {
                        nowMonth = nowMonth.add(saleDTO.getTotalAmount());
                    }
                }
                now.add(nowMonth);
            }
            line.setNow(now);
            return Result.success(line);
        } else {
            return Result.error("未查到身份信息");
        }
    }


    @GetMapping("api/dashboardBar")
    public Result bar(@RequestParam(value = "token") String token) {
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            Bar bar = new Bar();
            SaleQuery query = new SaleQuery();
            query.setPurchaseId(userId);
            List<SaleDTO> saleDTOS = saleService.getList(query);
            List<SaleDTO> nowYear = saleDTOS.stream().filter(saleDTO -> DateUtils.isThisTime(saleDTO.getGMTCreate(), "yyyy")).collect(Collectors.toList());
            Map<Integer, List<SaleDTO>> nowMap = nowYear.stream().collect(Collectors.groupingBy(saleDTO -> DateUtils.getMonth(saleDTO.getGMTCreate())));
            List<BigDecimal> pay = new ArrayList<>();
            List<BigDecimal> freeze = new ArrayList<>();
            List<BigDecimal> refund = new ArrayList<>();
            List<BigDecimal> noInbound = new ArrayList<>();
            for (int i = 1; i <= DateUtils.getMonth(new Date()); i++) {
                BigDecimal payAmount = new BigDecimal(0);
                BigDecimal freezeAmount = new BigDecimal(0);
                BigDecimal refundAmount = new BigDecimal(0);
                BigDecimal noInboundAmount = new BigDecimal(0);
                if(nowMap.get(i) != null) {
                    for (SaleDTO saleDTO : nowMap.get(i)) {
                        boolean tag = true;
                        if (saleDTO.getPayAmount() != null && saleDTO.getPayAmount().compareTo(new BigDecimal(0)) != 0) {
                            payAmount = payAmount.add(saleDTO.getPayAmount());
                            tag = false;
                        }
                        if (saleDTO.getFreezeAmount() != null && saleDTO.getFreezeAmount().compareTo(new BigDecimal(0)) != 0) {
                            freezeAmount = freezeAmount.add(saleDTO.getFreezeAmount());
                            tag = false;
                        }
                        if (saleDTO.getReturnAmount() != null && saleDTO.getReturnAmount().compareTo(new BigDecimal(0)) != 0) {
                            refundAmount = refundAmount.add(saleDTO.getReturnAmount());
                            tag = false;
                        }
                        if (tag) {
                            noInboundAmount = noInboundAmount.add(saleDTO.getTotalAmount());
                        }
                    }
                }
                pay.add(payAmount);
                freeze.add(freezeAmount);
                refund.add(refundAmount);
                noInbound.add(noInboundAmount);
            }
            bar.setPay(pay);
            bar.setFreeze(freeze);
            bar.setRefund(refund);
            bar.setNoInbound(noInbound);
            return Result.success(bar);
        } else {
            return Result.error("未查到身份信息");
        }
    }
}
