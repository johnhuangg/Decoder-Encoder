//importing classes
import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.StringBuilder;

/**
 * [Encoder.java]
 * compresses a file by encoding the characters
 * @author Carl Zhang, John Huang
 * @version 1.0
 */
class Encoder{
  public static void main(String args[]) throws Exception{
    
    //declaring vars
    StringBuilder fIle=new StringBuilder();
    
    StringBuilder chars=new StringBuilder();//all chars in the file
    
    ArrayList<Integer> charsCount=new ArrayList<Integer>();//count of chars occurance in the file
    
    int[] pairs;//array to keep track of pairs
    
    StringBuilder[] binaries;//array for binary code of chars
    
    String fileName;//file requested to be compressed
    
    String compressedName;//file requested to be compressed
    
    Scanner input=new Scanner(System.in);//scanner for user input
    
    FileInputStream file = null;//file reader
    
    //char currentChar;//for taking in chars from file
    
    int c;//current char for reading
    
    //getting file
    System.out.print("Enter name of file to be compressed");
    fileName=input.nextLine();
    System.out.print("Enter name of compressed file");
    compressedName=input.nextLine();
    input.close();
    
    try{//if errors come up from reading file
      
      file=new FileInputStream(new File(fileName));//getting file
      
      //counting chars from file
      while((c = file.read()) != -1){//if there is still stuff in the file
        fIle.append((char)c);//adds char to string
      }
      
      
      for (int i=0;i<fIle.length();i++){
        if (chars.indexOf(Character.toString(fIle.charAt(i)))==-1){
          
          chars.append(fIle.charAt(i));
          charsCount.add(1);       //if this char didnt get taken in before, add to chars and add the char to array
          
        }else{
          charsCount.set(chars.indexOf(Character.toString(fIle.charAt(i))),charsCount.get(chars.indexOf(Character.toString(fIle.charAt(i))))+1);//otherwise add 1 to count of char
          
        }
      }
      file.close();//close first file reader
      
      //setting up vars to make tree
      pairs=new int[charsCount.size()];
      binaries=new StringBuilder[charsCount.size()];//sets these arrays to match arraylist
      
      for (int i=0;i<pairs.length;i++){
        
        pairs[i]=i;//gives every char different pair index
        binaries[i]=new StringBuilder();
        
      }
      
      int frequency1;
      int frequency2;//variables to keep track of 2 frequencies being checked
      int pair1;
      int pair2;//vars to keep track of the 2 lowest frequencies indexes
      int frequency;//var to keep track of frequency being counted currently
      
      
      //loop for assigning binaries
      do{
        
        frequency1=0;
        frequency2=0;//resets the 2 frequencies being checked to 0
        pair1=-1;
        pair2=-1;
        
        //loop through every pair
        for (int i=0;i<pairs.length;i++){
          
          frequency=0;//resets current frequency to 0
          
          //loops through pairs looking for every pair with same index
          for (int j=0;j<pairs.length;j++){
            
            if (pairs[j]==i){
              
              frequency+=charsCount.get(j);//adds frequency of every char with same pair index
            }
          }
          
          if (frequency1!=0){//if first one is not blank check second one first (prevent error)
            
            if ((frequency<frequency2&&frequency!=0)||(frequency2==0&&frequency!=0)){
              
              frequency2=frequency;
              pair2=i;//if frequency is lower or didnt save frequency yet save current one
              
            }else if (frequency<frequency1&&frequency!=0){
              
              frequency1=frequency;
              pair1=i;//if frequency is lower or didnt save frequency yet save current one
              
            }
          }else if ((frequency<frequency1&&frequency!=0)||(frequency1==0&&frequency!=0)){
            
            frequency1=frequency;
            pair1=i;//if frequency is lower or didnt save frequency yet save current one
            
          }else if ((frequency<frequency2&&frequency!=0)||(frequency2==0&&frequency!=0)){
            
            frequency2=frequency;
            pair2=i;//if frequency is lower or didnt save frequency yet save current one
          }
        }
        
        if(pair1!=-1&&pair2!=-1){
          
          for (int i=0;i<pairs.length;i++){
            if (pairs[i]==pair1){
              binaries[i].insert(0,"0");//assigns all chars in pair 1 as a 0 and all chars in pair 2 as a 1
              pairs[i]=pair1;
            }else if (pairs[i]==pair2){
              binaries[i].insert(0,"1");
              pairs[i]=pair1;
            }
          }
        }
      }while(pair1!=-1&&pair2!=-1);//while theres 2 or more pairs left(not all chars have been paired)
      
      
      StringBuilder binary=new StringBuilder();//to keep track of current bits
      
      FileOutputStream fileOut = new FileOutputStream(compressedName);//file outputter
      
      fileOut.write(binaries.length);//write number of different chars
      
      String binaryCurrent;//takes in current binary string for manipulation
      
      for (int i=0;i<chars.length();i++){//loops through all chars to output decoding instructions
        
        fileOut.write((int)(char)chars.charAt(i));//writes the char
        
        fileOut.write(binaries[i].length());//writes the length of compressed code
        
        binaryCurrent=binaries[i].toString();//saves the current binary string being added to table
        
        //to output the chars binary code
        do{
          
          if (binaryCurrent.length()>8){//if code is longer than 8
            if (binaryCurrent.length()%8!=0){//if code is not whole 8 bits
              
              fileOut.write(Integer.parseInt(binaryCurrent.substring(0,binaryCurrent.length()%8),2));
              binaryCurrent=binaryCurrent.substring(binaryCurrent.length()%8);//writes the remainder at the beginning as 8 bits and cuts those off the current code saved
              
            }else{//if code is whole 8 bits
              
              fileOut.write(Integer.parseInt(binaryCurrent.substring(0,8),2));
              binaryCurrent=binaryCurrent.substring(8);//writes the 8 bits at the beginning and cuts them off
            }
            
          }else{//if code is less than 8 bits
            
            
            fileOut.write(Integer.parseInt(binaryCurrent,2));//outputs code
            binaryCurrent="";
            
          }
        }while(binaryCurrent.length()!=0);//exit when code is less than 8 bits
      }
      
      for (int i=0;i<fIle.length();i++){//not end of original file
        
        binary.append(binaries[chars.indexOf(Character.toString(fIle.charAt(i)))]);//adds the chars compressed code to binary
        
        while (binary.length()>=8){
          fileOut.write(Integer.parseInt(binary.substring(0,8),2));
          binary.delete(0,8);//if binary is more than 8 bits output the first 8 bits and remove them from the binary
        }
      }
      
      int binaryLength=binary.length();//get length of remaining binary (not whole 8 bits)
      
      if (binary.length()!=0){//if not whole 8 bits
        
        for (int i=0;i<(8-binaryLength);i++){
          binary.append("0");//adds 0s until its 8 bits
          
        }
        
        fileOut.write(Integer.parseInt(binary.toString(),2));//writes the last 8 bits
      }
      
      fileOut.write(8-binaryLength);//writes how many 0s were added
      
      fileOut.close();//closes everything
      
    }catch (Exception FileNotFoundException){
      
      System.out.println("file not found");//outputs error file not found
    }
    
  }
}
