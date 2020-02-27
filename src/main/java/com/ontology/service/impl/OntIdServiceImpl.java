package com.ontology.service.impl;

import com.alibaba.fastjson.JSON;
import com.ontology.controller.vo.DataIdDto;
import com.ontology.controller.vo.DataIdResp;
import com.ontology.entity.DataAuth;
import com.ontology.exception.OntIdException;
import com.ontology.mapper.DataAuthMapper;
import com.ontology.service.OntIdService;
import com.ontology.thread.TxThread;
import com.ontology.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class OntIdServiceImpl implements OntIdService {
    @Autowired
    private SDKUtil sdkUtil;
    @Autowired
    private TxThread txThread;
    @Autowired
    private DataAuthMapper dataAuthMapper;

    private RootKey rootKey = RootKeyUtil.rootKey;


    @Override
    public String registerUserOntId(String action, String userId) throws Exception {
        String[] ids = new String[]{userId};
        byte[] key = rootKey.generateKeys(RootKeyUtil.userKey, ids)[0];
        return sdkUtil.registerOntId(key);
    }

    @Override
    public String registerDataOntId(String action, String userId, String dataId, String version) throws Exception {
        String[] userIds = new String[]{userId};
        byte[] userPk = rootKey.generateKeys(RootKeyUtil.userKey, userIds)[0];
        String userOntId = sdkUtil.getOntId(userPk);

        String[] dataIds = new String[]{dataId + version};
        byte[] dataPk = rootKey.generateKeys(RootKeyUtil.dataKey, dataIds)[0];
        String dataOntId = sdkUtil.getOntId(dataPk);

        List userPks = new ArrayList();
        userPks.add(userPk);

        List args1 = new ArrayList();
        List args2 = new ArrayList();
        args2.add(dataOntId);
        args2.add(userOntId.getBytes());
        args2.add(1);

        args1.add(args2);

        String hash = sdkUtil.regIdsWithController(args1, userPks);
        return dataOntId;
    }

    @Override
    public List<DataIdResp> registerMultiDataOntId(String action, List<DataIdDto> list) throws Exception {
        List<DataIdResp> resps = new ArrayList<>();
        Map<String, List<String>> userMap = new HashMap<>();
        for (DataIdDto dto : list) {
            String userId = dto.getUserId();

            String dataId = dto.getDataId();
            String newVersion = dto.getNewVersion() == null ? "" : dto.getNewVersion();
            String[] dataIds = new String[]{dataId + newVersion};
            byte[] dataPk = rootKey.generateKeys(RootKeyUtil.dataKey, dataIds)[0];
            String dataOntId = sdkUtil.getOntId(dataPk);

            if (!userMap.containsKey(userId)) {
                List<String> dataOntIds = new ArrayList<>();
                dataOntIds.add(dataOntId);

                userMap.put(userId, dataOntIds);
            } else {
                List<String> dataOntIds = userMap.get(userId);
                dataOntIds.add(dataOntId);
            }
            DataIdResp resp = new DataIdResp();
            resp.setDataId(dataId);
            resp.setUserId(userId);
            resp.setVersion(newVersion);
            resp.setDataOntId(dataOntId);
            resps.add(resp);
        }

        for (Map.Entry<String, List<String>> entry : userMap.entrySet()) {
            String userId = entry.getKey();
            List<String> dataOntIds = entry.getValue();
            String hash = sdkUtil.regIdsWithOneController(dataOntIds, userId);
        }
        return resps;
    }

    @Override
    public String updateDataOntId(String action, String userId, String dataId, String newVersion, String oldVersion) throws Exception {
        String[] userIds = new String[]{userId};
        byte[] userPk = rootKey.generateKeys(RootKeyUtil.userKey, userIds)[0];
        String userOntId = sdkUtil.getOntId(userPk);

        String[] oldDataIds = new String[]{dataId + oldVersion};
        byte[] oldDataPk = rootKey.generateKeys(RootKeyUtil.dataKey, oldDataIds)[0];
        String oldDataOntId = sdkUtil.getOntId(oldDataPk);

        List<String> controllers = sdkUtil.getDataIdController(oldDataOntId);

        if (CollectionUtils.isEmpty(controllers)) {
            throw new OntIdException(action, ErrorInfo.DATAID_OR_VERSION_ERROR.descCN(), ErrorInfo.DATAID_OR_VERSION_ERROR.descEN(), ErrorInfo.DATAID_OR_VERSION_ERROR.code());
        }
        if (!controllers.contains(userOntId)) {
            controllers.add(userOntId);
        }
        String[] newDataIds = new String[]{dataId + newVersion};
        byte[] newDataPk = rootKey.generateKeys(RootKeyUtil.dataKey, newDataIds)[0];
        String newDataOntId = sdkUtil.getOntId(newDataPk);

        List<String> dataOntIdList = new ArrayList<>();
        dataOntIdList.add(newDataOntId);
        String hash = sdkUtil.regIdWithGroup(dataOntIdList, controllers, userOntId, userPk);
        return newDataOntId;
    }

    @Override
    public List<DataIdResp> registerMultiDataOntIdWithGroupController(String action, List<String> userIdList, List<String> dataIdList, String version, Boolean isDataset) throws Exception {
        Boolean registerDataset = false;
        List<String> controllers = new ArrayList<>();
        for (String userId : userIdList) {
            String[] userIds = new String[]{userId};
            byte[] userPk = rootKey.generateKeys(RootKeyUtil.userKey, userIds)[0];
            String userOntId = sdkUtil.getOntId(userPk);
            controllers.add(userOntId);
        }

        String[] userIds = new String[]{userIdList.get(0)};
        byte[] userPk = rootKey.generateKeys(RootKeyUtil.userKey, userIds)[0];

        List<DataIdResp> resps = new ArrayList<>();
        List<String> dataOntIdList = new ArrayList<>();
        for (String dataId : dataIdList) {
            String[] dataIds = new String[]{dataId + version};
            byte[] dataPk = rootKey.generateKeys(RootKeyUtil.dataKey, dataIds)[0];
            String dataOntId = sdkUtil.getOntId(dataPk);
            dataOntIdList.add(dataOntId);

            DataIdResp resp = new DataIdResp();
            resp.setDataId(dataId);
            resp.setUserId(JSON.toJSONString(userIdList));
            resp.setVersion(version);
            resp.setDataOntId(dataOntId);
            resps.add(resp);

            if (isDataset) {
                DataAuth record = dataAuthMapper.selectByPrimaryKey(dataId);
                if (record == null) {
                    DataAuth dataAuth = new DataAuth();
                    dataAuth.setDataId(dataId);
                    dataAuth.setVersion(version);
                    dataAuth.setDataOntId(dataOntId);
                    dataAuth.setState(1);
                    dataAuthMapper.insertSelective(dataAuth);
                    registerDataset = true;
                } else {
                    String oldVersion = record.getVersion();
                    record.setVersion(version);
                    record.setDataOntId(dataOntId);
                    record.setState(1);
                    dataAuthMapper.updateByPrimaryKeySelective(record);
                    if (!version.equals(oldVersion)) {
                        registerDataset = true;
                    }
                }

            }
        }
        if (isDataset) {
            // register dataset and auth download
            if (registerDataset) {
                String hash = sdkUtil.regIdAndAuth(dataOntIdList, controllers, controllers.get(0), userPk);
                txThread.saveAuthId(hash);
            }
        } else {
            // only register data
            String hash = sdkUtil.regIdWithGroup(dataOntIdList, controllers, controllers.get(0), userPk);
        }
        return resps;
    }
}
