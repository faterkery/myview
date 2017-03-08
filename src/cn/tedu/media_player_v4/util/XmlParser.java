package cn.tedu.media_player_v4.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;
import cn.tedu.media_player_v4.entity.Music;

/** xml������ **/
public class XmlParser {
	/**
	 * ���������б����� 
	 * @param is
	 * @return
	 * @throws Exception 
	 */
	public static List<Music> parseMusicList(InputStream is) throws Exception {
		//1. XmlPullParser
		XmlPullParser parser = Xml.newPullParser();
		//2. parser.setInput()
		parser.setInput(is, "utf-8");
		//3. eventType
		int eventType = parser.getEventType();
		//4. while(){}
		List<Music> musics = new ArrayList<Music>();
		Music music = null;
		while(eventType!=XmlPullParser.END_DOCUMENT){
			switch (eventType) {
			case XmlPullParser.START_TAG: //��ʼ��ǩ
				String tag = parser.getName();
				if(tag.equals("song")){ //������һ���¸�
					music = new Music();
					musics.add(music);
				}else if(tag.equals("artist_id")){
					music.setArtist_id(parser.nextText());
				}else if(tag.equals("language")){
					music.setLanguage(parser.nextText());
				}else if(tag.equals("pic_big")){
					music.setPic_big(parser.nextText());
				}else if(tag.equals("pic_small")){
					music.setPic_small(parser.nextText());
				}else if(tag.equals("lrclink")){
					music.setLrclink(parser.nextText());
				}else if(tag.equals("hot")){
					music.setHot(parser.nextText());
				}else if(tag.equals("all_artist_id")){
					music.setAll_artist_id(parser.nextText());
				}else if(tag.equals("style")){
					music.setStyle(parser.nextText());
				}else if(tag.equals("song_id")){
					music.setSong_id(parser.nextText());
				}else if(tag.equals("title")){
					music.setTitle(parser.nextText());
				}else if(tag.equals("ting_uid")){
					music.setTing_uid(parser.nextText());
				}else if(tag.equals("author")){
					music.setAuthor(parser.nextText());
				}else if(tag.equals("album_id")){
					music.setAlbum_id(parser.nextText());
				}else if(tag.equals("album_title")){
					music.setAlbum_title(parser.nextText());
				}else if(tag.equals("artist_name")){
					music.setArtist_name(parser.nextText());
				}
				break;
			}
			//��������¼�
			eventType = parser.next();
		}
		return musics;
	}
	
}




