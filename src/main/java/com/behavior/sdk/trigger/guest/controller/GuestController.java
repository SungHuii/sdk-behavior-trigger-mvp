package com.behavior.sdk.trigger.guest.controller;

import com.behavior.sdk.trigger.guest.dto.GuestDto;
import com.behavior.sdk.trigger.guest.service.GuestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/guests")
public class GuestController {

   private final GuestService guestService;
   public GuestController(GuestService guestService) {
      this.guestService = guestService;
   }

   @PostMapping
   public ResponseEntity<GuestDto> create(@RequestParam String name, @RequestParam String email) {
      return ResponseEntity.status(201).body(guestService.create(name, email));
   }

   @GetMapping("/{guestId}")
   public ResponseEntity<GuestDto> get(@PathVariable UUID guestId) {
      GuestDto dto = guestService.get(guestId);
      return dto != null ? ResponseEntity.ok(dto)
            : ResponseEntity.notFound().build();
   }

   @DeleteMapping("/{guestId}")
   public ResponseEntity<Void> delete(@PathVariable UUID guestId) {
      guestService.delete(guestId);
      return ResponseEntity.noContent().build();
   }
}
