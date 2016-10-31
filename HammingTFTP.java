import java.net.UnknownHostException;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.SocketException;

public class HammingTFTP{
  private static String usage = "java HammingTFTP  [ error | noerror ]  tftphost  file";


  private static byte[] recBuff = new byte[1024];
  private static Integer recBuffSize = new Integer(1024);

  private static final int RET_PAR_NO_ERR = 1;
  private static final int RET_PAR_ERR = 2;
  private static final int DATA = 3;
  private static final int ACK = 4;
  private static final int ERROR = 5;
  private static final int NACK = 6;



  final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();


  private static boolean error = false;

  public static void main(String[] args){
      if(args.length != 3){
        println(usage);
        return;
      }
      if(args[0].equals("error")){
        error = true;
      }
      else if(args[0].equals("noerror")){
        error = false;
      }
      else{
        println(usage);
        return;
      }

      HammingNetGuy hng;
      try{ hng = new HammingNetGuy(args[1]); }
      catch(UnknownHostException e){
        println("Unknown Host " + args[1]);
        return;
      }
      catch(SocketException e){
        e.printStackTrace();
        return;
      }



      if(!error){

        try{
          hng.requestParOnly(args[2]);
          int buffSize = hng.receive(recBuff , recBuffSize);

          int block = 0;
          int blockNum = 0;
          boolean end = false;

          FileOutputStream fileout = new FileOutputStream(args[2]);

          while(!end){
            if(buffSize != 516){
              end = true;
            }
            blockNum = recBuff[3];
            for(int i = 4; i < buffSize; i += 4){

              block = ((int)(recBuff[i+3] << 24) & 0xFF000000)|
                      ((int)(recBuff[i+2] << 16) & 0xFF0000)|
                      ((int)(recBuff[i+1] << 8) & 0xFF00)|
                      ((int)(recBuff[i]) & 0xFF);
              println("REVBLOCK " + block);
              block = parseDataBlock(block);

              HammingWriterGuy.HamWrite(block,fileout);
            }
            if(!end){
              hng.ack(blockNum);
              buffSize = hng.receive(recBuff , recBuffSize);
            }

          }
          fileout.close();
          int overflow = (buffSize - 4) % 4;
          if(overflow != 0){
            trimFile(args[2] , overflow);
          }
        }
        catch(Exception e){
          e.printStackTrace();
          return;
        }

      }

  }

  private static void trimFile(String filename, int overflow) throws FileNotFoundException,IOException{
    RandomAccessFile f = new RandomAccessFile(filename, "rw");
    f.setLength(f.length() - overflow);
    f.close();
  }
  /*
  private static void responseHandler(int packetlen, HammingNetGuy hng){
    int op = recBuff[1];
    switch(op){
      case RET_PAR_NO_ERR:
      case RET_PAR_ERR:
      case DATA:

        break;
      case ACK:
      case ERROR:
        parseError(packetlen);
        break;
      case NACK:
    }

  }
  */


  private static int parseDataBlock(int block){
      println("Block " + block);
      int data = 0;
      int revblock = BitReverse.reverse(block);
      println("RevBlock " + block);
      for(int i = 0; i < 32; i++){
        switch(i){
          case 31:
            break;
          case 30:
            break;
          case 28:
            break;
          case 24:
            break;
          case 16:
            break;
          case 0:
            break;
          default:
            data = (revblock & 1) ^ data;
            data = data << 1;
        }
        revblock = revblock >>> 1;
      }
    data = data << 5;
    println("DATA " + data);
  }
  return data;

  private static int parseDataErrBlock(int block) throws Exception{
      println("Block " + block);
      int[] ps = {1,1,1,1,1,1};
      int p1 = block & 0xAAAAAAAA;
      int p2 = block & 0x33333333;
      int p4 = block & 0x0F0F0F0F;
      int p8 = block & 0x00FF00FF;
      int p16 = block & 0x0000FFFF;
      int pall = block;



      for(int i = 0; i < 32;i++){
        ps[0] = ps[0] ^ (p1 & 1);
        ps[1] = ps[1] ^ (p2 & 1);
        ps[2] = ps[2] ^ (p4 & 1);
        ps[3] = ps[3] ^ (p8 & 1);
        ps[4] = ps[4] ^ (p16 & 1);
        ps[5] = ps[5] ^ (pall & 1);

        p1 = p1 >>> 1;
        p2 = p2 >>> 1;
        p4 = p4 >>> 1;
        p8 = p8 >>> 1;
        p16 = p16 >>> 1;
        pall = pall >>> 1;
      }


      if(ps[5] != (block >>> 31)){
        throw new Exception();
        return;
      }
      int problemBit = 0;
      for(int i = 0; i < ps.length; i++){
        if(ps[i] == 0){

        }
      }


      int data = 0;


    return data;
  }
  private int checkParity(int block){
    int parity = 1;
    for(int i = 0; i < 32; i++){
      parity = (block & 1) ^ parity;
    }
    return parity;

  }
  private static void println(String line){
    System.out.println(line);
  }
  //DELETE

  public static String bytesToHex(byte[] bytes,int size) {
    char[] hexChars = new char[size * 2];
    for ( int j = 0; j < size; j++ ) {
        int v = bytes[j] & 0xFF;
        hexChars[j * 2] = hexArray[v >>> 4];
        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
}








}
