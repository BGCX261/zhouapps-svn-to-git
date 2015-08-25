package com.biz;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.io.FileUtils;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import com.AppConfig;
import com.UserOperation;


public class User {
	public static final String LOGIN_PAGE = "http://login.taobao.com/member/login.jhtml";
	public static final String MY_PAGE = "http://i.taobao.com/my_taobao.htm";
	public static final String DEL_FAVORITE_SERVICE = "http://favorite.taobao.com/json/delete_collection.htm?";
	private UserOperation usr = null;
	
	private String favPageCache = null;
	
	public User(UserOperation usr){
		this.usr=usr;
	}
	
	public void deleteAllFavorite() throws Exception{
		HtmlCleaner cleaner = new HtmlCleaner();
		if (favPageCache == null){
			TagNode myPageNode = cleaner.clean(usr.get(MY_PAGE));
			TagNode collecPageLinkNode = (TagNode)myPageNode.evaluateXPath("//div[@id='buyer']//a[@data-pid='favorite']")[0];
			String link1 = collecPageLinkNode.getAttributeByName("href");
			TagNode link1Node = cleaner.clean(usr.get(link1));
			TagNode allCollectionPageLinkNode = (TagNode)link1Node.evaluateXPath("//div[@id='FavorListMenu']//a")[0];
			favPageCache = "http://favorite.taobao.com"+allCollectionPageLinkNode.getAttributeByName("href");
		}
		String favPageString = usr.get(favPageCache);
		String[] IDString = getAllIds(favPageString);
		if (IDString!=null){
			TagNode node = cleaner.clean(favPageString);
			TagNode tokenInput = (TagNode)node.evaluateXPath("//form[@id='itemsForm']/input[@name='_tb_token_']")[0];
			String token = tokenInput.getAttributeByName("value");
			for (String id :IDString){
				String reqStr = DEL_FAVORITE_SERVICE+"collectinfoid="+id+"&_tb_token_="+token;
				try{
					usr.get(reqStr);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	private String[] getAllIds(String content){
		Set<String> set = new HashSet<String>();
		int start = 0;
		int end = 0;
		do{
			start = content.indexOf("collectinfoid=",end);
			if (start>0){
				end = content.indexOf("&",start);
				String collectId = content.substring(start+"collectinfoid=".length(),end);
				set.add(collectId);
			}else{
				break;
			}
		}while(true);
		if (set.size()>0){
			return set.toArray(new String[0]);
		}
		return null;
	}
	
	public void login(String username ,String pwd) throws Exception{
		String loginPage = usr.get(LOGIN_PAGE);

		HtmlCleaner cleaner = new HtmlCleaner();
		TagNode node = cleaner.clean(loginPage);
		TagNode formElement = (TagNode)node.evaluateXPath("//form[@id='J_StandardForm']")[0];
		Object[] inputs =  formElement.evaluateXPath("//div[@class='login-submit']//input");
		
		String formAction = formElement.getAttributeByName("action");
		
		List<NameValuePair> paramters = new ArrayList<NameValuePair>();
		for (Object input:inputs){
			TagNode p = (TagNode)input;
			String name = p.getAttributeByName("name");
			String value = p.getAttributeByName("value");
			NameValuePair pair = new NameValuePair(name,value);
			paramters.add(pair);
		}
		
		paramters.add(new NameValuePair("TPL_username",username));//设置用户名
		paramters.add(new NameValuePair("TPL_password",pwd));//设置密码
		
		NameValuePair[] allParas = paramters.toArray(new NameValuePair[0]);
		usr.post(formAction,allParas);
		
		FileUtils.writeStringToFile(AppConfig.getHtmlInfoPath("login_result.html"), usr.get(MY_PAGE));
	}
}
