package com;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.biz.Favorite;
import com.biz.User;
//
public class Test {
	public static void main(String[] args) throws Exception{
//		args = new String[2];
//		args[0] = "http://item.taobao.com/auction/item_detail-0db2-bf4581e3ae705ccc7712825472a72e86.htm";
//		args[1] = "C:/TaoBaoApp/TBIDS.txt";
		if (args.length==2){
			String item  = args[0];
			String account  = args[1];
			List accounts = FileUtils.readLines(new File(account));
			List<UserOperation> users = new ArrayList<UserOperation>();
			for (int i = 0 ; i<accounts.size() ; i++){
				String line = (String)accounts.get(i);
				String id = line.split("\\|")[0];
				String pwd = line.split("\\|")[1];
				//
				UserOperation usr = new UserOperation();
				User userBiz = new User(usr);
				
				try{
					userBiz.login(id,pwd);
				}catch(Exception ex){
					System.out.println("user: " +id+ " login failed...");
				}
				users.add(usr);
				
				System.out.println("user: " +id+ " login done...");
				try{
					userBiz.deleteAllFavorite();
				}catch(Exception ex){
					//System.out.println("deleteAllFavorite failed...");
				}
				
			}
			
			while(true){
				int num = 0;
				for (UserOperation u: users){
					User userBiz = new User(u);
					Favorite favBiz =new Favorite(u);
					System.out.println("user: "+num+" request start...");
					try{
						favBiz.addFavoriteShop(item);
					}catch(Exception ex){
						System.out.println("user: "+num+" addFavoriteItem failed...");
					}
					Thread.sleep(2000);
					try{
						userBiz.deleteAllFavorite();
					}catch(Exception ex){
						System.out.println("user: "+num+" deleteAllFavorite failed...");
					}
					Thread.sleep(2000);
					System.out.println("user: "+num+" request done...");
					num++;
				}
			}
		}else{
			System.out.println("start parameters error");
		}
	}
}
