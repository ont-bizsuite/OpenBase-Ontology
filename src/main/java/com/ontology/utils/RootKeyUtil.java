package com.ontology.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class RootKeyUtil {

    @Autowired
    private ConfigParam configParam;
    @Autowired
    private SDKUtil sdkUtil;

    public static RootKey rootKey = new RootKey();

    public static byte[] userKey;
    public static byte[] dataKey;

    @PostConstruct
    public void init() {
        rootKey.setChainCode(sdkUtil.getKey(configParam.SUPERKEY_CHAINCODE));
        userKey = sdkUtil.getKey(configParam.USER_KEY);
        dataKey = sdkUtil.getKey(configParam.DATA_KEY);
    }

}
