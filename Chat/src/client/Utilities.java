package client;


import javax.swing.JFrame;

public class Utilities {
	
	
	JFrame frame;
	SendReceive sr;
	Thread t;
	Interface inter;
	Storage s;
	Communication com;
	public Utilities( ){
		s= new Storage();						
		
		sr=new SendReceive(s,inter);
		inter=new Interface(s,sr);
		inter.frame.setEnabled(false);
		sr.inter=this.inter;
		
	//	System.out.println("hey");
		
		try {						/*Connected to server*/
				sr.checkConnection();
		} catch (InterruptedException e) {e.printStackTrace();}
		inter.frame.setEnabled(true);
		new Send(sr);
		new Receive(sr);
		System.out.println("Ready to go");
	}
	
	
	public void createAuthentication(){
		
		
		
	}

}
