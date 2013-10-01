import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.plaf.SliderUI;

public class PlayList {
	
	/**
	 * @param afileName 文件名
	 * @param aplayer	ThreadPlayer对象
	 * @param aslider	滚动条
	 * @param alabel	标签
	 * @param abutton	播放按钮 
	 */
	public PlayList(EnglishFrame aframe) {
		frame = aframe;
		
		// 添加播放列表
		list = new List(5);
		list.setBackground(new Color(57, 225, 187));
						
		list.addMouseListener(new MouseAdapter() {	//双击播放列表某一项，即播放
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					String name = list.getSelectedItem();
					frame.playInit(name);
					frame.playButton.doClick();
				}
			}
		});
	}
	
	/**
	 * 从 PLAYLISTPATH 读取播放列表
	 */
	public void ReadList() {
		File f = new File(PLAYLISTPATH);
		if(f.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(f));
				String s;
				while((s = reader.readLine()) != null) {
					list.add(s);
				}
				reader.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}	
	
	/**
	 * 将 list 组件添加到 frame 上
	 * @param frame 
	 * @param pos 添加的方位
	 */
	public void addTo(JFrame frame, String pos) {
		frame.add(list, pos);
	}
	
	/**
	 * 添加 音乐文件 到播放列表
	 * @param songName 音乐文件名
	 */
	public void addToList(String songName) {
		// 将如果文件不在播放列表中，则将文件添加到播放列表
		boolean flag = true;
		for(int i=0; i<list.getItemCount(); i++) {
			if(list.getItem(i).equals(songName)) {
				flag = false;
				break;
			}
		}
		if(flag) list.add(songName);
	}
	
	public void writeList(){
		//将播放列表保存到文件
		File f = new File(PLAYLISTPATH);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			for(int i=0; i<list.getItemCount(); i++) {
//				String name = list.getItem(i);
				// 去掉空项目
	//			if(name.equals("") continue;
				
				writer.write(list.getItem(i));
				writer.write('\n');
			}
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public List list;
	private EnglishFrame frame;
	
	private static final String PLAYLISTPATH = "list.dat";

}
