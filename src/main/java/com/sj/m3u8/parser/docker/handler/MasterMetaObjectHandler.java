package com.sj.m3u8.parser.docker.handler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.sj.m3u8.parser.docker.entity.McdConfig;

@Component
public class MasterMetaObjectHandler implements MetaObjectHandler {

	@Override
	public void insertFill(MetaObject metaObject) {
		this.strictInsertFill(metaObject, McdConfig.Fields.createTime, LocalDateTime.class,
				LocalDateTime.now(ZoneId.of("GMT+8")));
	}

	@Override
	public void updateFill(MetaObject metaObject) {
		this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now(ZoneId.of("GMT+8")));
	}

}
