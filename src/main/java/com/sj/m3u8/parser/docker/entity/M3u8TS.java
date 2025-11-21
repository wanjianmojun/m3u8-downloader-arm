package com.sj.m3u8.parser.docker.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class M3u8TS {
	private int index;
	private String url;
	private boolean finished;
	private boolean succeed;

	public M3u8TS(String url, int index) {
		super();
		this.index = index;
		this.url = url;
		this.finished = false;
		this.succeed = false;
	}

}
