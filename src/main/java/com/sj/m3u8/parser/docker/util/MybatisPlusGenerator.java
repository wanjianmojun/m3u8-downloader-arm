package com.sj.m3u8.parser.docker.util;

import java.sql.Types;
import java.util.Collections;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;

public class MybatisPlusGenerator {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		String url = "jdbc:sqlite:./db/mcd.sqlite3";
		String username = "";
		String password = "";
		FastAutoGenerator.create(url, username, password).globalConfig(builder -> {
			builder.author("wangsijia").outputDir("D://");
		}).dataSourceConfig(builder -> builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
			int typeCode = metaInfo.getJdbcType().TYPE_CODE;
			if (typeCode == Types.SMALLINT) {
				return DbColumnType.INTEGER;
			}
			return typeRegistry.getColumnType(metaInfo);
		})).packageConfig(builder -> {
			builder.parent("com.sj.m3u8.parser.docker").moduleName("")
					.pathInfo(Collections.singletonMap(OutputFile.xml, "D://com/sj/m3u8/parser/docker/mapper"));
		}).strategyConfig(builder -> {
			builder.entityBuilder().enableLombok().enableTableFieldAnnotation();
			builder.addInclude("mcd_task", "mcd_task_split", "mcd_subscribe", "mcd_device", "mcd_config");
		}).execute();
	}
}