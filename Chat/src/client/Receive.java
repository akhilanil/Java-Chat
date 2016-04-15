package client;

public class Receive implements Runnable{

	SendReceive sr;
	Thread r;
	Receive(SendReceive sr){
		this.sr=sr;
		r= new Thread(this,"Start");
		r.start();
	}
	
	public void run(){
		System.out.println("adqwqwqw");
		while(true){
			sr.receiveMessage();
		}
		
	}

}
