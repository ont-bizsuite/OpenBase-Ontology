package com.ontology.utils.myjwt;



public abstract class MyJwt {
    public MyJwt() {
    }

//    public static DecodedJWT decode(String token) throws JWTDecodeException {
//        return new JWTDecoder(token);
//    }
//
//    public static Verification require(Algorithm algorithm) {
//        return JWTVerifier.init(algorithm);
//    }

    public static MyJwtCreator.Builder create() {
        return MyJwtCreator.init();
    }
}
