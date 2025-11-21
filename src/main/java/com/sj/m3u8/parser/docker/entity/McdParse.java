package com.sj.m3u8.parser.docker.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author wangsijia
 * @since 2025-11-14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@JsonIgnoreProperties(ignoreUnknown = true)
@TableName(value = "mcd_parse", autoResultMap = true)
public class McdParse implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId(value = "rowid", type = IdType.AUTO)
	private Integer rowid;

	@TableField("name")
	private String name;

	@TableField("url")
	private String url;

	@TableField("finished")
	private Integer finished;

	@TableField("total")
	private Integer total;

	@TableField("status")
	private String status;

	@TableField(value = "data", typeHandler = JacksonTypeHandler.class)
	private List<Result> data;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@TableField(value = "create_time", fill = FieldFill.INSERT)
	private LocalDateTime createTime;
}
