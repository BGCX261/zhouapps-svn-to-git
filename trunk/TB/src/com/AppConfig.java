package com;

import java.io.File;

public class AppConfig {
	public static String HTML_INFO = "c:/TaoBaoApp/";
	
	public static File getHtmlInfoPath(String file){
		return new File(HTML_INFO+file);
	}
}
