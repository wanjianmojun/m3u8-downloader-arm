package com.sj.m3u8.parser.docker.service;

import java.util.List;
import com.sj.m3u8.parser.docker.dto.LoginDTO;
import com.sj.m3u8.parser.docker.dto.ResDTO;
import com.sj.m3u8.parser.docker.entity.McdConfig;
import com.sj.m3u8.parser.docker.entity.McdParse;
import com.sj.m3u8.parser.docker.entity.McdSubscribe;
import com.sj.m3u8.parser.docker.entity.McdTask;

public interface ITaskService {
	public ResDTO<?> downloadOne(McdTask mcdTask);

	public ResDTO<?> parse(McdParse mcdParse);

	public ResDTO<?> addParseDownload(McdParse mcdParse);

	public ResDTO<?> subscribe(McdSubscribe mcdSubscribe);

	public ResDTO<?> parseList();

	public ResDTO<?> taskList();

	public ResDTO<?> configList();

	public ResDTO<?> taskListHistory();

	public ResDTO<?> subscribeList();

	public ResDTO<?> delSubscribe(McdSubscribe mcdSubscribe);

	public ResDTO<?> saveConfig(List<McdConfig> confs);

	public ResDTO<?> login(LoginDTO loginDTO);
	
	public ResDTO<?> contact();
	
	public ResDTO<?> updatePassword(LoginDTO loginDTO);
	
	public ResDTO<?> cleanData();
}
