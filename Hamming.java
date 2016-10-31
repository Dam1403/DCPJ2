

public class Hamming{

  private static char overflow = (char)0;
  private static int size_of_overflow = 0;
  private static char write_pos = 0;
  private static char write_anti_pos = 255 ^ write_pos;
  private static int BLOCK_SIZE  = 8;

  public static int HamWrite(byte[] inbits,int numToWrite){
    int i = 0;
    for(i; numToWrite <= BLOCK_SIZE;i++){
        
    }
  }

  private static spliceWrite(char toWrite,char writeTo int pos){


  }

  private static writePosIncrement(){
    if(write_pos == 63){
      write_pos = 0;
      return;
    }
    write_pos = (write_pos << 2) + 3;
    write_anti_pos = 255 & write_pos;
  }

    /*
      Keep running counter of how much youve written. when the total is over 32
      Subtract the boundry from the number this is how much you will write on the new boundray
      Subtract the new number from how much you write at a time to get how much is left in the old
      boundry
      */

}
