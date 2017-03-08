package cn.tedu.media_player_v4.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import cn.tedu.media_player_v4.R;
import cn.tedu.media_player_v4.adapter.MusicAdapter;
import cn.tedu.media_player_v4.app.MusicApplication;
import cn.tedu.media_player_v4.entity.Music;
import cn.tedu.media_player_v4.entity.SongInfo;
import cn.tedu.media_player_v4.entity.SongUrl;
import cn.tedu.media_player_v4.model.MusicListCallback;
import cn.tedu.media_player_v4.model.MusicModel;
import cn.tedu.media_player_v4.model.SongInfoCallback;
import cn.tedu.media_player_v4.service.PlayMusicService.MusicBinder;
/** 新歌榜列表 */
public class NewMusicListFragment extends Fragment {
	private ListView listView;
	private MusicModel model;
	private List<Music> musics;
	private MusicAdapter adapter;
	private MusicBinder binder;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fragment_music_list, null);
		//初始化控件
		setViews(view);
		//调用业务层的方法 加载新歌榜列表
		model = new MusicModel();
		model.loadNewMusicList(0, 20, new MusicListCallback() {
			//当列表加载完毕后将会调用的回调方法
			public void onMusicListLoaded(List<Music> musics) {
				NewMusicListFragment.this.musics = musics;
				setAdapter();
			}
		});
		//添加监听
		setListeners();
		return view;
	}
	
	/**
	 * 添加监听
	 */
	private void setListeners() {
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final Music music=musics.get(position);
				//把musics与position都存入applicaton
				MusicApplication app = MusicApplication.getApp();
				app.setMusics(musics);
				app.setPosition(position);
				
				//通过music的songId加载这首音乐的基本信息
				String songId = music.getSong_id();
				model.loadSongInfoBySongId(songId, new SongInfoCallback() {
					//当请求发送完毕 基本信息解析完毕后 执行该回调方法
					public void onSongInfoLoaded(List<SongUrl> urls, SongInfo info) {
						//把解析出来的urls与songinfo存入music对象
						music.setUrls(urls);
						music.setInfo(info);
						//播放音乐
						String fileLink=urls.get(0).getFile_link();
						binder.playMusic(fileLink);
					}
				});
			}
		});
		//滚动listView时执行
				listView.setOnScrollListener(new OnScrollListener() {
					boolean isBottom = false;
					boolean requesting = false;
					//滚动状态改变时执行
					public void onScrollStateChanged(AbsListView view, int scrollState) {
						switch (scrollState) {
						case OnScrollListener.SCROLL_STATE_IDLE:
							//判断是否到底了
							if(isBottom && !requesting){ //加载下一页数据
								requesting = true;
								model.loadNewMusicList(NewMusicListFragment.this.musics.size(), 20, new MusicListCallback() {
									public void onMusicListLoaded(List<Music> musics) {
										if(musics==null || musics.isEmpty()){ //服务端返回的是空集合
											Toast.makeText(getActivity(), "木有数据了", Toast.LENGTH_SHORT).show();
											requesting = false;
											return;
										}
										//把新得到的音乐数据全部添加到旧音乐列表中
										NewMusicListFragment.this.musics.addAll(musics);
										adapter.notifyDataSetChanged();
										//列表更新完毕 把requesting改成false
										requesting = false;
									}
								});
							}
							break;
						case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
							break;
						case OnScrollListener.SCROLL_STATE_FLING:
							break;
						}
					}
					//当滚动时执行  该方法的执行频率非常高
					public void onScroll(AbsListView view, 
							int firstVisibleItem, //第一个可见项的position
							int visibleItemCount, //可见项的数量
							int totalItemCount //item的总数量 
							) {
						if(firstVisibleItem + visibleItemCount == totalItemCount){
							isBottom = true;
						}else{
							isBottom = false;
						}
					}
				});
	}

	private void setViews(View view) {
		listView = (ListView) view.findViewById(R.id.listView);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//把adapter中的线程给停掉
		adapter.stopThread();
	}
	
	/**
	 * 更新适配器
	 */
	public void setAdapter(){
		adapter = new MusicAdapter(getActivity(), musics, listView);
		listView.setAdapter(adapter);
	}

	/**
	 * 接收传递过来的binder对象
	 */
	public void setBinder(MusicBinder binder) {
		this.binder = binder;
	}
	
}
