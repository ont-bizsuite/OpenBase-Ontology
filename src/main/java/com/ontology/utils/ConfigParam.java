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

	@Value("${oj.address}")
	public String OJ_ADDRESS;

	@Value("${mp.contract}")
	public String MP_CONTRACT;

	@Value("${data.token.contract}")
	public String DATA_TOKEN_CONTRACT;

	@Value("${jwt.issuer.ontid}")
	public String JWT_ISSUER_ONTID;
	@Value("${jwt.issuer.publickey}")
	public String JWT_ISSUER_PUBLICKEY;
	@Value("${jwt.issuer.wif}")
	public String JWT_ISSUER_WIF;

}