import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class PlayThread implements Runnable {
	private String filePath; // 音乐文件的路径
	private Player player;  // 播放音乐的播放器
	private int totalFrame;  // 音乐文件的总帧数
	private Thread playerThread;  // 执行播放音乐的线程
	private boolean complete;  // 判断当前音乐是否播放完毕
	
	/*----------------初始化参数-------------*/
	public PlayThread(String filePath) {
		this.filePath = filePath;
		this.playerInitialize();
		this.totalFrame = player.getTotalFrame();
	}
	private void playerInitialize() {
   /*------------将播放路径加入到播放器MyPlayer类对象中------*/
		try {
			String urlAsString = "file:///"
					+ filePath;
			this.player = new Player(new java.net.URL(urlAsString));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void setFilePath() {
		
	}
  /*--------------执行播放的方法---------------*/
	private void play() {
    // 当歌曲播放时，整个播放执行处于一个阻塞状态，所以为了是软件能够
    //继续运行，播放动作需要在另外一个独立的线程中运行。
   // 这里创建了一个播放线程，并将类对象自己加入其中，然后启动线程
		this.playerThread = new Thread(this, "AudioPlayerThread");
		this.playerThread.start();
	}
	
	public void play(int from, int to) {
		try {
			player.play(from, to, 0);
		} catch (JavaLayerException e) {
			e.printStackTrace();
		}
	}
	
	public void playAt(int frameIndexStart) {
		this.close();
		//System.out.println(frameIndexStart);
		setFrameIndexCurrent(frameIndexStart);
		this.play();
	}
	
	public void playAt(double percent) {
//		System.out.println("total : " + getTotalFrame());
		playAt((int)(getTotalFrame()*percent));
	}

	/*---------------- 执行暂停功能的方法-------------*/
	private void pause() {
		this.player.pause();      // 让播放器player暂停
		this.playerThread.stop();  // 杀死当前执行的线程
	}
	//整个播放是处于一个非播即停的状态，所以设置了一个播放开关
	public void playToggle() {
		if (this.player.isPaused == true) {
			this.play();
		} else {
			this.pause();
		}
	}
	
	// 设置当前帧
	public void setFrameIndexCurrent(int frameIndexStart) {
		player.setFrameIndexCurrent(frameIndexStart);
	}
	
	public void setFrameIndexCurrent(double percent) {
		player.setFrameIndexCurrent((int)(totalFrame*percent));
	}
		
	// 给播放器进度监听线程使用，目的是获取当前已播放的帧数
	public int getFrameCurrent() {
		return player.getFrameCurrent();
	}
	// 该方法负责获取计算音乐文件的时长所需的总帧数参数。因为每帧约定为26ms
	// 播放时长T = 帧数 * 26ms,此方法也给播放进度监听线程提供数据来计算
	// 进度率
	public int getTotalFrame() {
		return totalFrame;
	}
	
	/**
	 *  获取当前播放文件路径
	 */
	public String getFilePath() {
		return filePath;
	}
	
	public void close() {
		if(player != null && !this.player.isClosed() && !this.player.isPaused) {
			pause();
		}
	}
	
	/**
	 *  是否已经到达文件末尾
	 * @return
	 */
	public boolean isComplete() {
		return complete;
	}
	
	/**
	 * 是否处于暂停状态
	 */
	public boolean isPaused() {
		return player.isPaused;
	}
	
	/**
	 * 是否处于关闭状态
	 */
	public boolean isClosed() {
		return player.isClosed();
	}
	
	public int getFrameFrom() {
		return player.getFrameIndexFrom();
	}
	
	public int getFrameTo() {
		return player.getFromeIndexTo();
	}
	
	// 线程执行的代码段
	public void run() {
		try {
			// 当歌曲为第一次播放，则从开头播放
			// 否则执行该方法，则播放上一次被暂停的位置的数据
			complete = this.player.resume();
		} catch (javazoom.jl.decoder.JavaLayerException ex) {
			ex.printStackTrace();
		}
	}
}