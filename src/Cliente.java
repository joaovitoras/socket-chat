import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.Font;

public class Cliente extends JFrame implements ActionListener, KeyListener {
  private static final long serialVersionUID = 1L;
  private JTextArea texto;
  private JTextField txtMsg;
  private JButton btnSend;
  private JLabel lblHistorico;
  private JPanel pnlContent;
  private Socket socket;
  private OutputStream ou ;
  private Writer ouw;
  private BufferedWriter bfw;
  private JTextField txtNome;
  private JTextField txtIp;
  private JTextField txtPorta;

  public Cliente() throws IOException {
    txtNome = new JTextField("Cliente");
    txtIp = new JTextField("127.0.0.1");
    txtPorta = new JTextField("2019");
    Object[] texts = {txtNome, txtIp, txtPorta};
    JOptionPane.showMessageDialog(null, texts);
    pnlContent = new JPanel();
    texto = new JTextArea(8, 20);
    texto.setBorder(UIManager.getBorder("TextField.border"));
    texto.setFont(new Font("Helvetica Neue", Font.PLAIN, 14));
    texto.setEditable(false);
    txtMsg = new JTextField(15);
    txtMsg.setPreferredSize(new Dimension(250, 34));
    lblHistorico = new JLabel("Mensagens");
    btnSend = new JButton("Enviar");
    btnSend.setPreferredSize(new Dimension(82, 34));
    btnSend.setToolTipText("Enviar Mensagem");
    btnSend.addActionListener(this);
    btnSend.addKeyListener(this);
    txtMsg.addKeyListener(this);
    JScrollPane scroll = new JScrollPane(texto);
    scroll.setBorder(null);
    scroll.setPreferredSize(new Dimension(278, 244));
    texto.setLineWrap(true);
    pnlContent.add(lblHistorico);
    pnlContent.add(scroll);
    pnlContent.add(txtMsg);
    pnlContent.add(btnSend);
    pnlContent.setBackground(Color.WHITE);
    setTitle(txtNome.getText());
    setContentPane(pnlContent);
    setLocationRelativeTo(null);
    setResizable(false);
    setSize(300,340);
    setVisible(true);
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent we) {
        try {
          sair();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
  }

  public void conectar() throws IOException {
    socket = new Socket(txtIp.getText(), Integer.parseInt(txtPorta.getText()));
    ou = socket.getOutputStream();
    ouw = new OutputStreamWriter(ou);
    bfw = new BufferedWriter(ouw);
    bfw.write(txtNome.getText()+ "\r\n");
    bfw.flush();
  }

  public void enviarMensagem(String msg) throws IOException {
    if( msg.equals("Sair")) {
      bfw.write("Desconectado \r\n");
      texto.append("Desconectado \r\n");
    } else if (!msg.equals("")) {
      bfw.write(msg + "\r\n");
      texto.append("[" + txtNome.getText() + "]: " + txtMsg.getText()+"\r\n");
    }
    bfw.flush();
    txtMsg.setText("");
    txtMsg.grabFocus();
  }

  public void escutarServidor() throws IOException {
    InputStream in = socket.getInputStream();
    InputStreamReader inr = new InputStreamReader(in);
    BufferedReader bfr = new BufferedReader(inr);
    String msg = "";

    while(!"Sair".equalsIgnoreCase(msg))
      if(bfr.ready()) {
        msg = bfr.readLine();

        if (msg.equals("Sair")) {
          texto.append("Servidor caiu! \r\n");
        } else if (!msg.contains("null")) {
          texto.append(msg + "\r\n");
        }
      }
  }

  public void sair() throws IOException {
    enviarMensagem("Sair");
    bfw.close();
    ouw.close();
    ou.close();
    socket.close();
    System.exit(0);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    try {
      if(e.getActionCommand().equals(btnSend.getActionCommand()))
        enviarMensagem(txtMsg.getText());
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

  @Override
  public void keyPressed(KeyEvent e) {

    if(e.getKeyCode() == KeyEvent.VK_ENTER) {
      try {
        enviarMensagem(txtMsg.getText());
      } catch (IOException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent arg0) {
  }

  @Override
  public void keyTyped(KeyEvent arg0) {
  }

  public static void main(String []args) throws IOException {
    Cliente app = new Cliente();
    app.conectar();
    app.escutarServidor();
  }
}
