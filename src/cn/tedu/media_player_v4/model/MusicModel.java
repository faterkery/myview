package cn.tedu.media_player_v4.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import cn.tedu.media_player_v4.entity.Music;
import cn.tedu.media_player_v4.entity.SongInfo;
import cn.tedu.media_player_v4.entity.SongUrl;
import cn.tedu.media_player_v4.util.HttpUtils;
import cn.tedu.media_player_v4.util.JSONParser;
import cn.tedu.media_player_v4.util.UrlFactory;
import cn.tedu.media_player_v4.util.XmlParser;

/**
 * 音乐相关的业务类
 */
public class MusicModel {
	
	/**
	 * 通过关键字 查询音乐列表
	 * @param keyword
	 * @param callback
	 */
	public void searchMusicList(final String keyword, final MusicListCallback callback ){
		AsyncTask<String, String, List<Music>> task = new AsyncTask<String, String, List<Music>>(){
			protected List<Music> doInBackground(String... params) {
				//发送查询请求
				String url = UrlFactory.getSearchMusicUrl(keyword);
				try {
					InputStream is = HttpUtils.getInputStream(url);
					String json = HttpUtils.isToString(is);
					//json : {  song_list:[{},{},{},{}]  }
					JSONObject obj = new JSONObject(json);
					JSONArray ary = obj.getJSONArray("song_list");
					List<Music> musics = JSONParser.parseMusicList(ary);
					return musics;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			protected void onPostExecute(List<Music> result) {
				callback.onMusicListLoaded(result);
			}
		};
		task.execute();
	}
	
	/**
	 * 通过songId  查询音乐的基本信息
	 * 当基本信息查询完毕后 调用callback的回调方法
	 * @param songId
	 * @param callback
	 */
	public void loadSongInfoBySongId(final String songId,final SongInfoCallback callback){
		AsyncTask<String, String, Music> task = new AsyncTask<String, String, Music>(){
			protected Music doInBackground(String... params) {
				try {
					//发送请求  解析响应 返回Music
					String path = UrlFactory.getSongInfoUrl(songId);
					InputStream is = HttpUtils.getInputStream(path);
					//把is读取成一个json字符串
					String json=HttpUtils.isToString(is);
					//解析json  { songurl:{url:[{},{}]}, songinfo:{} }  
					JSONObject obj = new JSONObject(json);
					JSONArray urlAry = obj.getJSONObject("songurl").getJSONArray("url");
					JSONObject infoObj = obj.getJSONObject("songinfo");
					//调用工具类 解析json
					List<SongUrl> urls=JSONParser.paseSongUrls(urlAry);
					SongInfo info=JSONParser.parseSongInfo(infoObj);
					//把urls与info封装到music对象 返回给主线程
					Music m = new Music();
					m.setUrls(urls);
					m.setInfo(info);
					return m;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			protected void onPostExecute(Music result) {
				//调用回调方法
				if(result==null){
					callback.onSongInfoLoaded(null, null);
				}else{
					callback.onSongInfoLoaded(result.getUrls(), result.getInfo());
				}
			}
		};
		task.execute();
	}
	
	/**
	 * 加载新歌榜列表 需要发送http请求
	 * 必须在工作线程中发送请求 
	 * @param offset  音乐的起始位置
	 * @param size  向后查询的条目数
	 */
	public void loadNewMusicList(final int offset,final int size, final MusicListCallback callback) {
		AsyncTask<String, String, List<Music>> task = new AsyncTask<String, String, List<Music>>(){
			protected List<Music> doInBackground(String... params) {
				try {
					//发送http请求
					String url = UrlFactory.getNewMusicListUrl(offset, size);
					InputStream is=HttpUtils.getInputStream(url);
					//解析is 中的xml文档 获取List<Music>
					List<Music> musics=XmlParser.parseMusicList(is);
					return musics;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			@Override
			protected void onPostExecute(List<Music> result) {
				//Log.i("info", "音乐列表:"+result.toString());
				//主线程 更新UI
				callback.onMusicListLoaded(result);
			}
		};
		task.execute();
	}

	/**
	 * 加载歌词数据  解析歌词文本  封装为
	 * HashMap<String, String> lrc;
	 * @param lrcPath
	 * @param lrcCallback
	 */
	public void loadLrc(final String lrcPath,final LrcCallback callback) {
		AsyncTask<String, String, HashMap<String, String> > task = new AsyncTask<String, String, HashMap<String,String>>(){
			protected HashMap<String, String> doInBackground(String... params) {
				try {
					//下载歌词
					InputStream is = HttpUtils.getInputStream(lrcPath);
					//解析输入流
					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					String line = null;
					HashMap<String, String> lrc = new HashMap<String, String>();
					while((line=reader.readLine())!=null){
						System.out.println("歌词"+line);
						//判断格式是否是: 
						//  [title]歌名 ...
						if(!line.startsWith("[") || !line.contains(":") || !line.contains(".")){
							continue;
						}
						//  [00:00.00]歌词内容
						String time = line.substring(1, line.indexOf("."));
						String content = line.substring(line.lastIndexOf("]")+1);
						lrc.put(time, content);	
					}
					return lrc;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			protected void onPostExecute(HashMap<String, String> result) {
				//调用回调方法
				callback.onLrcLoaded(result);
			}
		};
		task.execute();
}
}
