package cn.tedu.media_player_v4.model;

import java.util.List;

import cn.tedu.media_player_v4.entity.Music;

public interface MusicListCallback {
	/**
	 * ���б������Ϻ󽫻���õĻص�����
	 * @param musics
	 */
	void onMusicListLoaded(List<Music> musics);
}
