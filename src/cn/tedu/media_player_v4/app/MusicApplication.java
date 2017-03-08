package cn.tedu.media_player_v4.app;

import java.util.List;

import android.app.Application;
import cn.tedu.media_player_v4.entity.Music;

public class MusicApplication extends Application {
	private List<Music> musics; // ��ǰ�����б�
	private int position; // ��ǰ���ڲ������ֵ�λ��
	private static MusicApplication app;
	
	@Override
	public void onCreate() {
		super.onCreate();
		app = this;
	}
	
	public static MusicApplication getApp(){
		return app;
	}
	
	public void setMusics(List<Music> musics) {
		this.musics = musics;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * ��ȡ��ǰ���ڲ��ŵ�����
	 * 
	 * @return
	 */
	public Music getCurrentMusic() {
		return musics.get(position);
	}

	/**
	 * ������һ�׸���
	 */
	public void preMusic() {
		position = position == 0 ? 0 : position-1;
	}
	/**
	 * ������һ�׸���
	 */
	public void nextMusic() {
		position = position == musics.size()-1 ? 0 : position+1;
	}
}
