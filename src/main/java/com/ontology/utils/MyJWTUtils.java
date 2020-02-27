package com.ontology.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.ontology.exception.OntIdException;
import com.ontology.utils.myjwt.MyJwt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class MyJWTUtils {

    @Autowired
    ConfigParam configParam;


    @Autowired
    private SDKUtil sdkUtil;

    /**
     * 校验token是否正确
     *
     * @param token Token
     * @return boolean 是否正确
     */

    private void verify(String token) {
        verifyWithPublicKey(token, configParam.JWT_ISSUER_PUBLICKEY);
    }

    public void verifyWithPublicKey(String token, String publicKey) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            String content = String.format("%s.%s", jwt.getHeader(), jwt.getPayload());
            String signature = Base64ConvertUtil.decode(jwt.getSignature());
            Account account = new Account(false, Helper.hexToBytes(publicKey));
            boolean flag = account.verifySignature(content.getBytes(), Helper.hexToBytes(signature));
            if (!flag) {
                throw new OntIdException("Token verify", ErrorInfo.VERIFY_TOKEN_FAILED.descCN(), ErrorInfo.VERIFY_TOKEN_FAILED.descEN(), ErrorInfo.VERIFY_TOKEN_FAILED.code());
            }

            if (jwt.getExpiresAt().before(new Date())) {
                throw new OntIdException("Token verify", ErrorInfo.TOKEN_EXPIRED.descCN(), ErrorInfo.TOKEN_EXPIRED.descEN(), ErrorInfo.TOKEN_EXPIRED.code());
            }
        } catch (JWTDecodeException e) {
            e.printStackTrace();
            throw new OntIdException("解密Token中的公共信息出现JWTDecodeException异常");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("verifyWithPublicKey...", e);
            throw new OntIdException("Token verify", ErrorInfo.VERIFY_TOKEN_FAILED.descCN(), ErrorInfo.VERIFY_TOKEN_FAILED.descEN(), ErrorInfo.VERIFY_TOKEN_FAILED.code());
        }
    }

    public void verifyAccessToken(String token) {
        if (!getContentType(token).equals(Constant.ACCESS_TOKEN)) {
            throw new OntIdException("verify token", ErrorInfo.TOKEN_TYPE_ERROR.descCN(), ErrorInfo.TOKEN_TYPE_ERROR.descEN(), ErrorInfo.TOKEN_TYPE_ERROR.code());
        }
        verify(token);
    }

    /**
     * 获得Token中的信息无需secret解密也能获得
     *
     * @param token
     * @param claim
     * @return java.lang.String
     */
    public String getClaim(String token, String claim) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            // 只能输出String类型，如果是其他类型返回null
            return jwt.getClaim(claim).asString();
        } catch (JWTDecodeException e) {
            e.printStackTrace();
//            throw new CustomException("解密Token中的公共信息出现JWTDecodeException异常");
            throw new OntIdException();
        }
    }

    /**
     * 获得Token中的用户信息
     *
     * @param token
     * @return java.lang.String
     */
    public String getContentUser(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            // 只能输出String类型，如果是其他类型返回null
            return (String) jwt.getClaim("content").asMap().get("ontid");
        } catch (JWTDecodeException e) {
            e.printStackTrace();
            throw new OntIdException("解密Token中的公共信息出现JWTDecodeException异常");
        }
    }

    public String getContentDataId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            // 只能输出String类型，如果是其他类型返回null
            return (String) jwt.getClaim("content").asMap().get("dataId");
        } catch (JWTDecodeException e) {
            e.printStackTrace();
            throw new OntIdException("解密Token中的公共信息出现JWTDecodeException异常");
        }
    }

    /**
     * 获得Token中的datad
     *
     * @param token
     * @return java.lang.String
     */
    public String getDataId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            // 只能输出String类型，如果是其他类型返回null
            return (String) jwt.getClaim("content").asMap().get("dataId");
        } catch (JWTDecodeException e) {
            e.printStackTrace();
            throw new OntIdException("解密Token中的公共信息出现JWTDecodeException异常");
        }
    }

    /**
     * 获得Token类型
     *
     * @param token
     * @return java.lang.String
     */
    public String getContentType(String token) {
        try {
            if (token == null) {
                throw new OntIdException("token为空");
            }
            DecodedJWT jwt = JWT.decode(token);
            // 只能输出String类型，如果是其他类型返回null
            return (String) jwt.getClaim("content").asMap().get("type");
        } catch (JWTDecodeException e) {
            e.printStackTrace();
            throw new OntIdException("解密Token中的公共信息出现JWTDecodeException异常");
        }
    }

    /**
     * 获得Aud
     *
     * @param token
     * @return java.lang.String
     */
    public String getAud(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            // 只能输出String类型，如果是其他类型返回null
            return jwt.getClaim("aud").asString();
        } catch (JWTDecodeException e) {
            e.printStackTrace();
            throw new OntIdException("解密Token中的公共信息出现JWTDecodeException异常");
        }
    }

    /**
     * 生成签名
     *
     * @return java.lang.String 返回加密的Token
     */

    public String signAccess(String aud, String dataId) {
        HashMap<String, String> contentData = new HashMap<>();
        contentData.put("type", Constant.ACCESS_TOKEN);
        contentData.put("dataId", dataId);

        return MyJwt.create().withIssuer(configParam.JWT_ISSUER_ONTID).withExpiresAt(new Date(new Date().getTime() + Constant.ACCESS_TOKEN_EXPIRE)).withAudience(aud).withIssuedAt(new Date()).
                withJWTId(UUID.randomUUID().toString().replace("-", "")).withClaim("content", contentData).sign(Helper.toHexString(Account.getPrivateKeyFromWIF(configParam.JWT_ISSUER_WIF)));
    }


    /**
     * 获取三方相关信息
     */
    public Map<String, Object> getContentApp(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            // 只能输出String类型，如果是其他类型返回null
            return jwt.getClaim("app").asMap();
        } catch (JWTDecodeException e) {
            e.printStackTrace();
            throw new OntIdException("解密Token中的公共信息出现JWTDecodeException异常");
        }
    }

    /**
     * 获取交易内容
     */
    public String getInvokeContent(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            // 只能输出String类型，如果是其他类型返回null
            Map<String, Object> invokeConfig = jwt.getClaim("invokeConfig").asMap();
            HashMap<String, Object> params = new HashMap<>();
            params.put("invokeConfig", invokeConfig);
            JSONObject result = new JSONObject();
            result.put("action", "invoke");
            result.put("params", params);
            return JSON.toJSONString(result);
        } catch (JWTDecodeException e) {
            e.printStackTrace();
            throw new OntIdException("解密Token中的公共信息出现JWTDecodeException异常");
        }
    }

    /**
     * 获取Payload
     */
    public String getPayload(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            // 只能输出String类型，如果是其他类型返回null
            return jwt.getPayload();
        } catch (JWTDecodeException e) {
            e.printStackTrace();
            throw new OntIdException("解密Token中的公共信息出现JWTDecodeException异常");
        }
    }
}
