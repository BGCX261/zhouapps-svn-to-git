package com;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class UserOperation {
	private HttpClient client = null;
	
	public UserOperation(){
		client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(8000);
	}
	
	public UserOperation(HttpClient client){
		this.client = client;
	}
	
	public byte[] getURLBytes(String url) throws Exception{
		GetMethod get = null;
		try{
			get = new GetMethod(url);
			int statuscode = client.executeMethod(get);
			return get.getResponseBody();
		}catch(Exception e){
			throw new Exception("Can not getBytes:"+e.getMessage());
		}finally{
			get.releaseConnection();
		}
	}
	
	public String get(String url) throws Exception{
		GetMethod get = new GetMethod(url);
		get.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "gbk");
		String content = null;
		try{
			int statuscode = client.executeMethod(get);
			if ((statuscode == HttpStatus.SC_MOVED_TEMPORARILY) ||  //重定向的时候读目标页面内容
					(statuscode == HttpStatus.SC_MOVED_PERMANENTLY) ||     
					(statuscode == HttpStatus.SC_SEE_OTHER) ||
					(statuscode == HttpStatus.SC_TEMPORARY_REDIRECT)){
				Header header = get.getResponseHeader("location");
				if (header!=null){
					content = get(header.getValue());
				}
			}else{
				content = get.getResponseBodyAsString();
			}
		}catch(Exception e){
			throw new Exception("Can not get:"+e.getMessage());
		}finally{
			get.releaseConnection();
		}
		return content;
	}
	
	public String post(String url) throws Exception{
		return post(url,null);
	}
	
	public String post(String url,NameValuePair[] parameters) throws Exception{
		PostMethod post = new PostMethod(url);
		post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "gbk");
		String content = null;
		if (parameters!=null) post.setRequestBody(parameters);
		try{
			int statuscode = client.executeMethod(post);
			//System.out.println(state);
			if ((statuscode == HttpStatus.SC_MOVED_TEMPORARILY) ||  //重定向的时候读目标页面内容
					(statuscode == HttpStatus.SC_MOVED_PERMANENTLY) ||     
					(statuscode == HttpStatus.SC_SEE_OTHER) ||
					(statuscode == HttpStatus.SC_TEMPORARY_REDIRECT)){
				Header header = post.getResponseHeader("location");
				if (header!=null){
					content = post(header.getValue());
				}
			}else{
				content = post.getResponseBodyAsString();
			}
		}catch(Exception e){
			throw new Exception("Can not post:"+e.getMessage());
		}finally{
			post.releaseConnection();
		}
		return content;
	}
}
