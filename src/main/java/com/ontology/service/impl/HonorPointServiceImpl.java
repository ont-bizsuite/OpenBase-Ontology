package com.ontology.service.impl;

import com.ontology.controller.vo.DataVersionDto;
import com.ontology.service.HonorPointService;
import com.ontology.thread.TxThread;
import com.ontology.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class HonorPointServiceImpl implements HonorPointService {
    @Autowired
    private SDKUtil sdkUtil;

    private RootKey rootKey = RootKeyUtil.rootKey;

    @Autowired
    private TxThread txThread;

    @Override
    public Long queryPoint(String action, String userId) throws Exception {
        String[] ids = new String[]{userId};
        byte[] key = rootKey.generateKeys(RootKeyUtil.userKey, ids)[0];
        String address = sdkUtil.getOntId(key).substring(8);

        // invoke contract to query point
        return sdkUtil.queryHonorPoint(address);
    }

    @Override
    public void distribute(String action, String userId, String dataId, String version, Long amount) throws Exception {
        String[] dataIds = new String[]{dataId + version};
        byte[] dataPk = rootKey.generateKeys(RootKeyUtil.dataKey, dataIds)[0];
        String dataOntId = sdkUtil.getOntId(dataPk);
        List<String> controllers = sdkUtil.getDataIdController(dataOntId);

        for (String controller : controllers) {
            log.info("controller:{}", controller);
            // invoke contract to distribute point
            sdkUtil.distributeHonorPoint(controller.substring(8), amount);
        }
    }

    @Override
    public void distributeMulti(String action, String userId, List<DataVersionDto> dataIds, Long amount) throws Exception {
        Map<String, Integer> dataIdTimes = combineDuplicateDataId(dataIds);

        for (Map.Entry<String, Integer> entry : dataIdTimes.entrySet()) {
            String dataIdAndVersion = entry.getKey();
            Integer times = entry.getValue();
            distributeByDataIdTimes(action, userId, dataIdAndVersion, times, amount);
        }
    }


    @Override
    public void distributePointToAuditor(String action, List<String> userIds, String dataId, String version, Long amount) throws Exception {
        List<String> addressList = new ArrayList<>();

        // get data controller
        String[] dataIds = new String[]{dataId + version};
        byte[] dataPk = rootKey.generateKeys(RootKeyUtil.dataKey, dataIds)[0];
        String dataOntId = sdkUtil.getOntId(dataPk);
        List<String> controllers = sdkUtil.getDataIdController(dataOntId);

        for (String controller : controllers) {
            addressList.add(controller.substring(8));
        }

        // get user ont id
        for (String userId : userIds) {
            String[] userIdArray = new String[]{userId};
            byte[] userPk = rootKey.generateKeys(RootKeyUtil.userKey, userIdArray)[0];
            String userOntId = sdkUtil.getOntId(userPk);

            addressList.add(userOntId.substring(8));
        }

        txThread.distributePointToUsers(addressList, amount);
    }

    private Map<String, Integer> combineDuplicateDataId(List<DataVersionDto> dataIds) {
        Map<String, Integer> dataIdTimes = new HashMap<>();
        for (DataVersionDto dto : dataIds) {
            String version = dto.getVersion() == null ? "" : dto.getVersion();
            String dataId = dto.getDataId();
            String dataIdAndVersion = dataId + version;
            if (dataIdTimes.containsKey(dataIdAndVersion)) {
                Integer integer = dataIdTimes.get(dataIdAndVersion) + 1;
                dataIdTimes.put(dataIdAndVersion, integer);
            } else {
                dataIdTimes.put(dataIdAndVersion, 1);
            }
        }
        return dataIdTimes;
    }

    private void distributeByDataIdTimes(String action, String userId, String dataIdAndVersion, Integer times, Long amount) throws Exception {
        String[] dataIds = new String[]{dataIdAndVersion};
        byte[] dataPk = rootKey.generateKeys(RootKeyUtil.dataKey, dataIds)[0];
        String dataOntId = sdkUtil.getOntId(dataPk);
        List<String> controllers = sdkUtil.getDataIdController(dataOntId);

        txThread.distributeByDataIdTimes(controllers, times, amount);

    }

}
