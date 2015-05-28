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
 * ����������
 * 
 * @author owen
 */
public class MainActivity extends Activity implements IWordButtonClickListener {

	/** ����ȷ */
	public final static int STATUS_ANSWER_RIGHT = 1;
	/** �𰸴��� */
	public final static int STATUS_ANSWER_WRONG = 2;
	/** �𰸲����� */
	public final static int STATUS_ANSWER_LACK = 3;
	
	// ������˸�Ĵ���
	private final static int SPASH_TIMES = 6;
	
	public final static String TAG = "MainActivity";
	
	// ��Ƭ��ת����
	private Animation mPanAnim;
	private LinearInterpolator mPanLin;
	
	// ���˽��볪Ƭ����
	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;
	
	// �����뿪��Ƭ����
	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;
	
	// ��Ƭ ImageView
	private ImageView mViewPan;
	// ��Ƭ���� ImageView
	private ImageView mViewPanBar;
	// Play��ť ImageButton
	private ImageButton mBtnPlayStart;
	// ��ѡ���ְ�ť MyGridView
	private MyGridView mMyGridView;
	// ���ؽ���
	private LinearLayout mPassView;
	// ����� TextView
	private TextView mViewCurrentCoins;
	// ��ǰ������
	private TextView mCurrentStagePassView;
	
	private TextView mCurrentStageView;
	// ��ǰ��������
	private TextView mCurrentSongNamePassView;

	// ��Ϸ�Ƿ��������еı�־
	private boolean isRunning = false;
	
	// ���ֿ�����
	private List<WordButton> mAllWords;
	// ��ѡ�������
	private List<WordButton> mBtnSelectWords;
	// ��ѡ�����ֿ�UI����
	private LinearLayout mViewWordsContainer;
	
	// ��ǰ�صĸ���
	private Song mCurrentSong;
	// ��ǰ�ص�����
	private int mCurrentStageIndex = -1;
	// ��ǰ����ӵ�еĽ�ҵ�����
	private int mCurrentCoins = Const.TOTAL_COINS;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // ��ȡ��Ϸ����
        int gameData[] = Util.loadGameData(MainActivity.this);
        mCurrentStageIndex = gameData[Const.INDEX_LOAD_DATA_STAGE];
        mCurrentCoins = gameData[Const.INDEX_LOAD_DATA_COINS];
        
        // ��ʼ���ؼ�
        mViewPan = (ImageView) findViewById(R.id.imageView1);
        mViewPanBar = (ImageView) findViewById(R.id.imageView2);
        mViewCurrentCoins = (TextView) findViewById(R.id.text_bar_coins);
        mViewCurrentCoins.setText(mCurrentCoins + "");
        
        // ��ʼ����Ƭ����
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
				// ���������뿪����
				mViewPanBar.startAnimation(mBarOutAnim);
			}
		});
        
        // ��ʼ�����˽��붯��        
        mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_45);
        mBarInLin = new LinearInterpolator();
        mBarInAnim.setFillAfter(true);  // �����������֮�󣬱��ֲ������ʱ��λ��
        mBarInAnim.setInterpolator(mBarInLin);
        mBarInAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// ������Ƭ��ת����
				mViewPan.startAnimation(mPanAnim);
			}
		});
        
        // ��ʼ�������뿪����
        mBarOutAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_d_45);
        mBarOutLin = new LinearInterpolator();
        mBarOutAnim.setInterpolator(mBarOutLin);
        mBarOutAnim.setFillAfter(true);  // �����������֮�󣬱��ֲ������ʱ��λ��
        mBarOutAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// ��Ϸֹͣ��־
				isRunning = false;
				// ���Ű�ť�ɼ�
				mBtnPlayStart.setVisibility(View.VISIBLE);
			}
		});
        
        mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);
        mBtnPlayStart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// ����˲��Ű�ť
				handlePlayButton();
			}
		});
        
        mMyGridView = (MyGridView) findViewById(R.id.gridview);
        // �·����ֿ����ü�������ʹ�ù۲���ģʽ��
        mMyGridView.registerOnWordButtonClick(this);
        mViewWordsContainer = (LinearLayout) findViewById(R.id.word_select_container);
        
        initCurrentStageData();  // ��Ϸ�߼������￪ʼ
        
        handleDeleteWord();
        handleTipAnswer();
    }
    
    /**
     * ���Ʋ��Ű�ť
     */
    private void handlePlayButton() {
    	if (mViewPanBar != null) {
    		if (!isRunning) {
    			// ��Ϸ��ʼ
        		isRunning = true;
        		// �������˽��붯��
        		mViewPanBar.startAnimation(mBarInAnim);
        		// ���Ű�ť���ɼ�
        		mBtnPlayStart.setVisibility(View.INVISIBLE);
        		
        		// ��������
        		MyPlayer.playSong(MainActivity.this, mCurrentSong.getSongFileName());
        	}
    	}
    }
    
    @Override
    protected void onPause() {
    	// ����һ��ϸ�ڣ��������л�����̨��ʱ����Ҫ����һЩ����
    	// ������Ϸ
    	Util.saveGameData(MainActivity.this, mCurrentStageIndex-1, mCurrentCoins);
    	// �������
    	mViewPan.clearAnimation();
    	// ֹͣ����
    	MyPlayer.stopTheSone(MainActivity.this);
    	super.onPause();
    }
    
    /**
     * ��õ�ǰ�ص�����
     */
    private void initCurrentStageData() {
    	// ��ȡ��ǰ�صĸ�����Ϣ
    	mCurrentSong = loadStageSongInfo(++mCurrentStageIndex);
    	// ��ʼ����ѡ���
    	mBtnSelectWords = initWordSelect();

    	// ���ԭ���Ĵ�
    	mViewWordsContainer.removeAllViews();
    	
    	// �����µĴ𰸿�(������ѡ���Ĵ�С������̬��ӵ�GridView��ȥ)
    	LayoutParams params = new LayoutParams(70, 70);
    	for (int i = 0; i < mBtnSelectWords.size(); i++) {
    		mViewWordsContainer.addView(mBtnSelectWords.get(i).getViewButton(), params);
    	}
    	
    	// ��ʾ��ǰ�ص�����
    	mCurrentStageView = (TextView) findViewById(R.id.text_current_stage);
    	mCurrentStageView.setText("" + (mCurrentStageIndex + 1));
    	
    	// ��ȡ����
    	mAllWords = initAllWord();
    	// �������� - MyGridView
    	mMyGridView.updateData(mAllWords);
    	
    	// ��������
    	handlePlayButton();
    }
    
    /**
     * ��ȡ��ǰ�صĸ�����Ϣ
     */
    private Song loadStageSongInfo(int stageIndex) {
    	Song song = new Song();
    	
    	String[] stage = Const.SONG_INFO[stageIndex];
    	song.setSongFileName(stage[Const.INDEX_SONG_FILE_NAME]);
    	song.setSongName(stage[Const.INDEX_SONG_NAME]);
    	
    	return song;
    }
    
    /**
     * ��ʼ����ѡ�����ֿ�
     */
    private List<WordButton> initWordSelect() {
    	List<WordButton> data = new ArrayList<WordButton>();
    	
    	// ���������м����ַ��������ü�����ѡ�����ֿ�
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
     * ��ʼ����ѡ���ֿ��ܹ�24����ť
     */
    private List<WordButton> initAllWord() {
    	// ������д�ѡ����
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
		
		// ����
		int resultCode = checkTheAnswer();
		switch (resultCode) {
			case STATUS_ANSWER_LACK:
				// ��ɫ�ظ����������֮ǰ����˸���ֵĻ���
				for (int i = 0; i < mBtnSelectWords.size(); i++) {
					mBtnSelectWords.get(i).getViewButton().setTextColor(Color.WHITE);
				}
				break;
			case STATUS_ANSWER_WRONG:
				// ��˸���ֲ���ʾ�û�
				sparkTheWords();
				break;
			case STATUS_ANSWER_RIGHT:
				// ���ز�����
				handlePassEvent();
				break;
			default:
				break;
		}
	}
    
	/**
	 * ������صĽ��漰�¼�
	 */
	private void handlePassEvent() {
		// ��ʾ���ؽ���
		mPassView = (LinearLayout) findViewById(R.id.pass_view);
		mPassView.setVisibility(View.VISIBLE);

		// ֹͣ����
		mViewPan.clearAnimation();
		// ֹͣ���ڲ��ŵ�����
		MyPlayer.stopTheSone(MainActivity.this);
		// ���Ž����Ч
		MyPlayer.playTone(MainActivity.this, MyPlayer.INDEX_STONE_COIN);
	
		// ��ǰ�ص�����
		mCurrentStagePassView = (TextView) findViewById(R.id.text_current_stage_pass);
		mCurrentStagePassView.setText("" + (mCurrentStageIndex + 1));  // ��Ϊ��������ԭ�����������Ǵ�0��ʼ��
		// ע�⣺textView.setText(arg) �еĲ��� arg ������int���ͣ���Ϊ���Ƿ��������أ�int���͵Ĳ���������ԴID
		
		// ��ʾ��������
		mCurrentSongNamePassView = (TextView) findViewById(R.id.text_current_song_name_pass);
		mCurrentSongNamePassView.setText(mCurrentSong.getSongName());
		
		// ��һ�ذ���������һ�����ͨ�أ�
		ImageButton btnNext = (ImageButton) findViewById(R.id.btn_next);
		btnNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (judgeAppPassed()) {
					// ����ͨ�ؽ���
					Util.startActivity(MainActivity.this, AllPassView.class);
				} else {
					// ��ʼ�µ�һ��
					mPassView.setVisibility(View.GONE);
					initCurrentStageData();
				}
			}
		});
	}
	
	/**
	 * �ж��Ƿ�ͨ��
	 * @return
	 */
	private boolean judgeAppPassed() {
		return (mCurrentStageIndex == (Const.SONG_INFO.length - 1));
	}
	
	/**
	 * �����:
	 * �û���ʱ�������ѡ���е�һ����ť
	 */
	private void clearTheAnswer(WordButton wordButton) {
		wordButton.getViewButton().setText("");
		wordButton.setWordString("");
		wordButton.setIsVisible(false);
		
		setButtonVisible(mAllWords.get(wordButton.getIndex()), View.VISIBLE);
	}
	
	/**
	 * ���ô𰸣�
	 * �û���ʱ�����gridview�е�һ����ť����Ҫ�������ť���õ���ѡ���ֿ���ȥ
	 */
	private void setSelectWord(WordButton wordButton) {
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).getWordString().length() == 0) {
				// �������ֿ����ݼ��ɼ���
				mBtnSelectWords.get(i).getViewButton().setText(wordButton.getWordString());
				mBtnSelectWords.get(i).setIsVisible(true);
				mBtnSelectWords.get(i).setWordString(wordButton.getWordString());
				// ��¼����
				mBtnSelectWords.get(i).setIndex(wordButton.getIndex());
				// LOG 
				MyLog.i(TAG, mBtnSelectWords.get(i).getIndex() + "");
				
				// ���ô�ѡ��ɼ���
				setButtonVisible(wordButton, View.INVISIBLE);				
				
				break;
			}
		}
	}
	
	/**
	 * ���ô�ѡ���Ƿ�ɼ�
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
	 * �������д�ѡ����
	 */
	public String[] generateWords() {
		String[] words = new String[MyGridView.COUNTS_WORDS];
		
		// �������
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			words[i] = mCurrentSong.getNameCharacters()[i] + "";
		}
		
		// ��ȡ������֣�����������
		for (int i = mCurrentSong.getNameLength(); i < MyGridView.COUNTS_WORDS; i++) {
			words[i] = getRandomChar() + "";
		}
		
		// ��������˳��
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
	 * �����������
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
	 * ����
	 */
	private int checkTheAnswer() {
		// ���𰸵�������
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).getWordString().length() == 0) {
				return STATUS_ANSWER_LACK;
			}
		}
		
		// ���������ģ����ż����ȷ���
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			sb.append(mBtnSelectWords.get(i).getWordString());
		}
		
		return sb.toString().equals(mCurrentSong.getSongName()) ? STATUS_ANSWER_RIGHT : STATUS_ANSWER_WRONG;
	}
	
	/**
	 * ��˸����
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
						
						// ִ����˸���߼�����׽���
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
	 * �Զ�ѡ��һ����
	 */
	private void tipAnswer() {
		boolean tipWord = false;
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).getWordString().length() == 0) {
				// ���ݴ𰸿�����������Ӧ������
				onWordButtonClick(findIsAnswerWord(i));

				tipWord = true;
				
				// ���ٽ������
				if (!handleCoins(-getTipWordCoins())) {
					// ��Ҳ�������ʾ�Ի���
					showConfirmDialog(ID_DIALOG_LACK_COINS);
					return;
				}
				
				break;
			}
		}
		
		// û�еط���д��
		if (!tipWord) {
			// ��˸������ʾ�û�
			sparkTheWords();
		}
	}
	
	/**
	 * ɾ������
	 */
	private void deleteOneWord() {
		// ���ٽ��
		if (!handleCoins(-getDeleteWordCoins())) {
			// ��Ҳ�������ʾ�Ի���
			showConfirmDialog(ID_DIALOG_LACK_COINS);
			return;
		}
		
		// ��ȥ��׼�����⣬����ĳ��������Ӧ��WordButton���ɼ�
		setButtonVisible(findNotAnswerWord(), View.INVISIBLE);
	}
	
	/**
	 * �ҵ�һ��������
	 * @param index ��Ҫ����𰸿������
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
	 * �ҵ�һ�����Ǵ�,�����ǿɼ��ĵ�����
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
	 * �ҵ����пɼ��ģ��Ҳ��Ǵ𰸵İ�ť�б�
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
	 * �ж������Ƿ��Ǵ𰸵�һ����
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
	 * �������ӻ����ָ�������Ľ��
	 * 
	 * @return true����/���ٳɹ��� false����/����ʧ��
	 */
	private boolean handleCoins(int data) {
		if (mCurrentCoins + data >= 0) {

			// TODO �����Ժ���˵
			/*
			 *  �Ҳ����ɼ��ġ����Ǵ𰸵İ�ť��˵��ֻʣ����ȷ���ˣ�
			 *  ��ʱ���û��ٰ��°�ť�����ܿ۳����
			 
			if (findVisibleWordButton().size() == 0) {
				return false;
			}
			*/
			mCurrentCoins += data;
			
			// ����TextView
			mViewCurrentCoins.setText(mCurrentCoins + "");
			
			return true;
		} else {
			// ��Ҳ���
			return false;
		}
	}
	
	/**
	 * ��ȡ�����ļ����õ�ɾ������Ҫ�Ľ����Ŀ
	 */
	private int getDeleteWordCoins() {
		return getResources().getInteger(R.integer.pay_delete_answer);
	}
	
	/**
	 * ��ȡ�����ļ����õ���ʾ����Ҫ�Ľ����Ŀ
	 */
	private int getTipWordCoins() {
		return getResources().getInteger(R.integer.pay_tip_answer);
	}
	
	/**
	 * ����ɾ����ѡ�����¼�
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
	 * ������ʾ�¼�
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
	
	// �Զ���Dialog�¼���Ӧ
	// ɾ�������
	private IAlertDialogButtonListener mBtnOkDeleteWordListener = new IAlertDialogButtonListener() {
		
		@Override
		public void onClick() {
			// ִ���¼�
			deleteOneWord();
		}
	};
	
	// ��ʾ��
	private IAlertDialogButtonListener mBtnOkTipWordListener = new IAlertDialogButtonListener() {
		
		@Override
		public void onClick() {
			// ִ���¼�
			tipAnswer();
		}
	};
	
	// ��Ҳ���
	private IAlertDialogButtonListener mBtnOkLackCoinsListener = new IAlertDialogButtonListener() {
		
		@Override
		public void onClick() {
			// ִ���¼�
			// TODO
		}
	};
	

	private static final int ID_DIALOG_DELETE_WORD = 1;
	private static final int ID_DIALOG_TIP_ANSWER = 2;
	private static final int ID_DIALOG_LACK_COINS = 3;
	
	/**
	 * ��ʾ�Ի���
	 */
	private void showConfirmDialog(int code) {
		switch (code) {
		case ID_DIALOG_DELETE_WORD:
			Util.showDialog(MainActivity.this, "ȷ�ϻ���" + getDeleteWordCoins() + "�����ȥ��һ������𰸣�", mBtnOkDeleteWordListener);
			break;
		case ID_DIALOG_TIP_ANSWER:
			Util.showDialog(MainActivity.this, "ȷ�ϻ���" + getTipWordCoins() + "����һ��һ��������ʾ��", mBtnOkTipWordListener);
			break;
		case ID_DIALOG_LACK_COINS:
			Util.showDialog(MainActivity.this, "��Ҳ��㣬ȥ�̵겹�䣿", mBtnOkLackCoinsListener);
			break;
		default:
			break;
		}
	}
	
}