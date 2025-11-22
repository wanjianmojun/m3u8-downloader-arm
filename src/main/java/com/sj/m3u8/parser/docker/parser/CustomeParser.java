package com.sj.m3u8.parser.docker.parser;

import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.sj.m3u8.parser.docker.dto.ResDTO;
import com.sj.m3u8.parser.docker.entity.Result;

import cn.hutool.http.HtmlUtil;
import cn.hutool.http.HttpUtil;

public class CustomeParser {
	public static List<Result> parser(String url) {
		List<Result> results = new ArrayList<Result>();
		try {
			String content = HttpUtil.get(url);
			Document doc = Jsoup.parse(content);
			Element titleElement = doc.getElementsByTag("title").get(0);
			String title = HtmlUtil.cleanHtmlTag(titleElement.html());
			if (title.contains("《") && title.contains("》")) {
				title = title.substring(title.indexOf("《") + 1, title.indexOf("》"));
			}
			System.out.println(title);
			for (Element a : doc.getElementsByTag("a").asList()) {
				String aHtml = HtmlUtil.removeHtmlTag(a.html(), "span").trim();
				if (aHtml.length() >= 3) {
					if (aHtml.substring(0, 3).contains("第")
							&& aHtml.substring(aHtml.length() - 3, aHtml.length()).contains("集")) {
						System.out.println(aHtml + ":" + a.attr("href"));
					}
				}
			}
		} catch (Exception e) {
			System.out.println("列表解析异常");
		}
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

	public static void main(String[] args) {
		String url = "https://www.ifzfl.cn/vod/sixi2025.html";
		parser(url);
	}
}
