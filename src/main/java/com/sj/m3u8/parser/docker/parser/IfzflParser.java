package com.sj.m3u8.parser.docker.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sj.m3u8.parser.docker.dto.ResDTO;
import com.sj.m3u8.parser.docker.entity.Result;
import com.sj.m3u8.parser.docker.util.M3u8Util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;

public class IfzflParser {

	private static String baseUri = "https://www.ifzfl.cn/webPlayers/ply";

	public static List<Result> parser(String url) {
		List<Result> uriList = new ArrayList<Result>();
		Element ul = null;
		StringBuffer path = new StringBuffer();
		try {
			String content = HttpUtil.get(url);
			Document doc = Jsoup.parse(content);
			Element titleElement = doc.getElementsByClass("text-overflow").get(0);
			path.append(HtmlUtil.cleanHtmlTag(HtmlUtil.removeHtmlTag(titleElement.toString(), "em")));
			Elements playlists = doc.getElementsByClass("playlist");
			if (playlists.size() == 0) {
				System.out.println("播放列表读取失败");
			}
			Element playlist = playlists.get(0);
			ul = playlist.getElementsByTag("ul").get(0);
		} catch (Exception e) {
			System.out.println("播放列表解析失败");
		}
		ul.getElementsByTag("li").forEach(li -> {
			try {
				String href = li.getElementsByTag("a").get(0).attr("href");
				String name = li.getElementsByTag("a").get(0).text();
				href = href.replaceFirst("/vod", StrUtil.EMPTY).replaceFirst(".html", StrUtil.EMPTY);
				String m3u8Get = baseUri.concat(href);
				uriList.add(new Result(path.toString(), name, m3u8Get));
			} catch (Exception e) {
				System.out.println("列表解析异常");
			}
		});
		return uriList;
	}

	public static void collectM3u8(List<Result> uriList) {
		CountDownLatch count = new CountDownLatch(uriList.size());
		for (Result result : uriList) {
			Thread.startVirtualThread(() -> {
				try {
					String data = M3u8Util.getContent(result.getUri(), 80);
					int si = data.indexOf("[");
					int ei = data.lastIndexOf("]");
					String json = data.substring(si, ei + 1);
					json = json.replaceAll("\\\\u", "replaceCode").replaceAll("\\\\", "").replaceAll("replaceCode",
							"\\\\u");
					JSONArray pls = JSONUtil.parseArray(json);
					String m3u8 = JSONUtil.parse(pls.get(0)).getByPath("url").toString();
					result.setUri(m3u8);
					result.setCode(0);
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

	public static List<Result> removeRepeat(List<Result> uriList) {
		List<Result> results = new ArrayList<Result>();
		List<String> uris = new ArrayList<String>();
		Set<String> repeats = new HashSet<String>();
		for (Result res : uriList) {
			if (!uris.contains(res.getUri())) {
				uris.add(res.getUri());
			} else {
				repeats.add(res.getUri());
			}
		}
		for (Result res : uriList) {
			if (!repeats.contains(res.getUri())) {
				results.add(res);
			}
		}
		return results;
	}

	public static ResDTO<?> doParse(String url) {
		List<Result> uriList = parser(url);
		if (uriList.size() > 0) {
			collectM3u8(uriList);
			uriList = removeRepeat(uriList);
			return ResDTO.success(uriList);
		} else {
			return ResDTO.failure("列表解析异常", null);
		}
	}

	public static Integer getContentNumber(String url) {
		try {
			String content = HttpUtil.get(url);
			Document doc = Jsoup.parse(content);
			Elements playlists = doc.getElementsByClass("playlist");
			if (playlists.size() == 0) {
				return 0;
			}
			Element playlist = playlists.get(0);
			Element ul = playlist.getElementsByTag("ul").get(0);
			return ul.getElementsByTag("li").size();
		} catch (Exception e) {
			return 0;
		}
	}

}
