package com.sj.m3u8.parser.docker.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sj.m3u8.parser.docker.dto.LoginDTO;
import com.sj.m3u8.parser.docker.dto.ResDTO;
import com.sj.m3u8.parser.docker.entity.McdConfig;
import com.sj.m3u8.parser.docker.entity.McdParse;
import com.sj.m3u8.parser.docker.entity.McdSubscribe;
import com.sj.m3u8.parser.docker.entity.McdTask;
import com.sj.m3u8.parser.docker.enu.Msg;
import com.sj.m3u8.parser.docker.service.IMcdConfigService;
import com.sj.m3u8.parser.docker.service.IMcdParseService;
import com.sj.m3u8.parser.docker.service.IMcdSubscribeService;
import com.sj.m3u8.parser.docker.service.IMcdTaskService;
import com.sj.m3u8.parser.docker.service.ITaskService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.jwt.JWTUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class TaskServiceImpl implements ITaskService {

	private String baseDir;

	@Value("${jwt.secret}")
	private String secret;

	@Autowired
	private IMcdParseService mcdParseService;

	@Autowired
	private IMcdTaskService mcdTaskService;

	@Autowired
	private IMcdSubscribeService mcdSubscribeService;

	@Autowired
	private IMcdConfigService mcdConfigService;

	@Override
	public ResDTO<?> downloadOne(McdTask mcdTask) {
		if (StrUtil.isBlankIfStr(mcdTask.getName())) {
			mcdTask.setName(DateUtil.format(new Date(), "yyyyMMddHHmmssSSS"));
		}
		if (StrUtil.isNotBlank(mcdTask.getPath())) {
			mcdTask.setPath(mcdTask.getPath().trim().replaceAll("\\\\", "/"));
			if (mcdTask.getPath().startsWith("/")) {
				mcdTask.setPath(filterBaseDir().concat(mcdTask.getPath()));
			} else {
				mcdTask.setPath(filterBaseDir().concat("/").concat(mcdTask.getPath()));
			}
		} else {
			mcdTask.setPath(filterBaseDir());
		}
		try {
			mcdTask.setUrl(mcdTask.getUrl().trim().replaceAll("\t", StrUtil.EMPTY));
			mcdTask.setPath(mcdTask.getPath().trim().replaceAll("\t", StrUtil.EMPTY));
			return ResDTO.parse(mcdTaskService.save(mcdTask));
		} catch (Exception e) {
			return ResDTO.failure();
		}
	}

	@Override
	public ResDTO<?> parse(McdParse mcdParse) {
		if (StrUtil.isBlankIfStr(mcdParse.getName())) {
			mcdParse.setName(DateUtil.format(new Date(), "yyyyMMddHHmmssSSS"));
		}
		String url = mcdParse.getUrl();
		if (!url.startsWith("https://www.ifzfl.cn") && !url.startsWith("https://www.gangjuw.tv")) {
			return ResDTO.failure("该网站暂不支持提取,请联系作者添加支持", null);
		}
		try {
			return ResDTO.parse(mcdParseService.save(mcdParse));
		} catch (Exception e) {
			return ResDTO.failure();
		}
	}

	@Override
	public ResDTO<?> addParseDownload(McdParse mcdParse) {
		mcdParse.getData().forEach(result -> {
			if (result.getSelected() != null && result.getSelected()) {
				McdTask mcdTask = result.toTask(filterBaseDir());
				if (StrUtil.isBlankIfStr(mcdTask.getName())) {
					mcdTask.setName(DateUtil.format(new Date(), "yyyyMMddHHmmssSSS"));
				}
				if (StrUtil.isNotBlank(mcdTask.getPath())) {
					mcdTask.setPath(mcdTask.getPath().trim().replaceAll("\\\\", "/"));
				}
				mcdTask.setUrl(mcdTask.getUrl().trim().replaceAll("\t", StrUtil.EMPTY));
				mcdTask.setPath(mcdTask.getPath().trim().replaceAll("\t", StrUtil.EMPTY));
				mcdTaskService.save(mcdTask);
			}
		});
		mcdParse.setStatus("C");
		mcdParseService.updateById(mcdParse);
		return ResDTO.success();
	}

	@Override
	public ResDTO<?> subscribe(McdSubscribe mcdSubscribe) {
		try {
			return ResDTO.parse(mcdSubscribeService.save(mcdSubscribe));
		} catch (Exception e) {
			return ResDTO.failure();
		}
	}

	@Override
	public ResDTO<?> parseList() {
		return ResDTO.success(mcdParseService.list(new LambdaQueryWrapper<McdParse>().orderByDesc(McdParse::getRowid)));
	}

	@Override
	public ResDTO<?> taskList() {
		return ResDTO.success(mcdTaskService.list(new LambdaQueryWrapper<McdTask>()
				.in(McdTask::getStatus, Arrays.asList("S", "N")).orderByAsc(McdTask::getRowid)));
	}

	@Override
	public ResDTO<?> taskListHistory() {
		return ResDTO.success(mcdTaskService.list(new LambdaQueryWrapper<McdTask>()
				.in(McdTask::getStatus, Arrays.asList("E", "ER")).orderByAsc(McdTask::getRowid)));
	}

	@Override
	public ResDTO<?> subscribeList() {
		return ResDTO.success(mcdSubscribeService
				.list(new LambdaQueryWrapper<McdSubscribe>().orderByAsc(McdSubscribe::getCreateTime)));
	}

	@Override
	public ResDTO<?> delSubscribe(McdSubscribe mcdSubscribe) {
		try {
			return ResDTO.success(mcdSubscribeService.removeById(mcdSubscribe));
		} catch (Exception e) {
			return ResDTO.failure();
		}
	}

	@Override
	public ResDTO<?> configList() {
		LambdaQueryWrapper<McdConfig> query = new LambdaQueryWrapper<McdConfig>().eq(McdConfig::getSecret, false);
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			query.or(q -> q.eq(McdConfig::getKey, "base_dir"));
		}
		return ResDTO.success(mcdConfigService.list(query));
	}

	@Override
	public ResDTO<?> saveConfig(List<McdConfig> confs) {
		mcdConfigService.updateBatchById(confs);
		return ResDTO.success();
	}

	@Override
	public ResDTO<?> login(LoginDTO loginDTO) {
		List<McdConfig> confs = mcdConfigService
				.list(new LambdaQueryWrapper<McdConfig>().in(McdConfig::getKey, Arrays.asList("salt", "password")));
		String password = getConfig("password", confs, String.class);
		String salt = getConfig("salt", confs, String.class);
		String hex = SecureUtil.hmacMd5(loginDTO.getPassword() + salt).digestHex(salt);
		if (StrUtil.equals(password, hex)) {
			Map<String, Object> map = Map.of("expire_time", System.currentTimeMillis() + 300000 * 60 * 60 * 24);
			String token = JWTUtil.createToken(map, secret.getBytes());
			LoginDTO res = new LoginDTO();
			res.setToken(token);
			return ResDTO.success(res);
		} else {
			return ResDTO.failure(Msg.MSG_PASSWORD_ERROR);
		}
	}

	@Override
	public ResDTO<?> contact() {
		List<McdConfig> confs = mcdConfigService
				.list(new LambdaQueryWrapper<McdConfig>().in(McdConfig::getKey, Arrays.asList("contact")));
		return ResDTO.success(getConfig("contact", confs, String.class));
	}

	@Override
	public ResDTO<?> updatePassword(LoginDTO loginDTO) {
		List<McdConfig> confs = mcdConfigService
				.list(new LambdaQueryWrapper<McdConfig>().in(McdConfig::getKey, Arrays.asList("salt", "password")));
		String salt = RandomUtil.randomString(8);
		String hex = SecureUtil.hmacMd5(loginDTO.getPassword() + salt).digestHex(salt);
		McdConfig saltConf = getConfig("salt", confs);
		McdConfig hexConf = getConfig("password", confs);
		saltConf.setVal(salt);
		hexConf.setVal(hex);
		mcdConfigService.updateBatchById(confs);
		return ResDTO.success();
	}

	private McdConfig getConfig(String key, List<McdConfig> configs) {
		for (McdConfig config : configs) {
			if (config.getKey().equals(key)) {
				return config;
			}
		}
		return null;
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

	@Override
	public ResDTO<?> cleanData() {
		LambdaQueryWrapper<McdParse> queryParseHis = new LambdaQueryWrapper<McdParse>();
		queryParseHis.select(McdParse::getRowid);
		queryParseHis.eq(McdParse::getStatus, "S");
		List<McdParse> parseListing = mcdParseService.list(queryParseHis);
		LambdaQueryWrapper<McdTask> queryHis = new LambdaQueryWrapper<McdTask>();
		queryHis.select(McdTask::getRowid);
		queryHis.eq(McdTask::getStatus, "S");
		List<McdTask> taskListing = mcdTaskService.list(queryHis);
		if (parseListing.size() == 0 && taskListing.size() == 0) {
			mcdSubscribeService.remove(new LambdaQueryWrapper<McdSubscribe>().ge(McdSubscribe::getRowid, 0));
			mcdParseService.remove(new LambdaQueryWrapper<McdParse>().ge(McdParse::getRowid, 0));
			mcdTaskService.remove(new LambdaQueryWrapper<McdTask>().ge(McdTask::getRowid, 0));
			return ResDTO.success();
		} else {
			return ResDTO.failure("有进行中的任务,无法清理", null);
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
