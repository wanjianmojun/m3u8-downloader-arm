package com.sj.m3u8.parser.docker.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.sj.m3u8.parser.docker.dto.ResDTO;

import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTInterceptor implements HandlerInterceptor {

	@Value("${jwt.secret}")
	private String secret;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String token = request.getHeader("token");
		ResDTO<String> res = ResDTO.success(null);
		try {
			boolean pass = JWTUtil.verify(token, secret.getBytes());
			if (pass) {
				JWT jwt = JWTUtil.parseToken(token);
				long expireTime = Long.valueOf(String.valueOf(jwt.getPayload("expire_time")));
				if (System.currentTimeMillis() > expireTime) {
					res.setCode(1);
					res.setMsg("登录授权过期");
				} else {
					response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
					return true;
				}
			} else {
				res.setCode(1);
				res.setMsg("登录授权检查失败");
			}
		} catch (Exception e) {
			res.setCode(1);
			res.setMsg("登录授权检查错误");
		}
		String json = JSONUtil.toJsonStr(res);
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().println(json);
		return false;
	}
}
