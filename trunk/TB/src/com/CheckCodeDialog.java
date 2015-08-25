package com;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;



public class CheckCodeDialog extends JFrame{
	private static final long serialVersionUID = 1L;
	private String code = null;
	public CheckCodeDialog(byte[] imageBytes) throws Exception{
		super();
		Container container = getContentPane();
		container.setLayout(new FlowLayout());
		
		Image img =Toolkit.getDefaultToolkit().createImage(imageBytes);
		ImageIcon theIcon = new ImageIcon(img);
		
		JLabel label = new JLabel(theIcon);	

		final JTextArea input = new JTextArea(1,5);

		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				code = input.getText().trim();
			}
		});
		
		container.add(label);
		container.add(input);
		container.add(ok);
		
		setSize(200, 120);
		setResizable(false);
		setAlwaysOnTop(true);
		setVisible(true);
	}
	public String getCode() {
		return code;
	}
	
	
	
	
//	public static void main(String[] args) throws Exception{
//		UserOperation usr  = new UserOperation();
//		new CheckCodeDialog(usr,"http://checkcode.taobao.com/auction/checkcode?sessionID=ed76a088507664e0a753466cb5268286&r=1271391086339","",null);
//	}
}
