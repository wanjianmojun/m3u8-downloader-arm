package com.sj.m3u8.parser.docker.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("mcd_config")
public class McdConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId(value = "rowid", type = IdType.AUTO)
	private Integer rowid;

	@TableField("key")
	private String key;

	@TableField("val")
	private String val;

	@TableField("secret")
	private Boolean secret;

	@TableField("type")
	private String type;

	@TableField("min")
	private Integer min;

	@TableField("max")
	private Integer max;

	@TableField("desc")
	private String desc;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@TableField(value = "create_time", fill = FieldFill.INSERT)
	private LocalDateTime createTime;
}
