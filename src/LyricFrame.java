import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class LyricFrame extends JFrame{
	
	public static void main(String[] args) {

	}
	
	public LyricFrame(EnglishFrame aframe) {
		frame = aframe;
		
		setTitle("Lyric");
		setSize(200, 300);
		setVisible(false);
	//	frame.lyricItem.doClick();
	}
	
	private EnglishFrame frame;
}
