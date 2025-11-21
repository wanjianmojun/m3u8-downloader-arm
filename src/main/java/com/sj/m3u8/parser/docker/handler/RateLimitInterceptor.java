package com.sj.m3u8.parser.docker.handler;

import org.springframework.web.servlet.HandlerInterceptor;

import com.sj.m3u8.parser.docker.dto.ResDTO;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimitInterceptor implements HandlerInterceptor {
	private final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, Long> requestTime = new ConcurrentHashMap<>();
	private final int maxRequests;
	private final long timeWindow;

	public RateLimitInterceptor(int maxRequests, long timeWindow) {
		this.maxRequests = maxRequests;
		this.timeWindow = timeWindow;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String key = request.getRemoteAddr();
		requestCounts.forEach((k, _) -> {
			if (requestTime.get(key) != null && System.currentTimeMillis() - requestTime.get(key) > timeWindow) {
				requestCounts.remove(k);
				requestTime.remove(k);
			}
		});
		requestCounts.computeIfAbsent(key, _ -> new AtomicInteger()).incrementAndGet();
		if (requestCounts.get(key) != null && requestTime.get(key) == null) {
			requestTime.computeIfAbsent(key, _ -> System.currentTimeMillis());
		}
		if (requestCounts.get(key).get() > maxRequests) {
			String json = JSONUtil.toJsonStr(ResDTO.failure("每分钟只能尝试登录三次", null));
			response.setContentType("application/json;charset=UTF-8");
			try {
				response.getWriter().println(json);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
		return true;
	}
}
