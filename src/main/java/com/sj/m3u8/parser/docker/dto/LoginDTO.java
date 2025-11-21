package com.sj.m3u8.parser.docker.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String password;
	private String token;
}
