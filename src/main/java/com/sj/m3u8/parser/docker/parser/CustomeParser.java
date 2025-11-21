package com.sj.m3u8.parser.docker.parser;

import java.util.ArrayList;
import java.util.List;
import com.sj.m3u8.parser.docker.dto.ResDTO;
import com.sj.m3u8.parser.docker.entity.Result;

public class CustomeParser {
	public static List<Result> parser(String url) {
		List<Result> results = new ArrayList<Result>();
		
		return results;
	}

	public static void collectM3u8(List<Result> uriList) {
		
	}

	public static ResDTO<?> doParse(String url) {
		List<Result> uriList = parser(url);
		if (uriList.size() > 0) {
			collectM3u8(uriList);
			return ResDTO.success(uriList);
		} else {
			return ResDTO.failure("列表解析异常", null);
		}
	}

	public static Integer getContentNumber(String url) {
		return 0;
	}
}
