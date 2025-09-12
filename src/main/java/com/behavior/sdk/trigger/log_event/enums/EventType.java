package com.behavior.sdk.trigger.log_event.enums;

import com.behavior.sdk.trigger.common.exception.ErrorSpec;
import com.behavior.sdk.trigger.common.exception.FieldErrorDetail;
import com.behavior.sdk.trigger.common.exception.ServiceException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;

public enum EventType {
   @JsonProperty("page_view")
   PAGE_VIEW,

   @JsonProperty("click")
   CLICK,

   @JsonProperty("stay_time")
   STAY_TIME,

   @JsonProperty("scroll_depth")
   SCROLL_DEPTH;


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
      throw new ServiceException(
              ErrorSpec.VALID_PARAM_VALIDATION_FAILED,
              "eventType 값을 확인해주세요.",
              List.of(new FieldErrorDetail("eventType", "unknown enum value", value))
      );
   }
}
