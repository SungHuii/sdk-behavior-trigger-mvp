package com.behavior.sdk.trigger.guest.service;

import com.behavior.sdk.trigger.guest.dto.GuestDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class GuestService {

   private final RedisTemplate<String, GuestDto> redis;
   private static final Duration TTL = Duration.ofMinutes(30);

   public GuestService(@Qualifier("guestRedisTemplate") RedisTemplate<String, GuestDto> redis) {
      this.redis = redis;
   }

   public GuestDto create(String name, String email) {
      GuestDto dto = new GuestDto();
      dto.setGuestId(UUID.randomUUID());
      dto.setName(name);
      dto.setEmail(email);
      dto.setCreatedAt(System.currentTimeMillis());
      redis.opsForValue().set("guest:" + dto.getGuestId(), dto, TTL);
      return dto;
   }

   public GuestDto get(UUID id) {
      return redis.opsForValue().get("guest:" + id);
   }

   public void delete(UUID id) {
      redis.delete("guest:" + id);
   }

}
