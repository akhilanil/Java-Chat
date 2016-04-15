package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class BackEnd{

	Hashtable<InetAddress,Integer> connections;
	DatagramSocket ss;
	Enumeration search;
	Interface inter;
	byte[] rbyte;
	byte[] sbyte;
	byte[] dest;
	DatagramPacket receivePacket;
	String destination,message;
	 String ip,port,name;
	
	BackEnd(){
		rbyte=new byte[1024];
		sbyte=new byte[1024];
		dest=new byte[1024];
		connections=new Hashtable<InetAddress,Integer>();
		
		inter=new Interface(ss,this);
		
		//System.out.print(ss.getPort());
	}
	synchronized public void stop(){
		
		while(!inter.checkTermination()){
			
			try {
				notify();
			//	System.out.println("Waiting for start ");//+ss.getPort());
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		//send messages to all active connections...!!
		//System.out.println("Executing SERVER STOP ROUTINE");
		
	}
	
	
	synchronized public void start(){
		
		while(inter.checkTermination()){
			
			try {
				//System.out.println("Waiting for stop ");
				ss= new DatagramSocket(8080);
				inter.newSocket(ss);
				notify();
				wait();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
			
		}
		
		try {
			//ss= new DatagramSocket(8080);
			String msg,name;
			//System.out.println("Executing SERVER START ROUTINE "+ss.getLocalAddress().getHostAddress());
			receivePacket= new DatagramPacket(rbyte,rbyte.length);
			ss.receive(receivePacket);
			sbyte=Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
			System.out.println("Server: "+new String(sbyte));
			name= new String(sbyte, "UTF-8"); 
			search=connections.keys();
			int flag=0;
			while(search.hasMoreElements()){
				InetAddress test= (InetAddress) search.nextElement();
				if(receivePacket.getAddress().equals(test)){
					flag=1;
					break;
					
				}
				
			}
			if(flag==0){  //new connection
				
				message=String.valueOf(inter.table.getRowCount());
				sbyte=message.getBytes();
				DatagramPacket sendPacket=new DatagramPacket(sbyte, sbyte.length,receivePacket.getAddress(),receivePacket.getPort());
				ss.send(sendPacket);

				for(int i=0; i<inter.table.getRowCount(); i++){// sending online details
					
					msg=(String)inter.table.getModel().getValueAt(i, 0);
					sbyte=msg.getBytes();
					sendPacket.setData(sbyte);
					sendPacket.setLength(sbyte.length);
					sendPacket.setAddress(receivePacket.getAddress());
					sendPacket.setPort(receivePacket.getPort());
					ss.send(sendPacket);
					System.out.println("Server: "+msg);
					
					msg=(String)inter.table.getModel().getValueAt(i, 1);
					sbyte=msg.getBytes();
					sendPacket.setData(sbyte);
					sendPacket.setLength(sbyte.length);
					sendPacket.setAddress(receivePacket.getAddress());
					sendPacket.setPort(receivePacket.getPort());
					ss.send(sendPacket);
					System.out.println("Server: "+msg);
					
					msg=String.valueOf(inter.table.getModel().getValueAt(i, 2));
					sbyte=msg.getBytes();
					sendPacket.setData(sbyte);
					sendPacket.setLength(sbyte.length);
					sendPacket.setAddress(receivePacket.getAddress());
					sendPacket.setPort(receivePacket.getPort());
					ss.send(sendPacket);
					System.out.println("Server: "+msg);
					
					
					
				 }
				setDetails(receivePacket.getPort(),receivePacket.getAddress(),name);
				broadcast("1000",ss,null);
				
				connections.put(receivePacket.getAddress(), receivePacket.getPort());
				
				inter.insertTable(name,receivePacket.getAddress().getHostAddress(),receivePacket.getPort());//add to table
				
				
			
			}
			else{//existing connection
				sbyte=Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
				message= new String(sbyte, "UTF-8"); 
				Scanner s= new Scanner(message).useDelimiter("`934~ *");
				message=s.next();
				destination=s.next();
			//	System.out.println("message = "+message+"\tdestination= "+destination);
				if(destination.equals(receivePacket.getAddress().getHostAddress())){
				//	System.out.println("message1 = "+message+"\tdestination1= "+destination);
					broadcast("1001",ss,destination);
				}
				else{
					message=receivePacket.getAddress().getHostAddress()+"`934~ "+message;//appending sender ip
					InetAddress ip=InetAddress.getByName(destination);
					int port= (int) connections.get(ip);	
					sbyte=message.getBytes();
					DatagramPacket sendPacket=new DatagramPacket(sbyte, sbyte.length,ip,port );
					ss.send(sendPacket);
				}
			}
			//if(inter.checkTermination())

			
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
	}
	
	public void broadcast(String code,DatagramSocket ss,String disconnectip ){
		int work=Integer.parseInt(code);
		String message;
		InetAddress ip=null,ip1=null;
		int port=0;
		try {ip1=InetAddress.getLocalHost();} catch (UnknownHostException e1) {e1.printStackTrace();}
		byte[] servermsg;
		DatagramPacket sendPacket;
		
		
		switch (work){
			case 1000://add online
				message=ip1.getHostAddress()+"`934~ "+"1000"+"`934~ "+this.name+"`934~ "+
						this.ip+"`934~ "+this.port;//message to client with command code 1000 which indicates client to add new system
				servermsg=message.getBytes();
				sendPacket=new DatagramPacket(servermsg,servermsg.length);
				
				for(int i=0; i<inter.table.getRowCount(); i++){
					try {
						ip=InetAddress.getByName((String)inter.table.getValueAt(i, 1));
						port=(Integer)inter.table.getValueAt(i, 2);
						sendPacket.setAddress(ip);
						sendPacket.setPort(port);
						ss.send(sendPacket);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
		
			case 1001:/*A user disconnects. Server broadcasts that message to all clients*/
				message=ip1.getHostAddress()+"`934~ "+"1001"+"`934~ "+disconnectip;
				int rowLoc =-1;
				servermsg=message.getBytes();
				System.out.println("Message: "+message);
				sendPacket=new DatagramPacket(servermsg,servermsg.length);
			
				for(int i=0; i<inter.table.getRowCount(); i++){
					try {
						//System.out.println("i= "+i+"\tcount= "+inter.table.getRowCount());
						ip=InetAddress.getByName((String)inter.table.getValueAt(i, 1));
						//System.out.println("Disconnect: "+disconnectip+"\tcheck: "+ip.getHostAddress());
						if(ip.getHostAddress().equals(disconnectip)){
							System.out.println("MYIP: "+disconnectip);
							rowLoc=i;
							//connections.remove(InetAddress.getByName((String)inter.table.getValueAt(i, 1)));
							connections.remove(InetAddress.getByName(disconnectip));
						}
						else{
							port=(Integer)inter.table.getValueAt(i, 2);
							sendPacket.setAddress(ip);
							sendPacket.setPort(port);
							ss.send(sendPacket);
						}
					} catch (Exception e) {e.printStackTrace();}
			}
			if(rowLoc!=-1)
			inter.tableModel.removeRow(rowLoc);
			break;
		
		case 1002:/*Server disconnects*/
			message=ip1.getHostAddress()+"`934~ "+"1002"+"`934~ "+"Server Disconnect";
			System.out.println("Message: "+message);
			servermsg=message.getBytes();
			sendPacket=new DatagramPacket(servermsg,servermsg.length);
			
			search=connections.keys();
			int flag=0;
			
			while(inter.table.getRowCount()!=0){
				try {
					ip=InetAddress.getByName((String)inter.table.getValueAt(inter.table.getRowCount()-1, 1));
					port=(Integer)inter.table.getValueAt(inter.table.getRowCount()-1, 2);
					sendPacket.setAddress(ip);
					sendPacket.setPort(port);
					ss.send(sendPacket);
					inter.tableModel.removeRow(inter.table.getRowCount()-1);
				} catch (ArrayIndexOutOfBoundsException e) {e.printStackTrace();}
				catch(UnknownHostException e){e.printStackTrace();}
				catch(IOException e){e.printStackTrace();}
				
			}
			
			
			/*for(int i=inter.table.getRowCount()-1; i>=0; i--){
				try {
					ip=InetAddress.getByName((String)inter.table.getValueAt(i, 1));
					port=(Integer)inter.table.getValueAt(i, 2);
					sendPacket.setAddress(ip);
					sendPacket.setPort(port);
					ss.send(sendPacket);
				} catch (Exception e) {e.printStackTrace();}
				inter.table.remove(i);
			}*/
			connections.clear();
			this.ss.close();
			break;
		
		}
	}
	
	
	
	public void setDetails(int port,InetAddress ip, String newname){
		this.port=String.valueOf(port);
		this.name=newname;
		this.ip=ip.getHostAddress();
		
	}
}
