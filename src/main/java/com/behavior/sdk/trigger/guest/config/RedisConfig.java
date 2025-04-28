package com.behavior.sdk.trigger.guest.config;

import com.behavior.sdk.trigger.guest.dto.GuestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

   @Bean
   public RedisTemplate<String, GuestDto> guestRedisTemplate(
         RedisConnectionFactory connectionFactory, ObjectMapper om) {

      RedisTemplate<String, GuestDto> template = new RedisTemplate<>();
      template.setConnectionFactory(connectionFactory);

      template.setKeySerializer(new StringRedisSerializer());

      Jackson2JsonRedisSerializer<GuestDto> valueSerializer =
            new Jackson2JsonRedisSerializer<>(GuestDto.class);

      template.setValueSerializer(valueSerializer);
      template.afterPropertiesSet();
      return template;
   }
}
