package client;


import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;



public class Interface {

	
	JFrame frame;
	JSplitPane divider;
	JPanel lpanel,rpanel;
	JTable table,chats;
	DefaultTableModel tableModel,chatsModel;
	String[] online;
	JScrollPane scrollonline,scrollchats;
	Map<Integer, List<String>> map;// rowno,ip,port
	InetAddress sendip;
	int sendport;
	Storage s;
	SendReceive sr;
	String isAliveip="";
	ChatBox cb;
	String myName;
	Interface(Storage s,SendReceive sr){
		this.s=s;
		this.sr=sr;
		
		frame= new JFrame("Client");
		map = new HashMap<Integer, List<String>>();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		frame.setLocationRelativeTo(null);
		
		createleft();
		createright();
		
		divider= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		divider.setLeftComponent(scrollonline);
		divider.setRightComponent(scrollchats);
		
		divider.setDividerLocation(200);
		//divider.setEnabled(false);
		divider.setDividerSize(1);
		
		frame.add(divider);
		System.out.println(divider.getLeftComponent().getSize());
		frame.validate();
		
		
		frame.addWindowListener(new WindowAdapter(){

			
			public void windowClosing(WindowEvent e) {
				
				if (JOptionPane.showConfirmDialog(frame, 
						"Do you Want to LogOut..??","Logout",  
			            JOptionPane.YES_NO_OPTION,
			            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
						try {
							sr.setMessage("",InetAddress.getLocalHost().getHostAddress());
						} catch (UnknownHostException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						frame.dispose();
						System.exit(0);
			        }
				
				
				//sr
			}

			public void windowClosed(WindowEvent e) {
				try {
					sr.setMessage("",InetAddress.getLocalHost().getHostAddress());
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		});
		
		
	}
	
	void createright(){
		lpanel=new JPanel();
		lpanel.setLayout(new FlowLayout());
		System.out.println(lpanel.getSize());
		
		
		
		
		chats= new JTable();
		chatsModel = new DefaultTableModel(new Object[]{"My Chats","IP ADDRESS","UNREAD"},0){
			
			public boolean isCellEditable(int row, int column){
				return false;
			}
			
		};
		//String name="AKHIL";
		//chatsModel.addRow(new Object[]{name});
	//	chatsModel.addRow(new Object[]{"AKHIL","192.168.1.1"});
		
		chats.setModel(chatsModel);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		
		chats.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		chats.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		chats.getColumnModel().getColumn(0).setPreferredWidth(950);
		chats.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		chats.getColumnModel().getColumn(1).setPreferredWidth(100);	
		chats.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
		chats.getColumnModel().getColumn(2).setPreferredWidth(100);	
		chats.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		chats.setRowHeight(60);
		
				
		
		
		scrollchats=new JScrollPane(chats);
		scrollchats.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		Interface i=this;
		
		
		chats.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				
				if(e.getClickCount()==2){
					
					JTable target= (JTable)e.getSource();
					System.out.println(target.getSelectedRow()+1);
					
					String name=(String) target.getValueAt(target.getSelectedRow(), 0);
					String ip=target.getValueAt(target.getSelectedRow(), 1).toString().substring(1);
					String port=target.getValueAt(target.getSelectedRow(), 2).toString();
					int unread= (Integer) target.getValueAt(target.getSelectedRow(), 2);
					target.setValueAt((Integer) 0, target.getSelectedRow(), 2);
					try {
						cb=new ChatBox(name,InetAddress.getByName(ip),Integer.parseInt(port),sr,unread,s,i);
					} catch (Exception e1) {e1.printStackTrace();}
					
				}
				
			}
		});
		//scrollchats.setMaximumSize(new Dimension(1000,700));
		//lpanel.add(scrollchats,BorderLayout.CENTER);
		//lpanel.add(scrollchats);
	}
	
	void createleft(){//all online systems
		
		
		rpanel=new JPanel();
		rpanel.setVisible(true);
		rpanel.setPreferredSize(new Dimension(1012,703));
		
		table= new JTable();
		tableModel = new DefaultTableModel(new Object[]{"No","Online"},0){
			
			public boolean isCellEditable(int row, int column){
				return false;
			}
			
		};
				
		table.setModel(tableModel);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		
		
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(0).setPreferredWidth(20);
		table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(1).setPreferredWidth(185);	
		
		
		Interface i=this;
		table.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				int unread=0;
				if(e.getClickCount()==2){
					
					JTable target= (JTable)e.getSource();
					System.out.println(target.getSelectedRow()+1);
					String name=(String) target.getValueAt(target.getSelectedRow(), 1);
					System.out.println(name);
					getIP_port(target.getSelectedRow()+1);
					
					if(!checkTable(sendip)){
						//add to table
						chatsModel.addRow(new Object[]{name,sendip,0});
						
					}
					else{
						for(int i=0; i<chats.getRowCount(); i++){
							if(sendip.getHostAddress().equals((String) table.getValueAt(i, 1))){
								unread=(Integer)table.getValueAt(i, 2);
								break;
							}
						}
					}
					cb=new ChatBox(name,sendip,sendport,sr,unread,s,i);
				}
				
			}
		});
		
		scrollonline=new JScrollPane(table);
		
		
		
	}
	
	public void addOnline(String name,InetAddress ip, int port){//from server connected via utilities,new online system
		
		tableModel.addRow(new Object[]{table.getRowCount()+1,name});
		List<String> online = new ArrayList<String>();
		online.add(ip.getHostAddress());
		System.out.println("NEW MAN: "+ip.getHostAddress());
		System.out.println("NEW MAN1: "+ip.getHostAddress().substring(1));
		online.add(String.valueOf(port));
		map.put(table.getRowCount(), online);
		
		
		//onlineOne.
		
		
		
	}
	
	
	public void getIP_port(int row){//get ip from hash map
		
		InetAddress ip=null;
		 for (Map.Entry<Integer, List<String>> entry : map.entrySet()) {
	            int key = entry.getKey();
	            List<String> values = entry.getValue();
	            if(key==row){
	            	try {
						sendip=InetAddress.getByName(values.get(0));
						sendport=Integer.parseInt((values.get(1)));
						break;
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
	            }
	            
	        }
		
		
	}
	
	public boolean checkTable(InetAddress ip){
		
		boolean available=false;
		InetAddress ip1;
		for(int i=0; i<chats.getRowCount(); i++){
				try {
					if(ip.equals(InetAddress.getByName( (String) chats.getModel().getValueAt(i, 1)))){
						
						available=true;
						break;
					}
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		}
		return available; 
		
	}
	
	public void getMessage(String msg,String ip){
		
		InetAddress newip = null;
		int i;
		Object value;
		int unread=0;
		boolean available=false;
		System.out.println("ToCheck: "+isAliveip+"\t"+ip);
		//if(isAliveip.equals(ip)){
		
		if(isAliveip.equals(ip)){// display incoming message.. 
			System.out.println("IAM ALIVE: "+msg);
			cb.display(false, false,msg);
			s.storeMessage(ip, msg, false);
		}
		else{
			s.storeFile(msg,ip);
			for(i=0; i<chats.getRowCount(); i++){// check if already present in chats table
				try {
					if(InetAddress.getByName(ip).equals(InetAddress.getByName( chats.getModel().getValueAt(i, 1).toString().substring(1)))){
						
						available=true;
						break;
					}
				} catch (UnknownHostException e) {e.printStackTrace();}
			}
			
			if(available){// if available in chats table
				unread=(Integer)chats.getValueAt(i, 2);
				unread++;
				Object obj=(Integer) unread;
				chats.setValueAt(obj, i, 2);
				
			}
			else{
				
				int row=0;
				String name;
				for (Map.Entry<Integer, List<String>> entry : map.entrySet()) {
		            int key = entry.getKey();
		            List<String> values = entry.getValue();
		            try {
						sendip=InetAddress.getByName(values.get(0));
						if(sendip.equals(ip)){
							row=key-1;
							break;
						}
					} catch (UnknownHostException e) {e.printStackTrace();}
		        }
				name=(String)table.getValueAt(row, 1);
				chatsModel.addRow(new Object[]{name,sendip,1});
			}
			
		}
			
		
		
		
	}
	
	public void serverCommands(String code,String task){
		System.out.println("Code: "+code+"\ntask: "+task+"\n");
		int work=Integer.parseInt(code);
		String[] adding;
		InetAddress ip=null;
		switch (work){
		
			case 1000://add online
				adding=task.split("`934~ ",3);
				try {ip = InetAddress.getByName(adding[1]);} catch (UnknownHostException e) {e.printStackTrace();}
				addOnline(adding[0],ip,Integer.parseInt(adding[2]));
				break;
		
			case 1001://a user disconnects
				task.replaceAll("\\s","");
				System.out.println("Alive:"+isAliveip+"\tTask: "+task);
				try {ip = InetAddress.getByName(task);} catch (UnknownHostException e) {e.printStackTrace();}
				
				for(int i=0; i<chats.getRowCount(); i++){// removing from chats if present
					System.out.println("Task: "+task+"\tchats: "+chats.getValueAt(i, 1).toString().substring(1));
					if(task.equals( chats.getValueAt(i, 1).toString().substring(1))){
						this.chatsModel.removeRow(i);
						break;	
					}
				}
				if(isAliveip.equals(task)){// removing chat is chatbox is active
					JOptionPane pane = new JOptionPane("Chat will be closed in 5 seconds",JOptionPane.DEFAULT_OPTION);
					JDialog d = pane.createDialog(cb.frame, "Connection lost...!!");
					d.pack();
					d.setModal(false);
					d.setVisible(true);
					try{
						Thread.sleep(1000);
						int counter=4;
						while (counter>=0) {
							pane.setMessage("Chat will be closed in "+counter+" second(s)");
							counter--;
							Thread.sleep(1000);
						} 
					}catch (InterruptedException ie) {ie.printStackTrace();}
					d.dispose();
					cb.frame.dispose();
				}
			
				for (Map.Entry<Integer, List<String>> entry : map.entrySet()) {
					int key = entry.getKey();
					List<String> values = entry.getValue();
					if(task.equals(values.get(0))){
						map.remove(key);// remove from hashmap
						for(int i=0; i<table.getRowCount(); i++){
							if(key==(Integer)table.getValueAt(i, 0))
								this.tableModel.removeRow(i);
						}
						break;
					}
				}
				break;
			
				
				
			case 1002:
				System.out.println("Disconnecting....!!!!");
				int i=0;
				
				while(table.getRowCount()!=0){
					tableModel.removeRow(i);
				}
				while(chats.getRowCount()!=0){
					chatsModel.removeRow(i);
				}
				if(!isAliveip.equals(""))
					cb.frame.dispose();
				JOptionPane pane = new JOptionPane("Program will exit in 5 second(s)",JOptionPane.DEFAULT_OPTION);
				JDialog d = pane.createDialog(this.frame, "Server Connection lost...!!");
				d.pack();
				d.setModal(false);
				d.setVisible(true);
				try{
					Thread.sleep(1000);
					int counter=4;
					while (counter>=0) {
						pane.setMessage("Program will exit in "+counter+" second(s)");
						counter--;
						Thread.sleep(1000);
					} 
				}catch (InterruptedException ie) {ie.printStackTrace();}
				d.dispose();
				
				frame.dispose();
				
				System.exit(0);
				
		}
		
	}
	
	
	
}
