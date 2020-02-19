package com.ontology.service;


import com.ontology.controller.vo.DataIdDto;
import com.ontology.controller.vo.DataIdResp;

import java.util.List;

public interface OntIdService {

    String registerUserOntId(String action, String userId) throws Exception;

    String registerDataOntId(String action, String userId, String dataId, String version) throws Exception;

    List<DataIdResp> registerMultiDataOntId(String action, List<DataIdDto> list) throws Exception;

    String updateDataOntId(String action, String userId, String dataId, String newVersion, String oldVersion) throws Exception;

    List<DataIdResp> registerMultiDataOntIdWithGroupController(String action, List<String> userIdList, List<String> dataIdList, String version) throws Exception;

}
