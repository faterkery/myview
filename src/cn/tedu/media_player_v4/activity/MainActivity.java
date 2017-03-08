package cn.tedu.media_player_v4.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.tedu.media_player_v4.R;
import cn.tedu.media_player_v4.adapter.SearchMusicAdapter;
import cn.tedu.media_player_v4.app.MusicApplication;
import cn.tedu.media_player_v4.entity.Music;
import cn.tedu.media_player_v4.entity.SongInfo;
import cn.tedu.media_player_v4.entity.SongUrl;
import cn.tedu.media_player_v4.fragment.HotMusicListFragment;
import cn.tedu.media_player_v4.fragment.NewMusicListFragment;
import cn.tedu.media_player_v4.model.LrcCallback;
import cn.tedu.media_player_v4.model.MusicListCallback;
import cn.tedu.media_player_v4.model.MusicModel;
import cn.tedu.media_player_v4.model.SongInfoCallback;
import cn.tedu.media_player_v4.service.PlayMusicService;
import cn.tedu.media_player_v4.service.PlayMusicService.MusicBinder;
import cn.tedu.media_player_v4.util.BitmapCallback;
import cn.tedu.media_player_v4.util.BitmapUtils;
import cn.tedu.media_player_v4.util.DateUtils;
import cn.tedu.media_player_v4.util.GlobalConsts;

public class MainActivity extends FragmentActivity {
	private ViewPager viewPager;
	private RadioGroup radioGroup;
	private RadioButton rbNew;
	private RadioButton rbHot;
	private ImageView ivCMPic;
	private TextView tvCMTitle;
	private ImageButton tvButtonToPause;
	private ImageButton tvButtonPre;
	private ImageButton tvButtonNext;

	private Button btnToSearch;
	private Button btnSearch;
	private Button btnCancel;
	private EditText etSearch;
	private RelativeLayout rlSearch; 
	private ListView lvSearchResult;
	
	
	private RelativeLayout rlPlayMusic;
	private TextView tvPMTitle, tvPMSinger, tvPMLrc, tvPMCurrentTime, tvPMTotalTime;
	private ImageView ivPMBackground, ivPMAlbum;
	private SeekBar seekBar;
	private boolean isPlay = true;
	
	private List<Fragment> fragments;
	private List<Music> searchMusics;
	private ServiceConnection conn;
	private MusicInfoReceiver receiver;
	private MusicBinder binder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//��ʼ���ؼ�
		setViews();
		//��ʼ��viewpager��������
		setPagerAdapter();
		//����
		setListeners();
		//��service
		bindMusicService();
		//ע�����
		registComponents();
	}

	@Override
	protected void onDestroy() {
		//ȡ��service�İ�
		this.unbindService(conn);
		//ȡ���㲥��������ע��
		this.unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	/**
	 * ��������˼�ʱִ��
	 */
	public void onBackPressed() {
		if(rlPlayMusic.getVisibility() == View.VISIBLE){
			//����
			rlPlayMusic.setVisibility(View.INVISIBLE);
			ScaleAnimation anim = new ScaleAnimation(1, 0, 1, 0, 0, rlPlayMusic.getHeight());
			anim.setDuration(300);
			rlPlayMusic.startAnimation(anim);
			
		}else{
			super.onBackPressed();
		}
	}
	
	/**
	 * ע����� 
	 */
	private void registComponents() {
		receiver = new MusicInfoReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalConsts.ACTION_MUSIC_STARTED);
		filter.addAction(GlobalConsts.ACTION_MUSIC_PROCESS);
		this.registerReceiver(receiver, filter);
	}

	/**
	 * ��Service���
	 */
	private void bindMusicService() {
		Intent intent = new Intent(this, PlayMusicService.class);
		conn = new ServiceConnection() {
			/** �����쳣�Ͽ�ʱִ�� */
			public void onServiceDisconnected(ComponentName name) {
			}
			/** ���ӽ�����ɺ�ִ�� */
			public void onServiceConnected(ComponentName name, IBinder service) {
				binder = (MusicBinder)service;
				//��binder���󴫸�NewMusicListFragment
				NewMusicListFragment f1=(NewMusicListFragment) fragments.get(0);
				f1.setBinder(binder);
			}
		};
		this.bindService(intent, conn, Service.BIND_AUTO_CREATE);
	}

	
	//����
	private void setListeners() {
		//��lvSearchResult�б���Ӽ���
				lvSearchResult.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						//����������б� �� position����application
						MusicApplication app = MusicApplication.getApp();
						app.setMusics(searchMusics);
						app.setPosition(position);
						//��ǰѡ�еĸ�������
						final Music m = searchMusics.get(position);
						String songId = m.getSong_id();
						new MusicModel().loadSongInfoBySongId(songId, new SongInfoCallback() {
							public void onSongInfoLoaded(List<SongUrl> urls, SongInfo info) {
								//�Ѳ�ѯ�����Ļ�����Ϣ��music�������Ը�ֵ
								m.setUrls(urls);
								m.setInfo(info);
								//��������
								String fileLink = m.getUrls().get(0).getFile_link();
								binder.playMusic(fileLink);
							}
						});
						rlSearch.setVisibility(View.INVISIBLE);
						TranslateAnimation anim = new TranslateAnimation(0, 0, 0, -rlSearch.getHeight());
						anim.setDuration(300);
						rlSearch.startAnimation(anim);
						rlPlayMusic.setVisibility(View.VISIBLE);
					}
				});
		//��btnSearch��Ӽ���
				btnSearch.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						//��������
						String keyword = etSearch.getText().toString();
						if("".equals(keyword)){
							return;
						}
						//�������� ִ������
						new MusicModel().searchMusicList(keyword, new MusicListCallback() {
							public void onMusicListLoaded(List<Music> musics) {
								searchMusics = musics;
								//������������б�
								SearchMusicAdapter searchAdapter = new SearchMusicAdapter(MainActivity.this, musics);
								lvSearchResult.setAdapter(searchAdapter);
							}
						});
					}
				});
				
				//��btnCancel��Ӽ���
				btnCancel.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						rlSearch.setVisibility(View.INVISIBLE);
						TranslateAnimation anim = new TranslateAnimation(0, 0, 0, -rlSearch.getHeight());
						anim.setDuration(300);
						rlSearch.startAnimation(anim);
					}
				});
				
				//��btnToSearch��Ӽ���
				btnToSearch.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						rlSearch.setVisibility(View.VISIBLE);
						TranslateAnimation anim = new TranslateAnimation(0, 0, -rlSearch.getHeight(), 0);
						anim.setDuration(300);
						rlSearch.startAnimation(anim);
					}
				});
		
		//��seekBar����¼�����
				seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					public void onStopTrackingTouch(SeekBar seekBar) {
					}
					public void onStartTrackingTouch(SeekBar seekBar) {
					}
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
						if(fromUser){ //�������ĸ��������û������
							binder.seekTo(progress);
						}
					}
				});
				
	    //����ť���ӵ㲥����ͣ�¼�
		tvButtonToPause.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				binder.pauseOrstart();
				if(isPlay)
				{
				}else{
					
				}
				}
		});
		tvButtonPre.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MusicApplication app = MusicApplication.getApp();
				app.preMusic();  //��applicationִ����һ���Ĳ���
				final Music m = app.getCurrentMusic();
				new MusicModel().loadSongInfoBySongId(m.getSong_id(), new SongInfoCallback() {
					public void onSongInfoLoaded(List<SongUrl> urls, SongInfo info) {
						m.setUrls(urls);
						m.setInfo(info);
						//��������
						String filelink = m.getUrls().get(0).getFile_link();
						binder.playMusic(filelink);
					}
				});
			}
		});
		tvButtonNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MusicApplication app = MusicApplication.getApp();
				app.nextMusic(); //��ת����һ��
				final Music m2=app.getCurrentMusic();
				new MusicModel().loadSongInfoBySongId(m2.getSong_id(), new SongInfoCallback() {
					public void onSongInfoLoaded(List<SongUrl> urls, SongInfo info) {
						m2.setUrls(urls);
						m2.setInfo(info);
						String url = m2.getUrls().get(0).getFile_link();
						binder.playMusic(url);
					}
				});
			}
		});
		//��Բ��ImageView����¼����� ����󵯳�rlPlayMusic
		ivCMPic.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				rlPlayMusic.setVisibility(View.VISIBLE);
				//ʹ�ö�����ʾ����rlPlayMusic
				ScaleAnimation anim = new ScaleAnimation(0, 1, 0, 1, 0, rlPlayMusic.getHeight());
				anim.setDuration(300);
				rlPlayMusic.startAnimation(anim);
			}
		});
		
		//viewpager����radioGroup
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int position) {
				switch (position) {
				case 0:  //ѡ���˵�һҳ
					rbNew.setChecked(true);
					break;
				case 1:  //ѡ���˵ڶ�ҳ
					rbHot.setChecked(true);
					break;
				}
			}
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		
		//radioGroup����viewpager
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radioNew:  //ѡ�����¸��
					viewPager.setCurrentItem(0);
					break;
				case R.id.radioHot:  //ѡ�����ȸ��
					viewPager.setCurrentItem(1);
					break;
				}
			}
		});
	}

	//��ʼ��viewpager��������
	private void setPagerAdapter() {
		//׼������Fragment��Ϊ����Դ
		fragments = new ArrayList<Fragment>();
		fragments.add(new NewMusicListFragment());
		fragments.add(new HotMusicListFragment());
		PagerAdapter pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(pagerAdapter );
	}

	/**
	 * ��ʼ���ؼ�
	 */
	private void setViews() {
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		rbNew = (RadioButton) findViewById(R.id.radioNew);
		rbHot = (RadioButton) findViewById(R.id.radioHot);
		
		ivCMPic = (ImageView) findViewById(R.id.ivCMPic);
		tvCMTitle = (TextView) findViewById(R.id.tvCMTitle);
		
		rlPlayMusic = (RelativeLayout) findViewById(R.id.rlPlayMusic);
		tvPMTitle = (TextView) findViewById(R.id.tvPMTitle);
		tvPMSinger = (TextView) findViewById(R.id.tvPMSinger);
		tvPMLrc = (TextView) findViewById(R.id.tvPMLrc);
		tvPMCurrentTime = (TextView) findViewById(R.id.tvPMCurrentTime);
		tvPMTotalTime = (TextView) findViewById(R.id.tvPMTotalTime);
		ivPMBackground = (ImageView) findViewById(R.id.ivPMBackground);
		ivPMAlbum = (ImageView) findViewById(R.id.ivPMAlbum);
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		tvButtonToPause =(ImageButton) findViewById(R.id.ivPMStart);
		tvButtonPre =(ImageButton) findViewById(R.id.ivPMPre);
		tvButtonNext =(ImageButton) findViewById(R.id. ivPMNext);
		
		rlPlayMusic.setVisibility(View.GONE);
		
		btnToSearch = (Button) findViewById(R.id.btnToSearch);
		btnSearch = (Button) findViewById(R.id.btnSearch);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		etSearch = (EditText) findViewById(R.id.etSearch);
		rlSearch = (RelativeLayout) findViewById(R.id.rlSearch);
		lvSearchResult = (ListView) findViewById(R.id.lvSearchResult);
	}

	//����ViewPager����������
	class MainPagerAdapter extends FragmentPagerAdapter{

		public MainPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public Fragment getItem(int position) {
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}
		
	}
	
	/**
	 * �㲥������ ����������Ϣ��صĹ㲥 
	 */
	class MusicInfoReceiver extends BroadcastReceiver{
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(GlobalConsts.ACTION_MUSIC_STARTED)){
				//���ֿ�ʼ����  ��ȡ���ֵĻ�����Ϣ
				 Music m=MusicApplication.getApp().getCurrentMusic();
				//���µײ����е�imageView �� TextView
				String smallPicPath=m.getPic_small();
				String title = m.getTitle();
				tvCMTitle.setText(title);
				BitmapUtils.loadBitmap(smallPicPath, new BitmapCallback() {
					public void onBitmapLoaded(Bitmap bitmap) {
						if(bitmap!=null){ //���سɹ�
							ivCMPic.setImageBitmap(bitmap);
							//��imageViewת����
							RotateAnimation anim = new RotateAnimation(0, 360, ivCMPic.getWidth()/2, ivCMPic.getHeight()/2);
							anim.setDuration(10000);
							//������ת
							anim.setInterpolator(new LinearInterpolator());
							//�����ظ�
							anim.setRepeatCount(Animation.INFINITE);
							ivCMPic.startAnimation(anim);
						}else{
							ivCMPic.setImageResource(R.drawable.ic_launcher);
						}
					}
				});
				//���²��Ž����е�ivPMAlbum 
				String albumPic=m.getInfo().getAlbum_500_500();
				if(albumPic.equals("")){
					albumPic = m.getInfo().getAlbum_1000_1000();
				}
				BitmapUtils.loadBitmap(albumPic, new BitmapCallback() {
					public void onBitmapLoaded(Bitmap bitmap) {
						if(bitmap!=null){ //����
							ivPMAlbum.setImageBitmap(bitmap);
						}else{
							ivPMAlbum.setImageResource(R.drawable.default_music_pic);
						}
					}
				});
				//���±���ͼƬ ivPMBackground
				String backPic = m.getInfo().getArtist_480_800();
				if(backPic.equals("")){
					backPic = m.getInfo().getArtist_640_1136();
				}
				if(backPic.equals("")){
					backPic = m.getInfo().getArtist_500_500();
				}
				if(backPic.equals("")){
					backPic = albumPic;
				}
				BitmapUtils.loadBitmap(backPic, 8, new BitmapCallback() {
					public void onBitmapLoaded(Bitmap bitmap) {
						if(bitmap!=null){ //����ͼƬ���سɹ�
							//�ѱ���ͼƬģ��������
							BitmapUtils.loadBlurBitmap(bitmap, 10, new BitmapCallback() {
								public void onBitmapLoaded(Bitmap bitmap) {
									ivPMBackground.setImageBitmap(bitmap);
								}
							});
						}else{
							ivPMBackground.setImageResource(R.drawable.default_music_background);
						}
					}
				});
				//����tvPMTitle  tvPMSinger
				tvPMTitle.setText(m.getInfo().getTitle());
				tvPMSinger.setText(m.getInfo().getAuthor());
				//���ظ�� ���ҽ������
				if(m.getLrc()!=null){ //��ǰ�Ѿ����ع���
					return;
				}
				String lrcPath = m.getInfo().getLrclink();
				if(lrcPath==null || lrcPath.equals("")){
					Toast.makeText(context, "�ø���û�и��", Toast.LENGTH_SHORT).show();
					return;
				}
				new MusicModel().loadLrc(lrcPath, new LrcCallback(){
					Music m=MusicApplication.getApp().getCurrentMusic();
					public void onLrcLoaded(HashMap<String, String> lrc) {
						m.setLrc(lrc);  //��ʼ��ؽ����������
					}
				});
			}else if(action.equals(GlobalConsts.ACTION_MUSIC_PROCESS)){
				int total =intent .getIntExtra("total", 0);
				int progress =intent.getIntExtra("progress", 0);
				seekBar.setMax(total);
				seekBar.setProgress(progress);
				String time=DateUtils.parseTime(progress);
				tvPMCurrentTime.setText(time);
				tvPMTotalTime.setText(DateUtils.parseTime(total));
				//�������Ѿ�������ϸ��¸����Ϣ
				Music m = MusicApplication.getApp().getCurrentMusic();
				HashMap<String, String> lrc = m.getLrc();
				System.out.println((lrc==null));
				if(lrc!=null){ //����Ǵ��ڵ�
					String content=lrc.get(time);
					if(content!=null){ //��ǰʱ����Ҫ���¸��
						tvPMLrc.setText(content);
					}
				}
			}
		}
	}
	
}


