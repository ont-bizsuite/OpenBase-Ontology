package com.ontology.schedulers;

import com.alibaba.fastjson.JSONObject;
import com.ontology.controller.vo.AuditPointDto;
import com.ontology.entity.AsyncTx;
import com.ontology.mapper.AsyncTxMapper;
import com.ontology.service.HonorPointService;
import com.ontology.utils.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Slf4j
public class CacheTxScheduler {
    @Autowired
    private AsyncTxMapper asyncTxMapper;
    @Autowired
    private HonorPointService honorPointService;

    /**
     * getCachedAuditorTx
     */
    @Scheduled(initialDelay = 5 * 1000, fixedDelay = 10 * 1000)
    public void getCachedAuditorTx() throws Exception {
        log.info("getCachedAuditorTx : {}", Thread.currentThread().getName());
        List<AsyncTx> list = asyncTxMapper.getCacheTx(Constant.AUDITOR_ACTION, 0, 100);
        for (AsyncTx asyncTx : list) {
            log.info("getCachedAuditorTx : {}", Thread.currentThread().getName());
            String param = asyncTx.getParam();
            AuditPointDto dto = JSONObject.parseObject(param, AuditPointDto.class);
            List<String> userIds = dto.getUserIds();
            String dataId = dto.getDataId();
            String version = dto.getVersion() == null ? "" : dto.getVersion();
            Long amount = dto.getAmount();
            try {
                honorPointService.distributePointToAuditor(Constant.AUDITOR_ACTION, userIds, dataId, version, amount);
                asyncTx.setState(Constant.SUCCESS);
            } catch (Exception e) {
                asyncTx.setState(Constant.FAIL);
            }
            asyncTxMapper.updateByPrimaryKeySelective(asyncTx);
        }
    }

}
