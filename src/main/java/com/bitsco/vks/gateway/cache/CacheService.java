package com.bitsco.vks.gateway.cache;

import com.bitsco.vks.common.constant.Constant;
import com.bitsco.vks.common.model.Otp;
import com.bitsco.vks.common.response.Token;
import com.bitsco.vks.gateway.model.Role;
import com.bitsco.vks.gateway.model.User;
import com.bitsco.vks.gateway.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: truongnq
 * @date: 06-May-19 4:02 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class CacheService {
    @Autowired
    RedisTemplate<String, Otp> otpRedis;

    @Autowired
    RedisTemplate<String, Token> tokenRedis;

    @Autowired
    RedisTemplate<String, User> userRedis;

    @Autowired
    RedisTemplate<String, Role> roleUriRedis;

    @Autowired
    RedisTemplate<String, UserRole> userRoleRedis;

    public Token getTokenFromCache(String accessToken) {
        final ValueOperations<String, Token> opsForValue = tokenRedis.opsForValue();
        if (tokenRedis.hasKey(accessToken))
            return opsForValue.get(accessToken);
        else return null;
    }

    public User getUserFromCache(String username) {
        final ValueOperations<String, User> opsForValue = userRedis.opsForValue();
        String key = Constant.TABLE.USERS + Constant.DASH + username.trim().toLowerCase();
        if (userRedis.hasKey(key))
            return opsForValue.get(key);
        else return null;
    }

    public Role getRoleFromCache(String uri) {
        final ValueOperations<String, Role> opsForValue = roleUriRedis.opsForValue();
        if (userRedis.hasKey(Constant.TABLE.ROLE + Constant.DASH + uri))
            return opsForValue.get(Constant.TABLE.ROLE + Constant.DASH + uri);
        else return null;
    }

    public UserRole getUserRoleFromCache(long userId, long roleId) {
        final ValueOperations<String, UserRole> opsForValue = userRoleRedis.opsForValue();
        String key = Constant.TABLE.USERS + userId + Constant.DASH + Constant.TABLE.ROLE + roleId;
        if (userRedis.hasKey(key))
            return opsForValue.get(key);
        else return null;
    }
}
