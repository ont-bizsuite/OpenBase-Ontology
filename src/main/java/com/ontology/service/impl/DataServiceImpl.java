package com.ontology.service.impl;

import com.ontology.controller.vo.DataIdResp;
import com.ontology.entity.DataAuth;
import com.ontology.exception.OntIdException;
import com.ontology.mapper.DataAuthMapper;
import com.ontology.service.DataService;
import com.ontology.service.HonorPointService;
import com.ontology.thread.TxThread;
import com.ontology.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class DataServiceImpl implements DataService {
    @Autowired
    private SDKUtil sdkUtil;
    private RootKey rootKey = RootKeyUtil.rootKey;
    @Autowired
    private DataAuthMapper dataAuthMapper;
    @Autowired
    private TxThread txThread;
    @Autowired
    private MyJWTUtils myJWTUtils;

    @Override
    public String download(String action, String userId, String dataId, Long amount) throws Exception {
        // search data auth record
        DataAuth dataAuth = new DataAuth();
        dataAuth.setDataId(dataId);
        dataAuth.setState(1);
        dataAuth = dataAuthMapper.selectOne(dataAuth);
        if (dataAuth == null) {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }
        String version = dataAuth.getVersion();

        String[] userIds = new String[]{userId};
        byte[] userPk = rootKey.generateKeys(RootKeyUtil.userKey, userIds)[0];
        String userOntId = sdkUtil.getOntId(userPk);
        String receiveAddress = userOntId.substring(8);

        String[] dataIds = new String[]{dataId + version};
        byte[] dataPk = rootKey.generateKeys(RootKeyUtil.dataKey, dataIds)[0];
        String dataOntId = sdkUtil.getOntId(dataPk);


        String authId = dataAuth.getAuthId();
        String downloadUrl = dataAuth.getDownloadUrl();

        // take order with 0 pay
        String hash = sdkUtil.takeOrder(authId, receiveAddress, userPk);
        log.info("takeOrder:{}",hash);
        txThread.useTokenAndDistributePoint(hash, userPk, dataOntId,amount);

        // generate jwt
        String token = myJWTUtils.signAccess(userId, dataId);

        return token;
    }
}
