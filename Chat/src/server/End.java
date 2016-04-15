package server;

public class End implements Runnable {
	BackEnd be;
	Thread t;
	End(BackEnd be){
		
		this.be=be;
		t= new Thread(this,"START");
		t.start();	

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){be.stop();}
		
	}
	

}
