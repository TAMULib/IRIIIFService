package edu.tamu.iiif.controller.resolver;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

import edu.tamu.iiif.controller.ManifestRequest;

public class ManifestRequestArgumentResolver implements HandlerMethodArgumentResolver {

    private final static String UPDATE_PARAMATER_NAME = "update";
    private final static String ALLOW_PARAMATER_NAME = "allow";
    private final static String DISALLOW_PARAMATER_NAME = "disallow";

    private final static List<String> PARAMETER_TRUE_VALUES = Arrays.asList("true", "");

    private final static AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mvContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        return ManifestRequest.of(getContext(request), getUpdate(request), getAllowList(request), getDisallowList(request));
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(ManifestRequest.class);
    }

    private String getContext(HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String query = PATH_MATCHER.extractPathWithinPattern(bestMatchPattern, path);
        int index = query.indexOf("?");
        return index >= 0 ? query.substring(0, index) : query;
    }

    private boolean getUpdate(HttpServletRequest request) {
        Optional<String> updateValue = Optional.ofNullable(request.getParameter(UPDATE_PARAMATER_NAME));
        return updateValue.isPresent() && PARAMETER_TRUE_VALUES.contains(updateValue.get());
    }

    private List<String> getAllowList(HttpServletRequest request) {
        return getListParameter(request, ALLOW_PARAMATER_NAME);
    }

    private List<String> getDisallowList(HttpServletRequest request) {
        return getListParameter(request, DISALLOW_PARAMATER_NAME);
    }

    private List<String> getListParameter(HttpServletRequest request, String parameter) {
        Optional<String[]> values = Optional.ofNullable(request.getParameterValues(parameter));
        return Arrays.asList(values.isPresent() ? values.get() : new String[] {});
    }

}
