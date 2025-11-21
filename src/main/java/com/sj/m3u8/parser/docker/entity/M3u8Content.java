package com.sj.m3u8.parser.docker.entity;

import java.util.List;

import cn.hutool.crypto.symmetric.AES;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class M3u8Content {
	private String url;
	private boolean hasKey;
	private AES aes;
	private List<M3u8TS> tsList;
}
