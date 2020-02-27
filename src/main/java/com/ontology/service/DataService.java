package com.ontology.service;


import com.ontology.controller.vo.DataIdResp;

import java.util.List;

public interface DataService {

    String download(String action, String userId, String dataId, Long amount) throws Exception;

}
