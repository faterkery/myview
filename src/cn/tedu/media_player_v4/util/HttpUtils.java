package cn.tedu.media_player_v4.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/** ����http����Ĺ����� **/
public class HttpUtils {
	/**
	 * ����http get���� ���ؽ��յ�����Ӧ������
	 * @param path  ������Դ·��
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
	 * ��ȡ������  ��utf-8�ı��������String 
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



