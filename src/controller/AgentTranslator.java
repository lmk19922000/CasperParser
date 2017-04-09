package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import parser.Parser;
import parser.Process;
import parser.ProtocolStep;

public class AgentTranslator {
	public static List<String> enums;
	public static Map<Integer,String> channels= new HashMap<Integer,String>();
	public static Map<String,CSPprocess> listPro = new HashMap<String, CSPprocess>();
	
	
	public static void main (String[] args) throws IOException
	{
		String inputFilePath = "C:\\Users\\kumar\\Downloads\\input.txt";
		File fout = new File("NS3out.txt");
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));


		Parser.parseInputFile(inputFilePath);
		
		enums = new ArrayList<String>(Parser.freeVariables.agents);
		enums.addAll(Parser.freeVariables.nonces);
		
       String varDeclared = "enum {";
		//for each #ActualVariable add to Enum
		for (String ele: enums) 
			varDeclared+=ele+",";
    		varDeclared = varDeclared.replaceAll(",$", "");
		
		varDeclared= varDeclared+"}";
		
		System.out.println(varDeclared);
		bw.write(varDeclared);
	 	bw.newLine();
		
       // for each type of message #prototocolDescription create a chennel
		for (Map.Entry<Integer, ProtocolStep> entry : Parser.protocolDescription.entrySet()) 
			{
		   	if (entry.getValue().messageContent != null)
				{
				   int msgCount= entry.getValue().messageContent.size();
				     if (!channels.containsKey(msgCount))
				    	channels.put(msgCount,"c"+(char)(96+msgCount));
				}
				
			} 
		
		for (Integer key : channels.keySet()){ 
		  System.out.println("Channel\t"+channels.get(key)+" 0");
		  bw.write("Channel\t"+channels.get(key)+" 0");
		 	bw.newLine();}

		
        int i=0;
		//for each process p in #Processes
	   for(Process p:Parser.processes)
	   {
		String localprocess="P"+p.name.toLowerCase();
		
		CSPprocess c= new CSPprocess();
		c.processName= localprocess;
		c.Agent =Parser.freeVariables.agents.get(i++) ;
		c.listener=null;
		c.IsRunning = false;
		c.Iscommit = false;
		c.parameters = "(sender,reciever,naunce) = ";
		c.description = new ArrayList<String>();
		listPro.put(c.Agent, c);
		
		

		 
	   }
	 		  for (int k2: Parser.protocolDescription.keySet())
			  {
				  if (k2==0)
					  continue;
				  
			  				  
		         String sAgent=Parser.protocolDescription.get(k2).senderAgent,
		        		 rAgent=Parser.protocolDescription.get(k2).receiverAgent;
				  
                 CSPprocess s= listPro.get(sAgent);
                 CSPprocess r= listPro.get(rAgent);
                 		         
                 String _sPrecond="",_rPrecond="";
 				String _spostcond="";
                if (k2==1)
				   {
					  s.IsRunning=true;
					  r.IsRunning = true;
					  _sPrecond= s.processName+"IsRunning { if ( sender == "+sAgent+" AND receiver == "+rAgent+" ) { "+ s.processName+"IsRunning = "+s.IsRunning+"} }  -> ";
                      _rPrecond= "->"+r.processName+"IsRunning { if ( sender == "+sAgent+" AND receiver == "+rAgent+" ) { "+ r.processName+"IsRunning = "+s.IsRunning+"} }  ";				
				  }

				else if(s.listener != rAgent && r.Iscommit == false){
                	r.Iscommit = true; 
					_spostcond = " - > "+r.processName+"Iscommit { if ( sender == "+rAgent+" AND receiver == "+sAgent+" ) { "+ r.processName+"Iscommit = "+r.Iscommit+"} } ";;
                	 }
                	
                	
				  String _tos= "sender"+"."+"receiver"+"."+Arrays.toString(Parser.protocolDescription.get(k2).messageContent.toArray()).replace('[', ' ').replace(']', ' ').replace(',', '.').trim()+"."+"sender";
					    _tos=_sPrecond+  channels.get(Parser.protocolDescription.get(k2).messageContent.size())+"!"+_tos;
				
				
				
			
			      String _tor = "receiver"+"."+Arrays.toString(Parser.protocolDescription.get(k2).messageContent.toArray()).replace('[', ' ').replace(']', ' ').replace(',', '.').trim()+".receiver";
					    _tor= channels.get(Parser.protocolDescription.get(k2).messageContent.size())+"?"+_tor+"  "+_rPrecond+_spostcond;
					
				  
				   s.description.add(_tos);
				   r.description.add(_tor);
						 
                   
	                s.listener=rAgent;		
				  listPro.put(sAgent,s);
				  listPro.put(rAgent,r);
						
			}//End of Protocol Description */
		
		
		for (String key : listPro.keySet())
		{
					CSPprocess p1= listPro.get(key);
					System.out.println(p1.processName);
					bw.write(p1.processName);
				 	bw.newLine();
					
					System.out.println(p1.parameters);
					bw.write(p1.parameters);
				 	bw.newLine();
					
					//System.out.println(p1.listener);
					for (String st:p1.description){
					 if (st == p1.description.get(p1.description.size()- 1)){
						System.out.println(st+" - > skip");
						bw.write(st+" - > skip ;");}
					 	
					 else{
					 System.out.println(st+" - >");
					 bw.write(st+" - >");}
					 
					 bw.newLine();
						
					}
					
					
		

       }//end of protocol description

			

		
		System.out.println("CSP Processes created !");
		bw.close();
	
	
	}

	
	
	
}
