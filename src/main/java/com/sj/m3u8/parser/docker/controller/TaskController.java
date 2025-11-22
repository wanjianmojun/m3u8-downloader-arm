package com.sj.m3u8.parser.docker.controller;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.sj.m3u8.parser.docker.dto.LoginDTO;
import com.sj.m3u8.parser.docker.dto.ResDTO;
import com.sj.m3u8.parser.docker.entity.McdConfig;
import com.sj.m3u8.parser.docker.entity.McdParse;
import com.sj.m3u8.parser.docker.entity.McdSubscribe;
import com.sj.m3u8.parser.docker.entity.McdTask;
import com.sj.m3u8.parser.docker.enu.Msg;
import com.sj.m3u8.parser.docker.service.ITaskService;
import com.sj.m3u8.parser.docker.util.M3u8Util;
import cn.hutool.core.util.ZipUtil;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class TaskController {

	@Autowired
	private ITaskService taskService;

	@PostMapping("/downloadOne")
	public ResDTO<?> downloadOne(@RequestBody McdTask mcdTask) {
		return taskService.downloadOne(mcdTask);
	}

	@PostMapping("/parse")
	public ResDTO<?> parse(@RequestBody McdParse mcdParse) {
		return taskService.parse(mcdParse);
	}

	@PostMapping("/addParseDownload")
	public ResDTO<?> addParseDownload(@RequestBody McdParse mcdParse) {
		return taskService.addParseDownload(mcdParse);
	}

	@PostMapping("/subscribe")
	public ResDTO<?> subscribe(@RequestBody McdSubscribe mcdSubscribe) {
		return taskService.subscribe(mcdSubscribe);
	}

	@PostMapping("/parseList")
	public ResDTO<?> parseList() {
		return taskService.parseList();
	}

	@PostMapping("/taskListHistory")
	public ResDTO<?> taskListHistory() {
		return taskService.taskListHistory();
	}

	@PostMapping("/taskList")
	public ResDTO<?> taskList() {
		return taskService.taskList();
	}

	@PostMapping("/configList")
	public ResDTO<?> configList() {
		return taskService.configList();
	}

	@PostMapping("/subscribeList")
	public ResDTO<?> subscribeList() {
		return taskService.subscribeList();
	}

	@PostMapping("/delSubscribe")
	public ResDTO<?> delSubscribe(@RequestBody McdSubscribe mcdSubscribe) {
		return taskService.delSubscribe(mcdSubscribe);
	}

	@PostMapping("/saveConfig")
	public ResDTO<?> saveConfig(@RequestBody List<McdConfig> confs) {
		return taskService.saveConfig(confs);
	}

	@PostMapping("/restart")
	public ResDTO<?> restart() {
		M3u8Util.restart();
		return ResDTO.success();
	}

	@PostMapping("/login")
	public ResDTO<?> login(@RequestBody LoginDTO loginDTO) {
		return taskService.login(loginDTO);
	}

	@PostMapping("/contact")
	public ResDTO<?> contact() {
		return taskService.contact();
	}

	@PostMapping("/updatePassword")
	public ResDTO<?> updatePassword(@RequestBody LoginDTO loginDTO) {
		return taskService.updatePassword(loginDTO);
	}

	@PostMapping("/cleanData")
	public ResDTO<?> cleanData() {
		return taskService.cleanData();
	}

	@PostMapping(value = "/update")
	public ResDTO<?> update(@RequestParam("file") MultipartFile file, HttpServletResponse httpServletResponse) {
		try {
			File path = new File("/update");
			ZipUtil.unzip(file.getInputStream(), path, Charset.defaultCharset());
			return ResDTO.success();
		} catch (Exception e) {
			httpServletResponse.setStatus(500);
			return ResDTO.failure(Msg.MSG_SERVICE_ERROR);
		}
	}

}
