package controller;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import parser.Parser;
import parser.Process;
import parser.Specification;
import translator.Channel;
import translator.ChannelExtractor;

public class AgentTranslator {
	public static List<String> enums;
	public static Map<String,CSPprocess> listPro = new HashMap<String, CSPprocess>();
	public static Map<String,Boolean> specs = new HashMap<String,Boolean>();
	public static List<String> protcol = new ArrayList<String>();	
	
	
	public static void createAgentprocess () 
	{
		
		enums = new ArrayList<String>(Parser.actualVariables.agents);
		enums.addAll(Parser.actualVariables.nonces);
		
       String varDeclared = "enum {";
		//for each #ActualVariable add to Enum
		for (String ele: enums) 
			varDeclared+=ele+",";
    		varDeclared = varDeclared.replaceAll(",$", "");
		
		varDeclared= varDeclared+"}";
		
		System.out.println(varDeclared);

	 	
		
    	for (Channel ch: ChannelExtractor.channelList) 
		  System.out.println("Channel\t"+ch.channelName);
		  

		
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
		c.isRunning = false;
		c.isCommit = false;
 
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
	   //Emit variabls
		for (String key : listPro.keySet())
		{
					CSPprocess p1= listPro.get(key);
					System.out.println("var "+p1.processName+"isRunning =  "+p1.isRunning);
					System.out.println("var "+p1.processName+"isCommit =  "+p1.isCommit);

		}
		
		
	   String Senderactual="",recieveractual="";
	 		 for (int k2: Parser.protocolDescription.keySet())
			  {
					  
				  
			  				  
		         String sAgent=Parser.protocolDescription.get(k2).senderAgent,
		        		 rAgent=Parser.protocolDescription.get(k2).receiverAgent;
				  
                 CSPprocess s= listPro.get(sAgent);
                 CSPprocess r= listPro.get(rAgent);
                 String _sPrecond="",_rPrecond="";
  				String _spostcond="";String channelname="";

  				
				  if (k2==0){
					  Senderactual = s.actualAgent;
					  recieveractual= r.actualAgent;
					
					  continue;
				  }

                 		         
                 for (Channel c: ChannelExtractor.channelList)
         		{
                	 if (c.messageContentLen==Parser.protocolDescription.get(k2).messageContent.size())
                	 {    channelname = c.channelName;
                	 break;
                	 }
                	 
         		}
                 
                
                if (k2==1)
				   {
					  s.isRunning=true;
					  r.isRunning = true;
					  _sPrecond= s.processName+"isRunning { if ( sender == "+Senderactual+" AND receiver == "+recieveractual+" ) { "+ s.processName+"isRunning = "+s.isRunning+"} }  -> ";
                      _rPrecond= "->"+r.processName+"isRunning { if ( sender == "+Senderactual+" AND receiver == "+recieveractual+" ) { "+ r.processName+"isRunning = "+s.isRunning+"} }  ";				
				  }

				else if(r.isCommit == false && r.listener != null){
                	r.isCommit = true;
					_spostcond = " - > "+r.processName+"isCommit { if ( sender == "+Senderactual+" AND receiver == "+recieveractual+" ) { "+ r.processName+"isCommit = "+r.isCommit+"} } ";;
                	 }
                	
                	
				  String _tos= s.parameters.get(1)+"."+Arrays.toString(Parser.protocolDescription.get(k2).messageContent.toArray()).replace('[', ' ').replace(']', ' ').replace(',', '.').trim()+".reciever";
					    _tos=_sPrecond+  channelname+"!"+_tos;
				
				
				
			
			      String _tor = r.parameters.get(1)+"."+Arrays.toString(Parser.protocolDescription.get(k2).messageContent.toArray()).replace('[', ' ').replace(']', ' ').replace(',', '.').trim()+".receiver";
					    _tor= channelname+"?"+_tor+"  "+_rPrecond+_spostcond;
					
				  
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
					
					
					System.out.println(p1.parameters.toString().replace('[', ' ').replace(']', ' ').trim().replace("( ,", "(").replace(", )", ")"));
				 	
					
					//System.out.println(p1.listener);
					for (String st:p1.description){
					 if (st == p1.description.get(p1.description.size()- 1))
						System.out.println(st+" - > skip ;");
						
					 	
					 else
					 System.out.println(st+" - >");
						
					}
					
					
		

       }//end of protocol description

	
        
        System.out.println("//CSP Processes created !");
	
	}

	
	public static void declaretheSystemSpecs()
	{
    
		//Protocol = ( PIni(A, I, Na) [] PIni(A, B, Na) ) ||| PRes(B, Nb) ||| PI;
		String p= "";

		 for(String k:listPro.keySet())
		   {
			CSPprocess c= listPro.get(k);
	
				if (p.length()==0 && c.processName.toUpperCase().contains("INITIATOR"))
				{
					p= c.processName+"("+c.actualAgent+","+translator.IntruderProcess.name+","+c.naunce +" )"+" [] ";
					p= p+c.processName+"("+c.actualAgent+","+translator.IntruderProcess.name+","+c.naunce +" )"+" []";
				
					p= "( "+p+"  )";
				}
				else if (p.length()>0 && c.processName.toUpperCase().contains("RESPONDER"))
				{
					p= p+" ||| "+c.processName+"("+c.actualAgent+","+c.naunce +" )";
				}

		   }
		    p= p+" ||| "+translator.IntruderProcess.name;
		
		    
		System.out.println("Protocol = "+p);
		
		System.out.println("//specification");
		//Specification
		for (String cp: listPro.keySet())
		{	
			CSPprocess csp= listPro.get(cp);
			System.out.println("#define "+csp.processName.toLowerCase()+"isRunning"+"  ( "+csp.processName+"isRunning == "+csp.isRunning +") ;");
            specs.put(csp.processName+"isRunning ",csp.isRunning);

			System.out.println("#define "+csp.processName.toLowerCase()+"isCommit"+"  ( "+csp.processName+"isCommit == "+csp.isCommit +") ;");
          
			specs.put(csp.processName+"isCommit ",csp.isCommit);
		}
		
		System.out.println("//protocol");

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
				System.out.println("#assert Protocol |= [] (  ([] !"+csp1.processName.toLowerCase()+"isCommit )"+"||"+"( !"+csp1.processName.toLowerCase()+"isCommit )"+"U ("+csp2.processName.toLowerCase()+"isRunning )) ;");
								
		        protcol.add("#assert Protocol |= [] (  ([] !"+csp1.processName.toLowerCase()+"isCommit )"+"||"+"( !"+csp1.processName.toLowerCase()+"isCommit )"+"U ("+csp2.processName.toLowerCase()+"isRunning )) ;");	 
               

			  }
		}


		System.out.println("#assert Protocol deadlockfree;");

        protcol.add("#assert Protocol deadlockfree;");

    
	}
	
	
}
