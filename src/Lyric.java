import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class Lyric {
	public Lyric(File lyricFile) {
		this.lyricFile = lyricFile;	
	}
		
	/**
	 * 获取lrc文件的内容部分, 即除去时间部分
	 */
	public String getLyricContent() {
		return getLyricContent(lyricFile);
	}
	
	public String getTextContent() {
		return getTextContent(lyricFile);
	}
	
	public static String getLyricContent(File file) {
		String content = "";

		try {
			BufferedReader bf = new BufferedReader(new FileReader(file));
			String s;
			while((s = bf.readLine()) != null) {
				for(String e : s.split("]")) {
					if(!e.contains("[")) 
						content += e + "\n";
				}
			}
			bf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
	
	public boolean transLyricToText(String txtPathName) {
		String content = this.getLyricContent();
		try {
			BufferedWriter rf = new BufferedWriter(new FileWriter(txtPathName));
			rf.write(content);
			rf.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static String getTextContent(File file) {
		String content = "";

		try {
			BufferedReader bf = new BufferedReader(new FileReader(file));
			String s;
			while((s = bf.readLine()) != null) {
				content += s + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}	
	
	/**
	 * 根据播放文件，检测歌词文件是否存在
	 * @param filePath 播放文件的路径
	 * @return 如果歌词文件存在，返回路径，否则返回null
	 */
	public static String getLyric(String filePath) {
		String lyricPath = filePath.split("\\.")[0] + ".lrc";
		File f = new File(lyricPath);
		if(f.exists())
			return lyricPath;
		else 
			return null;
	}
	
	/**
	 * get the path of the lyric 
	 * @return the path of the lyric
	 */
	public String getLyricPath() {
		return this.lyricFile.getPath();
	}
	
	private File lyricFile;
}
