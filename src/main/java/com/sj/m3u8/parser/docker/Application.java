package com.sj.m3u8.parser.docker;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "com.sj" }, proxyBeanMethods = false)
@MapperScan(basePackages = "com.sj.m3u8.parser.docker.mapper", sqlSessionTemplateRef = "sqlSessionTemplate")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}