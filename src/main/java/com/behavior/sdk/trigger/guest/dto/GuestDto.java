package com.behavior.sdk.trigger.guest.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class GuestDto {

   private UUID guestId;
   private String name;
   private String email;
   private long createdAt;
}
