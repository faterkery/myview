package cn.tedu.media_player_v4.util;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import cn.tedu.media_player_v4.R;

/**
 * �첽��������ͼƬ�Ĺ�����
 * �Զ�ʵ��ͼƬ�ڴ滺����ļ�����
 */
public class ImageLoader {
	//��������map
	private HashMap<String, SoftReference<Bitmap>> cache = new HashMap<String, SoftReference<Bitmap>>();
	private Context context;
	//�������񼯺�
	private List<ImageLoadTask> tasks = new ArrayList<ImageLoadTask>();
	//����������ѭ���񼯺ϵ����߳�
	private Thread workThread;
	private boolean isLoop = true;
	private ListView listView;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_IMAGE_LOAD_SUCCESS:
				//��imageView����Bitmap
				ImageLoadTask task=(ImageLoadTask) msg.obj;
				ImageView imageView = (ImageView) listView.findViewWithTag(task.path);
				if(imageView != null){
					if(task.bitmap!=null){ //ͼƬ���سɹ�
						imageView.setImageBitmap(task.bitmap);
					}else{ //ͼƬ����ʧ��
						imageView.setImageResource(R.drawable.ic_launcher);
					}
				}
				break;
			}
		}
	};
	public static final int HANDLER_IMAGE_LOAD_SUCCESS=1;
	
	public ImageLoader(Context context, ListView listView) {
		this.context = context;
		this.listView = listView;
		workThread = new Thread(){
			public void run() {
				//������ѭ���񼯺� ����������ж�
				//�� ��ȡ������ִ��ͼƬ��������
				while(isLoop){
					if(!tasks.isEmpty()){ //������������
						ImageLoadTask task = tasks.remove(0);
						Bitmap bitmap = loadBitmap(task.path);
						task.bitmap = bitmap;
						//ͼƬ���سɹ� ��handler����Ϣ
						Message msg = new Message();
						msg.what = HANDLER_IMAGE_LOAD_SUCCESS;
						msg.obj = task;
						handler.sendMessage(msg);
						
					}else{ //������û������
						try {
							//������wait���� �����õ�ǰ�߳̽���ȴ�
							synchronized (workThread) {
								workThread.wait();
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		workThread.start();
	}

	/**
	 * ͨ����ַ ����һ��ͼƬ Bitmap
	 * @param path
	 * @return
	 */
	public Bitmap loadBitmap(String path){
		try {
			InputStream is = HttpUtils.getInputStream(path);
			Bitmap bit=BitmapUtils.loadBitmap(is,50,50);
			//��bit�����ڴ滺��
			SoftReference<Bitmap> ref = new SoftReference<Bitmap>(bit);
			cache.put(path, ref);
			//��bit�����ļ�����
			//ͨ��path  ��ȡ��ͼƬ���ļ���
			//  http://xxxx/xxx/x/x/x/xx/xxx/aaa.jpg
			String filename = path.substring(path.lastIndexOf("/"));
			File file = new File(context.getCacheDir(), "images"+filename);
			BitmapUtils.save(bit, file);
			return bit;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	/** ��ʾͼƬ */
	public void displayImage(ImageView imageView, String path){
		imageView.setTag(path);
		//���ڴ滺���в�ѯ �Ƿ��Ѿ����ع�
		SoftReference<Bitmap> ref = cache.get(path);
		if(ref!=null){ //ԭ�����
			Bitmap b = ref.get(); 
			if(b!=null){ //�����ͼƬ��û�б�����
				Log.i("info", "ͼƬ�Ǵ��ڴ滺���ж�ȡ��...");
				imageView.setImageBitmap(b);
				return;
			}
		}

		//����ڴ滺��û�� ���ѯ�ļ�����
		String filename = path.substring(path.lastIndexOf("/"));
		File file = new File(context.getCacheDir(), "images"+filename);
		Bitmap bitmap=BitmapUtils.loadBitmap(file);
		if(bitmap!=null){ //�ļ�����
			Log.i("info", "ͼƬ�Ǵ��ļ������ж�ȡ��...");
			//���ڴ滺�����ٴ�һ��
			cache.put(path, new SoftReference<Bitmap>(bitmap));
			imageView.setImageBitmap(bitmap);
			return;
		}
		
		//�����񼯺������һ��ͼƬ��������
		ImageLoadTask task = new ImageLoadTask();
		task.path = path;
		tasks.add(task);
		//���񼯺����������� ���ѹ����߳������ɻ�
		synchronized (workThread) {
			workThread.notify();
		}
		
	}
	
	/** ����һ��ͼƬ�������� */
	class ImageLoadTask{
		String path;
		Bitmap bitmap;
	}

	/**
	 * ֹͣ�����߳�
	 */
	public void stopThread() {
		isLoop = false;
		synchronized (workThread) {
			workThread.notify(); //���ѹ����߳�
		}
	}

}




