package cn.tedu.media_player_v4.model;

import java.util.List;

import cn.tedu.media_player_v4.entity.Music;

public interface MusicListCallback {
	/**
	 * 当列表加载完毕后将会调用的回调方法
	 * @param musics
	 */
	void onMusicListLoaded(List<Music> musics);
}
