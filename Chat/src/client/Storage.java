package client;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;

public class Storage {
	
	
	String msgq[];
	String ipq[];
	String msg,ip;
	int front=-1,rear=-1;
	//int mf=-1,mr=-1;
	Map<String,Integer> unread;
	String location;
	File file;
	Storage(){
		unread= new HashMap<String,Integer>();
		ipq= new String[50];
		msgq= new String[50];
		location=new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
		location=location+"\\LetsChat";
		File f=new File(location);
		f.mkdir();
	}


	/*Things to do:-
	 * create a function to write to file the send messages
	 * sendreceive needs an object of interface  
	 *
	 * */
	
	public String storeFile(String message,String ip) {//stores unread messages
		
		//this.ip=ip;
		String path;
		
	
		
		
		int flag=0;
		
		for(Map.Entry<String, Integer> entry : unread.entrySet()){//adding the number of unread to hash
			
			String key=entry.getKey();
			if(key.equals(ip)){
				unread.put(key, unread.get(key) + 1);
				flag=1;
				break;
			}		
		}
		
		if(flag!=1){unread.put(ip,1);}
		flag=0;
		
		
		path=location+"\\"+ip;
		file= new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
		try{
			/*FileOutputStream out = new FileOutputStream(path+"\\unread.txt",true);
			 
			//out.write(new String(message+"\n").getBytes());
			//out.close();*/
			PrintWriter outFile = new PrintWriter(new FileWriter(path+"\\unread.txt",true));
			outFile.println(message);
			outFile.close();
		}catch(Exception e){e.printStackTrace();}
		
		/*
		if(rear==-1){
			rear++;
			ipq[rear]=ip;
			msgq[rear]=msg;
		}
		else if(rear!=front-1){
			rear++;
			ipq[rear]=ip;
			msgq[rear]=msg;

		}
		*/
		
		return ip;
		
	}
	
	public String[] extractFile(String ip) {//extracts unread messages and store to main messages
		
		String tosend[]=new String[20];
		int i=0;
		String path;
		path=location+"\\"+ip;
		System.out.println(path);
		try{
			File f=new File(path+"\\unread.txt");
			FileInputStream in= new FileInputStream(path+"\\unread.txt");
			//FileOutputStream out = new FileOutputStream(path+"\\message.txt",new Boolean(true));
		    BufferedReader br= new BufferedReader(new InputStreamReader(in));
		    PrintWriter outFile = new PrintWriter(new FileWriter(path+"\\message.txt",true));
		    String msg;
			while((msg=br.readLine())!=null){
				
				
				 tosend[i]=msg;
			//	 out.write(new String("#R: "+msg+"\n").getBytes());
				 outFile.println(new String("#R: "+msg));
				 System.out.println("MESSAGE= "+tosend[i]);
				 
				 i++;
			}
			i=0;
			in.close();
			outFile.close();
			br.close();
			f.delete();
		}catch(Exception e){e.printStackTrace();}
	//	for(int k=0; k<tosend.length; k++)
		//System.out.println("Was: "+tosend[k]);
		
		List<String> list = new ArrayList<String>();
		for(String s : tosend){
			if(s!=null && s.length()>0){
				list.add(s);
			}
		}
		
		
		return list.toArray(new String[list.size()]);
	}
	
	
	public void storeMessage(String ip,String message, boolean send){// stores send messages and received messages if currently chatting.
		String path;
		path=location+"\\"+ip;
		
		if(!new File(path).isDirectory()){
			new File(path).mkdirs();
		}
		try{
			
			PrintWriter outFile = new PrintWriter(new FileWriter(path+"\\message.txt",true));
			if(send)
				message="#S: "+message;
			else
				message="#R: "+message;
			outFile.println(message);
			outFile.close();	
		}catch(Exception e){e.printStackTrace();}
		
	}
	
	
	public int checkUnread(String ip){
		
		int no=0;
		for(Map.Entry<String, Integer> entry : unread.entrySet()){
			String key=entry.getKey();
			if(key.equals(ip)){
				no=unread.get(key);
				break;
			}
		}
		return no;
	}
	
	
	
	
}
