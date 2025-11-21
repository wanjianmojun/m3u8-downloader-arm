package com.sj.m3u8.parser.docker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HtmlContrroller {
	
	@GetMapping("/")
	public String home() {
		return "index";
	}
	
}
