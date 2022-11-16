package com.bitsco.vks.gateway.cache;

import com.bitsco.vks.common.model.Otp;
import com.bitsco.vks.common.response.Token;
import com.bitsco.vks.common.util.StringCommon;
import com.bitsco.vks.gateway.model.Role;
import com.bitsco.vks.gateway.model.User;
import com.bitsco.vks.gateway.model.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.database}")
    private int database;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
        redisStandaloneConfiguration.setPassword(StringCommon.isNullOrBlank(password) ? RedisPassword.none() : RedisPassword.of(password));
        redisStandaloneConfiguration.setDatabase(database);
        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfiguration = JedisClientConfiguration.builder();
        jedisClientConfiguration.connectTimeout(Duration.ofSeconds(10));// 10s connection timeout

        JedisConnectionFactory jedisConFactory = new JedisConnectionFactory(redisStandaloneConfiguration,
                jedisClientConfiguration.build());
        return jedisConFactory;
    }

    @Bean
    RedisTemplate<String, Token> tokenRedis() {
        final RedisTemplate<String, Token> tokenRedis = new RedisTemplate<String, Token>();
        tokenRedis.setConnectionFactory(jedisConnectionFactory());
        tokenRedis.setValueSerializer(new Jackson2JsonRedisSerializer<Token>(Token.class));
        return tokenRedis;
    }

    @Bean
    RedisTemplate<String, Otp> otpRedis() {
        final RedisTemplate<String, Otp> otpRedis = new RedisTemplate<String, Otp>();
        otpRedis.setConnectionFactory(jedisConnectionFactory());
        otpRedis.setValueSerializer(new Jackson2JsonRedisSerializer<Otp>(Otp.class));
        return otpRedis;
    }

    @Bean
    RedisTemplate<String, User> userRedis() {
        final RedisTemplate<String, User> userRedis = new RedisTemplate<String, User>();
        userRedis.setConnectionFactory(jedisConnectionFactory());
        userRedis.setValueSerializer(new Jackson2JsonRedisSerializer<User>(User.class));
        return userRedis;
    }

    @Bean
    RedisTemplate<String, Role> roleUriRedis() {
        final RedisTemplate<String, Role> roleUriRedis = new RedisTemplate<String, Role>();
        roleUriRedis.setConnectionFactory(jedisConnectionFactory());
        roleUriRedis.setValueSerializer(new Jackson2JsonRedisSerializer<Role>(Role.class));
        return roleUriRedis;
    }

    @Bean
    RedisTemplate<String, UserRole> userRoleRedis() {
        final RedisTemplate<String, UserRole> userRoleRedis = new RedisTemplate<String, UserRole>();
        userRoleRedis.setConnectionFactory(jedisConnectionFactory());
        userRoleRedis.setValueSerializer(new Jackson2JsonRedisSerializer<UserRole>(UserRole.class));
        return userRoleRedis;
    }
}
