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
 * ������ص�ҵ����
 */
public class MusicModel {
	
	/**
	 * ͨ���ؼ��� ��ѯ�����б�
	 * @param keyword
	 * @param callback
	 */
	public void searchMusicList(final String keyword, final MusicListCallback callback ){
		AsyncTask<String, String, List<Music>> task = new AsyncTask<String, String, List<Music>>(){
			protected List<Music> doInBackground(String... params) {
				//���Ͳ�ѯ����
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
	 * ͨ��songId  ��ѯ���ֵĻ�����Ϣ
	 * ��������Ϣ��ѯ��Ϻ� ����callback�Ļص�����
	 * @param songId
	 * @param callback
	 */
	public void loadSongInfoBySongId(final String songId,final SongInfoCallback callback){
		AsyncTask<String, String, Music> task = new AsyncTask<String, String, Music>(){
			protected Music doInBackground(String... params) {
				try {
					//��������  ������Ӧ ����Music
					String path = UrlFactory.getSongInfoUrl(songId);
					InputStream is = HttpUtils.getInputStream(path);
					//��is��ȡ��һ��json�ַ���
					String json=HttpUtils.isToString(is);
					//����json  { songurl:{url:[{},{}]}, songinfo:{} }  
					JSONObject obj = new JSONObject(json);
					JSONArray urlAry = obj.getJSONObject("songurl").getJSONArray("url");
					JSONObject infoObj = obj.getJSONObject("songinfo");
					//���ù����� ����json
					List<SongUrl> urls=JSONParser.paseSongUrls(urlAry);
					SongInfo info=JSONParser.parseSongInfo(infoObj);
					//��urls��info��װ��music���� ���ظ����߳�
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
				//���ûص�����
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
	 * �����¸���б� ��Ҫ����http����
	 * �����ڹ����߳��з������� 
	 * @param offset  ���ֵ���ʼλ��
	 * @param size  ����ѯ����Ŀ��
	 */
	public void loadNewMusicList(final int offset,final int size, final MusicListCallback callback) {
		AsyncTask<String, String, List<Music>> task = new AsyncTask<String, String, List<Music>>(){
			protected List<Music> doInBackground(String... params) {
				try {
					//����http����
					String url = UrlFactory.getNewMusicListUrl(offset, size);
					InputStream is=HttpUtils.getInputStream(url);
					//����is �е�xml�ĵ� ��ȡList<Music>
					List<Music> musics=XmlParser.parseMusicList(is);
					return musics;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			@Override
			protected void onPostExecute(List<Music> result) {
				//Log.i("info", "�����б�:"+result.toString());
				//���߳� ����UI
				callback.onMusicListLoaded(result);
			}
		};
		task.execute();
	}

	/**
	 * ���ظ������  ��������ı�  ��װΪ
	 * HashMap<String, String> lrc;
	 * @param lrcPath
	 * @param lrcCallback
	 */
	public void loadLrc(final String lrcPath,final LrcCallback callback) {
		AsyncTask<String, String, HashMap<String, String> > task = new AsyncTask<String, String, HashMap<String,String>>(){
			protected HashMap<String, String> doInBackground(String... params) {
				try {
					//���ظ��
					InputStream is = HttpUtils.getInputStream(lrcPath);
					//����������
					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					String line = null;
					HashMap<String, String> lrc = new HashMap<String, String>();
					while((line=reader.readLine())!=null){
						System.out.println("���"+line);
						//�жϸ�ʽ�Ƿ���: 
						//  [title]���� ...
						if(!line.startsWith("[") || !line.contains(":") || !line.contains(".")){
							continue;
						}
						//  [00:00.00]�������
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
				//���ûص�����
				callback.onLrcLoaded(result);
			}
		};
		task.execute();
}
}
