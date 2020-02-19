package com.ontology.service.impl;

import com.ontology.service.HonorPointService;
import com.ontology.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
public class HonorPointServiceImpl implements HonorPointService {
    @Autowired
    private SDKUtil sdkUtil;

    private RootKey rootKey = RootKeyUtil.rootKey;

    @Override
    public Long queryPoint(String action, String userId) throws Exception {
        String[] ids = new String[]{userId};
        byte[] key = rootKey.generateKeys(RootKeyUtil.userKey, ids)[0];
        String address = sdkUtil.getOntId(key).substring(8);

        // invoke contract to query point
        return sdkUtil.queryHonorPoint(address);
    }

    @Override
    public String distribute(String action, String userId, String dataId, String version, Long amount) throws Exception {
        String[] dataIds = new String[]{dataId + version};
        byte[] dataPk = rootKey.generateKeys(RootKeyUtil.dataKey, dataIds)[0];
        String dataOntId = sdkUtil.getOntId(dataPk);
        List<String> controllers = sdkUtil.getDataIdController(dataOntId);

        if (StringUtils.isEmpty(dataOntId)) {
            throw new Exception();
        }
        for (String controller : controllers) {
            log.info("controller:{}",controller);
            // invoke contract to distribute point
            sdkUtil.distributeHonorPoint(controller.substring(8), amount);
        }
        return null;
    }
}
