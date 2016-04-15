package client;

public class Send implements Runnable {
	
	SendReceive sr;
	Thread s;
	Send(SendReceive sr){
		this.sr=sr;
		System.out.println("hello");
		s= new Thread(this,"Start");
		s.start();
	}
	@Override
	public void run() {
		System.out.println("asd");
		while(true){
			sr.sendMessage();
		}
		
	}
	
		

}
