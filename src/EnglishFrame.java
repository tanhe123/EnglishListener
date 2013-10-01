import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;

public class EnglishFrame extends JFrame{
	
	public EnglishFrame() {
		setTitle("英语默写软件助手");
		setSize(690, 500);
		
		// 歌词面板
		lyricFrame = new LyricFrame(this);
		//lyricFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		lyricFrame.setVisible(false);
		
		// 添加文件菜单
		JMenu fileMenu = new JMenu("文件");
		openItem = fileMenu.add(new OpenFile("打开听力"));
		fileMenu.addSeparator();
		JMenuItem readItem = new JMenuItem("读取文件");
		fileMenu.add(readItem);
		JMenuItem saveItem = new JMenuItem("保存文件");
		fileMenu.add(saveItem);
		JMenuItem exitItem = new JMenuItem("退出");
		fileMenu.add(exitItem);
				
		// 添加爱视图菜单
		JMenu viewMenu = new JMenu("视图");
		lyricItem = new JCheckBoxMenuItem("歌词面板");
		viewMenu.add(lyricItem);
		
		// 添加控制菜单
		JMenu controlMenu = new JMenu("控制");
		playItem = new JMenuItem("播放/暂停");
		controlMenu.add(playItem);	//播放子菜单
		JMenuItem stopItem = new JMenuItem("停止");
		controlMenu.add(stopItem);
		controlMenu.addSeparator();
		backItem = new JMenuItem("后退");
		controlMenu.add(backItem);
		forwardItem = new JMenuItem("快进");
		controlMenu.add(forwardItem);
		controlMenu.addSeparator();
		JMenuItem mark1Item = new JMenuItem("标记起点");
		controlMenu.add(mark1Item);
		JMenuItem mark2Item = new JMenuItem("标记终点");
		controlMenu.add(mark2Item);
		JMenuItem repeatItem = new JMenuItem("复读");
		controlMenu.add(repeatItem);
		
		// 工具菜单
		JMenu toolMenu = new JMenu("工具");
		JMenuItem scoreItem = new JMenuItem("得分");
		toolMenu.add(scoreItem);
		
		// 帮助菜单
		JMenu helpMenu = new JMenu("帮助");
		JMenuItem TipsItem = new JMenuItem("使用说明");
		helpMenu.add(TipsItem);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(viewMenu);
		menuBar.add(controlMenu);
		menuBar.add(toolMenu);
		menuBar.add(helpMenu);
		
		setJMenuBar(menuBar);
		
		// 设置菜单快捷键
		openItem.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
		saveItem.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
		playItem.setAccelerator(KeyStroke.getKeyStroke("ctrl P"));
		readItem.setAccelerator(KeyStroke.getKeyStroke("ctrl R"));
		backItem.setAccelerator(KeyStroke.getKeyStroke("ctrl B"));
		forwardItem.setAccelerator(KeyStroke.getKeyStroke("ctrl F"));
		exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl C"));
		stopItem.setAccelerator(KeyStroke.getKeyStroke("ctrl T"));
		mark1Item.setAccelerator(KeyStroke.getKeyStroke("ctrl 1"));
		mark2Item.setAccelerator(KeyStroke.getKeyStroke("ctrl 2"));
		repeatItem.setAccelerator(KeyStroke.getKeyStroke("ctrl 3"));
		TipsItem.setAccelerator(KeyStroke.getKeyStroke("ctrl H"));
		scoreItem.setAccelerator(KeyStroke.getKeyStroke("ctrl K"));
	
		// 添加监听器
		mark1Item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(player == null) {
					JOptionPane.showMessageDialog(null, "无法标记，因为未有听力正在播放");
					return ;
				}
				mark1 = player.getFrameCurrent();
			}
		});
		
		mark2Item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(player == null) {
					JOptionPane.showMessageDialog(null, "无法标记，因为未有听力正在播放");
					return ;
				}
				mark2 = player.getFrameCurrent();
//				System.out.println(mark2);
			}
		});
		
		
		repeatItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				player.close();
				player.play(mark1, mark2);
			}
		});
		
		readItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File("."));
				//chooser.setCurrentDirectory(new File(lastPath));
				int ret = chooser.showOpenDialog(null);
				
				if(ret == JFileChooser.CANCEL_OPTION) {	//未选择文件
					return ;
				}
				
				BufferedReader fr;
				try {
					fr = new BufferedReader(new FileReader(chooser.getSelectedFile()));
					String s;
					textPane.setText("");
					while((s = fr.readLine()) != null) {
						textPane.append(s);
						textPane.append("\n");
					}
					saveName = chooser.getSelectedFile().getPath();
					fr.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(saveName == "") {	//选择保存路径
					chooser = new JFileChooser();
					chooser.setCurrentDirectory(new File("."));
					//chooser.setCurrentDirectory(new File(lastPath));
					int ret = chooser.showSaveDialog(null);
					
					if(ret == JFileChooser.CANCEL_OPTION) {	//未选择文件
						JOptionPane.showMessageDialog(null, "未能成功保存文件!");
						return ;
					}
					
					saveName = chooser.getSelectedFile().toString();
				}
				
				try {
					BufferedWriter wf = new BufferedWriter(new FileWriter(saveName));
					wf.write(textPane.getText());
					wf.close();
					JOptionPane.showMessageDialog(null, "保存成功");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		backItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//player.playAt(BACK_FRAMES);
				slider.setValue(slider.getValue()-BACK_FRAMES_PERCENT);
			}
		});
		
		forwardItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				slider.setValue(slider.getValue()+FORWARD_FRAMES_PERCENT);
			}
		});
		
		playItem.addActionListener(new ActionListener() { //play监听器
			public void actionPerformed(ActionEvent e) {
				
				if(fileName == "") {
					JOptionPane.showMessageDialog(null, "请先打开听力文件");
					openItem.doClick();
					if(fileName == "") return ;
				}

				if(!player.isClosed() && !player.isPaused()) {	// 如果正在播放
					playButton.setText("播放");
					player.playToggle();
				} else if(!player.isClosed() && player.isPaused()){
					playButton.setText("暂停");

					// 如果已完成播放，则重新播放
					if(player.isComplete()) {
						player.playAt(0);
						return ;
					}
					
					// 获取当前播放的进度
					double percent = slider.getValue()/100.0;
					player.playAt(percent);
				}
			}
		});
		
		stopItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(player == null) return ;
				
				player.close();
				player.setFrameIndexCurrent(0);
				slider.setValue(0);
			}
		});
		
		// 视图菜单监听器
		lyricItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(lyricItem.getState()) {
					// 获取EnglishFrame的位置
					lyricFrame.setVisible(true);
		//			setLocation();
				} else {
					lyricFrame.setVisible(false);
				}
			}
		});
		
		// 工具监听器
		scoreItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(player == null) {
					JOptionPane.showMessageDialog(null, "请先默写");
					return ;
				}
				
				content.setContent(textPane.getText());
				
				int match = content.LCS();
				
				String score = "";
				score += "默写单词总数: " + content.getContentLength() + "\n";
				score += "文章单词总数: " + content.getPassageLength() + "\n";
				score += "正确数： " + match + "\n";
				score += "正确率: " + (int)((double)match/content.getPassageLength()*100) + "%" + "\n";
				
				JOptionPane.showMessageDialog(null, score);
				
				
			}
		});
		
		// 帮助菜单监听器
		TipsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, HOWTOUSE);
			}
		});
		
		JPanel northPanel = new JPanel();
		// 向左对齐
		northPanel.setLayout(new BorderLayout());
		
		JPanel panel1 = new JPanel();
		panel1.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		// 添加快退、播放/暂停、快进按钮
		playButton = new JButton("播放");
		backButton = new JButton("后退");
		forwardButton = new JButton("前进");
		
		panel1.add(backButton);
		panel1.add(playButton);
		panel1.add(forwardButton);
		
		playButton.addActionListener(new ActionListener() {	// 单击播放
			public void actionPerformed(ActionEvent e) {
				if(fileName == "") {
					JOptionPane.showMessageDialog(null, "请先打开听力文件");
					openItem.doClick();
					if(fileName == "") return ;
				}
				
				if(playButton.getText().equals("播放")) {
					playButton.setText("暂停");
				}
				else playButton.setText("播放");
				
				playItem.doClick();
			}
		});
		
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backItem.doClick();
			}
		});
		
		forwardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				forwardItem.doClick();
			}
		});
		
		// 添加标签
		//JLabel label = new JLabel("歌名: NULL");
		label = new JTextField("歌名: NULL", 10);
		label.setEditable(false);
		//label.setEnabled(false);
		panel1.add(label);
		
		// 添加滑块
		slider = new JSlider(0, 100, 0);
		panel1.add(slider, BorderLayout.NORTH);
		
		northPanel.add(panel1, BorderLayout.NORTH);
		
		add(northPanel, BorderLayout.NORTH);
		
		slider.setMajorTickSpacing(20);
		slider.setMinorTickSpacing(10);
		slider.setPaintTicks(true);
		slider.setPaintTrack(true);

		// 为滑块添加监听器
		listener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {

				JSlider source = (JSlider)e.getSource();
				if(!source.getValueIsAdjusting()) {
					int value  = source.getValue();
					String svalue = String.valueOf(value);
					textSlider.setText(svalue + "%");	//显示百分比

					if(fileName != "" && !player.isPaused()) {	// 当前正在播放，则改变进度
						player.playAt(value/100.0);
					}
					else if(fileName != "" && player.isPaused()) {	//当前已暂停
						player.setFrameIndexCurrent(value/100.0);
					}
				}
			}
		};
		slider.addChangeListener(listener);
		
		// 添加滑块显示百分比
		textSlider = new JTextField("0%", 10);
		panel1.add(textSlider);
		textSlider.setEditable(false);
		
		// 添加文本区
		textPane = new MyTextPane();
		textPane.setText(HOWTOUSE);	// 设置内容
		textPane.setBackground(new Color(57, 225, 187));	//设置背景颜色
		textPane.setFont(new Font("SansSerif", Font.BOLD, 16));	// 设置字体
		//textPane.setAutoscrolls(true);		
		getContentPane().add(new JScrollPane(textPane)); 
		
		// 设置时间控件
		Timer t = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 使得改变slider的值 而不触发ChangeListener
				slider.removeChangeListener(listener);
				
				if(player != null && !player.isPaused() && !player.isClosed()) {
					slider.setValue((int)((double)player.getFrameCurrent()/player.getTotalFrame()*100));
					int has = player.getFrameCurrent()/26;
					textSlider.setText("" + has + "/" + player.getTotalFrame()/ 26);	//显示已播放时间和总的时间
				}
				
				// 调用完，增加ChangeListener
				slider.addChangeListener(listener);
				
				//更新状态
				if(player != null 
						&& ((player.getFrameTo() == -1 && player.getFrameCurrent() >= player.getTotalFrame())
							|| (player.getFrameTo() != -1 && player.getFrameCurrent() >= player.getFrameTo()))) {
					playButton.setText("播放");
					player.setFrameIndexCurrent(0);
					player.close();
				} 
				
				// 时刻显示正确状态
				if(content != null) {
					textPane.NormalContent();
					content.setContent(textPane.getText());
					textPane.MarkContent(content.getRightList());
				}
				
				// TODO 动态更新播放按钮的状态
			}
		});
	
		// 计时器 启动
		t.start();
		
		// 添加播放列表组件
		playList = new PlayList(this);
		playList.addTo(this, BorderLayout.SOUTH);
		// 读取播放列表
		playList.ReadList();
	}
		
//	private JTextArea textArea;
	
	/**
	 * 打开文件
	 * @author tan
	 */
	public class OpenFile extends AbstractAction {
		public OpenFile(String name) {
			putValue(Action.NAME, name);
		}
		
		public OpenFile(String shortDescription, Icon icon) {
			putValue(Action.SHORT_DESCRIPTION, shortDescription);
			putValue(Action.SMALL_ICON, icon);
		}
		
		public void actionPerformed(ActionEvent e) {
			chooser = new JFileChooser();
			//chooser.showOpenDialog(MenuFrame.this);
			chooser.setCurrentDirectory(new File("."));
			//chooser.setCurrentDirectory(new File(lastPath));
			chooser.showOpenDialog(null);
			String name = chooser.getSelectedFile().getPath();
			
			playInit(name);
		}
	}
	
	/**
	 * 将要播放新的文件
	 * 简化代码，同时为了能够让playlist操作PlayerThread对象和设置fileName，因为java不支持引用
	 */
	public void playInit(String aname) {
		this.fileName = aname;
		if(fileName.equals("")) return;	//未选择文件
		
		if(player != null && !player.isClosed()) 
			player.close();
		player = new PlayThread(fileName);
		
		// 将滑块置为 0
		slider.setValue(0);
		
		// 将播放按钮改名为 "播放"
		playButton.setText("播放");

		// 设置标签, 显示音乐名
		int pos; 
		String name;
		if((pos = fileName.lastIndexOf('/')) != -1) {
			name = fileName.substring(pos+1);
		}
		else name = fileName;
		label.setText(name);
		
		// 将文件添加至播放列表
		playList.addToList(fileName);
		
		// 将列表写入文件
		playList.writeList();
		
		// 如果文本区是提示内容，则清空
		if(textPane.getText().equals(HOWTOUSE)) 
			textPane.setText("");
		
		// 设置content
		content = new CheckContent();
		content.setPassage(Lyric.getContent(Lyric.getLyric(player.getFilePath())));
		//System.out.println(content.getPassageLength());
	}
	
	private static final int BACK_FRAMES_PERCENT = 3;
	private static final int FORWARD_FRAMES_PERCENT = 3;
	private static final String HOWTOUSE = "使用说明：\n\t1.文件->打开听力\n\t2.选择播放\n\t3.在本文本区默写\n" 
			+ "复读功能:\n\t1.菜单->标记起点\n\t2.菜单->标记终点\n\t3.菜单->复读\n\n使用Ctrl+H可以快速打开帮助菜单哦";
	
	private JTextField label;
	private JSlider slider;
	private String fileName = "";
	private String saveName = "";
//	private String lyricPath = "";
	private CheckContent content;		//CheckContent类
	
	private JFileChooser chooser;	
	private PlayThread player;
	private JTextField textSlider;	//显示进度百分比
	private ChangeListener listener;
	private PlayList playList;	//播放列表
	private JTextArea lyric;
	private MyTextPane textPane;
	
	private JMenuItem openItem;
	private JMenuItem playItem;
	private JMenuItem backItem;
	private JMenuItem forwardItem;
	public JCheckBoxMenuItem lyricItem;
	
	public JButton playButton;
	private JButton forwardButton;
	private JButton backButton;
	private LyricFrame lyricFrame; 
		
	private int mark1 = 0;
	private int mark2 = 0;
}
