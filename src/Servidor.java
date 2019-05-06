import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Servidor extends Thread {
  private static ArrayList<Writer>clientes;
  private static ServerSocket server;
  private static JTextField txtPorta;
  private String nome;
  private Socket con;
  private InputStreamReader inputStreamReader;
  private BufferedReader servidor;

  public Servidor(Socket con) {
    this.con = con;

    try {
      inputStreamReader = new InputStreamReader(con.getInputStream());
      servidor = new BufferedReader(inputStreamReader);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void run() {
    try {
      String msg;
      Writer cliente = new OutputStreamWriter(this.con.getOutputStream());
      clientes.add(cliente);
      nome = msg = servidor.readLine();
      enviarParaTodos(cliente, "Se conectou");
      
      while(!"Sair".equalsIgnoreCase(msg) && msg != null) {
        msg = servidor.readLine();
        enviarParaTodos(cliente, msg);
      } 

      clientes.remove(cliente);
      cliente.close();
      servidor.close();
    } catch (Exception e) {
      e.printStackTrace();

    }
  }

  public void enviarParaTodos(Writer cliente2, String msg) throws  IOException {
    msg = "[" + nome + "]: " + msg+ "\r\n";
    
    System.out.println(msg);
    
    for(Writer cliente : clientes) {
      if(!(cliente2 == cliente)) {
        cliente.write(msg);
        cliente.flush();
      }
    }
  }
  
  public static void main(String []args) {
    try { 
      txtPorta = new JTextField("2019");
      Object[] texts = {txtPorta};
      JOptionPane.showMessageDialog(null, texts);
      server = new ServerSocket(Integer.parseInt(txtPorta.getText()));
      clientes = new ArrayList<Writer>();
      JOptionPane.showMessageDialog(null, "Servidor iniciado na porta " + txtPorta.getText());
      
      while(true) {
        System.out.println("Aguardando conexão");
        Socket con = server.accept();
        System.out.println("Cliente conectado");
        Thread t = new Servidor(con);
        t.start();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
