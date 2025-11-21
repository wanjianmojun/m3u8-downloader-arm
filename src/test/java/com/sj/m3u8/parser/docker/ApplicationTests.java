package com.sj.m3u8.parser.docker;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

//import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;

import com.iheartradio.m3u8.Encoding;
import com.iheartradio.m3u8.Format;
import com.iheartradio.m3u8.ParseException;
import com.iheartradio.m3u8.PlaylistException;
import com.iheartradio.m3u8.PlaylistParser;
import com.iheartradio.m3u8.data.Playlist;
import com.sj.m3u8.parser.docker.dto.ResDTO;
import com.sj.m3u8.parser.docker.entity.M3u8Content;
import com.sj.m3u8.parser.docker.entity.M3u8TS;
import com.sj.m3u8.parser.docker.entity.McdParse;
import com.sj.m3u8.parser.docker.entity.McdTask;
import com.sj.m3u8.parser.docker.service.IMcdConfigService;
import com.sj.m3u8.parser.docker.service.IMcdParseService;
import com.sj.m3u8.parser.docker.service.IMcdTaskService;
import com.sj.m3u8.parser.docker.service.ITaskService;
import com.sj.m3u8.parser.docker.util.M3u8Util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.http.HttpUtil;

@SuppressWarnings("unused")
//@SpringBootTest
class ApplicationTests {

	@Autowired
	private IMcdConfigService mcdConfigService;

	@Autowired
	private IMcdTaskService mcdTaskService;

	@Autowired
	private IMcdParseService mcdParseService;

	@Autowired
	private ITaskService taskService;

//	public static void main(String[] args) throws Exception {
//		String password = RandomUtil.randomString(12);
//		password = "123456";
//		String salt = RandomUtil.randomString(8);
//		String hex = SecureUtil.hmacMd5(password + salt).digestHex(salt);
//		System.out.println(hex);
//		System.out.println(salt);
//	}

//	@Test
	void contextLoads() {
//		List<McdConfig> configs = new ArrayList<McdConfig>();
//		configs.add(new McdConfig(null, "download_count", "3", "同时下载任务数", null));
//		configs.add(new McdConfig(null, "parse_count", "1", "同时解析任务数", null));
//		mcdConfigService.saveBatch(configs);
//		mcdConfigService.list().forEach(System.out::println);

//		mcdTaskService.save(new McdTask(null, "唐朝诡事录之长安17", null, "D:/网盘/唐朝诡事录之长安17.mp4",
//				"https://cdn.yzzy31-play.com/20251114/6444_8a36dfc6/index.m3u8", null, null));
//		mcdTaskService.save(new McdTask(null, "唐朝诡事录之长安18", null, "D:/网盘/唐朝诡事录之长安18.mp4",
//				"https://cdn.yzzy31-play.com/20251114/6445_59139a91/index.m3u8", null, null));

//		McdParse parse = new McdParse(null, "凡人修仙传", "https://www.gangjuw.tv/content/3246.html", null, null, null, null, null);
//		mcdParseService.save(parse);

//		McdParse mcdParse = mcdParseService.list().get(2);
//		mcdParse.getData().forEach(result -> {
//			System.out.print(result);
//			result.setSelected(true);
//		});
//		taskService.addParseDownload(mcdParse);

		McdTask mcdTask = new McdTask(null, "唐朝诡事录之长安17", null, "D:/网盘/唐朝诡事录之长安",
				"https://cdn.yzzy31-play.com/20251114/6444_8a36dfc6/index.m3u8", 0, 0, null, null);
		taskService.downloadOne(mcdTask);
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
