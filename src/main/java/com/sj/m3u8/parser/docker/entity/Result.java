package com.sj.m3u8.parser.docker.entity;

import java.io.Serializable;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Result implements Serializable {
	private static final long serialVersionUID = 1L;

	private String title;
	private String name;
	private String uri;
	private Boolean selected;
	private int code;

	public Result() {
		super();
		this.code = 1;
		this.selected = false;
	}

	public Result(String title, String name, String uri) {
		super();
		this.title = title;
		this.name = name;
		this.uri = uri;
		this.code = 1;
		this.selected = false;
	}

	public McdTask toTask(String baseDir) {
		this.title = title.replaceAll(" ", "_");
		this.name = name.replaceAll(" ", "_");
		String fileName = title.concat("_").concat(name);
		String path = baseDir.concat("/").concat(title);
		FileUtil.mkdir(path);
//		path = path.concat("/").concat(fileName).concat(".mp4");
		return new McdTask(null, fileName, null, path, uri, 0, 0, null, null);
	}

	public String toFileName() {
		this.title = title.replaceAll(" ", "_");
		this.name = name.replaceAll(" ", "_");
		return title.concat("-").concat(name);
	}

	@Override
	public String toString() {
		if (this.code == 0) {
			return uri.concat(StrUtil.SPACE).concat(title.toString().replaceAll(" ", "_")).concat("-")
					.concat(name.replaceAll(" ", "_")).concat(StrUtil.SPACE)
					.concat(title.toString().replaceAll(" ", "_")).concat("\n");
		} else {
			return StrUtil.EMPTY;
		}
	}
}
