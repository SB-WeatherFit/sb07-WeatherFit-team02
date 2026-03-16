package com.codeit.weatherfit.global.util;

import org.jspecify.annotations.NullMarked;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@NullMarked
public class ContextCopyingTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        Map<String, String> callerMdc = MDC.getCopyOfContextMap();
        Authentication callerAuth = SecurityContextHolder.getContext().getAuthentication();

        return () -> {
            try {
                if (callerMdc != null) {
                    MDC.setContextMap(callerMdc);
                }


                if (callerAuth != null) {
                    SecurityContext newContext = SecurityContextHolder.createEmptyContext();
                    newContext.setAuthentication(callerAuth);
                    SecurityContextHolder.setContext(newContext);
                }

                runnable.run();
            } finally {
                MDC.clear();
                SecurityContextHolder.clearContext();
            }
        };
    }
}
