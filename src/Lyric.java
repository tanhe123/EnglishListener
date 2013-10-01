import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class Lyric {
	public Lyric(String pathname) {
		lyricPath = pathname;	
	}
		
	/**
	 * 获取lrc文件的内容部分, 即除去时间部分
	 */
	public String getContent() {
		return getContent(lyricPath);
	}
	
	public static String getContent(String pathName) {
		String content = "";

		try {
			BufferedReader bf = new BufferedReader(new FileReader(pathName));
			String s;
			while((s = bf.readLine()) != null) {
				for(String e : s.split("]")) {
					if(!e.contains("[")) 
						content += e + "\n";
				}
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
	
	private String lyricPath;
}
