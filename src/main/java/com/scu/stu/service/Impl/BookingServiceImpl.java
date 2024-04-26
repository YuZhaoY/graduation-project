package com.scu.stu.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.scu.stu.common.SaleStatus;
import com.scu.stu.mapper.BookingMapper;
import com.scu.stu.mapper.RelationMapper;
import com.scu.stu.mapper.SaleMapper;
import com.scu.stu.pojo.DO.BookingDO;
import com.scu.stu.pojo.DO.RelationDO;
import com.scu.stu.pojo.DO.SaleDO;
import com.scu.stu.pojo.DO.queryParam.BookingQuery;
import com.scu.stu.pojo.DO.queryParam.RelationQuery;
import com.scu.stu.pojo.DO.queryParam.SaleQuery;
import com.scu.stu.pojo.DTO.BookingDTO;
import com.scu.stu.service.BookingService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    @Resource
    private BookingMapper bookingMapper;

    @Resource
    private SaleMapper saleMapper;

    @Override
    public List<BookingDTO> query(BookingQuery query) {
        PageHelper.startPage(query.getPage(), query.getLimit());
        List<BookingDO> bookingDOList = bookingMapper.query(query);
        if(!CollectionUtils.isEmpty(bookingDOList)) {
            List<BookingDTO> bookingDTOList = bookingDOList.stream().map(bookingDO -> {
                BookingDTO bookingDTO = new BookingDTO();
                BeanUtils.copyProperties(bookingDO, bookingDTO);
                return bookingDTO;
            }).collect(Collectors.toList());
            PageInfo<BookingDTO> pageInfo = new PageInfo<>(bookingDTOList);
            return pageInfo.getList();
        }
        return null;
    }

    @Override
    public List<BookingDTO> batchQuery(List<String> bookingIdList) {
        List<BookingDO> bookingDOList = bookingMapper.batchQuery(bookingIdList);
        if(!CollectionUtils.isEmpty(bookingDOList)) {
            List<BookingDTO> bookingDTOList = bookingDOList.stream().map(bookingDO -> {
                BookingDTO bookingDTO = new BookingDTO();
                BeanUtils.copyProperties(bookingDO, bookingDTO);
                return bookingDTO;
            }).collect(Collectors.toList());
            return bookingDTOList;
        }
        return null;
    }

    @Override
    public int total(BookingQuery query) {
        return bookingMapper.total(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(BookingDTO bookingDTO, String saleId) {
        BookingDO bookingDO = new BookingDO();
        BeanUtils.copyProperties(bookingDTO, bookingDO);

        SaleQuery saleQuery = new SaleQuery();
        saleQuery.setSaleId(saleId);
        List<SaleDO> saleDOS = saleMapper.querySaleOrder(saleQuery);
        if (saleDOS != null && !CollectionUtils.isEmpty(saleDOS)) {
            SaleDO saleDO = saleDOS.get(0);
            saleDO.setStatus(SaleStatus.BOOKING.getCode());
            saleMapper.updateSaleOrder(saleDO);
        }

        return bookingMapper.create(bookingDO);
    }

    @Override
    public boolean update(BookingDTO bookingDTO) {
        BookingDO bookingDO = new BookingDO();
        BeanUtils.copyProperties(bookingDTO, bookingDO);
        return bookingMapper.update(bookingDO);
    }

}
