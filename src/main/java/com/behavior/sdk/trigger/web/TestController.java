package com.behavior.sdk.trigger.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;

@Controller
public class TestController {

   /*@Value("${app.sdk.projectId}")
   private UUID projectId;

   @GetMapping("/test")
   public String testPage(Model model) {
      model.addAttribute("projectId", projectId);
      return "test"; // resource/templates/test.
   }*/
}
