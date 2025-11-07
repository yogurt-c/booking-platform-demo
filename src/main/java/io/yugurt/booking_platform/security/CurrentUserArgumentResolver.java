package io.yugurt.booking_platform.security;

import io.yugurt.booking_platform.security.annotation.CurrentUser;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Aspect
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isCurrentUserAnnotation = parameter.getParameterAnnotation(CurrentUser.class) != null;

        boolean isUserType = UserContext.class.isAssignableFrom(parameter.getParameterType());

        return isCurrentUserAnnotation && isUserType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        
        return UserContextHolder.getContext();
    }
}
