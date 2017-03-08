package cn.tedu.media_player_v4.service;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import cn.tedu.media_player_v4.util.GlobalConsts;

/** 播放音乐的组件 */
public class PlayMusicService extends Service{
	private MediaPlayer player = new MediaPlayer();
	private boolean isLoop =true;
	/**
	 * 当Service创建时执行1次
	 */
	public void onCreate() {
		super.onCreate();
		player.setOnPreparedListener(new OnPreparedListener() {
			//当音乐准备完毕后执行的监听方法
			public void onPrepared(MediaPlayer mp) {
				player.start();
				//发出自定义广播  音乐开始播放
				Intent intent = new Intent(GlobalConsts.ACTION_MUSIC_STARTED);
				sendBroadcast(intent);
			}
		});
		new Thread()
		{
			public void run(){
				while(isLoop){
					try{
						Thread.sleep(1000);
						if(player.isPlaying()){
							int total =player.getDuration();
							Intent intent =new Intent(GlobalConsts.ACTION_MUSIC_PROCESS);
							intent.putExtra("total", total);
							intent.putExtra("progress", player.getCurrentPosition());
							sendBroadcast(intent);
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	public void onDestroy() {
		//释放mediaplayer
		player.release(); 
		super.onDestroy();
	}
	
	public IBinder onBind(Intent intent) {
		return new MusicBinder();
	}
	
	public class MusicBinder extends Binder{
        /*
         * 播放		
         */
		public void seekTo(int position){
			player.seekTo(position);
		}
		/**  播放音乐的接口方法  */
		public void playMusic(String url){
			try {
				player.reset();
				player.setDataSource(url);
				player.prepareAsync(); //异步的准备
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		/*
		 * 暂停
		 */
		public void pauseOrstart(){
			if(player.isPlaying()){
				player.pause();
			}else{
				player.start();
			}
			
		}
		/*
		 *继续
		 */
	}
	
}
