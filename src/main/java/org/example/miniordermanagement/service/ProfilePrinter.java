package org.example.miniordermanagement.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import jakarta.annotation.PostConstruct; // or javax.annotation.PostConstruct
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Component
public class ProfilePrinter {

    @Autowired
    private Environment env;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedisConnectionFactory connectionFactory;

    @PostConstruct
    public void printRedisConfig() {
        System.out.println("Redis Host: " + env.getProperty("spring.redis.host"));
        System.out.println("Redis Port: " + env.getProperty("spring.redis.port"));
        System.out.println("Active Profiles: " + String.join(", ", env.getActiveProfiles()));
    }


    @PostConstruct
    public void testRedis() {
        try {
            redisTemplate.opsForValue().set("test-key", "hello");
            System.out.println("Value from Redis: " + redisTemplate.opsForValue().get("test-key"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void inspect() {
        System.out.println("RedisConnectionFactory = " + connectionFactory.getClass());

        if (connectionFactory instanceof LettuceConnectionFactory lettuce) {
            System.out.println("Redis Host = " + lettuce.getHostName());
            System.out.println("Redis Port = " + lettuce.getPort());
        }
    }


}

