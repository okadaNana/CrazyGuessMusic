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
 * �Զ���GridView
 * 
 * @author owen
 */
public class MyGridView extends GridView {
	
	public final static int COUNTS_WORDS = 24;
	
	// ��ť����
	private List<WordButton> mArrayList;
	// GridView��Adapter
	private MyGridAapter mAdapter;

	private Context mContext;
	private Animation mScaleAnimation;

	private IWordButtonClickListener mWordButtonClickListener;
	
	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// ��ʼ��
		mArrayList = new ArrayList<WordButton>();
		mAdapter = new MyGridAapter();
		mContext = context;
		
		// MyGridView��adapter���й���
		this.setAdapter(mAdapter);
	}
	
	/**
	 * �������ݣ���������adapter
	 */
	public void updateData(List<WordButton> wordButtons) {
		mArrayList = wordButtons;
		
		// ������������Դ
		setAdapter(mAdapter);
	}
	
	/**
	 * �Զ���Adapter
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
				
				// ���ض���
				mScaleAnimation = AnimationUtils.loadAnimation(mContext, R.anim.scale);
				// ���ö����ӳ�ʱ��
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
			// ���Ŷ���
			view.startAnimation(mScaleAnimation);
			
			return view;
		}
		
	}
	
	/**
	 * ע������ӿ�
	 */
	public void registerOnWordButtonClick(IWordButtonClickListener listener) {
		mWordButtonClickListener = listener;
	}
	
}
