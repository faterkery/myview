package cn.tedu.media_player_v4.model;

import java.util.List;

import cn.tedu.media_player_v4.entity.SongInfo;
import cn.tedu.media_player_v4.entity.SongUrl;

public interface SongInfoCallback {
	/**
	 * 回调方法 
	 * 当音乐基本信息加载完毕后执行
	 * @param urls
	 * @param info
	 */
	public void onSongInfoLoaded(List<SongUrl> urls, SongInfo info);
}




