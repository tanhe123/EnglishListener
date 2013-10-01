import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class MyTextPane extends JTextPane {
   public MyTextPane() {
	   super();
	   rightStyle = this.addStyle("MARKING", null);
	   rightStyle.addAttribute(StyleConstants.Foreground, Color.RED);
	   normalStyle = this.addStyle("NORMAL", null);
	   normalStyle.addAttribute(StyleConstants.Foreground, Color.BLACK);
   }
   
   public MyTextPane(String content) {
	   super();
	   setText(content);
   }
   
   /**
    * 标记出正确的单词
    * @param list
    */
   public void MarkContent(final ArrayList<Words> list) {
	   if(list.size() == 0) return ;
	   
//	/   try {
	//	   SwingUtilities.invokeAndWait(new Runnable() {
//			   public void run() {
	   for(Words w : list) {
		   getStyledDocument().setCharacterAttributes(w.begin, w.end-w.begin, rightStyle, true);
	   }
//			   }
//		   });
//	   } catch (Exception e) {
//		   	   System.out.println("Exception when" +
//				   "constructing document:" + e);
//		   System.exit(1);
//	   }
   }
   
   /**
    * 将内容前景色全部变成normalStyle
    */
   public void NormalContent() {
	   StyledDocument doc = this.getStyledDocument();
	   doc.setCharacterAttributes(0, doc.getLength(), normalStyle, true);
   }
   
   public void append(String s) {
	   try {
		   this.getDocument().insertString(this.getDocument().getLength(), s, normalStyle);
	   } catch (BadLocationException e) {
		   e.printStackTrace();
	   }
   }
   
   private Style rightStyle;
   private Style normalStyle;
}