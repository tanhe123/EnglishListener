import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckContent {
	public CheckContent() {
		content = null;
		passage = null;
	}
	
	public CheckContent(String acontent, String apasage) {
		content = new ArrayList<Words>();
		getWords(acontent, content);
		passage = new ArrayList<Words>();
		getWords(apasage, passage);
	}
	
	/**
	 * 设置 content
	 * @param s 内容字符串
	 */
	public void setContent(String s) {
		content = new ArrayList<Words>();
		getWords(s, content);
	}
	
	/**
	 * 设置 passage
	 * @param s 内容字符串
	 */
	public void setPassage(String s) {
		passage = new ArrayList<Words>();
		getWords(s, passage);
	}
	
	/**
	 * 一篇文章中的单词
	 * @param s 将要提取的字符串
	 * @param arr 存储提取出的每个单词
	 */
	public void getWords(String s, ArrayList<Words> arr) {
		arr.clear();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(s);
		
		while(matcher.find()) {
			Words word = new Words(matcher.group(),
					matcher.start(), matcher.end());
			arr.add(word);
		}
	}
	
	/**
	 * 获取一篇文章中的单词总数
	 * @param s 文章字符串
	 * @return 返回单词总数
	 */
	public static int getWordsCount(String s) {
		int cnt = 0;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(s);
		
		while(matcher.find()) {
			cnt++;
		}
		
		return cnt;
	}
	
	/**
	 * 获取两字符串的最长公共子序列，字符串比较时，忽略大小写
	 * @return 最长公共子序列的长度
	 */
	public int LCS() {
		int M = content.size();
		int N = passage.size();

		// opt[i][j] = length of LCS of x[i..M] and y[j..N]
		int[][] opt = new int[M + 1][N + 1];
		
		// compute length of LCS and all subproblems via dynamic programming
		for (int i = M - 1; i >= 0; i--) {
			for (int j = N - 1; j >= 0; j--) {
				// 字符串比较时，忽略大小写
				if (content.get(i).word.equalsIgnoreCase(passage.get(j).word))
					opt[i][j] = opt[i + 1][j + 1] + 1;
				else
					opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
			}
		}
		
		int cnt = 0;
		rightList = new ArrayList<Words>();
		//recover LCS itself and print it to standard output
		int i = 0, j = 0;
		while (i < M && j < N) {
			//if (x.charAt(i) == y.charAt(j)) {
			if (content.get(i).word.equalsIgnoreCase(passage.get(j).word)) {
				//System.out.println(content.get(i).word);
				rightList.add(content.get(i));
				cnt++;
				i++;
				j++;
			} else if (opt[i + 1][j] >= opt[i][j + 1])
				i++;
			else
				j++;
		}

		return cnt;
	}
	
	/**
	 * 获取默写单词的总数
	 * @return
	 */
	public int getContentLength() {
		return content.size();
	}
	
	/**
	 * 获取文章单词的总数
	 * @return
	 */
	public int getPassageLength() {
		return passage.size();
	}
	
	/**
	 * 获取正确匹配的单词
	 */
	public ArrayList<Words> getRightList() {
		this.LCS();
		return rightList;
	}
	
	public static final String regex = "[\\w']+";
	
	private ArrayList<Words> rightList;
	private ArrayList<Words> content;
	private ArrayList<Words> passage;
}
