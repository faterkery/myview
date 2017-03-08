package cn.tedu.media_player_v4.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	public static final SimpleDateFormat format =new SimpleDateFormat("mm:ss");
	/*
	 * 按照format的格式 解析time时间戳
	 * 返回格式化之后的字符串
	 */
	public static String parseTime(int time){
		return format.format(new Date(time));
	}
}
