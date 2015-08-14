package xmps.androiddebugtool.factorytest.testmodules;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class PlayTestAudioService extends Service{
	private final String tag = "<PlayTestAudioService>";
	private MediaPlayer player = null;
	private IBinder binder = new AudioPlayServiceBinder();
	
	public class AudioPlayServiceBinder extends Binder{
		public PlayTestAudioService getObject(){
			return PlayTestAudioService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return binder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		logout("service created");
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(null!=player)
			player.release();
		//this.stopSelf();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		boolean play = intent.getBooleanExtra("play", false);
		if(play)
			player.start();
		else
			player.pause();
		
		return super.onStartCommand(intent, flags, startId); 
	}
	
	
	public void createPlayer(int audioResource){
		player = MediaPlayer.create(this, audioResource);
		player.setLooping(true);
		Log.i("", "player created");
	}
	
	public void play(){
		if(null!=player)
			player.start();
	}
	
	public void pause(){
		if(null!=player && player.isPlaying())
			player.pause();
	}
	
	private void logout(String logstr){
		Log.i(tag, logstr);
	}
}

