package com.behavior.sdk.trigger.log_event.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EventType {
   @JsonProperty("page_view")
   PAGE_VIEW;

   private final String value;

   EventType(String value) {
      this.value = value;
   }

   EventType() {
      this.value = name().toLowerCase();
   }

   @JsonValue
   public String getValue() {
      return value;
   }

   @JsonCreator
   public static EventType fromValue(String value) {
      for (EventType eventType : EventType.values()) {
         if (eventType.value.equalsIgnoreCase(value)) {
            return eventType;
         }
      }
      throw new IllegalArgumentException("알 수 없는 값: " + value);
   }
}
