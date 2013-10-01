/*
 * 11/19/04		1.0 moved to LGPL.
 * 29/01/00		Initial version. mdm@techie.com
 *-----------------------------------------------------------------------
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *----------------------------------------------------------------------
 */

package javazoom.jl.player;

import java.io.InputStream;
import java.net.URL;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;
	
/**
 * The <code>Player</code> class implements a simple player for playback
 * of an MPEG audio stream. 
 * 
 * @author	Mat McGowan
 * @since	0.0.8
 */

// REVIEW: the audio device should not be opened until the
// first MPEG audio frame has been decoded. 
public class Player
{	  	
	/**
	 * The current frame number. 
	 */
	private int frame = 0;
	
	/**
	 * The MPEG audio bitstream. 
	 */
	// javac blank final bug. 
//	/*final*/ private Bitstream		bitstream;
	
	/**
	 * The MPEG audio decoder. 
	 */
//	/*final*/ private Decoder		decoder; 
	
	/**
	 * The AudioDevice the audio samples are written to. 
	 */
//	private AudioDevice	audio;
	
	/**
	 * Has the player been closed?
	 */
	private boolean		closed = false;
	
	/**
	 * Has the player played back all frames from the stream?
	 */
	private boolean		complete = false;

	private int			lastPosition = 0;
	
	/**
	 * Creates a new <code>Player</code> instance. 
	 */
	/*public Player(InputStream stream) throws JavaLayerException
	{
		this(stream, null);	
	}*/
	
	public Player(URL urlToStreamFrom) throws JavaLayerException {
		this.urlToStreamFrom = urlToStreamFrom;
		isPaused = true;
	}
	
	public Player(InputStream stream, AudioDevice device) throws JavaLayerException
	{
		bitstream = new Bitstream(stream);		
		decoder = new Decoder();
				
		if (device!=null)
		{		
			audio = device;
		}
		else
		{			
			FactoryRegistry r = FactoryRegistry.systemRegistry();
			audio = r.createAudioDevice();
		}
		audio.open(decoder);
	}
	
	/*
	public void play() throws JavaLayerException
	{
		play(Integer.MAX_VALUE);
	}*/
	
	/**
	 * Plays a number of MPEG audio frames. 
	 * 
	 * @param frames	The number of frames to play. 
	 * @return	true if the last frame was played, or false if there are
	 *			more frames. 
	 */
	/*
	public boolean play(int frames) throws JavaLayerException
	{
		boolean ret = true;
			
		while (frames-- > 0 && ret) 
		{
			ret = decodeFrame();			
		}
		
		if (!ret)
		{
			// last frame, ensure all data flushed to the audio device. 
			AudioDevice out = audio;
			if (out!=null)
			{				
				out.flush();
				synchronized (this)
				{
					complete = (!closed);
					close();
				}				
			}
		}
		return ret;
	}*/
		
	/**
	 * Cloases this player. Any audio currently playing is stopped
	 * immediately. 
	 */
	public synchronized void close()
	{		
		AudioDevice out = audio;
		if (out!=null)
		{ 
			closed = true;
			audio = null;	
			// this may fail, so ensure object state is set up before
			// calling this method. 
			out.close();
			lastPosition = out.getPosition();
			try
			{
				bitstream.close();
			}
			catch (BitstreamException ex)
			{
			}
		}
	}
	
	/**
	 * Returns the completed status of this player.
	 * 
	 * @return	true if all available MPEG audio frames have been
	 *			decoded, or false otherwise. 
	 */
	public synchronized boolean isComplete()
	{
		return complete;	
	}
				
	/**
	 * Retrieves the position in milliseconds of the current audio
	 * sample being played. This method delegates to the <code>
	 * AudioDevice</code> that is used by this player to sound
	 * the decoded audio samples. 
	 */
	/*public int getPosition()
	{
		int position = lastPosition;
		
		AudioDevice out = audio;		
		if (out!=null)
		{
			position = out.getPosition();	
		}
		
		return position;
	}*/		
	
	/**
	 * Decodes a single frame.
	 * 
	 * @return true if there are no more frames to decode, false otherwise.
	 */
	public boolean decodeFrame() throws JavaLayerException
	{		
		try
		{
			AudioDevice out = audio;
			if (out==null)
				return false;

			Header h = bitstream.readFrame();	

			if (h==null)
				return false;
				
			// sample buffer set when decoder constructed
			SampleBuffer output = (SampleBuffer)decoder.decodeFrame(h, bitstream);
																																					
			synchronized (this)
			{
				out = audio;
				if (out!=null)
				{					
					out.write(output.getBuffer(), 0, output.getBufferLength());
				}				
			}
																			
			bitstream.closeFrame();
		}		
		catch (RuntimeException ex)
		{
			throw new JavaLayerException("Exception decoding audio frame", ex);
		}
/*
		catch (IOException ex)
		{
			System.out.println("exception decoding audio frame: "+ex);
			return false;	
		}
		catch (BitstreamException bitex)
		{
			System.out.println("exception decoding audio frame: "+bitex);
			return false;	
		}
		catch (DecoderException decex)
		{
			System.out.println("exception decoding audio frame: "+decex);
			return false;				
		}
*/		
		return true;
	}
	
	public void init(InputStream stream, AudioDevice device) throws JavaLayerException
	{
		bitstream = new Bitstream(stream);		
		decoder = new Decoder();
				
		if (device!=null)
		{		
			audio = device;
		}
		else
		{			
			FactoryRegistry r = FactoryRegistry.systemRegistry();
			audio = r.createAudioDevice();
		}
		audio.open(decoder);
	}
	
	
	// 自己添加的成员函数
	
	/**
	 * 跳过一帧
	 * @throws JavaLayerException
	 */
	public boolean skipFrame() throws JavaLayerException {
		boolean returnValue = false;
		Header header = bitstream.readFrame();
		if (header != null) {
			bitstream.closeFrame();
			returnValue = true;
		}
		return returnValue;
	}
	
	// 暂停状态
	public void pause() {
		this.isPaused = true;  // 置暂停状态为真
		this.close();  // 执行关闭方法
	}
	
	// 播放开始从0开始播放
	public boolean play() throws JavaLayerException {
		return this.play(0);
	}
	
	// 从指定帧位置播放
	public boolean play(int frameIndexStart) throws JavaLayerException {  
		return this.play(frameIndexStart, -1, 52);
	}
	
	/**
	 * 从frameIndexstart开始播放至frameIndexFinal
	 * 如果frameIndexStart 为 -2, 表示用上一次的起点
	 * 如果frameIndexTo 为 -2， 表示用上一次的终点
	 */
	public boolean play(int frameIndexStart, int frameIndexFinal,
			int correctionFactorInFrames) throws JavaLayerException {
		if(frameIndexStart != -2) frameIndexFrom = frameIndexStart;
		if(frameIndexFinal != -2) frameIndexTo = frameIndexFinal;
		
	//	System.out.println("from " + frameIndexFrom);
	//	System.out.println("to: " + frameIndexTo);
		
		try {
			// 打开文件流
			this.bitstream=new Bitstream(this.urlToStreamFrom.openStream());
		} catch (java.io.IOException ex) {
		}
        // 创建一个声卡对象
		this.audio = FactoryRegistry.systemRegistry().createAudioDevice();
		// 创建一个解码器对象
		this.decoder = new Decoder();
		this.audio.open(decoder);  // 将解码器置于声卡中
		boolean shouldContinueReadingFrames = true;
		this.isPaused = false;
		this.frameIndexCurrent = 0;
	    // 跳帧：跳到上一次被暂停的帧位置
		while (shouldContinueReadingFrames == true
				&& this.frameIndexCurrent < frameIndexStart//frameIndexFrom
						- correctionFactorInFrames) {
			shouldContinueReadingFrames = this.skipFrame();
			this.frameIndexCurrent++;  
		}
		
	//	System.out.println("skip " + frameIndexCurrent);
				
		while (shouldContinueReadingFrames == true
				&& (this.frameIndexCurrent < frameIndexFinal || frameIndexFinal == -1)) {
			if (this.isPaused == true) {
				shouldContinueReadingFrames = false;
				try {
					// 如果暂停状态被改变，则让线程睡眠
					Thread.sleep(1);  
				} catch (Exception ex) {
				}
			} else {
				shouldContinueReadingFrames = this.decodeFrame();
				this.frameIndexCurrent++;
//				System.out.println(this.frameIndexCurrent + " " + shouldContinueReadingFrames);
			}
		}
		
		// 最后，为了保证所有的帧都进入声卡中
		if (this.audio != null) {
			this.audio.flush();
			synchronized (this) {
				this.isComplete = (this.isClosed == false);
				this.close();
			}
		}
		return this.isComplete;
	}
	
	// 执行恢复方法
	public boolean resume() throws JavaLayerException {
     //  返回是否完成状态，副作用是启动播放
		return this.play(this.frameIndexCurrent);  
	}
	
	// 获得总的帧数
	// 基本思想是打开流并调用skipFrame()方法，如果该方法返回真
	// 说明文件没有到结尾，因为skipFrame()方法每次只读一帧，所以
	// 可以对其计数
	public int getTotalFrame() {
		int total = 0;
		try {
			this.bitstream=new Bitstream(this.urlToStreamFrom.openStream());
		} catch (java.io.IOException ex) {
		}

		try {
			while (skipFrame())
				total++;
		} catch (JavaLayerException e) {
			e.printStackTrace();
		}

		return total;
	}
	
	// 获得当前的帧索引
	public int getFrameCurrent() {
		return frameIndexCurrent;
	}
	
	public void setFrameIndexCurrent(int frameIndexCurrent) {
		this.frameIndexCurrent = frameIndexCurrent; 
	}
	
	/**
	 * 如果当前已关闭，返回true，未关闭返回 false
	 */
	public boolean isClosed() {
		return isClosed;
	}
	
	public int getFrameIndexFrom() {
		return frameIndexFrom;	
	}
	
	public int getFromeIndexTo() {
		return frameIndexTo;
	}
	
	private java.net.URL urlToStreamFrom;  // 音乐文件路径
	private Bitstream bitstream;  // 音乐文件位流对象
	private Decoder decoder;  //  Mp3解码器
	private AudioDevice	audio;		//声卡
	private boolean isClosed = false;  // 是否关闭状态
	private boolean isComplete = false;  // 是否完成播放状态
	private int frameIndexCurrent;  // 当前播放的帧索引
	public boolean isPaused;  // 是否暂停播放状态
	
	// 起始帧和结束帧，用于循环播放
	private int frameIndexFrom;
	private int frameIndexTo;
	private boolean isRepeat;	// 是否处于循环模式
}
