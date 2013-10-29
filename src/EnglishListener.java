import java.awt.EventQueue;
import javax.swing.JFrame;

public class EnglishListener {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				EnglishFrame frame = new EnglishFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}
}
