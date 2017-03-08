package cn.tedu.media_player_v4.entity;

import java.util.HashMap;
import java.util.List;

/**
 * √Ë ˆ“ª ◊“Ù¿÷
 */
public class Music {
	private String artist_id;
	private String language;
	private String pic_big;
	private String pic_small;
	private String lrclink;
	private String hot;
	private String all_artist_id;
	private String style;
	private String song_id;
	private String title;
	private String ting_uid;
	private String author;
	private String album_id;
	private String album_title;
	private String artist_name;
	private List<SongUrl> urls;
	private SongInfo info;
	private HashMap<String, String> lrc;

	public HashMap<String, String> getLrc() {
		return lrc;
	}

	public void setLrc(HashMap<String, String> lrc) {
		this.lrc = lrc;
	}

	public Music() {
		// TODO Auto-generated constructor stub
	}

	public Music(String artist_id, String language, String pic_big, String pic_small, String lrclink, String hot,
			String all_artist_id, String style, String song_id, String title, String ting_uid, String author,
			String album_id, String album_title, String artist_name) {
		super();
		this.artist_id = artist_id;
		this.language = language;
		this.pic_big = pic_big;
		this.pic_small = pic_small;
		this.lrclink = lrclink;
		this.hot = hot;
		this.all_artist_id = all_artist_id;
		this.style = style;
		this.song_id = song_id;
		this.title = title;
		this.ting_uid = ting_uid;
		this.author = author;
		this.album_id = album_id;
		this.album_title = album_title;
		this.artist_name = artist_name;
	}

	public String getArtist_id() {
		return artist_id;
	}

	public void setArtist_id(String artist_id) {
		this.artist_id = artist_id;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getPic_big() {
		return pic_big;
	}

	public void setPic_big(String pic_big) {
		this.pic_big = pic_big;
	}

	public String getPic_small() {
		return pic_small;
	}

	public void setPic_small(String pic_small) {
		this.pic_small = pic_small;
	}

	public String getLrclink() {
		return lrclink;
	}

	public void setLrclink(String lrclink) {
		this.lrclink = lrclink;
	}

	public String getHot() {
		return hot;
	}

	public void setHot(String hot) {
		this.hot = hot;
	}

	public String getAll_artist_id() {
		return all_artist_id;
	}

	public void setAll_artist_id(String all_artist_id) {
		this.all_artist_id = all_artist_id;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getSong_id() {
		return song_id;
	}

	public void setSong_id(String song_id) {
		this.song_id = song_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTing_uid() {
		return ting_uid;
	}

	public void setTing_uid(String ting_uid) {
		this.ting_uid = ting_uid;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAlbum_id() {
		return album_id;
	}

	public void setAlbum_id(String album_id) {
		this.album_id = album_id;
	}

	public String getAlbum_title() {
		return album_title;
	}

	public void setAlbum_title(String album_title) {
		this.album_title = album_title;
	}

	public String getArtist_name() {
		return artist_name;
	}

	public void setArtist_name(String artist_name) {
		this.artist_name = artist_name;
	}

	public List<SongUrl> getUrls() {
		return urls;
	}

	public void setUrls(List<SongUrl> urls) {
		this.urls = urls;
	}

	public SongInfo getInfo() {
		return info;
	}

	public void setInfo(SongInfo info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return this.title;
	}

}
