package com.owen.crazygussmusic.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.owen.crazygussmusic.R;
import com.owen.crazygussmusic.data.Const;
import com.owen.crazygussmusic.model.IAlertDialogButtonListener;
import com.owen.crazygussmusic.model.IWordButtonClickListener;
import com.owen.crazygussmusic.model.Song;
import com.owen.crazygussmusic.model.WordButton;
import com.owen.crazygussmusic.myui.MyGridView;
import com.owen.crazygussmusic.util.MyLog;
import com.owen.crazygussmusic.util.MyPlayer;
import com.owen.crazygussmusic.util.Util;

/**
 * 程序主界面
 * 
 * @author owen
 */
public class MainActivity extends Activity implements IWordButtonClickListener {

	/** 答案正确 */
	public final static int STATUS_ANSWER_RIGHT = 1;
	/** 答案错误 */
	public final static int STATUS_ANSWER_WRONG = 2;
	/** 答案不完整 */
	public final static int STATUS_ANSWER_LACK = 3;
	
	// 文字闪烁的次数
	private final static int SPASH_TIMES = 6;
	
	public final static String TAG = "MainActivity";
	
	// 唱片旋转动画
	private Animation mPanAnim;
	private LinearInterpolator mPanLin;
	
	// 拨杆进入唱片动画
	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;
	
	// 拨杆离开唱片动画
	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;
	
	// 唱片 ImageView
	private ImageView mViewPan;
	// 唱片拨杆 ImageView
	private ImageView mViewPanBar;
	// Play按钮 ImageButton
	private ImageButton mBtnPlayStart;
	// 待选文字按钮 MyGridView
	private MyGridView mMyGridView;
	// 过关界面
	private LinearLayout mPassView;
	// 金币数 TextView
	private TextView mViewCurrentCoins;
	// 当前关索引
	private TextView mCurrentStagePassView;
	
	private TextView mCurrentStageView;
	// 当前歌曲名称
	private TextView mCurrentSongNamePassView;

	// 游戏是否正在运行的标志
	private boolean isRunning = false;
	
	// 文字框容器
	private List<WordButton> mAllWords;
	// 待选择框容器
	private List<WordButton> mBtnSelectWords;
	// 已选择文字框UI容器
	private LinearLayout mViewWordsContainer;
	
	// 当前关的歌曲
	private Song mCurrentSong;
	// 当前关的索引
	private int mCurrentStageIndex = -1;
	// 当前手里拥有的金币的数量
	private int mCurrentCoins = Const.TOTAL_COINS;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 读取游戏数据
        int gameData[] = Util.loadGameData(MainActivity.this);
        mCurrentStageIndex = gameData[Const.INDEX_LOAD_DATA_STAGE];
        mCurrentCoins = gameData[Const.INDEX_LOAD_DATA_COINS];
        
        // 初始化控件
        mViewPan = (ImageView) findViewById(R.id.imageView1);
        mViewPanBar = (ImageView) findViewById(R.id.imageView2);
        mViewCurrentCoins = (TextView) findViewById(R.id.text_bar_coins);
        mViewCurrentCoins.setText(mCurrentCoins + "");
        
        // 初始化盘片动画
        mPanAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        mPanLin = new LinearInterpolator();
        mPanAnim.setInterpolator(mPanLin);
        mPanAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// 启动拨杆离开动画
				mViewPanBar.startAnimation(mBarOutAnim);
			}
		});
        
        // 初始化拨杆进入动画        
        mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_45);
        mBarInLin = new LinearInterpolator();
        mBarInAnim.setFillAfter(true);  // 动画播放完毕之后，保持播放完毕时的位置
        mBarInAnim.setInterpolator(mBarInLin);
        mBarInAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// 启动盘片旋转动画
				mViewPan.startAnimation(mPanAnim);
			}
		});
        
        // 初始化拨杆离开动画
        mBarOutAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_d_45);
        mBarOutLin = new LinearInterpolator();
        mBarOutAnim.setInterpolator(mBarOutLin);
        mBarOutAnim.setFillAfter(true);  // 动画播放完毕之后，保持播放完毕时的位置
        mBarOutAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// 游戏停止标志
				isRunning = false;
				// 播放按钮可见
				mBtnPlayStart.setVisibility(View.VISIBLE);
			}
		});
        
        mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);
        mBtnPlayStart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 点击了播放按钮
				handlePlayButton();
			}
		});
        
        mMyGridView = (MyGridView) findViewById(R.id.gridview);
        // 下方文字框设置监听器（使用观察者模式）
        mMyGridView.registerOnWordButtonClick(this);
        mViewWordsContainer = (LinearLayout) findViewById(R.id.word_select_container);
        
        initCurrentStageData();  // 游戏逻辑从这里开始
        
        handleDeleteWord();
        handleTipAnswer();
    }
    
    /**
     * 控制播放按钮
     */
    private void handlePlayButton() {
    	if (mViewPanBar != null) {
    		if (!isRunning) {
    			// 游戏开始
        		isRunning = true;
        		// 启动拨杆进入动画
        		mViewPanBar.startAnimation(mBarInAnim);
        		// 播放按钮不可见
        		mBtnPlayStart.setVisibility(View.INVISIBLE);
        		
        		// 播放音乐
        		MyPlayer.playSong(MainActivity.this, mCurrentSong.getSongFileName());
        	}
    	}
    }
    
    @Override
    protected void onPause() {
    	// 这是一个细节，当程序切换到后台的时候需要进行一些操作
    	// 保存游戏
    	Util.saveGameData(MainActivity.this, mCurrentStageIndex-1, mCurrentCoins);
    	// 清除动画
    	mViewPan.clearAnimation();
    	// 停止音乐
    	MyPlayer.stopTheSone(MainActivity.this);
    	super.onPause();
    }
    
    /**
     * 获得当前关的数据
     */
    private void initCurrentStageData() {
    	// 读取当前关的歌曲信息
    	mCurrentSong = loadStageSongInfo(++mCurrentStageIndex);
    	// 初始化已选择框
    	mBtnSelectWords = initWordSelect();

    	// 清空原来的答案
    	mViewWordsContainer.removeAllViews();
    	
    	// 增加新的答案框(设置已选择框的大小，并动态添加到GridView中去)
    	LayoutParams params = new LayoutParams(70, 70);
    	for (int i = 0; i < mBtnSelectWords.size(); i++) {
    		mViewWordsContainer.addView(mBtnSelectWords.get(i).getViewButton(), params);
    	}
    	
    	// 显示当前关的索引
    	mCurrentStageView = (TextView) findViewById(R.id.text_current_stage);
    	mCurrentStageView.setText("" + (mCurrentStageIndex + 1));
    	
    	// 获取数据
    	mAllWords = initAllWord();
    	// 更新数据 - MyGridView
    	mMyGridView.updateData(mAllWords);
    	
    	// 播放音乐
    	handlePlayButton();
    }
    
    /**
     * 读取当前关的歌曲信息
     */
    private Song loadStageSongInfo(int stageIndex) {
    	Song song = new Song();
    	
    	String[] stage = Const.SONG_INFO[stageIndex];
    	song.setSongFileName(stage[Const.INDEX_SONG_FILE_NAME]);
    	song.setSongName(stage[Const.INDEX_SONG_NAME]);
    	
    	return song;
    }
    
    /**
     * 初始化已选择文字框
     */
    private List<WordButton> initWordSelect() {
    	List<WordButton> data = new ArrayList<WordButton>();
    	
    	// 歌曲名称有几个字符，就设置几个已选择文字框
    	for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
    		View view = Util.getView(MainActivity.this, R.layout.self_ui_gridview_item);
    		
    		final WordButton holder = new WordButton();
    		holder.setViewButton((Button) view.findViewById(R.id.item_btn));
    		holder.getViewButton().setTextColor(Color.WHITE);
    		holder.getViewButton().setText("");
    		holder.setIsVisible(false);
    		holder.getViewButton().setBackgroundResource(R.drawable.game_wordblank);
    		holder.getViewButton().setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					clearTheAnswer(holder);
				}
			});
    		
    		data.add(holder);
    	}
    	
    	return data;
    }
    
    /**
     * 初始化待选文字框，总共24个按钮
     */
    private List<WordButton> initAllWord() {
    	// 获得所有待选数据
    	String[] words = generateWords();
    	
    	List<WordButton> data = new ArrayList<WordButton>();
    	for (int i = 0; i < MyGridView.COUNTS_WORDS; i++) {
    		WordButton wordButton = new WordButton();

    		wordButton.setWordString(words[i]);
    		
    		data.add(wordButton);
    	}
    	return data;
    }
    

	@Override
	public void onWordButtonClick(WordButton wordButton) {
		setSelectWord(wordButton);
		
		// 检查答案
		int resultCode = checkTheAnswer();
		switch (resultCode) {
			case STATUS_ANSWER_LACK:
				// 颜色回复正常（如果之前有闪烁文字的话）
				for (int i = 0; i < mBtnSelectWords.size(); i++) {
					mBtnSelectWords.get(i).getViewButton().setTextColor(Color.WHITE);
				}
				break;
			case STATUS_ANSWER_WRONG:
				// 闪烁文字并提示用户
				sparkTheWords();
				break;
			case STATUS_ANSWER_RIGHT:
				// 过关并奖励
				handlePassEvent();
				break;
			default:
				break;
		}
	}
    
	/**
	 * 处理过关的界面及事件
	 */
	private void handlePassEvent() {
		// 显示过关界面
		mPassView = (LinearLayout) findViewById(R.id.pass_view);
		mPassView.setVisibility(View.VISIBLE);

		// 停止动画
		mViewPan.clearAnimation();
		// 停止正在播放的音乐
		MyPlayer.stopTheSone(MainActivity.this);
		// 播放金币音效
		MyPlayer.playTone(MainActivity.this, MyPlayer.INDEX_STONE_COIN);
	
		// 当前关的索引
		mCurrentStagePassView = (TextView) findViewById(R.id.text_current_stage_pass);
		mCurrentStagePassView.setText("" + (mCurrentStageIndex + 1));  // 因为歌词数组的原因，所以索引是从0开始的
		// 注意：textView.setText(arg) 中的参数 arg 不能是int类型，因为这是方法的重载，int类型的参数代表资源ID
		
		// 显示歌曲名称
		mCurrentSongNamePassView = (TextView) findViewById(R.id.text_current_song_name_pass);
		mCurrentSongNamePassView.setText(mCurrentSong.getSongName());
		
		// 下一关按键处理（下一题或者通关）
		ImageButton btnNext = (ImageButton) findViewById(R.id.btn_next);
		btnNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (judgeAppPassed()) {
					// 进入通关界面
					Util.startActivity(MainActivity.this, AllPassView.class);
				} else {
					// 开始新的一关
					mPassView.setVisibility(View.GONE);
					initCurrentStageData();
				}
			}
		});
	}
	
	/**
	 * 判断是否通关
	 * @return
	 */
	private boolean judgeAppPassed() {
		return (mCurrentStageIndex == (Const.SONG_INFO.length - 1));
	}
	
	/**
	 * 清除答案:
	 * 用户这时点击了已选框中的一个按钮
	 */
	private void clearTheAnswer(WordButton wordButton) {
		wordButton.getViewButton().setText("");
		wordButton.setWordString("");
		wordButton.setIsVisible(false);
		
		setButtonVisible(mAllWords.get(wordButton.getIndex()), View.VISIBLE);
	}
	
	/**
	 * 设置答案：
	 * 用户这时点击了gridview中的一个按钮，需要将这个按钮设置掉已选文字框中去
	 */
	private void setSelectWord(WordButton wordButton) {
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).getWordString().length() == 0) {
				// 设置文字块内容及可见性
				mBtnSelectWords.get(i).getViewButton().setText(wordButton.getWordString());
				mBtnSelectWords.get(i).setIsVisible(true);
				mBtnSelectWords.get(i).setWordString(wordButton.getWordString());
				// 记录索引
				mBtnSelectWords.get(i).setIndex(wordButton.getIndex());
				// LOG 
				MyLog.i(TAG, mBtnSelectWords.get(i).getIndex() + "");
				
				// 设置待选框可见性
				setButtonVisible(wordButton, View.INVISIBLE);				
				
				break;
			}
		}
	}
	
	/**
	 * 设置待选框是否可见
	 */
	private void setButtonVisible(WordButton wordButton, int visibility) {
		if (wordButton == null) {
			return;
		}
		
		wordButton.getViewButton().setVisibility(visibility);
		wordButton.setIsVisible(visibility == View.VISIBLE ? true : false);
		
		MyLog.d(TAG, wordButton.isIsVisible() + "");
	}
	
	/**
	 * 生成所有待选文字
	 */
	public String[] generateWords() {
		String[] words = new String[MyGridView.COUNTS_WORDS];
		
		// 存入歌名
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			words[i] = mCurrentSong.getNameCharacters()[i] + "";
		}
		
		// 获取随机文字，存入数组中
		for (int i = mCurrentSong.getNameLength(); i < MyGridView.COUNTS_WORDS; i++) {
			words[i] = getRandomChar() + "";
		}
		
		// 打乱文字顺序
		Random random = new Random();
		for (int i = MyGridView.COUNTS_WORDS - 1; i >= 0 ; i--) {
			int index = random.nextInt(i + 1);
			
			String buf = words[index];
			words[index] = words[i];
			words[i] = buf;
		}
		
		return words;
	}
	
	/**
	 * 生成随机汉字
	 */
	public char getRandomChar() {
		String str = "";
		int highPos;
		int lowPos;
		
		Random random = new Random();
		highPos = (176 + Math.abs(random.nextInt(39)));
		lowPos = (161 + Math.abs(random.nextInt(93)));
		
		byte[] b = new byte[2];
		b[0] = (Integer.valueOf(highPos)).byteValue();
		b[1] = (Integer.valueOf(lowPos)).byteValue();
		
		try {
			str = new String(b, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return str.charAt(0);
	}
	
	/**
	 * 检查答案
	 */
	private int checkTheAnswer() {
		// 检测答案的完整性
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).getWordString().length() == 0) {
				return STATUS_ANSWER_LACK;
			}
		}
		
		// 答案是完整的，接着检测正确与否
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			sb.append(mBtnSelectWords.get(i).getWordString());
		}
		
		return sb.toString().equals(mCurrentSong.getSongName()) ? STATUS_ANSWER_RIGHT : STATUS_ANSWER_WRONG;
	}
	
	/**
	 * 闪烁文字
	 */
	private void sparkTheWords() {
		TimerTask task = new TimerTask() {
			int mSpardTime = 0;
			boolean mChange = false;
			
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						if (++mSpardTime > SPASH_TIMES) {
							return;
						}
						
						// 执行闪烁的逻辑，红白交替
						for (int i = 0; i < mBtnSelectWords.size(); i++) {
							mBtnSelectWords.get(i).getViewButton().setTextColor(
									mChange ? Color.RED : Color.WHITE);
						}
						
						mChange = !mChange;
					}
				});
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(task, 1, 150);
	}
	
	/**
	 * 自动选择一个答案
	 */
	private void tipAnswer() {
		boolean tipWord = false;
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).getWordString().length() == 0) {
				// 根据答案框的条件填入对应的文字
				onWordButtonClick(findIsAnswerWord(i));

				tipWord = true;
				
				// 减少金币数量
				if (!handleCoins(-getTipWordCoins())) {
					// 金币不够，显示对话框
					showConfirmDialog(ID_DIALOG_LACK_COINS);
					return;
				}
				
				break;
			}
		}
		
		// 没有地方填写答案
		if (!tipWord) {
			// 闪烁文字提示用户
			sparkTheWords();
		}
	}
	
	/**
	 * 删除文字
	 */
	private void deleteOneWord() {
		// 减少金币
		if (!handleCoins(-getDeleteWordCoins())) {
			// 金币不够，提示对话框
			showConfirmDialog(ID_DIALOG_LACK_COINS);
			return;
		}
		
		// 除去标准答案以外，设置某个索引对应的WordButton不可见
		setButtonVisible(findNotAnswerWord(), View.INVISIBLE);
	}
	
	/**
	 * 找到一个答案文字
	 * @param index 需要填入答案框的索引
	 */
	private WordButton findIsAnswerWord(int index) {
		WordButton buf = null;
		
		for (int i = 0; i < MyGridView.COUNTS_WORDS; i++) {
			buf = mAllWords.get(i);
			
			if (buf.getWordString().equals("" + mCurrentSong.getNameCharacters()[index])) {
				return buf;
			}
		}
		
		return null;
	}
	
	/**
	 * 找到一个不是答案,并且是可见的的文字
	 */
	private WordButton findNotAnswerWord() {
		Random random = new Random();
		WordButton buf = null;
		
		/*
		while (true) {
			int index = random.nextInt(MyGridView.COUNTS_WORDS);
			buf = mAllWords.get(index);
			
			if (buf.isIsVisible() && !isTheAnswerWord(buf)) {
				return buf;
			}
		}
		*/
		List<WordButton> btns = findVisibleWordButton();
		if (btns.size() == 0) {
			buf = null;
		} else {
			buf = btns.get(random.nextInt(btns.size()));
		}
		
		return buf;
	}
	
	/**
	 * 找到所有可见的，且不是答案的按钮列表
	 */
	private List<WordButton> findVisibleWordButton() {
		List<WordButton> wordBtns = new ArrayList<WordButton>();
		
		for (WordButton btn : mAllWords) {
			if (btn.isIsVisible() && !isTheAnswerWord(btn)) {
				wordBtns.add(btn);
			}
		}
		
		return wordBtns;
	}
	
	/**
	 * 判断文字是否是答案的一部分
	 */
	private boolean isTheAnswerWord(WordButton wordButton) {
		boolean result = false;
		
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			if (wordButton.getWordString().equals("" + mCurrentSong.getNameCharacters()[i])) {
				result = true;
				
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * 处理增加或减少指定数量的金币
	 * 
	 * @return true增加/减少成功， false增加/减少失败
	 */
	private boolean handleCoins(int data) {
		if (mCurrentCoins + data >= 0) {

			// TODO 这里以后再说
			/*
			 *  找不到可见的、不是答案的按钮，说明只剩下正确答案了，
			 *  这时候用户再按下按钮，不能扣除金币
			 
			if (findVisibleWordButton().size() == 0) {
				return false;
			}
			*/
			mCurrentCoins += data;
			
			// 更新TextView
			mViewCurrentCoins.setText(mCurrentCoins + "");
			
			return true;
		} else {
			// 金币不够
			return false;
		}
	}
	
	/**
	 * 读取配置文件，得到删除答案需要的金币数目
	 */
	private int getDeleteWordCoins() {
		return getResources().getInteger(R.integer.pay_delete_answer);
	}
	
	/**
	 * 读取配置文件，得到提示答案需要的金币数目
	 */
	private int getTipWordCoins() {
		return getResources().getInteger(R.integer.pay_tip_answer);
	}
	
	/**
	 * 处理删除待选文字事件
	 */
	private void handleDeleteWord() {
		ImageButton btnDelete = (ImageButton) findViewById(R.id.btn_delete_word);
		btnDelete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// deleteOneWord();
				showConfirmDialog(ID_DIALOG_DELETE_WORD);
			}
		});
	}
	
	/**
	 * 处理提示事件
	 */
	private void handleTipAnswer() {
		ImageButton btnTip = (ImageButton) findViewById(R.id.btn_tip_answer);
		btnTip.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//tipAnswer();
				showConfirmDialog(ID_DIALOG_TIP_ANSWER);
			}
		});
	}
	
	// 自定义Dialog事件响应
	// 删除错误答案
	private IAlertDialogButtonListener mBtnOkDeleteWordListener = new IAlertDialogButtonListener() {
		
		@Override
		public void onClick() {
			// 执行事件
			deleteOneWord();
		}
	};
	
	// 提示答案
	private IAlertDialogButtonListener mBtnOkTipWordListener = new IAlertDialogButtonListener() {
		
		@Override
		public void onClick() {
			// 执行事件
			tipAnswer();
		}
	};
	
	// 金币不足
	private IAlertDialogButtonListener mBtnOkLackCoinsListener = new IAlertDialogButtonListener() {
		
		@Override
		public void onClick() {
			// 执行事件
			// TODO
		}
	};
	

	private static final int ID_DIALOG_DELETE_WORD = 1;
	private static final int ID_DIALOG_TIP_ANSWER = 2;
	private static final int ID_DIALOG_LACK_COINS = 3;
	
	/**
	 * 显示对话框
	 */
	private void showConfirmDialog(int code) {
		switch (code) {
		case ID_DIALOG_DELETE_WORD:
			Util.showDialog(MainActivity.this, "确认花掉" + getDeleteWordCoins() + "个金币去掉一个错误答案？", mBtnOkDeleteWordListener);
			break;
		case ID_DIALOG_TIP_ANSWER:
			Util.showDialog(MainActivity.this, "确认花掉" + getTipWordCoins() + "个金币获得一个文字提示？", mBtnOkTipWordListener);
			break;
		case ID_DIALOG_LACK_COINS:
			Util.showDialog(MainActivity.this, "金币不足，去商店补充？", mBtnOkLackCoinsListener);
			break;
		default:
			break;
		}
	}
	
}