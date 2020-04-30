package com.ontology.thread;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.common.Helper;
import com.ontology.mapper.DataAuthMapper;
import com.ontology.utils.ConfigParam;
import com.ontology.utils.SDKUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@EnableAutoConfiguration
public class TxThread {

    @Autowired
    private SDKUtil sdkUtil;
    @Autowired
    private ConfigParam configParam;
    @Autowired
    private DataAuthMapper dataAuthMapper;

    @Async("synTaskExecutor")
    public void useTokenAndDistributePoint(String hash, byte[] userPk, String dataOntId, Long amount) throws Exception {
        // get token id
        Long tokenId = null;
        while (true) {
            Object event = sdkUtil.checkEvent(hash);
            if (StringUtils.isEmpty(event)) {
                Thread.sleep(6000);
            } else {
                JSONObject jsonObject = (JSONObject) event;
                JSONArray notifys = jsonObject.getJSONArray("Notify");
                for (int k = 0; k < notifys.size(); k++) {
                    JSONObject notify = notifys.getJSONObject(k);
                    if (configParam.DATA_TOKEN_CONTRACT.equals(notify.getString("ContractAddress"))) {
                        Object statesObj = notify.get("States");

                        JSONArray states = (JSONArray) statesObj;
                        tokenId = (Long.parseLong(Helper.reverse(states.getString(6)), 16));
                        break;
                    }
                }
                break;
            }
        }

        // consume token
        String consumeTokenHash = sdkUtil.consumeToken(tokenId, userPk);


        // distribute honor point
        List<String> controllers = sdkUtil.getDataIdController(dataOntId);

        if (StringUtils.isEmpty(dataOntId)) {
            throw new Exception();
        }
        for (String controller : controllers) {
            log.info("controller:{}", controller);
            // invoke contract to distribute point
            sdkUtil.distributeHonorPoint(controller.substring(8), amount);
        }

    }

    @Async("synTaskExecutor")
    public void saveAuthId(String hash) throws Exception {
        // get auth id
        while (true) {
            Object event = sdkUtil.checkEvent(hash);
            if (StringUtils.isEmpty(event)) {
                Thread.sleep(6000);
            } else {
                JSONObject jsonObject = (JSONObject) event;
                JSONArray notifys = jsonObject.getJSONArray("Notify");
                for (int k = 0; k < notifys.size(); k++) {
                    JSONObject notify = notifys.getJSONObject(k);
                    if (configParam.MP_CONTRACT.equals(notify.getString("ContractAddress"))) {
                        Object statesObj = notify.get("States");

                        JSONArray states = (JSONArray) statesObj;
                        String authId = states.getString(2);
                        String dataOntId = new String(Helper.hexToBytes(states.getString(6)));
                        dataAuthMapper.insertAuthId(authId, dataOntId);
                    }
                }
                break;
            }
        }
    }

    @Async("synTaskExecutor")
    public void distributeByDataIdTimes(List<String> controllers, Integer times, Long amount) throws Exception {
        for (String controller : controllers) {
            // invoke contract to distribute point
            sdkUtil.distributeHonorPoint(controller.substring(8), amount * times);
        }
    }

//    @Async("synTaskExecutor")
    public void distributePointToUsers(List<String> addressList, Long amount) throws Exception {
        for (String address : addressList) {
            // invoke contract to distribute point
            sdkUtil.distributeHonorPoint(address, amount);
        }
    }
}
