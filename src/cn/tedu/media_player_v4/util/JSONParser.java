package cn.tedu.media_player_v4.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.tedu.media_player_v4.entity.Music;
import cn.tedu.media_player_v4.entity.SongInfo;
import cn.tedu.media_player_v4.entity.SongUrl;

/**
 * 解析json的工具类
 */
public class JSONParser {

	/**
	 * 解析jsonArray  获取音乐集合
	 * @param ary [{},{},{},{}]
	 * @return
	 * @throws Exception
	 */
	public static List<Music> parseMusicList(JSONArray ary)throws Exception{
		List<Music> musics = new ArrayList<Music>();
		for(int i=0; i<ary.length(); i++){
			JSONObject obj = ary.getJSONObject(i);
			Music m = new Music();
			m.setTitle(obj.getString("title"));
			m.setSong_id(obj.getString("song_id"));
			String author_name =obj.getString("author");
			author_name=	author_name.replaceAll("<em>","");
			author_name=	author_name.replaceAll("</em>","");
			m.setAuthor(author_name);
			m.setArtist_id(obj.getString("artist_id"));
			musics.add(m);
		}
		return musics;
	}
	/**
	 * 解析jsonArray  获取List<SongUrl>
	 * @param urlAry
	 * [{},{},{},{}]
	 * @return
	 * @throws JSONException 
	 */
	public static List<SongUrl> paseSongUrls(JSONArray urlAry) throws JSONException {
		List<SongUrl> urls= new ArrayList<SongUrl>();
		for(int i=0; i<urlAry.length(); i++){
			JSONObject obj = urlAry.getJSONObject(i);
			SongUrl url = new SongUrl(
					obj.getInt("song_file_id"), 
					obj.getInt("file_size"), 
					obj.getInt("file_duration"), 
					obj.getInt("file_bitrate"), 
					obj.getString("show_link"), 
					obj.getString("file_extension"), 
					obj.getString("file_link"));
			urls.add(url);
		}
		return urls;
	}
	
	/**
	 * 解析json 获取SongInfo对象 
	 * @param infoObj
	 * @return
	 * @throws JSONException 
	 */
	public static SongInfo parseSongInfo(JSONObject infoObj) throws JSONException {
		SongInfo info = new SongInfo(
				infoObj.getString("pic_huge"), 
				infoObj.getString("album_1000_1000"),  
				infoObj.getString("album_500_500"),  
				infoObj.getString("compose"), 
				infoObj.getString("artist_500_500"),  
				infoObj.getString("file_duration"),  
				infoObj.getString("album_title"),  
				infoObj.getString("title"),  
				infoObj.getString("pic_radio"),  
				infoObj.getString("language"),  
				infoObj.getString("lrclink"),  
				infoObj.getString("pic_big"),  
				infoObj.getString("pic_premium"),  
				infoObj.getString("artist_480_800"),  
				infoObj.getString("artist_id"),  
				infoObj.getString("album_id"),  
				infoObj.getString("artist_1000_1000"),  
				infoObj.getString("all_artist_id"),  
				infoObj.getString("artist_640_1136"),  
				infoObj.getString("publishtime"),  
				infoObj.getString("share_url"),  
				infoObj.getString("author"),  
				infoObj.getString("pic_small"),
				infoObj.getString("song_id"));
		return info;
	}

}
