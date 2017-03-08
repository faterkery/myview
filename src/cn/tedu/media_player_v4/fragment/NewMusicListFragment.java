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
/** �¸���б� */
public class NewMusicListFragment extends Fragment {
	private ListView listView;
	private MusicModel model;
	private List<Music> musics;
	private MusicAdapter adapter;
	private MusicBinder binder;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fragment_music_list, null);
		//��ʼ���ؼ�
		setViews(view);
		//����ҵ���ķ��� �����¸���б�
		model = new MusicModel();
		model.loadNewMusicList(0, 20, new MusicListCallback() {
			//���б������Ϻ󽫻���õĻص�����
			public void onMusicListLoaded(List<Music> musics) {
				NewMusicListFragment.this.musics = musics;
				setAdapter();
			}
		});
		//��Ӽ���
		setListeners();
		return view;
	}
	
	/**
	 * ��Ӽ���
	 */
	private void setListeners() {
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final Music music=musics.get(position);
				//��musics��position������applicaton
				MusicApplication app = MusicApplication.getApp();
				app.setMusics(musics);
				app.setPosition(position);
				
				//ͨ��music��songId�����������ֵĻ�����Ϣ
				String songId = music.getSong_id();
				model.loadSongInfoBySongId(songId, new SongInfoCallback() {
					//����������� ������Ϣ������Ϻ� ִ�иûص�����
					public void onSongInfoLoaded(List<SongUrl> urls, SongInfo info) {
						//�ѽ���������urls��songinfo����music����
						music.setUrls(urls);
						music.setInfo(info);
						//��������
						String fileLink=urls.get(0).getFile_link();
						binder.playMusic(fileLink);
					}
				});
			}
		});
		//����listViewʱִ��
				listView.setOnScrollListener(new OnScrollListener() {
					boolean isBottom = false;
					boolean requesting = false;
					//����״̬�ı�ʱִ��
					public void onScrollStateChanged(AbsListView view, int scrollState) {
						switch (scrollState) {
						case OnScrollListener.SCROLL_STATE_IDLE:
							//�ж��Ƿ񵽵���
							if(isBottom && !requesting){ //������һҳ����
								requesting = true;
								model.loadNewMusicList(NewMusicListFragment.this.musics.size(), 20, new MusicListCallback() {
									public void onMusicListLoaded(List<Music> musics) {
										if(musics==null || musics.isEmpty()){ //����˷��ص��ǿռ���
											Toast.makeText(getActivity(), "ľ��������", Toast.LENGTH_SHORT).show();
											requesting = false;
											return;
										}
										//���µõ�����������ȫ����ӵ��������б���
										NewMusicListFragment.this.musics.addAll(musics);
										adapter.notifyDataSetChanged();
										//�б������� ��requesting�ĳ�false
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
					//������ʱִ��  �÷�����ִ��Ƶ�ʷǳ���
					public void onScroll(AbsListView view, 
							int firstVisibleItem, //��һ���ɼ����position
							int visibleItemCount, //�ɼ��������
							int totalItemCount //item�������� 
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
		//��adapter�е��̸߳�ͣ��
		adapter.stopThread();
	}
	
	/**
	 * ����������
	 */
	public void setAdapter(){
		adapter = new MusicAdapter(getActivity(), musics, listView);
		listView.setAdapter(adapter);
	}

	/**
	 * ���մ��ݹ�����binder����
	 */
	public void setBinder(MusicBinder binder) {
		this.binder = binder;
	}
	
}
