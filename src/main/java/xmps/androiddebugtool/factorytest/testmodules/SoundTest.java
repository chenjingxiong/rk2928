package xmps.androiddebugtool.factorytest.testmodules;


import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;


/**
 * 通过播放音乐测试喇叭。
 * @author enjack
 * */
public class SoundTest {

	private AudioManager manager = null;
	private Context context = null;
	private int current = -1;
	private int mp3res = -1;
	private Activity activity = null;
	private ServiceConnection mConnect = null;
	private PlayTestAudioService serviceObject = null;
	public static int MAX = 15;
	public static int MIN = 0;

	public SoundTest(Activity activity, Context c, int resource){
		this.activity = activity;
		mp3res = resource;
		context = c;
		manager = (AudioManager)c.getSystemService(Context.AUDIO_SERVICE);
		MAX = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		//
		mConnect = new ServiceConnection(){

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				PlayTestAudioService.AudioPlayServiceBinder audionBinder = (PlayTestAudioService.AudioPlayServiceBinder)service;
				serviceObject = audionBinder.getObject();
				serviceObject.createPlayer(mp3res);
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				serviceObject = null;
				mConnect = null;
			}};
		Intent intent = new Intent(this.context, PlayTestAudioService.class);
		this.context.bindService(intent, mConnect , Context.BIND_AUTO_CREATE);
	}

	public void beforeExit(){
		if(null!=mConnect)
			this.context.unbindService(mConnect);
		serviceObject = null;
		mConnect = null;
	}

	public void lower(){
		this.manager.adjustVolume(AudioManager.ADJUST_LOWER, 0);
	}

	public void raise(){
		this.manager.adjustVolume(AudioManager.ADJUST_RAISE, 0);
	}

	public int getMax(){
		MAX = this.manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		return MAX;
	}


	public int get(){
		//this.manager.getMode();
		//this.manager.getRingerMode()
		return this.manager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	public void set(int val){

		this.manager.setStreamVolume(AudioManager.STREAM_MUSIC, val, 0);
	}

	public void play(){
		if(null!=serviceObject){
			serviceObject.play();
		}
	}

	public void pause(){
		if(null!=serviceObject)
			serviceObject.pause();
	}


	public class PlayService extends Service{

		private MediaPlayer player = null;

		public PlayService(){};

		@Override
		public IBinder onBind(Intent intent) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void onCreate() {
			super.onCreate();
			player = MediaPlayer.create(this, mp3res);
			Log.i("", "service create.");
		}

		@Override
		public void onDestroy(){
			super.onDestroy();
			player.release();
			this.stopSelf();
		}

		@Override
		public int onStartCommand(Intent intent, int flags, int startId){
			boolean play = intent.getBooleanExtra("play", false);
			Log.i("", "play test audio:"+play);
			if(play)
				player.start();
			else
				player.pause();

			return super.onStartCommand(intent, flags, startId);
		}

	}
}
