package edu.tamu.iiif.resolver;

import java.lang.annotation.Annotation;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

import edu.tamu.iiif.annotation.Context;

public class ContextArgumentResolver implements HandlerMethodArgumentResolver {

    private AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mvContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        return pathMatcher.extractPathWithinPattern(bestMatchPattern, path);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return findContextParameterAnnotation(parameter).isPresent();
    }

    private Optional<Context> findContextParameterAnnotation(MethodParameter parameter) {
        return Optional.ofNullable(parameter.getParameterAnnotation(Context.class));
    }

}
