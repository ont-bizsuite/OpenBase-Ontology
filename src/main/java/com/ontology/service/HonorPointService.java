package com.ontology.service;


public interface HonorPointService {

    Long queryPoint(String action, String userId) throws Exception;

    String distribute(String action, String userId, String dataId, String version, Long amount) throws Exception;

}
