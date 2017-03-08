package cn.tedu.media_player_v4.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.tedu.media_player_v4.R;
import cn.tedu.media_player_v4.entity.Music;

public class SearchMusicAdapter extends BaseAdapter{
	private Context context;
	private List<Music> musics;
	private LayoutInflater inflater;
	
	public SearchMusicAdapter(Context context, List<Music> musics) {
		this.context = context;
		this.musics = musics;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return musics.size();
	}

	@Override
	public Music getItem(int position) {
		return musics.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.item_lv_search_result	, null);
			holder = new ViewHolder();
			holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
			holder.tvSinger = (TextView) convertView.findViewById(R.id.tvSinger);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		//…Ë÷√øÿº˛ƒ⁄»›
		Music m = getItem(position);
		holder.tvTitle.setText(m.getTitle());
		holder.tvSinger.setText(m.getAuthor());
		return convertView;
	}
	
	class ViewHolder{
		TextView tvTitle;
		TextView tvSinger;
	}
	
}
