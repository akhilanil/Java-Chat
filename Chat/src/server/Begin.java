package server;

public class Begin implements Runnable {
	BackEnd be;
	Thread t;
	Begin(BackEnd be){
		
		this.be=be;
		t= new Thread(this,"START");
		t.start();	

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){be.start();}
		
	}
	

}
