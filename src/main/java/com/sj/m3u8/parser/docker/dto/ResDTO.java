package com.sj.m3u8.parser.docker.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sj.m3u8.parser.docker.enu.Msg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResDTO<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private int code;
	private String msg;
	private T data;

	public static <T> ResDTO<T> success() {
		return new ResDTO<T>(0, "操作成功", null);
	}

	public static <T> ResDTO<T> success(String msg, T data) {
		return new ResDTO<T>(0, msg, data);
	}

	public static <T> ResDTO<T> success(T data) {
		return new ResDTO<T>(0, "操作成功", data);
	}

	public static <T> ResDTO<T> failure() {
		return failure(Msg.MSG_SERVICE_ERROR);
	}

	public static <T> ResDTO<T> failure(T data) {
		return new ResDTO<T>(1, "操作失败", data);
	}

	public static <T> ResDTO<T> failure(Msg msg, T data) {
		return new ResDTO<T>(1, msg.getValue(), data);
	}

	public static <T> ResDTO<T> failure(String msg, T data) {
		return new ResDTO<T>(1, msg, data);
	}

	public static <T> ResDTO<T> failure(Msg msg) {
		return new ResDTO<T>(1, msg.getValue(), null);
	}

	public static <T> ResDTO<T> parse(boolean pass) {
		return pass ? success() : failure();
	}

	public static <T> ResDTO<T> parse(boolean pass, T data) {
		return pass ? success(data) : failure(data);
	}

	public <P> P toData(Class<P> clazz) {
		if (data == null)
			return null;
		else if (clazz.isInstance(data))
			return clazz.cast(data);
		else
			return null;
	}

	public <P> P toStrData(Class<P> clazz) {
		if (data == null)
			return null;
		else {
			try {
				return new ObjectMapper().readValue(String.valueOf(data), clazz);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	public <P> List<P> toDataList(Class<P> clazz) {
		if (data == null || code != 0)
			return null;
		else if (List.class.isInstance(data)) {
			List<P> list = new ArrayList<P>();
			List<?> dataList = List.class.cast(data);
			dataList.forEach(d -> {
				if (clazz.isInstance(d))
					list.add(clazz.cast(d));
			});
			return list;
		} else if (Set.class.isInstance(data)) {
			List<P> list = new ArrayList<P>();
			Set<?> dataList = Set.class.cast(data);
			dataList.forEach(d -> {
				if (clazz.isInstance(d))
					list.add(clazz.cast(d));
			});
			return list;
		} else
			return null;
	}

	public String toData() {
		if (data == null)
			return null;
		else if (String.class.isInstance(data))
			return String.class.cast(data);
		else
			return null;
	}

}
