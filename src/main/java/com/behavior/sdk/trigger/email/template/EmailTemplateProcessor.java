package com.behavior.sdk.trigger.email.template;

import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class EmailTemplateProcessor {

    /**
     * {{key}} 형태의 변수를 실제 값으로 치환하는 메서드
     * @param template 템플릿 문자열
     * @param variables 치환할 변수 맵
     * @return 치환된 문자열
     */
    public String processTemplate(String template, Map<String, String> variables) {
        if (template == null || variables == null) {
            return template;
        }
        
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            result = result.replace(placeholder, entry.getValue() != null ? entry.getValue() : "");
        }
        return result;
    }
}
