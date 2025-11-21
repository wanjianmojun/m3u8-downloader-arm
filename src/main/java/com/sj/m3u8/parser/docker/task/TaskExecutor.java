package com.sj.m3u8.parser.docker.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sj.m3u8.parser.docker.dto.ResDTO;
import com.sj.m3u8.parser.docker.entity.McdConfig;
import com.sj.m3u8.parser.docker.entity.McdParse;
import com.sj.m3u8.parser.docker.entity.McdSubscribe;
import com.sj.m3u8.parser.docker.entity.McdTask;
import com.sj.m3u8.parser.docker.entity.Result;
import com.sj.m3u8.parser.docker.parser.GangjuwParser;
import com.sj.m3u8.parser.docker.parser.IfzflParser;
import com.sj.m3u8.parser.docker.service.IMcdConfigService;
import com.sj.m3u8.parser.docker.service.IMcdParseService;
import com.sj.m3u8.parser.docker.service.IMcdSubscribeService;
import com.sj.m3u8.parser.docker.service.IMcdTaskService;
import com.sj.m3u8.parser.docker.util.M3u8Util;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

@Component
@EnableScheduling
public class TaskExecutor {

	private AtomicBoolean isExecuting = new AtomicBoolean(false);
	private AtomicBoolean firstRun = new AtomicBoolean(true);

	private String baseDir;

	@Autowired
	private IMcdConfigService mcdConfigService;

	@Autowired
	private IMcdTaskService mcdTaskService;

	@Autowired
	private IMcdParseService mcdParseService;

	@Autowired
	private IMcdSubscribeService mcdSubscribeService;

	@Scheduled(cron = "0/10 * * * * *")
	public void doCollect() {
		if (isExecuting.get()) {
			return;
		}
		isExecuting.set(true);
		try {
			if (firstRun.get()) {
				LambdaQueryWrapper<McdParse> queryParseHis = new LambdaQueryWrapper<McdParse>();
				queryParseHis.select(McdParse::getRowid);
				queryParseHis.eq(McdParse::getStatus, "S");
				List<McdParse> parseList = mcdParseService.list(queryParseHis);
				if (parseList.size() > 0) {
					parseList.forEach(parse -> parse.setStatus("N"));
					mcdParseService.updateBatchById(parseList);
				}
				LambdaQueryWrapper<McdTask> queryHis = new LambdaQueryWrapper<McdTask>();
				queryHis.select(McdTask::getRowid);
				queryHis.eq(McdTask::getStatus, "S");
				List<McdTask> taskList = mcdTaskService.list(queryHis);
				if (taskList.size() >= 0) {
					taskList.forEach(task -> task.setStatus("N"));
					mcdTaskService.updateBatchById(taskList);
				}
				firstRun.set(false);
			}

			List<McdConfig> configs = mcdConfigService.list();
			Integer downloadCount = getConfig("download_count", configs, Integer.class);
			Integer parseCount = getConfig("parse_count", configs, Integer.class);
			if (downloadCount == null) {
				downloadCount = 3;
			}
			if (parseCount == null) {
				parseCount = 1;
			}

			LambdaQueryWrapper<McdParse> queryParseHis = new LambdaQueryWrapper<McdParse>();
			queryParseHis.select(McdParse::getRowid);
			queryParseHis.eq(McdParse::getStatus, "S");
			List<McdParse> parseListing = mcdParseService.list(queryParseHis);
			if (parseListing.size() >= parseCount) {
				isExecuting.set(false);
				return;
			}
			LambdaQueryWrapper<McdTask> queryHis = new LambdaQueryWrapper<McdTask>();
			queryHis.select(McdTask::getRowid);
			queryHis.eq(McdTask::getStatus, "S");
			List<McdTask> taskListing = mcdTaskService.list(queryHis);
			int queryHisCount = taskListing.size();
			if (taskListing.size() >= downloadCount) {
				isExecuting.set(false);
				return;
			}
			if (parseListing.size() == 0 && taskListing.size() == 0) {
				List<McdSubscribe> subscribeList = mcdSubscribeService.list();
				if (subscribeList.size() > 0) {
					executeSubscribe(subscribeList);
				}
			}
			LambdaQueryWrapper<McdParse> queryParse = new LambdaQueryWrapper<McdParse>();
			queryParse.eq(McdParse::getStatus, "N");
			List<McdParse> parseList = mcdParseService.list(queryParse);
			if (parseList.size() > 0) {
				McdParse parse = parseList.get(0);
				parse.setStatus("S");
				mcdParseService.updateById(parse);
				Thread.startVirtualThread(() -> {
					try {
						ResDTO<?> res = null;
						if (parse.getUrl().startsWith("https://www.ifzfl.cn")) {
							res = IfzflParser.doParse(parse.getUrl());
						} else if (parse.getUrl().startsWith("https://www.gangjuw.tv")) {
							res = GangjuwParser.doParse(parse.getUrl());
						}
						if (res.getCode() == 0) {
							List<Result> results = res.toDataList(Result.class);
							parse.setData(results);
							parse.setTotal(results.size());
							parse.setStatus("E");
						} else {
							parse.setStatus("ER");
						}
					} catch (Exception e) {
						parse.setStatus("ER");
					}
					mcdParseService.updateById(parse);
				});
			}
			LambdaQueryWrapper<McdTask> query = new LambdaQueryWrapper<McdTask>();
			query.eq(McdTask::getStatus, "N");
			query.last("LIMIT " + (downloadCount - queryHisCount));
			List<McdTask> taskList = mcdTaskService.list(query);
			if (taskList.size() > 0) {
				McdConfig conf = mcdConfigService
						.getOne(new LambdaQueryWrapper<McdConfig>().eq(McdConfig::getKey, Arrays.asList("ts_count")));
				int tsCount = 30;
				if (conf != null) {
					tsCount = Integer.valueOf(conf.getVal());
				}
				M3u8Util.downloadM3u8ListBackground(taskList, mcdTaskService, tsCount);
			}
		} catch (Exception e) {
		}
		isExecuting.set(false);
	}

	private <T> T getConfig(String key, List<McdConfig> configs, Class<T> clazz) {
		for (McdConfig config : configs) {
			if (config.getKey().equals(key)) {
				String val = config.getVal();
				if (clazz == Integer.class) {
					return clazz.cast(NumberUtil.parseInt(val));
				} else if (clazz == String.class) {
					return clazz.cast(val);
				}
			}
		}
		return null;
	}

	private void executeSubscribe(List<McdSubscribe> subscribeList) {
		for (McdSubscribe mcdSubscribe : subscribeList) {
			try {
				ResDTO<?> res = null;
				if (mcdSubscribe.getUrl().startsWith("https://www.ifzfl.cn")) {
					int contentNumber = IfzflParser.getContentNumber(mcdSubscribe.getUrl());
					if (mcdSubscribe.getCount() >= contentNumber) {
						break;
					}
					mcdSubscribe.setCount(contentNumber);
					res = IfzflParser.doParse(mcdSubscribe.getUrl());
				} else if (mcdSubscribe.getUrl().startsWith("https://www.gangjuw.tv")) {
					int contentNumber = GangjuwParser.getContentNumber(mcdSubscribe.getUrl());
					if (mcdSubscribe.getCount() >= contentNumber) {
						break;
					}
					mcdSubscribe.setCount(contentNumber);
					res = GangjuwParser.doParse(mcdSubscribe.getUrl());
				} else {
					break;
				}
				if (res.getCode() == 0) {
					List<Result> results = res.toDataList(Result.class);
					List<McdTask> taskList = new ArrayList<McdTask>();
					Set<String> nameSet = new HashSet<String>();
					results.forEach(result -> {
						taskList.add(result.toTask(filterBaseDir().concat(mcdSubscribe.getPath())));
						nameSet.add(result.toFileName());
					});
					LambdaQueryWrapper<McdTask> query = new LambdaQueryWrapper<McdTask>();
					query.select(McdTask::getName);
					query.in(McdTask::getName, nameSet);
					List<McdTask> searchTaskList = mcdTaskService.list(query);
					Set<String> searchNameSet = searchTaskList.stream().map(task -> task.getName())
							.collect(Collectors.toSet());
					List<McdTask> filterTaskList = taskList.stream()
							.filter(task -> !searchNameSet.contains(task.getName())).toList();
					if (filterTaskList.size() > 0) {
						mcdTaskService.saveBatch(filterTaskList);
					}
					mcdSubscribeService.updateById(mcdSubscribe);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String filterBaseDir() {
		if (StrUtil.isBlankIfStr(baseDir)) {
			String osName = System.getProperty("os.name").toLowerCase();
			if (osName.contains("win")) {
				McdConfig conf = mcdConfigService
						.getOne(new LambdaQueryWrapper<McdConfig>().eq(McdConfig::getKey, Arrays.asList("base_dir")));
				if (conf != null) {
					baseDir = conf.getVal();
				} else {
					baseDir = "D:/网盘";
				}
			} else {
				baseDir = "/download";
			}
		}
		return baseDir;
	}
}
