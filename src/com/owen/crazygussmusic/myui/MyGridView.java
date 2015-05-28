package com.owen.crazygussmusic.myui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.owen.crazygussmusic.R;
import com.owen.crazygussmusic.model.IWordButtonClickListener;
import com.owen.crazygussmusic.model.WordButton;
import com.owen.crazygussmusic.util.Util;

/**
 * 自定义GridView
 * 
 * @author owen
 */
public class MyGridView extends GridView {
	
	public final static int COUNTS_WORDS = 24;
	
	// 按钮容器
	private List<WordButton> mArrayList;
	// GridView的Adapter
	private MyGridAapter mAdapter;

	private Context mContext;
	private Animation mScaleAnimation;

	private IWordButtonClickListener mWordButtonClickListener;
	
	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// 初始化
		mArrayList = new ArrayList<WordButton>();
		mAdapter = new MyGridAapter();
		mContext = context;
		
		// MyGridView与adapter进行关联
		this.setAdapter(mAdapter);
	}
	
	/**
	 * 更新数据，重新设置adapter
	 */
	public void updateData(List<WordButton> wordButtons) {
		mArrayList = wordButtons;
		
		// 重新设置数据源
		setAdapter(mAdapter);
	}
	
	/**
	 * 自定义Adapter
	 * 
	 * @author owen
	 */
	class MyGridAapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mArrayList.size();
		}

		@Override
		public Object getItem(int position) {
			return mArrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			final WordButton holder;
			
			if (view == null) {
				view = Util.getView(mContext, R.layout.self_ui_gridview_item);
				
				// 加载动画
				mScaleAnimation = AnimationUtils.loadAnimation(mContext, R.anim.scale);
				// 设置动画延迟时间
				mScaleAnimation.setStartOffset(position * 100);
				
				holder = (WordButton) getItem(position);
				holder.setIndex(position);
				if (holder.getViewButton() == null) {
					holder.setViewButton((Button) view.findViewById(R.id.item_btn));
					holder.getViewButton().setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							mWordButtonClickListener.onWordButtonClick(holder);
						}
					});
				}
				
				view.setTag(holder);
			} else {
				holder = (WordButton) view.getTag();
			}
			
			holder.getViewButton().setText(holder.getWordString());
			// 播放动画
			view.startAnimation(mScaleAnimation);
			
			return view;
		}
		
	}
	
	/**
	 * 注册监听接口
	 */
	public void registerOnWordButtonClick(IWordButtonClickListener listener) {
		mWordButtonClickListener = listener;
	}
	
}
