package com.sj.m3u8.parser.docker.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.iheartradio.m3u8.Encoding;
import com.iheartradio.m3u8.Format;
import com.iheartradio.m3u8.PlaylistParser;
import com.iheartradio.m3u8.data.Playlist;
import com.sj.m3u8.parser.docker.dto.ResDTO;
import com.sj.m3u8.parser.docker.entity.M3u8Content;
import com.sj.m3u8.parser.docker.entity.M3u8TS;
import com.sj.m3u8.parser.docker.entity.McdTask;
import com.sj.m3u8.parser.docker.service.IMcdTaskService;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.http.HttpDownloader;
import cn.hutool.http.HttpUtil;

public class M3u8Util {
	public static String getContent(String uri, int tryCount) {
		try {
			return HttpUtil.get(uri, 10000);
		} catch (Exception e) {
			if (tryCount <= 0) {
				return null;
			}
			try {
				Thread.sleep((1000 + (int) (Math.random() * 5000)));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return getContent(uri, --tryCount);
		}
	}

	public static void downloadTS(M3u8TS ts, String tsPath, AES aes, int tryCount) {
		try {
			if (tryCount <= 0) {
				System.out.println("废弃分片:" + ts.getUrl());
				ts.setFinished(true);
				ts.setSucceed(false);
				return;
			}
			if (aes == null) {
				HttpUtil.downloadFile(ts.getUrl(), new File(tsPath), 30000);
			} else {
				byte[] entry = HttpDownloader.downloadBytes(ts.getUrl(), 30000);
				byte[] decrypt = aes.decrypt(entry);
				entry = null;
				FileUtil.writeBytes(decrypt, tsPath);
				decrypt = null;
			}
			ts.setFinished(true);
			ts.setSucceed(true);
		} catch (Exception e) {
			try {
				Thread.sleep(1000);
			} catch (Exception e1) {
			}
			System.out.println("重试:" + ts.getUrl());
			downloadTS(ts, tsPath, aes, --tryCount);
		}
	}

	public static void concat(String outputFile, String tsText, String tempPath) {
		try {
			String cmd = String.format("/ffmpeg -f concat -safe 0 -i %s -c copy %s", tsText, outputFile);
			if (System.getProperty("os.name").toLowerCase().contains("win")) {
				cmd = String.format("ffmpeg.exe -f concat -safe 0 -i %s -c copy %s", tsText, outputFile);
			}
			Process process = Runtime.getRuntime().exec(cmd.split(StrUtil.SPACE));
			final InputStream is1 = process.getInputStream();
			final InputStream is2 = process.getErrorStream();
			readInputStream(is1);
			readInputStream(is2);
			process.waitFor();
			process.destroy();
			FileUtil.del(new File(tempPath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void restart() {
		try {
			String cmd = "reboot";
			Process process = Runtime.getRuntime().exec(cmd.split(StrUtil.SPACE));
			final InputStream is1 = process.getInputStream();
			final InputStream is2 = process.getErrorStream();
			readInputStream(is1);
			readInputStream(is2);
			process.waitFor();
			process.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void readInputStream(InputStream inputStream) {
		new Thread(() -> {
			BufferedReader br1 = new BufferedReader(new InputStreamReader(inputStream));
			try {
				String line;
				while ((line = br1.readLine()) != null) {
					if (line != null) {
//						System.out.println(line);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					inputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void startDownloadThread(AtomicInteger maxCount, M3u8TS ts, String tsPath, AES aes,
			CountDownLatch sync, McdTask task, IMcdTaskService mcdTaskService, int maxDownloadCount) {
		Thread.startVirtualThread(() -> {
			while (maxCount.get() >= maxDownloadCount) {
				try {
					Thread.sleep(1000 + (int) (Math.random() * 3000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			maxCount.addAndGet(1);
			downloadTS(ts, tsPath, aes, 30);
			maxCount.addAndGet(-1);
			sync.countDown();
			int finished = task.getTotal() - ((int) sync.getCount());
			if (finished % 10 == 0) {
				task.setFinished(finished);
				mcdTaskService.updateById(task);
			}
		});
	}

	public static ResDTO<?> downloadM3u8(String url, String path, String fileName, McdTask task,
			IMcdTaskService mcdTaskService, int maxDownloadCount) {
		ResDTO<?> res = getContent(url);
		if (res.getCode() != 0) {
			return res;
		}
		M3u8Content m3u8Content = res.toData(M3u8Content.class);
		FileUtil.mkdir(path);
		String tsPath = path.concat("/").concat(fileName).concat("/");
		String filePath = path.concat("/").concat(fileName).concat(".mp4");
		FileUtil.del(tsPath);
		FileUtil.mkdir(tsPath);
		FileUtil.del(filePath);
		AtomicInteger maxCount = new AtomicInteger(0);
		CountDownLatch sync = new CountDownLatch(m3u8Content.getTsList().size());
		task.setTotal(m3u8Content.getTsList().size());
		mcdTaskService.updateById(task);
		m3u8Content.getTsList().forEach(ts -> {
			startDownloadThread(maxCount, ts, tsPath.concat(String.valueOf(ts.getIndex())).concat(".ts"),
					m3u8Content.getAes(), sync, task, mcdTaskService, maxDownloadCount);
		});
		try {
			sync.await();
		} catch (Exception e) {
		}
		StringBuffer tsText = new StringBuffer();
		m3u8Content.getTsList().forEach(ts -> {
			if (ts.isSucceed()) {
				tsText.append("file ".concat(tsPath.concat(String.valueOf(ts.getIndex()).concat(".ts\n"))));
			}
		});
		FileUtil.writeString(tsText.toString(), tsPath.concat("list.txt"), Charset.defaultCharset());
		concat(filePath, tsPath.concat("list.txt"), tsPath);
		return ResDTO.success();
	}

	public static ResDTO<?> downloadM3u8ListBackground(List<McdTask> taskList, IMcdTaskService mcdTaskService,
			int maxDownloadCount) {
		taskList.forEach(task -> {
			Thread.startVirtualThread(() -> {
				task.setStatus("S");
				mcdTaskService.updateById(task);
				downloadM3u8(task.getUrl(), task.getPath(), task.getName(), task, mcdTaskService, maxDownloadCount);
				task.setStatus("E");
				task.setFinished(task.getTotal());
				mcdTaskService.updateById(task);
			});
		});
		return ResDTO.success();
	}

	public static ResDTO<?> getContent(String url) {
		String content = getContent(url, 10);
		if (StrUtil.isBlank(content)) {
			return ResDTO.failure("m3u8获取失败", url);
		}
		PlaylistParser parser = new PlaylistParser(new ByteArrayInputStream(content.getBytes()), Format.EXT_M3U,
				Encoding.UTF_8);
		Playlist playlist = null;
		try {
			playlist = parser.parse();
			if (playlist.getMediaPlaylist().getTracks().size() < 10) {
				throw new Exception("不是正确的m3u8跳转格式");
			}
		} catch (Exception e) {
			String[] lines = content.split("\n");
			String link = lines[lines.length - 1];
			if (link.startsWith("http")) {
				return getContent(link);
			} else {
				String header = StrUtil.EMPTY;
				if (link.startsWith("/")) {
					header = url.substring(0, 8).concat(url.substring(8).split("/")[0]);
				} else {
					String[] splits = url.split("/");
					header = url.replaceFirst(splits[splits.length - 1], StrUtil.EMPTY);
				}
				return getContent(header.concat(link));
			}
		}
		String[] splits = url.split("/");
		String header = url.replaceFirst(splits[splits.length - 1], StrUtil.EMPTY);
		M3u8Content m3u8Content = new M3u8Content();
		m3u8Content.setUrl(url);
		m3u8Content.setHasKey(false);
		m3u8Content.setTsList(new ArrayList<M3u8TS>());
		AtomicInteger count = new AtomicInteger(1);
		playlist.getMediaPlaylist().getTracks().forEach(track -> {
			String uri = track.getUri();
			if (!uri.contains("time")) {
				if (m3u8Content.getAes() == null && track.hasEncryptionData() && track.getEncryptionData().hasUri()) {
					m3u8Content.setHasKey(true);
					byte[] key = HttpUtil.downloadBytes(header.concat(track.getEncryptionData().getUri()));
					List<Byte> ivList = track.getEncryptionData().getInitializationVector();
					byte[] iv = new byte[16];
					if (ivList.size() >= 16) {
						for (int i = 0; i < 16; i++) {
							iv[i] = ivList.get(i);
						}
					}
					AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, key, iv);
					m3u8Content.setAes(aes);
				}
				if (uri.startsWith("http")) {
					m3u8Content.getTsList().add(new M3u8TS(uri, count.getAndAdd(1)));
				} else {
					int hostIndex = header.substring(8).indexOf("/");
					if (hostIndex > 0) {
						String repeat = header.substring(9 + hostIndex);
						uri = uri.replaceAll(repeat, StrUtil.EMPTY);
					}
					m3u8Content.getTsList().add(new M3u8TS(header.concat(uri), count.getAndAdd(1)));
				}
			}
		});
		return ResDTO.success(m3u8Content);
	}
}
