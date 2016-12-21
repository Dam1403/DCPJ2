import java.io.FileOutputStream;
import java.io.IOException;

public class HammingWriterGuy{

  private static char overflow = (char)0;
  private static int size_of_overflow = 0;
  private static int BLOCK_SIZE  = 8;
  private static byte[] buff = new byte[3];
  private static byte[] over8 = new byte[1];
  public static void HamWrite(int inbits,FileOutputStream outFile) throws IOException{

      char newByte = (char)0;
      //Write Overflow
      newByte = (char)(overflow ^ newByte);
      //Start writing after where you wrote the overflow
      inbits = inbits >>> size_of_overflow;
      //Write three Bytes to the file
      newByte = (char)(newByte ^ (inbits >>> 24));
      buff[0] = BitReverse.reverseByte((byte)newByte);
      buff[1] = BitReverse.reverseByte((byte)((inbits >>> 16) & 0xFF));
      buff[2] = BitReverse.reverseByte((byte)((inbits >>> 8) & 0xFF));

      outFile.write(buff);

      //Store overflow byte
      overflow = (char)(inbits & 0xFF);
      //increment overflow size
      overflow_inc();
      //WHen overflow is zero after the first time you have to write a full
      // byte of overflow
      if(size_of_overflow == 0){
        over8[0] = (byte)overflow;
        outFile.write(BitReverse.reverseByte(over8[0]));
        overflow = (char)0;
      }
  }


  private static void overflow_inc(){
    if(size_of_overflow == 6){
       size_of_overflow = 0;
    }
    else{
      size_of_overflow += 2;
    }
  }


}
