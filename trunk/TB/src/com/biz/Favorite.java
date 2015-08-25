package com.biz;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.io.FileUtils;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import com.CheckCodeDialog;
import com.UserOperation;

public class Favorite {
    //TODO- name=inputCode
	private UserOperation usr = null;
	
	public Favorite(UserOperation usr){
		this.usr=usr;
	}
	
    public void addFavoriteItem(String itemPageURL) throws Exception {
        HtmlCleaner cleaner = new HtmlCleaner();
        String itemPage = usr.get(itemPageURL);

        //get add favorite page url
        TagNode itemPageNode = cleaner.clean(itemPage);
        TagNode favoriteLink = (TagNode) itemPageNode.evaluateXPath("//p[@class='action clearfix']/a[2]")[0];
        String addFavoriteURL = favoriteLink.getAttributeByName("href");

        //get add favorite form and action url
        String favPageHtml = usr.get(addFavoriteURL);
        FileUtils.writeStringToFile(new File("c:/result.html"), favPageHtml);
        TagNode addFavoriteNode = cleaner.clean(favPageHtml);
        TagNode formNode = (TagNode) addFavoriteNode.evaluateXPath("//form[@id='PopupFavorForm']")[0];
        String actionPage = formNode.getAttributeByName("action");
        
        //get hidden input values in the script files
        String scriptOneURL = formNode.getElementsByName("script", true)[0].getAttributeByName("src");
        String scriptTwoURL = formNode.getElementsByName("script", true)[1].getAttributeByName("src");
        List<NameValuePair> paramters = getParamsFromScript(new String[]{scriptOneURL, scriptTwoURL});
        
        //get hidden input values in add favorite page
        TagNode[] inputs = formNode.getElementsByName("input",true);
        for (TagNode input : inputs) {
            String name = input.getAttributeByName("name");
            String value = input.getAttributeByName("value");
            NameValuePair pair = null;
            if ("inputCode".equals(name)){
            	TagNode imgCode = (TagNode) formNode.evaluateXPath("//img[@id='checkCodeImg']")[0];
            	String imgUrl = imgCode.getAttributeByName("src");
            	byte[] imgBytes = usr.getURLBytes(imgUrl);
            	CheckCodeDialog dialog = new CheckCodeDialog(imgBytes);
            	while(true){
            		try{
            			Thread.sleep(1000);
            		}catch(Exception e){}
            		if (dialog.getCode()!=null){
            			String code = dialog.getCode();
            			pair = new NameValuePair("inputCode", code);
            			dialog.dispose();
            			break;
            		}
            	}
            }else{
            	pair = new NameValuePair(name, value);
            }
            paramters.add(pair);
        }
        paramters.add(new NameValuePair("isShared", "1"));
        paramters.add(new NameValuePair("shopIncluded", ""));
        
        // convert parameters and post
        NameValuePair[] allParas = paramters.toArray(new NameValuePair[0]);
        usr.post(actionPage, allParas);
    }
    
    
    public void addFavoriteShop(String itemPageURL) throws Exception {
        HtmlCleaner cleaner = new HtmlCleaner();
        String itemPage = usr.get(itemPageURL);

        //get add favorite page url
        TagNode itemPageNode = cleaner.clean(itemPage);
        TagNode favoriteLink = (TagNode) itemPageNode.evaluateXPath("//a[@id='xshop_collection_href']")[0];
        String addFavoriteURL = favoriteLink.getAttributeByName("href");

        //get add favorite form and action url
        String favPageHtml = usr.get(addFavoriteURL);
        FileUtils.writeStringToFile(new File("c:/result.html"), favPageHtml);
        TagNode addFavoriteNode = cleaner.clean(favPageHtml);
        TagNode formNode = (TagNode) addFavoriteNode.evaluateXPath("//form[@id='PopupFavorForm']")[0];
        String actionPage = formNode.getAttributeByName("action");
        
        //get hidden input values in the script files
        String scriptOneURL = formNode.getElementsByName("script", true)[0].getAttributeByName("src");

        List<NameValuePair> paramters = getParamsFromScript(new String[]{scriptOneURL});
        
        //get hidden input values in add favorite page
        TagNode[] inputs = formNode.getElementsByName("input",true);
        for (TagNode input : inputs) {
            String name = input.getAttributeByName("name");
            String value = input.getAttributeByName("value");
            NameValuePair pair = null;
            if ("inputCode".equals(name)){
            	TagNode imgCode = (TagNode) formNode.evaluateXPath("//img[@id='checkCodeImg']")[0];
            	String imgUrl = imgCode.getAttributeByName("src");
            	byte[] imgBytes = usr.getURLBytes(imgUrl);
            	CheckCodeDialog dialog = new CheckCodeDialog(imgBytes);
            	while(true){
            		try{
            			Thread.sleep(1000);
            		}catch(Exception e){}
            		if (dialog.getCode()!=null){
            			String code = dialog.getCode();
            			pair = new NameValuePair("inputCode", code);
            			dialog.dispose();
            			break;
            		}
            	}
            }else{
            	pair = new NameValuePair(name, value);
            }
            paramters.add(pair);
        }
        paramters.add(new NameValuePair("isShared", "1"));
        
        // convert parameters and post
        NameValuePair[] allParas = paramters.toArray(new NameValuePair[0]);
        usr.post(actionPage, allParas);
    }
    

    private List<NameValuePair> getParamsFromScript(String[] scriptURLs) throws Exception {
        HtmlCleaner cleaner = new HtmlCleaner();
        List<NameValuePair> paramters = new ArrayList<NameValuePair>();
        for (String scriptURL : scriptURLs) {
            String content = usr.get(scriptURL);
            content = content.replace("document.write('", "");
            content = content.replace("');", "");
            TagNode inputsNode = cleaner.clean(content);
            Object[] inputs = inputsNode.evaluateXPath("//input");
            for (Object input : inputs) {
                TagNode p = (TagNode) input;
                String name = p.getAttributeByName("name");
                String value = p.getAttributeByName("value");
                NameValuePair pair = new NameValuePair(name, value);
                paramters.add(pair);
            }
        }
        return paramters;
    }

}
