package cn.tedu.media_player_v4.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/** 发送http请求的工具类 **/
public class HttpUtils {
	/**
	 * 发送http get请求 返回接收到的响应输入流
	 * @param path  请求资源路径
	 * @return 
	 */
	public static InputStream getInputStream(String path)throws Exception{
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("GET");
		InputStream is = conn.getInputStream();
		return is;
	}
	
	/**
	 * 读取输入流  以utf-8的编码解析成String 
	 * @param is
	 * @return
	 */
	public static String isToString(InputStream is)throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while((line=reader.readLine())!=null){
			sb.append(line);
		}
		String respText = sb.toString();
		return respText;
	}
	
}



