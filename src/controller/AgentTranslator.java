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
import parser.Specification;

public class AgentTranslator {
	public static List<String> enums;
	public static Map<Integer,String> channels= new HashMap<Integer,String>();
	public static Map<String,CSPprocess> listPro = new HashMap<String, CSPprocess>();
	public static Map<String,Boolean> specs = new HashMap<String,Boolean>();
	public static List<String> protcol = new ArrayList<String>();	
	
	public static void main (String[] args) throws IOException
	{
		String inputFilePath = "C:\\Users\\kumar\\Downloads\\input.txt";
		File fout = new File("NS3out.txt");
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));


		Parser.parseInputFile(inputFilePath);
		
		enums = new ArrayList<String>(Parser.actualVariables.agents);
		enums.addAll(Parser.actualVariables.nonces);
		
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
		c.freeAgent =Parser.freeVariables.agents.get(i) ;
		c.actualAgent=Parser.actualVariables.agents.get(i) ;
		c.naunce = Parser.actualVariables.nonces.get(i) ;
		c.listener=null;
		c.IsRunning = false;
		c.Iscommit = false;
 
		c.parameters= new ArrayList<String>();
		c.parameters.add("( ");
			if (p.name.equalsIgnoreCase("INITIATOR"))
			{
				c.parameters.add("sender");
				c.parameters.add("reciever");
				c.parameters.add("naunce");
			}	
			if (p.name.equalsIgnoreCase("RESPONDER"))
			{
				c.parameters.add("reciever");
				c.parameters.add("naunce");
			}
		c.parameters.add(") = ");

		c.description = new ArrayList<String>();
		listPro.put(c.freeAgent, c);
		
		
		i++;
		 
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
					  _sPrecond= s.processName+"IsRunning { if ( sender == "+s.actualAgent+" AND receiver == "+r.actualAgent+" ) { "+ s.processName+"IsRunning = "+s.IsRunning+"} }  -> ";
                      _rPrecond= "->"+r.processName+"IsRunning { if ( sender == "+s.actualAgent+" AND receiver == "+r.actualAgent+" ) { "+ r.processName+"IsRunning = "+s.IsRunning+"} }  ";				
				  }

				else if(r.Iscommit == false && r.listener != null){
                	r.Iscommit = true;
					_spostcond = " - > "+r.processName+"Iscommit { if ( sender == "+r.actualAgent+" AND receiver == "+s.actualAgent+" ) { "+ r.processName+"Iscommit = "+r.Iscommit+"} } ";;
                	 }
                	
                	
				  String _tos= s.parameters.get(1)+"."+Arrays.toString(Parser.protocolDescription.get(k2).messageContent.toArray()).replace('[', ' ').replace(']', ' ').replace(',', '.').trim()+"."+"reciever";
					    _tos=_sPrecond+  channels.get(Parser.protocolDescription.get(k2).messageContent.size())+"!"+_tos;
				
				
				
			
			      String _tor = r.parameters.get(1)+"."+Arrays.toString(Parser.protocolDescription.get(k2).messageContent.toArray()).replace('[', ' ').replace(']', ' ').replace(',', '.').trim()+".receiver";
					    _tor= channels.get(Parser.protocolDescription.get(k2).messageContent.size())+"?"+_tor+"  "+_rPrecond+_spostcond;
					
				  
				   s.description.add(_tos);
				   r.description.add(_tor);
						 
                   
	                s.listener=r.actualAgent;		
				  listPro.put(sAgent,s);
				  listPro.put(rAgent,r);
						
			}//End of Protocol Description */
		
		
		for (String key : listPro.keySet())
		{
					CSPprocess p1= listPro.get(key);
					System.out.println(p1.processName);
					bw.write(p1.processName);
				 	bw.newLine();
					
					System.out.println(p1.parameters.toString().replace('[', ' ').replace(']', ' ').trim().replace("( ,", "(").replace(", )", ")"));
					bw.write(p1.parameters.toString().replace('[', ' ').replace(']', ' ').trim().replace("( ,", "(").replace(", )", ")"));
				 	bw.newLine();
					
					//System.out.println(p1.listener);
					for (String st:p1.description){
					 if (st == p1.description.get(p1.description.size()- 1)){
						System.out.println(st+" - > skip ;");
						bw.write(st+" - > skip ;");}
					 	
					 else{
					 System.out.println(st+" - >");
					 bw.write(st+" - >");}
					 
					 bw.newLine();
						
					}
					
					
		

       }//end of protocol description

		System.out.println("//specification");
		//Specification
		for (String cp: listPro.keySet())
		{	
			CSPprocess csp= listPro.get(cp);
			System.out.println("#define "+csp.processName.toLowerCase()+"IsRunning"+"  ( "+csp.processName+"IsRunning == "+csp.IsRunning +") ;");
			bw.write("#define "+csp.processName.toLowerCase()+"IsRunning"+"  ( "+csp.processName+"IsRunning == "+csp.IsRunning +") ;");
            bw.newLine();
			specs.put(csp.processName+"IsRunning ",csp.IsRunning);

			System.out.println("#define "+csp.processName.toLowerCase()+"Iscommit"+"  ( "+csp.processName+"Iscommit == "+csp.Iscommit +") ;");
			bw.write("#define "+csp.processName.toLowerCase()+"Iscommit"+"  ( "+csp.processName+"Iscommit == "+csp.Iscommit +") ;");
            bw.newLine();
			specs.put(csp.processName+"Iscommit ",csp.Iscommit);
		}
		
		System.out.println("//protocol");
		bw.write("//protocol");
		bw.newLine();
		//Authentication of B to A can thus be expressed saying that ResRunningAB must become true before IniCommitAB.
		//i.e., the initiator A commits to a session with B only if B has indeed taken part in a run of the protocol with A.
		//System.out.println("#assert Protocol |= [] ( ([] !iniCommitAB) || (!iniCommitAB U resRunningAB) ");

		//The converse authentication property corresponds to saying that IniRunningAB becomes true before ResCommitAB.
		//The flaw of the protocol is shown by this model
    	//System.out.println("#assert Protocol |= [] ( ([] !resCommitAB) || (!resCommitAB U iniRunningAB) ");
 
		
		for(Specification s :Parser.specification)
		{
			if(s.type.equals("Agreement")){
				//System.out.println("Authen");
				CSPprocess csp1= listPro.get(s.identifier);
				CSPprocess csp2= listPro.get(s.atom);
				System.out.println("#assert Protocol |= [] (  ([] !"+csp1.processName.toLowerCase()+"Iscommit )"+"||"+"( !"+csp1.processName.toLowerCase()+"Iscommit )"+"U ("+csp2.processName.toLowerCase()+"IsRunning )) ;");
				bw.write("#assert Protocol |= [] (  ([] !"+csp1.processName.toLowerCase()+"Iscommit )"+"||"+"( !"+csp1.processName.toLowerCase()+"Iscommit )"+"U ("+csp2.processName.toLowerCase()+"IsRunning )) ;");
								
		        protcol.add("#assert Protocol |= [] (  ([] !"+csp1.processName.toLowerCase()+"Iscommit )"+"||"+"( !"+csp1.processName.toLowerCase()+"Iscommit )"+"U ("+csp2.processName.toLowerCase()+"IsRunning )) ;");	 
               
			   bw.newLine();
			  }
		}


		System.out.println("#assert Protocol deadlockfree;");
		bw.write("#assert Protocol deadlockfree;");
		bw.newLine();
        protcol.add("#assert Protocol deadlockfree;");

        
        System.out.println("CSP Processes created !");
		bw.close();
	
	
	}

	
	
	
}
