package cn.tedu.media_player_v4.model;

import java.util.HashMap;

/**
 * �����صĻص��ӿ�
 */
public interface LrcCallback {
	/**
	 * �ص�����   ��������ز�������Ϻ�ִ��
	 * @param lrc
	 */
	public void onLrcLoaded(HashMap<String, String> lrc);
}
