package server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class Interface {/*Create a User Interface*/

	
		JFrame frame;
		JPanel upanel,dpanel;
		JSplitPane divider;
		JButton start,stop;
		JScrollPane scroll;
		JTable table;
		DefaultTableModel tableModel;
		boolean terminate,flag;
		DatagramSocket ss;
		BackEnd be;
		
		Interface(DatagramSocket ss,BackEnd bef){
			terminate=true;
			this.be=bef;
			this.ss=ss;
			frame= new JFrame("Server");
			frame.setSize(500,500);
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frame.setVisible(true);
			frame.setLocationRelativeTo(null);
			
			divider= new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			createtop_panel();
			createbottom_panel();
			
			divider.setLeftComponent(upanel);
			divider.setRightComponent(dpanel);
			divider.setDividerLocation(430);
			divider.setEnabled(false);
			divider.setDividerSize(1);
			
			stop.setEnabled(false);
			frame.add(divider);
			frame.validate();
			
			
			
			
		}
		
		void createtop_panel(){/*Create a panel on top*/
			upanel=new JPanel();
		//	upanel.setBackground(Color.BLACK);
			createTable();
			scroll=new JScrollPane(table);
			upanel.add(scroll, BorderLayout.CENTER);

			
			
		}
		
		void createbottom_panel(){/*Create a panel on bottom*/
			
			dpanel=new JPanel();
			start= new JButton("START");
			stop= new JButton("STOP");
			dpanel.add(start);
			dpanel.add(stop);
			
			start.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					terminate=false;
					start.setEnabled(false);
					stop.setEnabled(true);
					System.out.println("STARTED");
					frame.setTitle("Server Running");
					//System.out.println(ss.getLocalPort());
					//if(flag==true){ss=new}
					
				}
			});
			stop.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					be.broadcast("1002", ss, null);
					terminate=true;
					stop.setEnabled(false);
					start.setEnabled(true);
					System.out.println("STOPPED");
					frame.setTitle("Server");
					//System.exit(0);
					
					
				} 
			});
			frame.addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent e) {
					be.broadcast("1002", ss, null);
					terminate=true;
					frame.dispose();
					System.exit(0);
					
				}
			});
		}

		void createTable(){/*table containing all online members */
			
			table= new JTable();
			tableModel = new DefaultTableModel(new Object[]{"Name","Ip Address","Port"},0);
			table.setModel(tableModel);
		}
		
		boolean checkTermination(){
			return terminate;
			
		}
		
		void insertTable(String name,String ip,int port){
			
			tableModel.addRow(new Object[]{name,ip,port});
			
			
			
		}
		
		void newSocket(DatagramSocket ss){
			
			this.ss=ss;
		}
		


}
