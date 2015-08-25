package com.ui;

import javax.swing.JFrame;

public class Main extends JFrame{
	public Main(){
		init();
	}
	
	public void init(){
		setSize(400,300);
		setVisible(true);
	}
	
	public static void main(String[] args){
		new Main();
	}

}
