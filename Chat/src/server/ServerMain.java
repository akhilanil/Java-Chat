package server;

public class ServerMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Interface inter=new Interface();
		BackEnd be= new BackEnd();
		new Begin(be);
		new End(be);
	}

}
