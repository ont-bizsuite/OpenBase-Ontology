package com.ontology.service;


import com.ontology.controller.vo.DataVersionDto;

import java.util.List;

public interface HonorPointService {

    Long queryPoint(String action, String userId) throws Exception;

    void distribute(String action, String userId, String dataId, String version, Long amount) throws Exception;

    void distributeMulti(String action, String userId, List<DataVersionDto> dataIds, Long amount) throws Exception;

    void distributePointToAuditor(String action, List<String> userIds, String dataId, String version, Long amount) throws Exception;
}
