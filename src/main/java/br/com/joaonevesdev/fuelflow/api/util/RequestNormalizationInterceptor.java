package br.com.joaonevesdev.fuelflow.api.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class RequestNormalizationInterceptor implements HandlerInterceptor {

    @Autowired
    private StringNormalizer normalizer;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        Map<String, String[]> params = request.getParameterMap();
        Map<String, String[]> normalizedParams = new HashMap<>();

        params.forEach((key, values) -> {
            if (values != null) {
                String[] normalizedValues = Arrays.stream(values)
                        .map(value -> {
                            if (key.toLowerCase().contains("state") || key.toLowerCase().contains("uf")) {
                                return normalizer.normalizeUF(value);
                            }
                            return normalizer.normalize(value);
                        })
                        .toArray(String[]::new);
                normalizedParams.put(key, normalizedValues);
            }
        });

        request.setAttribute("normalizedParams", normalizedParams);

        return true;
    }
}
