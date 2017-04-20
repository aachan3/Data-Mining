import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;


public class input_data {

	public static void main(String Args[]) throws NumberFormatException, IOException
	{
		//hash map MIS to store the minimal support values
		LinkedHashMap<Integer,Double> MIS = new LinkedHashMap<Integer,Double>();
		//array list of list Transactions to store the values of Transactions
		ArrayList<List<Integer>> Transactions = new ArrayList<List<Integer>>();
		//Temporary list to add the line by line transactions and finally add it to Transactions list of list.
		List<Integer> numbers = new ArrayList<>();
		//array list to store items that cannot be together.
		List<Integer> cannot_be_together = new ArrayList<>();
		//array list to store items that atleast one must be present in the frequent item list 
		List<Integer> must_have = new ArrayList<>();
		//n to count the number of transactions
		int n=0;
		//item_num to count the items involved
		int item_num=0;
		//support differential constraint.
		float sdc=0;
		//for Input data file
		//reads all the lines one by one from file and gives it to String line
		for(String line : Files.readAllLines(Paths.get("/Users/aravindachanta/Documents/MSApriori_implementation/src/input-data.txt")))
		{
			//replaces all the [{} ] with null space
			String filtered = line.replaceAll("[{} ]","");
			//splits the string with , as pivot and gives one by one to String part 
		    for (String part : filtered.split(","))
		    {   
		    	//add the line of transaction to numbers
		        Integer i = Integer.valueOf(part);
		        numbers.add(i);
		    }
		    //add all the lines of transactions to the Tarnsactions list of list.
		    Transactions.add(numbers);
		    //increment n as it is the number of transactions.
		    n++;
		    //initialize numbers to new as it will be used again to add the next line of transaction items.
		    numbers = new ArrayList<>();
		}
		
		
	
		//for parameter file
		//read the line by line and give it to String MISlines
		for(String MISlines : Files.readAllLines(Paths.get("/Users/aravindachanta/Documents/MSApriori_implementation/src/parameter-file.txt")))
		{
			
			if(MISlines.indexOf("MIS")!=-1) //to see if a line has MIS as a word.
			{
				//replace A-Z alphabets ( ) and space with null in the line 
			String MISfiltered = MISlines.replaceAll("[A-Z() ]","");
			//split strings with = as pivot.
		    String[] MISpart = MISfiltered.split("=");
		    //add the first number(Item) to Integer one
		    Integer one = Integer.valueOf(MISpart[0]);
		    //add the minimal support of item to Double two
		    double two = Double.valueOf(MISpart[1]);
		    //add one,two to MIS hash table.
		    MIS.put(one, two);
		    //increment item_num to find the number of items in the transaction.
		    item_num++;
			}
			//if line contains SDC string 
			else if(MISlines.indexOf("SDC")!=-1) //to see if line has SDC as a word.
			{ //replace alphabets and = and space with null
				String MISfiltered = MISlines.replaceAll("[A-Z= ]","");
				//remaining is the sdc value, which is added to flaot sdc.
				sdc = Float.valueOf(MISfiltered);
				
				
			}
			//if line contains cannot_be_together
			else if(MISlines.indexOf("cannot_be_together")!=-1)
			{
				//replace all of below specified with null
				String MISfiltered = MISlines.replaceAll("[a-z=_{}: ]","");
				//split the string with pivot , and add to string array p
				String[] p = MISfiltered.split(",");
				
				for(int ipl = 0;ipl< p.length;ipl++)
				{
					//add the values(cannot be together) to array list.
				    cannot_be_together.add(Integer.valueOf(p[ipl]));
				}
			}
			//if the String has must-have, then
			else if(MISlines.indexOf("must-have")!=-1)
			{
				//replace all below with null
				String MISfiltered = MISlines.replaceAll("[a-n p-q s-z=_{}: -]","");
				//split at or and add to string array nz
				String[] nz = MISfiltered.split("or");
				
				for(int ip = 0;ip< nz.length;ip++)
				{
					//add the string value nz to must_have array list
				    must_have.add(Integer.valueOf(nz[ip]));
				}
			}
			
		}
		//print out the values accordingly.
		  System.out.println("list of lists:"+ Transactions+"\n");
		 System.out.println("number of transactions:"+n+"\n");
		 System.out.println(MIS+"\n");
		 System.out.println("The number of items="+item_num+"\n");
		 System.out.println("value of SDC is "+sdc+"\n");
		 System.out.println(cannot_be_together);
		 System.out.println(must_have);

	
    //create an object of MSAprioriAlgo
		 MSAprioriAlgo algo = new MSAprioriAlgo();
		 //send the set of all the computed values to the function in the class algorithm.
		 algo.algorithm(Transactions,MIS,sdc,n,item_num,sdc,cannot_be_together,must_have);
	}
	}
	
	


