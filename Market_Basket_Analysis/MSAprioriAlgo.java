import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class MSAprioriAlgo {

	public void algorithm(ArrayList<List<Integer>> Transaction, HashMap<Integer,Double> MIS,float sdc,int transac_num,int item_num,float SDC,List<Integer> cannot_be_together,List<Integer> must_have) throws IOException
	{
	//Array list to store the sorted out items(using MIS values)	
	ArrayList<Integer> M = new ArrayList<Integer>();
	//Array list to get an intermediate item list from M to find the F1,C2.
	ArrayList<Integer> L = new ArrayList<Integer>();
	//Array list to store the Frequent 1 item set
	ArrayList<List<Integer>> F1 = new ArrayList<List<Integer>>();
	//List of list to store candidate sets to find Fn.
	List<List<Integer>> Ck = new ArrayList<List<Integer>>();
	//hash map to store the frequency of the items being present in all the transactions
	HashMap<ArrayList,Integer> countF1 = new HashMap<ArrayList,Integer>();
	//hash map to store the frequency of all the n-item sets being present in all the transactions
	HashMap<ArrayList,Integer> countCk = new HashMap<ArrayList,Integer>();
	//hash map to store the frequency of all the [n-item set - the first value] being present in all the transactions 
	HashMap<ArrayList,Integer> countCk_withoutfirst = new HashMap<ArrayList,Integer>();
	//list of list to store the Fn-1 list.
	List<List<Integer>> Ftemp = new ArrayList<List<Integer>>();
	//list of list to store the 
	List<List<Integer>> Ck_final = new ArrayList<List<Integer>>();
	//list of list of list to store all the frequent n itemsets
	ArrayList<List<List<Integer>>> F = new ArrayList<List<List<Integer>>>();
	//To count the frequency of all items in the transactions. 
	HashMap<Integer,Integer> count_frequency = new HashMap<Integer,Integer>();
	//integer to count the value of how many times a single itemset is being present in all of the transactions. 
	int count_value=0;
	//integer to count how many times a single (itemset-first_index_item) is present in all of the transactions.
	int count_without_first=0;
	//used as integer loopers.
	int x,y,z=0;
	//find M by calling sort function.
	M = sort(MIS,item_num);  
	//call count function to get the frequency of all items in Transaction.
	count_frequency = count(Transaction,M); 
	System.out.println("Frequency "+count_frequency);
	//call init-pass to get L.
    L=init_pass(M,count_frequency,MIS,transac_num);
    System.out.println("M ="+M);
    System.out.println("L ="+L);
    //made M null to avoid heap memory error 
    M=null;
    //call frequent_1_item function to get frequent-1-item set.
    F1 = frequent_1_item(L,MIS,transac_num,count_frequency,must_have);
    System.out.println("F1 ="+F1);
    //index to get a record of previous frequent itemset.
    int index = 0;
    //rec to start the loop from 2 to till the previous frequent item set is null
    int rec = 0;
    boolean must = false;
    //loopers for the loops to find the frequency of F1 sets in Transactions.
    int oo,y1=0;
    //adding the F1 to F(list of list of lists).
    F.add(F1);
    
        for(oo = 0; oo<F1.size();oo++) //for loop to iterate one item set at a time in F1
    {
    	for(y1 = 0; y1 < Transaction.size(); y1++) //for loop to iterate the transactions line by line
    	{
    		if((Transaction.get(y1)).contains(F1.get(oo).get(0))) //if the itemset(only contains 1 value) from F1 is in each Transaction. 
    		{
    			
    			count_value++; //increment this accordingly
    		}
    	}    	
    	countF1.put((ArrayList) F1.get(oo), count_value); //put the key-value pair accordingly to countF1 hashmap 
    	count_value=0; //make the value 0 for next iteration
    	y1=0; //make looper 0 to make it ready for next iteration.
    }
      
   //loop to start at 2(to find out frequent 2 itemsets and greater)
        //checks whether the previous Fn value is null, if null terminate
  for(rec = 2; !(F.get(index).isEmpty()); rec++) 
    {
    
    //will go into this block if it is to find the second candidate set.
    if(rec == 2)
    {
    	//call level2_candidate_gen function to get the 2 item candidate set(also stored in Ck only)
    	Ck = level2_candidate_gen(L,SDC,count_frequency,transac_num,MIS,cannot_be_together);	
    	for(int to=0;to<Ck.size();to++)
        System.out.println(Ck.get(to)+"\n");
    	//to avoid heap memory error as L is not needed anymore
    	L=null;
    }
	//will go into this if it is for greater than 2 candidate generation.
    else
    {
    	//get the previous frequent generation set as it will be used to generate next candidate set.
    	Ftemp = F.get(index);
    	//get candidate n set according to rec loop by calling ms_candidate_gen function
    	Ck = ms_candidate_gen(Ftemp,SDC,count_frequency,transac_num,MIS,cannot_be_together);	
    	//make the Ftemp to new as it will  be used again in next iteration.
    	Ftemp = new ArrayList<List<Integer>>();
    		
    }
    if(!(Ck.isEmpty()))
    {
    for(z = 0; z<Ck.size();z++)
    {
    	for(y = 0; y< Transaction.size();y++)
    	{
    		if((Transaction.get(y)).containsAll(Ck.get(z)))
    		{
    			count_value++;
    		}
    		int val = Ck.get(z).remove(0);
    		if(Transaction.get(y).containsAll(Ck.get(z)))
    		{
    			count_without_first++;
    		}
    		Ck.get(z).add(0,val);
    		
    	}
    	//System.out.println(count_value);
    	//System.out.println(count_without_first);
    	countCk.put((ArrayList)Ck.get(z), count_value);
    	countCk_withoutfirst.put((ArrayList)Ck.get(z), count_without_first);
        count_without_first=0;
    	count_value=0;
    }
    
    for(x = 0;x<Ck.size();x++)
    {

    	must=false;
    	//System.out.println(Ck+"   "+x);
    	if(((((float)countCk.get(Ck.get(x)))/(transac_num))>=(MIS.get(Ck.get(x).get(0)))))
    {
    		Ck_final.add(Ck.get(x));
		
    }
    }
       F.add(Ck_final);
       index++;
   
    Ck_final = new ArrayList<List<Integer>>();
    x=y=z=0;
    }
    else
    {
    	index++;
    	F.add(Ck); 
    }
    
    Ck = new ArrayList<List<Integer>>();
    
    }
    F.remove(F.size()-1);
    int ito =0;
   for(int po = 1; po<F.size();po++)
    {
    	for(int lo=0; lo<F.get(po).size();lo++)
    	{
    		for(int h=0; h<must_have.size();h++)
    		{
    			//System.out.println(must_have.get(h));
    			if(((F.get(po).get(lo)).contains(must_have.get(h))))
    			{
    				System.out.println("   "+F.get(po).get(lo));

    				must=true;
    			}
    		}

    	if(must == false)
    	{
    	     System.out.println(F.get(po).remove(lo));
    	     
    	     lo--;
    		 must=false;
    		 
    		} 
    	must = false;
	
    	}
    	//System.out.println(ito);
    	System.out.println(F.get(po).size());
    	
    	if(F.get(po).size()==0)
    	{
    		F.remove(F.get(po));
    		po--;
    	}
    }
   
    int d=0;
    
    FileWriter fileWriter = new FileWriter("/Users/aravindachanta/Documents/MSApriori_implementation/src/output-pattern.txt",false);
    BufferedWriter buff = new BufferedWriter(fileWriter);
    String sb;
    sb ="Frequent 1-itemsets";
	buff.write(sb);
	buff.newLine();
	sb = "-----------------------";
	buff.write(sb);
	buff.newLine();
	buff.newLine();
	
    for(int ij =0; ij<F1.size();ij++)
    {
    buff.write("\t");
    sb = countF1.get(F1.get(ij)).toString();
    buff.write(sb);
    sb = " : ";
    buff.write(sb);
    sb = F1.get(ij).toString();
    buff.write(sb);
    buff.newLine();
    }
    buff.newLine();
    sb = "Total number of frequent 1-itemsets = "+F1.size();
    buff.write(sb);
    buff.newLine();
    buff.newLine();
    buff.newLine();
	   
   for (int fi = 1; fi < F.size(); fi++) {
	   System.out.println(F.size());
	   System.out.println(fi);
	   System.out.println(F.get(fi));
    	d = ++fi;
    	
    	sb ="Frequent "+d+"-itemsets";
    	buff.write(sb);
    	buff.newLine();
    	sb = "-----------------------";
    	buff.write(sb);
    	buff.newLine();
    	buff.newLine();
    	buff.newLine();
    	fi--;
    	for(int lk = 0; lk< F.get(fi).size(); lk++)
    	{
    		buff.write("\t");
    			sb = countCk.get(F.get(fi).get(lk)).toString();
    			buff.write(sb);
    			 sb = " : ";
    			    buff.write(sb);
    			    sb = F.get(fi).get(lk).toString();
    			    buff.write(sb);
    			    buff.newLine();
    			    
    			    sb = "Tailcount = "+countCk_withoutfirst.get(F.get(fi).get(lk));
    			    buff.write(sb);
    			    
    			    buff.newLine();
    			    	
    	}
    
    	buff.newLine();
        sb = "Total number of frequent"+d +"-itemsets= "+F.get(fi).size();
        buff.write(sb);
        buff.newLine();
        buff.newLine();
        buff.newLine();
        
        }
   buff.close();
    }
    
    
	
	
	public ArrayList<Integer> sort(HashMap<Integer,Double> MIS,int item_num){
		
		ArrayList<Double> value= new ArrayList<Double>();
		ArrayList<Integer> M = new ArrayList<Integer>();
		for(Entry<Integer,Double>entry:MIS.entrySet())
		{	
		  value.add(entry.getValue());	//add the values to array list value
		}
		Collections.sort(value); //sort the array list
		System.out.println(value);
		for(int i=0;i<value.size();i++)
		{
		for(Entry<Integer,Double>entry:MIS.entrySet())
		{	
			if(i!=0) //if i is 0, there will be no previous value
			{
				if(value.get(i).equals(value.get(i-1))) //if the previous value is equal to the present
				{
					break;   //break the inner loop and increment i, to get the next index value
				}
			}
			if((value.get(i)).equals(entry.getValue()))   //get the key with value that is equal to the sorted array.
			{
				M.add(entry.getKey());
			}
		}
		}
		value.clear();
		return M;
	}
	public HashMap<Integer,Integer> count(ArrayList<List<Integer>> Transaction,ArrayList<Integer> M)//count frequency 
	{
		int o=0;
		HashMap<Integer,Integer> count_items = new HashMap<Integer,Integer>();
		System.out.println("The sorted item list is:"+M);
		for(int p = 0;p<M.size();p++)
		{
		for(int k = 0; k < Transaction.size();k++)
		{
		for (int lo = 0; lo< Transaction.get(k).size();lo++) {
			if(Transaction.get(k).get(lo).equals(M.get(p))) //used 3 loops to find the outer list, and go to inner loop and go one by one to see if it equals the number.
			{
			o++;	
			} 
		    }
			}
		count_items.put(M.get(p), o);
		o=0;    
		}
		 
		return count_items;
	}
	
	public ArrayList<Integer> init_pass(ArrayList<Integer> M,HashMap<Integer,Integer> count_frequency,HashMap<Integer,Double> MIS,int transac_num )
	{
		int start=0;
		
		ArrayList<Integer> L_temp = new ArrayList<Integer>();
		for(start = 0;start<M.size();start++)
		{
			if(((((float)count_frequency.get(M.get(start)))/(transac_num))>=(MIS.get(M.get(start))))) //condition check
			{
				L_temp.add(M.get(start));
				
				
				for(int ik = start+1;ik<M.size();ik++)
				{
					if(((((float)count_frequency.get(M.get(ik)))/(transac_num))>=(MIS.get(M.get(start))))) //condition check
					{
						L_temp.add(M.get(ik));
					}
				}
				break;	
		}
			}
		
		return L_temp;
	}
	
	public ArrayList<List<Integer>> frequent_1_item(ArrayList<Integer> L, HashMap<Integer,Double> MIS,int transac_num,HashMap<Integer,Integer> count_frequency,List<Integer> must_have)
	{
		ArrayList<Integer> F1_temp = new ArrayList<Integer>();
		ArrayList<List<Integer>> F1_listtemp = new ArrayList<List<Integer>>();
		for(int f = 0; f<L.size();f++)
		{
			if(must_have.contains(L.get(f)))
			{
			if(((((float)count_frequency.get(L.get(f)))/(transac_num))>=(MIS.get(L.get(f)))))//condition check
			{
				F1_temp.add(L.get(f));
				F1_listtemp.add(F1_temp);
				System.out.println(F1_listtemp);
				F1_temp = new ArrayList<Integer>();
			}
			}
		}
		return F1_listtemp;
	}
	
	public ArrayList<List<Integer>> level2_candidate_gen(ArrayList<Integer> L,float SDC,HashMap<Integer,Integer> count_frequency, int transac_num,HashMap<Integer,Double> MIS,List<Integer> cannot_be_together)
	{
		ArrayList<List<Integer>> C2_temp  = new ArrayList<List<Integer>>();
		List<Integer> C2_inner_temp = new ArrayList<Integer>();
		for(int in = 0; in < L.size(); in++)
		{
			System.out.println("L"+ L.get(in));
			if(((((float)count_frequency.get(L.get(in))/(transac_num)))>=(MIS.get(L.get(in))))) //condition check
			{
				for(int ip = in+1; ip<L.size();ip++)
				{ 
					System.out.println("H"+ L.get(ip));
					
					
					if((((((float)count_frequency.get(L.get(ip)))/(transac_num))>=(MIS.get(L.get(in)))))&&((Math.abs(((float)count_frequency.get(L.get(ip)))/(transac_num)-(((float)count_frequency.get(L.get(in)))/(transac_num))))<=SDC)) //condition check
					{
						System.out.println("H_freq "+count_frequency.get(L.get(ip)));
						System.out.println("L_freq "+count_frequency.get(L.get(in)));
						System.out.println("H_supp "+((float)count_frequency.get(L.get(ip)))/(transac_num));
						System.out.println("L_supp "+((float)count_frequency.get(L.get(in)))/(transac_num));
						System.out.println();
						System.out.println("-------------------------");
					C2_inner_temp.add(L.get(in));
					C2_inner_temp.add(L.get(ip));
					
					if(!(cannot_be_together.containsAll(C2_inner_temp)))
					{
					C2_temp.add(C2_inner_temp);
					C2_inner_temp = new ArrayList<Integer>();
					}
					else{
					C2_inner_temp = new ArrayList<Integer>();
					}
					}
					
				}
			}
			
		}
		C2_inner_temp.clear();
		return C2_temp;
	}
		
	public List<List<Integer>> ms_candidate_gen(List<List<Integer>> list,float SDC,HashMap<Integer,Integer> count_frequency, int transac_num,HashMap<Integer,Double> MIS,List<Integer> cannot_be_together)
	{
		int k=0;
		int flag=1;
		//System.out.println(list);
		ArrayList<Integer> Cn_temp  = new ArrayList<Integer>();
		ArrayList<List<Integer>> Cn_inner_temp = new ArrayList<List<Integer>>();
		ArrayList<Integer> Cn_inner_inner_temp = new ArrayList<Integer>();
		ArrayList<Integer> Cn_check = new ArrayList<Integer>();

		for(int in = 0; in < list.size(); in++)
		{
				for(int ip = in+1; ip<list.size();ip++)
				{
					for(int sl=0;sl<list.get(in).size();sl++)
					{
					if((list.get(in).get(sl)).equals(list.get(ip).get(sl)))
						//condition check
					{
						k++;
					}
					else{
						
					if((k==list.get(in).size()-1))
					{
					if((MIS.get(list.get(in).get(sl)))<(MIS.get(list.get(ip).get(sl)))&&(((Math.abs(((float)count_frequency.get(list.get(in).get(sl)))/(transac_num)-(((float)count_frequency.get(list.get(ip).get(sl)))/(transac_num))))<=SDC)))
					{
						for(int p=0;p<list.get(in).size();p++)
						Cn_inner_inner_temp.add(list.get(in).get(p));
						Cn_inner_inner_temp.add(list.get(ip).get(sl));
						Cn_temp =  Cn_inner_inner_temp;
						
						for(int pk = 0; pk<Cn_temp.size();pk++)
						{
							//System.out.println(Cn_temp);
							int f = Cn_temp.remove(pk);
							int g=0;
							int lm =0;
							int kj =0;
							boolean is_match = false;
							//System.out.println(list);
							//System.out.println(Cn_temp);
							
							for(kj=0; kj<list.size();kj++)
							{
								for(lm=0;lm<list.get(kj).size();lm++)
								{
									//System.out.println(list.get(kj).get(lm));
									//System.out.println("   "+Cn_temp.get(lm));
									if((list.get(kj).get(lm)).equals(Cn_temp.get(lm)))
									{
										//System.out.println("true");
										g++;
									}
								}
								if(g==Cn_temp.size())
								{
			                  
									//System.out.println(Cn_temp);
									is_match=true;
								}
								g=0;
							}
							
							if(!(is_match))
							{
								//System.out.println("1"+list);
								//System.out.println("1"+Cn_temp);
								if(pk==0)
								{
										
								  if(MIS.get(f)==MIS.get(Cn_temp.get(pk+1)))
								 {
									flag = 0;
								 }
								 else
								{
									//do nothing just add
							     }
								}
								else
								{
									flag = 0;
								}
							}
							else
							{
								//System.out.println(list);
								//System.out.println(Cn_temp);
								//do nothing just add
							}
							Cn_temp.add(pk,f);
							
							}
						Cn_temp  = new ArrayList<Integer>();

						if(flag == 1)
						{
						boolean t =true;
						 for(int ap=0; ap<cannot_be_together.size();ap++)
						 {
							 for(int ts = ap+1;ts<cannot_be_together.size();ts++)
							 {
								 Cn_check.add(cannot_be_together.get(ap));
								 Cn_check.add(cannot_be_together.get(ts));
								System.out.println(Cn_check.get(0));
								System.out.println(Cn_check.get(1));
								 if((Cn_inner_inner_temp.contains(Cn_check.get(0)))&&((Cn_inner_inner_temp.contains(Cn_check.get(1)))))
								 {
									 t = false;
									Cn_inner_inner_temp = new ArrayList<Integer>(); 
								 }
								 Cn_check = new ArrayList<Integer>();
							 }
							 
						 }
					if(t==true)
						{
								Cn_inner_temp.add(Cn_inner_inner_temp);
								Cn_inner_inner_temp = new ArrayList<Integer>();
					}
						 else
						 {
							 Cn_inner_inner_temp = new ArrayList<Integer>();
						 }
						}
					}
					}
					}
					flag=1;
					}
					k=0;
					
					}
			}
				return Cn_inner_temp;
	}
 }
