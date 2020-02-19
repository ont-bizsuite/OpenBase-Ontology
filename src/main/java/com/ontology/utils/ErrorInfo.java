package com.ontology.utils;


public enum ErrorInfo {

    /**
     * success
     */
    SUCCESS(0, "SUCCESS", "成功"),

    /**
     * param error
     */
    PARAM_ERROR(61001, "FAIL, param error.", "参数错误"),

    /**
     * already exist
     */
    USER_ALREADY_EXIST(61002, "FAIL, user already exist.", "用户已存在"),

    OWNER_ALREADY_EXIST(61002, "FAIL, owner already exist.", "owner已存在"),
    /**
     * not found in db
     */
    NOT_FOUND(61003,"FAIL, not found.", "未找到"),

    /**
     * mobile not found in db
     */
    MOBILE_NOT_FOUND(61003, "FAIL, mobile not found.", "手机号未找到"),

    /**
     * not exist
     */
    NOT_EXIST(61004, "FAIL, not exist.", "不存在"),

    /**
     * no permission
     */
    NO_PERMISSION(61005, "FAIL, no permission", "权限错误"),

    /**
     * not register
     */
    NOT_REGISTRY(61006, "FAIL, not registry.", "未注册"),

    /**
     * expires
     */
    EXPIRES(61007, "FAIL, expires.", "已过期"),

    /**
     * revoked
     */
    REVOKED(61008, "FAIL, revoked.", "已注销"),

    /**
     * serialized error
     */
    SERIALIZE_ERROR(61009, "FAIL, serialized error.", "序列化错误"),

    /**
     * serialized error
     */
    TIME_EXCEEDED(61010, "FAIL, time exceeded error.", "次数超限"),

    /**
     * serialized error
     */
    VERIFY_CODE_FREQUENT(61011, "The verification code is too frequent.", "验证码请求过于频繁"),

    /**
     * serialized error
     */
    VERIFY_CODE_ERROR(61012, "The verification code is error.", "验证码错误"),

    /**
     * verification code expires
     */
    VERIFY_CODE_EXPIRES(61013, "FAIL, The verification code expires.", "验证码已过期"),

    /**
     * verify failed
     */
    VERIFY_FAILED(62001, "FAIL, verify fail.", "校验失败"),

    /**
     * verify failed
     */
    VERIFY_PROVIDE_FAILED(62009, "FAIL, verify provide fail.", "三方身份校验失败"),

    /**
     * verify token failed
     */
    VERIFY_TOKEN_FAILED(62002, "FAIL, verify token fail.", "校验token失败"),

    /**
     * token expired
     */
    TOKEN_EXPIRED(62003, "FAIL, token expired.", "token过期"),

    /**
     * token type error
     */
    TOKEN_TYPE_ERROR(62015, "FAIL, token type error.", "token类型错误"),

    /**
     * error occur whern communicate
     */
    COMM_SMS_FAIL(62007, "FAIL, sms communication fail.", "SMS通信异常"),
    /**
     * error net whern communicate
     */
    COMM_NET_FAIL(62008, "FAIL, net communication fail.", "网络通信异常"),

    /**
     * error occur whern operate file
     */
    FILE_ERROR(62004, "FAIL, file operate fail.", "文件操作异常"),

    /**
     * error occur when operate db
     */
    DB_ERROR(62005, "FAIL, db operate fail.", "数据库操作异常"),

    /**
     * verify failed
     */
    SIG_VERIFY_FAILED(62006, "FAIL, verify signature fail.", "验签失败"),


    /**
     * inner error
     */
    INNER_ERROR(63001, "FAIL, inner error.", "内部异常"),

    /**
     * exception
     */
    EXCEPTION(63002, "FAIL, exception.", "异常"),

    /**
     * verify failed
     */
    CODE_VERIFY_FAILED(63003, "FAIL, verify devicecode fail.", "设备码校验失败"),

    /**
     * verify failed
     */
    IDENTITY_VERIFY_FAILED(63004, "FAIL, verify identity fail.", "身份认证失败"),

    /**
     * invalid password
     */
    INVALID_PASSWORD(61024, "the password is error", "密码错误"),

    /**
     * ONT ID locked
     */
    ONTID_LOCKED(61030, "ONT ID has locked，try it after 24 hours", "ONT ID 已经锁住，请24小时后再试"),

    /**
     * decode error
     */
    DECODE_ERROR(61031, "Decode error", "解密失败"),
    /**
     * encode error
     */
    ENCODE_ERROR(61032, "Encode error", "加密失败"),

    /**
     * GeeTest
     */
    GEETEST_ERROR(61033, "GeeTest error", "Gee校验失败"),

    /**
     * transaction param error
     */
    TRANSACTION_PARAM_ERROR(61034, "Transaction param error", "交易参数错误"),
    /**
     * order not exist
     */
    ORDER_NOT_EXIST(61035, "order not exist", "订单不存在"),

    /**
     * order expire
     */
    ORDER_EXPIRED(61036, "order expired", "订单已过期"),

    /**
     * order expire
     */
    ACCOUNT_NOT_EXIST(61037, "account not exist", "钱包不存在"),

    /**
     * error net when query balance
     */
    QUERY_BALANCE_FAIL(61038, "FAIL, query balance fail.", "查询余额失败"),
    /**
     * transaction payer error
     */
    TRANSACTION_PAYER_ERROR(61039, "Transaction payer error", "交易付款人错误"),
    /**
     * Repeated requests
     */
    REPEATED_REQUESTS_ERROR(61040, "Repeated requests", "重复的请求"),
    /**
     * DDO not exist
     */
    DDO_NOT_EXIST(61041, "DDO not exist", "DDO不存在"),
    /**
     * developer not exist
     */
    DEVELOPER_NOT_EXIST(61042, "developer not exist", "developer不存在"),
    /**
     * ONTID not exist
     */
    ONTID_NOT_EXIST(61043, "ONT ID not exist", "ONT ID不存在"),
    /**
     * password not exist
     */
    PASSWORD_NOT_EXIST(61044, "password not exist", "密码不存在"),
    /**
     * public key not exist
     */
    PUBLIC_KEY_NOT_EXIST(61045, "public key not exist", "公钥不存在"),
    /**
     * pre transaction fail
     */
    PRE_TRANSACTION_FAIL(61046, "pre transaction fail", "预执行失败"),
    /**
     * hmac expire
     */
    HMAC_REQUEST_EXPIRED(61047, "hmac request expired", "hmac请求已过期"),
    /**
     * provider not exist
     */
    PROVIDER_NOT_EXIST(61048, "provider not exist", "provider不存在"),
    /**
     * provider sign error
     */
    PROVIDER_SIGN_ERROR(61049, "provider sign error", "provider签名错误"),
    /**
     * dataId or version error
     */
    DATAID_OR_VERSION_ERROR(61050, "dataId or version error", "dataId或版本号错误");

    private int errorCode;
    private String errorDescEN;
    private String errorDescCN;

    ErrorInfo(int errorCode, String errorDescEN, String errorDescCN) {
        this.errorCode = errorCode;
        this.errorDescEN = errorDescEN;
        this.errorDescCN = errorDescCN;
    }

    public int code(){
        return errorCode;
    }

    public String descEN() {
        return errorDescEN;
    }

    public String descCN() {
        return errorDescCN;
    }



}
