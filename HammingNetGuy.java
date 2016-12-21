import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.io.IOException;



public class HammingNetGuy{
  private static int TFTP_PORT_NUM = 7000;
  private InetAddress host;
  private DatagramSocket conn;
  private byte[] toSend;
  private int toSendSize;
  private DatagramPacket received;
  private byte[] receivedBuff;
  private int receivedBuffSize;

  private static byte[] octet = "octet".getBytes();


  /*
      This function returns a HammingNetGUy that handles all incoming
      and outgoing connections

  */
  public HammingNetGuy(String thehost) throws UnknownHostException,SocketException{
    conn = new DatagramSocket();
    System.out.println(conn.getLocalPort());
    toSend = new byte[1024];
    toSendSize = 0;
    receivedBuff = new byte[1024];
    receivedBuffSize = 1024;
    host =  InetAddress.getByName(thehost);

  }


  public boolean requestParOnly(String filename) throws IOException{
    //Set OPCODE
    toSend[0] = (char)0;
    toSend[1] = (char)1;


    toSendSize = 2;
    //Set FILENAME
    for(int i=0; i < filename.length(); i++){
        if((i+2) > 1013){
          System.out.println("filename too long.");
          return false;
        }
        toSend[i+2] = (byte)filename.charAt(i);
        toSendSize += 1;
    }
    //nullbyte
    toSend[toSendSize] = (char)0;
    toSendSize += 1;
    //octet
    for(int i = 0; i < 5; i++){
      toSend[toSendSize] = octet[i];
      toSendSize += 1;
    }
    // nullbyte
    toSend[toSendSize] = (char)0;
    toSendSize += 1;
    DatagramPacket packet = new DatagramPacket(
                                toSend,toSendSize,host,TFTP_PORT_NUM);
    conn.send(packet);

    return true;
  }


  public boolean requestParErr(String filename) throws IOException{
    toSend[0] = (char)0;
    toSend[1] = (char)1;
    toSendSize = 2;
    for(int i=0; i < filename.length(); i++){
        if((i+2) > 1013){
          System.out.println("filename too long.");
          return false;
        }
        toSend[i+2] = (byte)filename.charAt(i);
        toSendSize += 1;
    }
    toSend[toSendSize] = (char)0;
    toSendSize += 1;
    for(int i = 0; i < 5; i++){
      toSend[toSendSize] = octet[i];
      toSendSize += 1;
    }
    toSend[toSendSize] = (char)0;
    toSendSize += 1;
    DatagramPacket packet = new DatagramPacket(
                                toSend,toSendSize,host,TFTP_PORT_NUM);
    conn.send(packet);

    return true;
  }

  public void ack(int blockNum) throws IOException{
    toSend[0] = (char)0;
    toSend[1] = (char)4;
    toSend[2] = (byte)((blockNum & 0xFF00) >>> 8);
    toSend[3] = (byte)(blockNum & 0xFF);
    DatagramPacket packet = new DatagramPacket(
                                toSend,4,host,TFTP_PORT_NUM);
    conn.send(packet);

  }
  public void nack(int blockNum)throws IOException{
    toSend[0] = (char)0;
    toSend[1] = (char)6;
    toSend[2] = (byte)((blockNum & 0xFF00) >>> 8);
    toSend[3] = (byte)(blockNum & 0xFF);
    DatagramPacket packet = new DatagramPacket(
                                toSend,4,host,TFTP_PORT_NUM);
    conn.send(packet);

  }


/*
  This function recieves packets and fills provided buffers

*/
  public int receive(byte[] buffToFill, Integer buffSize ) throws IOException,SocketTimeoutException{

        DatagramPacket packetIN = new DatagramPacket(
                                    buffToFill,buffSize);
        conn.setSoTimeout(10000);
        conn.receive(packetIN);
        buffSize = packetIN.getLength();
        if(buffToFill[1] == 5){
          System.out.println(new String(buffToFill));
          throw new IOException("Server Error");
        }
        //System.out.println("recieved " + receivedBuff[1]);
        return buffSize;

  }




}
