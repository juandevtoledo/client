package com.lulobank.clients.starter.outboundadapter.redis;

import com.lulobank.clients.services.ports.repository.TimestampRepository;
import com.lulobank.redis.service.GenericsMapper;
import com.lulobank.redis.service.LuloRedis;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisTimestampConfig {


    @Value("${cloud.aws.redis.host}")
    private String host;
    @Value("${cloud.aws.redis.port}")
    private int port;

    @Bean
    public TimestampRepository getTimestampRepository(){
        return new RedisTimestampAdapter(getLuloRedis());
    }


    private LuloRedis getLuloRedis(){
        return new LuloRedis(redisTemplate(connectionFactory()),new GenericsMapper());
    }

    @Bean
    public RedisConnectionFactory connectionFactory() {
        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration()
                .clusterNode(host, port);

        return new LettuceConnectionFactory(clusterConfiguration);
    }

    @Bean
    public StringRedisTemplate redisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

}
