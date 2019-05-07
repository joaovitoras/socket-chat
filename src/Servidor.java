import java.io.BufferedReader;
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
  private InputStreamReader leitorEntradaCliente;
  private BufferedReader leitorCliente;

  public Servidor(Socket con) {
    this.con = con;

    try {
      leitorEntradaCliente = new InputStreamReader(con.getInputStream());
      leitorCliente = new BufferedReader(leitorEntradaCliente);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void run() {
    try {
      String message;
      Writer escritorSaidaCliente = new OutputStreamWriter(this.con.getOutputStream());
      clientes.add(escritorSaidaCliente);
      nome = message = leitorCliente.readLine();
      enviarParaTodos(escritorSaidaCliente, "Se conectou");
      
      while(!"Sair".equalsIgnoreCase(message) && message != null) {
        message = leitorCliente.readLine();
        enviarParaTodos(escritorSaidaCliente, message);
      } 

      clientes.remove(escritorSaidaCliente);
      escritorSaidaCliente.close();
      leitorCliente.close();
    } catch (Exception e) {
      e.printStackTrace();

    }
  }

  public void enviarParaTodos(Writer escritorCliente, String msg) throws  IOException {
    msg = "[" + nome + "]: " + msg+ "\r\n";
    
    System.out.println(msg);
    
    for(Writer escritorOutroCliente : clientes) {
      if(escritorCliente != escritorOutroCliente) {
        escritorOutroCliente.write(msg);
        escritorOutroCliente.flush();
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
