package com.ontology.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service("ConfigParam")
public class ConfigParam {

	/**
	 *  SDK param
	 */
	@Value("${service.restfulUrl}")
	public String RESTFUL_URL;

	@Value("${payer.addr}")
	public String PAYER_ADDRESS;


	@Value("${payer.wif}")
	public String PAYER_WIF;

	@Value("${superkey.chaincode}")
	public String SUPERKEY_CHAINCODE;
	@Value("${user.key}")
	public String USER_KEY;
	@Value("${data.key}")
	public String DATA_KEY;

	@Value("${data.contract}")
	public String DATA_CONTRACT;

	@Value("${honor.point.contract}")
	public String HONOR_POINT_CONTRACT;

	@Value("${honor.point.wif}")
	public String HONOR_POINT_WIF;
}