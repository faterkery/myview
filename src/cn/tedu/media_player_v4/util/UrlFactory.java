package cn.tedu.media_player_v4.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/** URL工厂 用于生产URL字符串 **/
public class UrlFactory {
	/**
	 * 获取新歌榜url地址
	 * @param offset  起始位置
	 * @param size  查询结果的条目数
	 * @return
	 */
	public static String getNewMusicListUrl(int offset, int size) {
		String url ="http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.billboard.billList&format=xml&type=1&offset="+offset+"&size="+size;
		return url;
	}

	/**
	 * 通过songid 获取查询songinfo的url路径
	 * @param songId
	 * @return
	 */
	public static String getSongInfoUrl(String songId) {
		String url = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.song.getInfos&format=json&songid="+songId+"&ts=1408284347323&e=JoN56kTXnnbEpd9MVczkYJCSx%2FE1mkLx%2BPMIkTcOEu4%3D&nw=2&ucf=1&res=1";
		return url;
	}

	/**
	 * 通过关键字 查询音乐列表请求
	 * @param keyword
	 * @return
	 */
	public static String getSearchMusicUrl(String keyword) {
		try {
			keyword = URLEncoder.encode(keyword, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String url = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.search.common&format=json&query="+keyword+"&page_no=1&page_size=50";
		return url;
	}

}
