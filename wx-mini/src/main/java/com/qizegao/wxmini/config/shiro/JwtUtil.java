package com.qizegao.wxmini.config.shiro;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/6/18 22:14
 */

//JWT工具类

@Component
@Slf4j
public class JwtUtil {

    //密钥
    @Value("${emos.jwt.secret}")
    private String secret;

    //过期时间
    @Value("${emos.jwt.expire}")
    private int expire;

    //创建令牌，参数是载荷部分，仅用户的id
    public String createToken(int userId){
        Date date= DateUtil.offset(new Date(), DateField.DAY_OF_YEAR,5);
        Algorithm algorithm=Algorithm.HMAC256(secret);
        JWTCreator.Builder builder= JWT.create();
        String token=builder.withClaim("userId",userId).withExpiresAt(date).sign(algorithm);
        return token;
    }

    //获取载荷部分的数据
    public int getUserId(String token){
        DecodedJWT jwt=JWT.decode(token);
        int userId=jwt.getClaim("userId").asInt();
        return userId;
    }

    //验证参数中的令牌
    public void verifierToken(String token){
        Algorithm algorithm=Algorithm.HMAC256(secret);
        JWTVerifier verifier=JWT.require(algorithm).build();
        verifier.verify(token);
    }
}
