package com.sj.m3u8.parser.docker.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class MybatisPlusJoinUtil {
	public static void main(String[] args) throws Exception {
		File dir = new File("D:\\com\\sj\\m3u8\\parser\\docker");
		for (File dirs : dir.listFiles()) {
			if (dirs.getName().equals("service")) {
				for (File file : dirs.listFiles()) {
					if (file.isDirectory()) {
						for (File impl : file.listFiles()) {
							BufferedReader br = new BufferedReader(new FileReader(impl));
							StringBuffer data = new StringBuffer("");
							String line = null;
							while ((line = br.readLine()) != null) {
								line = line.replaceFirst("extends ServiceImpl", "extends MPJBaseServiceImpl");
								line = line.replaceFirst("com.baomidou.mybatisplus.extension.service.impl.ServiceImpl",
										"com.github.yulichang.base.MPJBaseServiceImpl");
								data.append(line + "\r\n");
							}
							br.close();
							impl.delete();
							impl.createNewFile();
							BufferedWriter bw = new BufferedWriter(new FileWriter(impl));
							bw.write(data.toString());
							bw.flush();
							bw.close();
						}
					} else {
						BufferedReader br = new BufferedReader(new FileReader(file));
						StringBuffer data = new StringBuffer("");
						String line = null;
						while ((line = br.readLine()) != null) {
							line = line.replaceFirst("extends IService", "extends MPJDeepService");
							line = line.replaceFirst("com.baomidou.mybatisplus.extension.service.IService",
									"com.github.yulichang.extension.mapping.base.MPJDeepService");
							data.append(line + "\r\n");
						}
						br.close();
						file.delete();
						file.createNewFile();
						BufferedWriter bw = new BufferedWriter(new FileWriter(file));
						bw.write(data.toString());
						bw.flush();
						bw.close();
					}
				}
			} else if (dirs.getName().equals("mapper")) {
				for (File file : dirs.listFiles()) {
					if (!file.isDirectory()) {
						BufferedReader br = new BufferedReader(new FileReader(file));
						StringBuffer data = new StringBuffer("");
						String line = null;
						while ((line = br.readLine()) != null) {
							line = line.replaceFirst("extends BaseMapper", "extends MPJBaseMapper");
							line = line.replaceFirst("com.baomidou.mybatisplus.core.mapper.BaseMapper",
									"com.github.yulichang.base.MPJBaseMapper");
							data.append(line + "\r\n");
						}
						br.close();
						file.delete();
						file.createNewFile();
						BufferedWriter bw = new BufferedWriter(new FileWriter(file));
						bw.write(data.toString());
						bw.flush();
						bw.close();
					}
				}
			}
		}
		File entities = new File("D:\\com\\sj\\m3u8\\parser\\docker\\entity");
		for (File entity : entities.listFiles()) {
			if (!entity.isDirectory()) {
				BufferedReader br = new BufferedReader(new FileReader(entity));
				StringBuffer data = new StringBuffer("");
				String line = null;
				while ((line = br.readLine()) != null) {
					line = line.replaceFirst("private Byte", "private Boolean");
					line = line.replaceFirst("@Setter",
							"@Setter\r\n@ToString\r\n@FieldNameConstants\r\n@JsonIgnoreProperties(ignoreUnknown = true)");
					line = line.replaceFirst("@TableField\\(\"create_time\"\\)",
							"@JsonSerialize\\(using = LocalDateTimeSerializer.class\\)\r\n    @JsonDeserialize\\(using = LocalDateTimeDeserializer.class\\)\r\n    @JsonFormat\\(pattern = \"yyyy-MM-dd HH:mm:ss\", timezone = \"GMT+8\"\\)\r\n    @TableField\\(\"create_time\"\\)");
					line = line.replaceFirst("@TableField\\(\"update_time\"\\)",
							"@JsonSerialize\\(using = LocalDateTimeSerializer.class\\)\r\n    @JsonDeserialize\\(using = LocalDateTimeDeserializer.class\\)\r\n    @JsonFormat\\(pattern = \"yyyy-MM-dd HH:mm:ss\", timezone = \"GMT+8\"\\)\r\n    @TableField\\(value = \"update_time\", fill = FieldFill.UPDATE\\)");
					data.append(line + "\r\n");
				}
				br.close();
				entity.delete();
				entity.createNewFile();
				BufferedWriter bw = new BufferedWriter(new FileWriter(entity));
				bw.write(data.toString());
				bw.flush();
				bw.close();
			}
		}
	}
}
