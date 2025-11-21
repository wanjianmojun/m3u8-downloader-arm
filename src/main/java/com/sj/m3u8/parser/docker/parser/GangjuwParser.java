package com.sj.m3u8.parser.docker.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.sj.m3u8.parser.docker.dto.ResDTO;
import com.sj.m3u8.parser.docker.entity.Result;
import com.sj.m3u8.parser.docker.util.M3u8Util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

public class GangjuwParser {

	private static String baseUri = "https://www.gangjuw.tv";

	public static List<Result> parser(String url) {
		List<Result> uriList = new ArrayList<Result>();
		Element ul = null;
		StringBuffer path = new StringBuffer();
		try {
			String content = HttpUtil.get(url);
			Document doc = Jsoup.parse(content);
			String pathStr = doc.getElementsByClass("text-overflow").get(0).html();
			if (pathStr.contains(" ")) {
				pathStr = pathStr.split(" ")[0];
			}
			path.append(pathStr);
			int size = 0;
			for (Element tul : doc.getElementsByTag("ul").asList()) {
				if (tul.attr("id").startsWith("con_playlist_")) {
					int tsize = tul.getElementsByTag("li").size();
					if (tsize > size) {
						ul = tul;
						size = tsize;
					}
				}
			}
		} catch (Exception e) {
			System.out.println("内容解析异常");
		}
		ul.getElementsByTag("li").forEach(li -> {
			try {
				Element a = li.getElementsByTag("a").get(0);
				String href = baseUri.concat(a.attr("href"));
				String title = a.html();
				uriList.add(new Result(path.toString(), title, href));
			} catch (Exception e) {
				System.out.println("列表解析异常");
			}
		});
		return uriList.reversed();
	}

	public static void collectM3u8(List<Result> uriList) {
		CountDownLatch count = new CountDownLatch(uriList.size());
		for (Result result : uriList) {
			Thread.startVirtualThread(() -> {
				try {
					String content = M3u8Util.getContent(result.getUri(), 80);
					if (content != null) {
						String data = Jsoup.parse(content).getElementById("zanpiancms_player").child(0).html();
						if (data.startsWith("var player_aaaa=")) {
							data = data.replaceFirst("var player_aaaa=", StrUtil.EMPTY);
							String m3u8 = JSONUtil.parse(data).getByPath("url", String.class);
							result.setUri(m3u8);
							result.setCode(0);
						}
					} else {
						System.out.println("m3u8解析异常");
						result.setUri(StrUtil.EMPTY);
					}
				} catch (Exception e) {
					System.out.println("m3u8解析异常");
					result.setUri(StrUtil.EMPTY);
				}
				count.countDown();
			});
		}
		try {
			count.await();
		} catch (Exception e) {
		}
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
		try {
			String content = HttpUtil.get(url);
			Document doc = Jsoup.parse(content);
			Element ul = null;
			int size = 0;
			for (Element tul : doc.getElementsByTag("ul").asList()) {
				if (tul.attr("id").startsWith("con_playlist_")) {
					int tsize = tul.getElementsByTag("li").size();
					if (tsize > size) {
						ul = tul;
						size = tsize;
					}
				}
			}
			return ul.getElementsByTag("li").size();
		} catch (Exception e) {
			return 0;
		}
	}

}
