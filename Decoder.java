/**
 * [Decoder].java
 * @author John Huang
 * @date 10/5/2017
 * To decode a huffman compressed file
 */

//imports
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * class Decoder
 * this is the main class. it decodes a huffman compressed file
 */ 
class Decoder{
  public static void main (String [] args)throws Exception{
    //declare variables
    Scanner input = new Scanner (System.in);
    FileInputStream in = null;
    FileOutputStream out = null;  
    ArrayList<Integer> encodedArray = new ArrayList<Integer>();
    HashMap<String,Character> storedBytes = new HashMap<String,Character>();
    String fileNameIn;
    String fileNameOut;      
    StringBuilder tempEncoded;
    int numChars=0;
    int count;
    int zeroIgnore;
    //ask for file name
    System.out.println("Which file do you want to decode?");
    //store file name to read
    fileNameIn = input.next();
    System.out.println("What do you want to name your file?");
    //store file name to output to
    fileNameOut=input.next();
    //close input
    input.close();
    //take in the encoded message
    try {
      //create reader
      in = new FileInputStream(fileNameIn);
      //declare variables
      int c;
      int char1;
      int char2;      
      //take in char
      numChars=in.read();
      // if num chars is equal to 0 then let it equal 256, as if its 0 then it can only be 256 bytes
      if (numChars==0){
        numChars=256;
      }
      //loop for every character in the table
      for (int i=0;i<numChars;i++){
        //string to keep the key
        String bin="";
        //declare variable for number of bytes in the character
        int numBytes=0;
        //take in 2 chars, one for the character, one for the length of the bytes needed to store 
        char1=in.read();
        char2=in.read();
        //div by 8
        numBytes=char2/8;
        //if the remainder isnt 0 when modding, add 1
        if (char2%8!=0){
          numBytes++;
        }
        //loop through to get the bytes representing the characters
        for (int g=0;g<numBytes;g++){
          //take in the character
          int char34 = in.read();
          //convert to binary and add to bin string
          bin=bin+OutputBinaryEquivalent(char34); 
        }
        //take out needed parts of the string
        bin=bin.substring(bin.length()-char2);
        //put the key and respective character in hashmap
        storedBytes.put(bin,(char)char1);
      }
      //loop through rest of file to get the encoded msg
      while ((c = in.read()) != -1) {        
        encodedArray.add(c);            
      }
      //finally block to close inputstream
    } finally {
      if (in != null) {
        in.close();
      }      
    }
    //get the number of zeros to ignore at the end
    zeroIgnore=encodedArray.get(encodedArray.size()-1);
    //remove the last one cus its the numebr of zeros to ignore
    encodedArray.remove(encodedArray.size()-1);   
    
    //initialize count
    count=-1;
    //initialize index
    int index=0;
    //initialize tempEncoded
    tempEncoded=new StringBuilder();
    //add the first character converted into binary to tempEncoded
    tempEncoded.append(OutputBinaryEquivalent((int)encodedArray.get(0)));
    //try and catch
    try{
      //initialize the outputstream
      out = new FileOutputStream(fileNameOut);
      //loop through encodedarray
      while (index<encodedArray.size()-1){
        
        //add 1 to count
        count++;
        //if the substring is found in the hashmap, do this
        if (storedBytes.containsKey(tempEncoded.substring(0,count))){ 
          //output to file the character associated with the substring
          out.write((int)storedBytes.get(tempEncoded.substring(0,count)));
          //delete from the variable the used bits
            tempEncoded.delete(0,count);                     
          //set count to -1
          count=-1;          
          //if count is equal to the length of tempEncoded
        }else if (count==tempEncoded.length()){      
          //add 1 to index
          index++;
          //add to the next byte to the tempEncoded
          tempEncoded.append(OutputBinaryEquivalent((int)encodedArray.get(index)));    
          //if its the last one in the array
          if (index==encodedArray.size()-1){
            //delete the zeros specified earlier
            tempEncoded.delete(tempEncoded.length()-zeroIgnore,tempEncoded.length());            
          }          
          //set count to -1
          count=-1;
        }
      }
      //initialize variable to -1
      int countLastByte=-1;
      //same logic as above loop, except i needed to separate it due to the mechanisms of the loop as it won't work for the last few bits
      while (tempEncoded.length()>0){
        countLastByte++;
        if (storedBytes.containsKey(tempEncoded.substring(0,countLastByte))){          
          out.write((int)storedBytes.get(tempEncoded.substring(0,countLastByte)));        
          tempEncoded.delete(0,countLastByte);         
          //set count to 0
          countLastByte=-1;
        }
      }
      //close output
      out.close();    
      //catch block to catch exceptions
    }catch(Exception FileNotFoundException) {
      System.out.println("file not found");
    }
  }
  /**
   * Accepts integer and converts it into its binary form and returns it in the form of a StringBuilder
   * @param int c represents a char
   * @return StringBuilder representing binary of int c
   */ 
  public static StringBuilder OutputBinaryEquivalent(int c) { 
    //initialize variable
    StringBuilder binEquiv=new StringBuilder();
    //loop 8 times
    for (int i=7;i>=0;i--) { 
      //if greater than 0 
      if (( (char)c & (char)Math.pow(2,i)  ) > 0) {
        //add 1
        binEquiv.append("1");
      }else {
        //add 0
        binEquiv.append("0");
      }
    }
    //return
    return binEquiv;
  }
}
