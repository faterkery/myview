package cn.tedu.media_player_v4.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	public static final SimpleDateFormat format =new SimpleDateFormat("mm:ss");
	/*
	 * ����format�ĸ�ʽ ����timeʱ���
	 * ���ظ�ʽ��֮����ַ���
	 */
	public static String parseTime(int time){
		return format.format(new Date(time));
	}
}
