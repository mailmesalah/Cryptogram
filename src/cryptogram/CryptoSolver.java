package cryptogram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CryptoSolver {

	//To keep all dictionary words
	private static List<String> lDictionary = new ArrayList<>();
	//To keep matched characters
	private static Map<String,String> characterMap = new HashMap<>();
	private static String[] mProblem;
	private static String sProblem;
	
	public static void main(String[] args) throws IOException {
		
		// Reading the Input from keyboard
        Scanner sc = new Scanner(System.in);        
        System.out.println("Please Enter Cryptogram Input");
        sProblem=sc.nextLine();
        mProblem = sProblem.split(" ");        
        sc.close();
        
        System.out.println("The Convertion has started");
        long millisec=System.currentTimeMillis();
        //Finding biggest size and smallest size to filter loading words to check
        //Sort Words in problem with the size
        int big=0;
        int small=1000;
        for (int i = 0; i < mProblem.length-1; i++) {
			for (int j = i+1; j < mProblem.length; j++) {
				if(mProblem[i].length()<mProblem[j].length()){
					String temp=mProblem[i];
					mProblem[i]=mProblem[j];
					mProblem[j]=temp;					
				}				
			}
			//finding biggest size
			if(big<mProblem[i].length()){
				big=mProblem[i].length();
			}
			
			//finding smallest size
			if(small>mProblem[i].length()){
				small=mProblem[i].length();
			}
		}
        
        
        //Reading the dictionary file
        FileInputStream fis = new FileInputStream(new File("dictionary.txt"));
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String word="";
        while((word=br.readLine())!=null){
        	//Inserts words based on its size, bigger at the beginning
        	if(word.length()>=small&&word.length()<=big){
        		int index=0;
	        	//if(!lDictionary.contains(word)){        		
		        	for (int i = 0; i < lDictionary.size(); i++) {	        	
		        		//if the size is bigger insert at that index
						if(lDictionary.get(i).length()<=word.length()){
							index=i;
							break;
						}
						index=i+1;
					}
		        	//adding the word at the right index
		        	lDictionary.add(index, word);
	        	//}
        	}
        }                
        
        //Process and prints the output
        System.out.println("The decrypted Text is :");
        System.out.println(decrypt());
        
        System.out.println("Time Taken is : "+(System.currentTimeMillis()-millisec));

	}
	
	private static String decrypt(){
		
        //creates the map for keeping the encrypted characters and its possible match
        createCharacterMap();
        
        //Processing
        boolean found=false;
        for (int i = 0; i < lDictionary.size(); i++) {
			if(lDictionary.get(i).length()==mProblem[0].length()){
				if(matchPattern(lDictionary.get(i),0)){
					//match found
					found= true;
					break;
				}else{
					//Reset Pattern Match
					resetPatternMatch(characterMap);
				}
			}
		}

        String decryptedText = "";
        if(found){
	        //Decrypting the characters to real text 	        
	      	char[] decryptedChars=sProblem.toCharArray();
			for (int j = 0; j < decryptedChars.length; j++) {
				if(decryptedChars[j]!=' '){
					decryptedChars[j]=characterMap.get(decryptedChars[j]+"").charAt(0);
				}				
			}				
			
			decryptedText = new String(decryptedChars);
        }else{
        	System.out.println("No possible match found!");
        }
        
        return decryptedText;
	}
			
	private static boolean matchPattern(String dictionaryWord, int indexProb){
		boolean matched = false;
		char sCheck[]=mProblem[indexProb].toCharArray();
		char sResult[]=new char[sCheck.length];
		Map<String,String> wordMap= new HashMap<>();
		//creating map
		for (int i = 0; i < sCheck.length; i++) {
			if(characterMap.containsKey(sCheck[i]+"")){
				if(characterMap.get(sCheck[i]+"").equals("")&&!characterMap.containsValue(dictionaryWord.charAt(i))){
					wordMap.put(mProblem[indexProb].charAt(i)+"", dictionaryWord.charAt(i)+"");			
				}
			}
			
		}
		//checking if decrypted word is same as the dictionary word 
		for (int i = 0; i < sCheck.length; i++) {
			if(characterMap.containsKey(sCheck[i]+"")){
				if(!characterMap.get(sCheck[i]+"").equals("")){
					sResult[i]=characterMap.get(sCheck[i]+"").charAt(0);
				}else if(isAlreadyExists(dictionaryWord.charAt(i))){
					sResult[i]=' ';
				}else{
					sResult[i]=wordMap.get(sCheck[i]+"").charAt(0);
				}
			}
		}
		//checks if equal
		if(new String(sResult).equals(dictionaryWord)){
			//match found			
			
			//Adding the new found characters to the character map
			for (int i = 0; i < sCheck.length; i++) {
				if(characterMap.containsKey(sCheck[i]+"")){
					if(characterMap.get(sCheck[i]+"").equals("")){
						characterMap.put(sCheck[i]+"",wordMap.get(sCheck[i]+""));
					}
				}	
			}
			
			//Recursive call for the next word
			if(indexProb+1<mProblem.length){
				for (int i = 0; i < lDictionary.size(); i++) {
					if(lDictionary.get(i).length()==mProblem[indexProb+1].length()){
						if(matchPattern(lDictionary.get(i),indexProb+1)){						
							matched=true;							
							break;
						}
					}else if(lDictionary.get(i).length()<mProblem[indexProb+1].length()){
						break;
					}
				}
				if(!matched){
					//Reset Pattern Match with depth
					resetPatternMatch(wordMap);
				}
			}else{
				//no more words to check, checking finished
				return true;
			}
		}
		
		
		return matched;
	}
	

	private static void resetPatternMatch(Map<String,String> wordMap){
		
		//reseting the newly added wordMap
		for (String key : wordMap.keySet()) {
			characterMap.put(key, "");
		} 		
	}
	private static void createCharacterMap(){
		for (int i = 0; i < mProblem.length; i++) {
			for (int j = 0; j < mProblem[i].length(); j++) {				
				characterMap.put(mProblem[i].charAt(j)+"", "");
			}
		}
	}	
	
	private static boolean isAlreadyExists(char c){
		for (String string : characterMap.keySet()) {
			if(characterMap.get(string).equals(c+"")){
				return true;
			}
		}		
		return false;
	}
	
	
}
