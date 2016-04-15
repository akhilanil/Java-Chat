package client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;

public  class SendReceive {

	InetAddress serverip;
	int serverport,myport;
	DatagramSocket cs=null;
	byte[] sbyte,rbyte;
	boolean send=false;
	boolean connected=false;
	JProgressBar progressBar;
	JFrame frame;
	JPanel panel;
	JLabel name,address,port;
	JTextField name1,address1,port1;
	JButton enter,quit;
	String myname;
	Interface inter;
	String smsg,destip,destport,destmsg;
	Storage s;
	Communication comm;
	
	
	SendReceive(Storage s,Interface inter){
		this.inter=inter;
		this.s=s;
		sbyte= new byte[1024];
		rbyte= new byte[1024];
	}
	public void serverConnect(String initial){// Initial Connection
		
		sbyte=myname.getBytes();
		byte[] real;
		String name,ip,port;
		DatagramPacket initialsendPacket= new DatagramPacket(sbyte, sbyte.length, serverip, serverport);
		try {
			cs= new DatagramSocket();
			cs.send(initialsendPacket);//Sending Client Details
			
			DatagramPacket receivePacket= new DatagramPacket(rbyte, rbyte.length);
			cs.receive(receivePacket);//receives number of online systems
			real=Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
			int number= Integer.parseInt(new String(real, "UTF-8"));//number of people online
			
			
			for(int i=0; i<number; i++){
				cs.receive(receivePacket);
				real=Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
				name=new String(real, "UTF-8");
				
				
				cs.receive(receivePacket);
				real=Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
				ip=new String(real, "UTF-8");
				
				cs.receive(receivePacket);
				real=Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
				port=new String(real, "UTF-8");
				
				inter.addOnline(name, InetAddress.getByName(ip), Integer.parseInt(port));
				
			}
			myport=cs.getLocalPort();
			cs.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	synchronized public void sendMessage(){
		while(!send){
			try{
				System.out.println("Waiting for a message...!!");
				wait();
			}catch(Exception e){e.printStackTrace();}
		}
		String toSend=smsg+" `934~ "+destip ;
		System.out.println("Sending: "+toSend);
		sbyte=toSend.getBytes();
		try{
			System.out.println("Sending a message...!!");
			DatagramPacket sendPacket= new DatagramPacket(sbyte, sbyte.length, serverip, serverport);
			cs= new DatagramSocket(myport);
			cs.send(sendPacket);
			cs.close();
			//s.storeMessage(destip,smsg,true);
		}catch(Exception e){e.printStackTrace();}
		alterState();
		cs.close();
		notify();
		
		
	}
	
	synchronized public void receiveMessage(){
		while(send){
			try{
				System.out.println("Waiting for a message to send...!!");
				notify();
				wait();
			}catch(Exception e){e.printStackTrace();}
		}
		
		try{
			System.out.println("Receiving a message...!!");
			String realMessage; 
			DatagramPacket receivePacket= new DatagramPacket(rbyte, rbyte.length);
			cs= new DatagramSocket(myport);
			cs.receive(receivePacket);
			byte[] real=Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
			realMessage= new String(real, "UTF-8");
			System.out.println("Real1: "+realMessage);
			String split[]=realMessage.split("`934~ ",2);
			String ip=split[0];
			String msg=split[1];
			
			if(ip.equals(serverip.getHostAddress())){
				System.out.println("From Server: "+msg);
				try{
				String[] secondsplit=msg.split("`934~ ",2);
				System.out.println("Code: "+secondsplit[0]);
				
				inter.serverCommands(secondsplit[0],secondsplit[1]);
				}catch(ArrayIndexOutOfBoundsException e){
					//s.storeFile(msg,ip);
					this.destmsg=realMessage;
					inter.getMessage(msg,ip);
				}
			}
			else{
				System.out.println("Real2:  "+realMessage);
				//s.storeFile(msg,ip);
				this.destmsg=realMessage;
				inter.getMessage(msg,ip);
			}
			cs.close();
			//display message
			
		}catch(Exception e){e.printStackTrace();cs.close();}
	}
	
	public void alterState(){
		
		//send=(!send)? true:false;
		if(send){send=false;}
		else{send=true;}
	}
	
	
	
	public void  createAuthentication(){
		
		Progress p=new Progress();
		frame= new JFrame("Authenticate");
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(350,200);
		frame.setLocationRelativeTo(null);
		frame.setAlwaysOnTop(true);
		
		panel=new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc= new GridBagConstraints();
		
		name=new JLabel("Name: ");
		address=new JLabel("IP Address: ");
		port= new JLabel("Port: ");
		
		name1= new JTextField(20);
		address1=new JTextField(15);
		port1=new JTextField(7);
		
		enter= new JButton("CONNECT");
		quit= new JButton("QUIT");
		
		gbc.insets=new Insets(10,10,0,0);
		
		gbc.gridx=0;
		gbc.gridy=0;
		//gbc.weightx=0.1;
		//gbc.weighty=0.2;
		gbc.gridwidth=1;
		gbc.gridheight=1;
		panel.add(name,gbc);
		
		gbc.gridx=1;
		gbc.gridy=0;
		//gbc.weightx=0.5;
		//gbc.weighty=1.0;
		gbc.gridwidth=2;
		gbc.gridheight=1;
		panel.add(name1,gbc);

		gbc.gridx=0;
		gbc.gridy=1;
		gbc.weightx=0.0;
		//gbc.weighty=1.0;
		gbc.gridwidth=1;
		gbc.gridheight=1;
		panel.add(address,gbc);
		
		gbc.gridx=1;
		gbc.gridy=1;
		gbc.weightx=0.0;
		//gbc.weighty=1.0;
		gbc.gridwidth=2;
		gbc.gridheight=1;
		panel.add(address1,gbc);
		
		
		gbc.gridx=0;
		gbc.gridy=2;
		gbc.weightx=0.0;
		//gbc.weighty=1.0;
		gbc.gridwidth=1;
		gbc.gridheight=1;
		panel.add(port,gbc);
		
		gbc.gridx=1;
		gbc.gridy=2;
		gbc.weightx=0.0;
		//gbc.weighty=1.0;
		gbc.gridwidth=2;
		gbc.gridheight=1;
		panel.add(port1,gbc);
		
		gbc.gridx=0;
		gbc.gridy=3;
		gbc.weightx=0.0;
		//gbc.weighty=1.0;
		gbc.gridwidth=1;
		gbc.gridheight=1;
		gbc.anchor=GridBagConstraints.CENTER;
		panel.add(enter,gbc);
		
		gbc.gridx=1;
		gbc.gridy=3;
		gbc.weightx=0.0;
		//gbc.weighty=1.0;
		gbc.gridwidth=1;
		gbc.gridheight=1;
		gbc.anchor=GridBagConstraints.CENTER;
		panel.add(quit,gbc);
		
		gbc.gridx=1;
		gbc.gridy=4;
		gbc.weightx=0.0;
		//gbc.weighty=1.0;
		gbc.gridwidth=1;
		gbc.gridheight=1;
		gbc.anchor=GridBagConstraints.CENTER;
		panel.add(progressBar,gbc);
		
		frame.add(panel);
		frame.validate();
		

		enter.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				myname=name1.getText();
				inter.frame.setTitle("LetsChat - "+myname);
				try {
					serverip=InetAddress.getByName(address1.getText());
				} catch (UnknownHostException e) {e.printStackTrace();}
				serverport=Integer.parseInt(port1.getText());
				p.start();
			}
			
		});
		
		quit.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				if(cs!=null)cs.close();
				frame.dispose();
				connected=true;
				System.exit(0);
			}
			
		});
		
		 frame.addWindowListener(new WindowAdapter() {
	         public void windowClosing(WindowEvent windowEvent){
	        	 if(cs!=null)cs.close();
	        	 frame.dispose();
	        	 connected=true;
	        	 System.exit(0);
	            
	         }        
	      }); 
		
		
	}
	
	private class Progress extends Thread{
		public Progress(){
			  progressBar = new JProgressBar(0, 100);
		      progressBar.setValue(0);
		      progressBar.setStringPainted(true);
		}
		public void run(){
	         for(int i =0; i<= 100; i+=50){
	            final int progress = i;
	            if(progress==50){	
	            	progressBar.setValue(progress);
	            	serverConnect(name1.getText());
	            	try{Thread.sleep(3000);}catch(InterruptedException e){}
	            }
	            progressBar.setValue(progress);
	         }
	         
	         connected=true;
	         try{Thread.sleep(1000);}catch(InterruptedException e){}
	         frame.dispose();
	         try{Thread.sleep(200);}catch(InterruptedException e){}
	         
	         try{checkConnection();}catch(Exception e){}
	         //notifyAll();
	         
	      }
	
	}
	
	
	public String getName(){return myname;}
	//public boolean checkState(){return send;}
	public synchronized void checkConnection() throws InterruptedException{
		while(!connected){
			//inter=new Interface(s);
			createAuthentication();
			wait();
			
		}
		
		notify();
	}
	public void setMessage(String text,String destip) {
		this.smsg=text;
		this.destip=destip;
		alterState();
		cs.close();
		
		System.out.println("Sending: "+text);
	}
	
	public String getMessage(){return destmsg;}
	
	
}
