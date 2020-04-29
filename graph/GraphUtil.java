/**
 * 
 */
package graph;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;


/**
 * *TODO: The utility class for graph, mainly for web application
 * 
 * @author Wuzhi Xu,  Date: Jan 5, 2007
 *	Modified by Nan Li, Date: March 4, 2009
 *
 */
public class GraphUtil {
	// \x20 represents space; | represents 'OR' [a-zA-Z] represents characters without regarding LOWER CASE or UPPER CASE
	// , represents a comma; [0-9] represents natural numbers from 0-9; + represents *(repeated as many as possible times)
	// edgePat represents ([a-zA-Z]|[0-9])* followed by (,|\\x20)([a-zA-Z]|[0-9])*
	// For example: x,1; x 2......
	// defusePat represents ([a-zA-Z]|[0-9])* followed by ((,|\\x20)([a-zA-Z]|[0-9])*)*
	// For example: x,1,2; x 1 2......
	private static final String edgePat = "([a-zA-Z]|[0-9])+(,|\\x20)([a-zA-Z]|[0-9])+";
	private static final String defusePat = "([a-zA-Z]|[0-9])+((,|(\\x20)+)([a-zA-Z]|[0-9])+)+";
	private static final String infeasibleSubpathsPat = "[0-9]+(,(\\x20)*[0-9]+(\\x20)*)*(;(\\x20)*[0-9]+(,(\\x20)*[0-9]+(\\x20)*)*)*";
	
	/**
	 * *TODO: return a standard string to meet the requirement of imported java applet
	 * 
	 * the delimiter of nodes is ',' and the of edges is ';'
	 * there are three parts in this string: edges, initial node, and ending nodes, 
	 * separated by ':'
	 * 
	 * Example "1,2;2,4;1,3;3,4;1,5:1:4,5"
	 * this method is mainly for applet
	 * 
	 * @param g
	 * @return a string including all edges, the initial node, and the final nodes 
	 */
	public static String outputGraph(Graph g)
	{
		String result = "";
		Iterator<Edge> it = g.getEdgeIterator();
		
		//return all edges
		int count = 0;//a counter to judge whether it reaches the end of the all edges
		//if it has more edges, put a semicolon between edges
		while(it.hasNext())
		{			
			Edge e = it.next();
			if(count == g.sizeOfEdges()-1)
				result += e.getSrc() + "," + e.getDest();
			else
				result += e.getSrc() + "," + e.getDest() + ";";
			count++;
		}
		
		//return the initial nodes
		if(g.sizeOfInitialNode() == 0)
			result += "::";
		else
			result += ":";
		Iterator<Node> initials = g.getInitialNodeIterator();
		count = 0;
		while(initials.hasNext())
		{
			if(count == g.sizeOfInitialNode()-1)
				result += initials.next() + ":";
			else
				result += initials.next() + ",";
			count++;
		}
		
		//return the ending nodes
		Iterator<Node> ends = g.getEndingNodeIterator();
		count = 0;
		while(ends.hasNext())
		{
			if(count == g.sizeOfEndingNode()-1)
				result += ends.next();
			else
				result += ends.next() + ",";
			count++;
		}
		
		return result;
	}
	
	/**
	 * It is the converse method of outputGraph
	 * 
	 * @param s the string outputed by the above method 'outputGraph'
	 * @return
	 */
	public static Graph inputGraph(String s)	
	{		
		String[] parts= new String[3];
		StringTokenizer st=new StringTokenizer(s, ":");		
		for(int count=0;st.hasMoreTokens()&& count<parts.length;count++)	
			parts[count]=st.nextToken();
		
		//create edges;
		String[] edges=parts[0].trim().split(";");
		Graph g=new Graph();
		for(int i=0;i<edges.length;i++)
		{
	       String[] nodes=edges[i].split(",");
	       if(nodes.length!=2)
	    	   continue;
	       
	       Node src=g.createNode(nodes[0].trim());
	       Node des=g.createNode(nodes[1].trim());
	       g.addEdge(new Edge(src, des));
		}
		
		//create initial node
		if(parts[1] != null && !parts[1].trim().equals(""))
		{
			String[] initials = parts[1].trim().split(",");
			for(int i = 0;i < initials.length;i++)
			{
				Node initialNode = g.createNode(initials[i].trim());
				g.addInitialNode(initialNode);
			}
		}
		
		//create ending nodes
		if(parts[2] != null && !parts[2].trim().equals(""))
		{
			String[] endings = parts[2].trim().split(",");
			for(int i = 0;i < endings.length;i++)
			{
				Node endNode = g.createNode(endings[i].trim());
				g.addEndingNode(endNode);
			}
		}
		
		return g;
	}
	
	/**
	 * return a String for variables in a data flow graph.  
	 * each variable is separated by':'.
	 * three parts are included in each variable: name, nodes that have definitions, and nodes that have uses. They are separated by ';'.
	 * nodes that have definitions and uses are separated by ','. 
	 * For example, 'v; 2,3; 7,9: u; 1; 5'
	 * 
	 * if want to output information about nodes and edges, use outputGraph()
	 * @param dfg
	 * @return a string including all nodes that have definitions and uses in a data flow graph
	 */
	public static String outputVariables(DFGraph dfg)
	{
		String result = "";
		//get an iterator of all variables in a data flow graph
		Iterator<Variable> vars = dfg.getVariableIterator();
		while(vars.hasNext())
		{
			Variable v = vars.next();
			result += v.getName() + ";";
			//get all nodes that have variable definitions 
			Iterator<Node> defs = v.getDefIterator();
			while(defs.hasNext())
			{
				//separate nodes with a comma
				result += defs.next().toString() + ",";				
			}
			//get all edges that have variable definitions
			List<Edge> defsOnEdges = v.getDefsOnEdges();
			for(int i = 0;i < defsOnEdges.size();i++){
				result += defsOnEdges.get(i).getSrc().toString() + "." + defsOnEdges.get(i).getDest().toString() + ",";
			}
			//separate definitions and uses
			result += ";";
			//get all nodes that have variable uses 
			Iterator<Node> uses = v.getUseIterator();
			while(uses.hasNext())
			{
				//separate nodes with a comma
				result += uses.next().toString() + ",";
			}
			//get all edges that have variable uses
			List<Edge> usesOnEdges = v.getUsesOnEdges();
			for(int i = 0;i < usesOnEdges.size();i++){
				result +=  usesOnEdges.get(i).getSrc().toString() + "." + usesOnEdges.get(i).getDest().toString() + ",";
			}
			
			result += ":";
		}
		return result;
	}
	
	/**
	 * 
	 * @param sg String that represents graph, such as nodes, edges, start node, and ending node
	 * @param sv String that represents variables, such as name, def, and use
	 * @return
	 */
	public static DFGraph inputDFGraph(String sg, String sv)
	{
		Graph g = inputGraph(sg);
		DFGraph dfg = g.createDFGraph();
		String[] vars = sv.split(":");
		if(vars != null)
			for(int i = 0;i < vars.length;i++)
			{
				if(vars[i] != null && !vars[i].trim().equals(""))
				{
					String[] parts = vars[i].split(";", 3);
					if(parts != null)
					{
						if(parts[0] != null && !parts[0].trim().equals(""))
						{
							Variable v = dfg.createVariable(parts[0].trim());
							//	get the defs on nodes and save the nodes
							if(parts[1] != null && !parts[1].trim().equals(""))
							{							
								String[] defs = parts[1].split(",");
								if(defs != null)
									for(int j = 0;j < defs.length;j++){
										
										if(defs[j] != null && !defs[j].trim().equals("") && defs[j].indexOf(".") == -1)
										{
											Node d = dfg.findNode(defs[j]);
											if(d != null)
												v.addDef(d);
										}
										else if(defs[j] != null && !defs[j].trim().equals("") && defs[j].indexOf(".") != -1){
											StringTokenizer defsST = new StringTokenizer(defs[j], ".");
											String srcString = defsST.nextToken();
											String destString = defsST.nextToken();
											Edge e = dfg.findEdge(srcString, destString);
											if(e != null)
												v.addDefOnEdges(e);
										}
									}
							}
							// tet the uses on nodes and save the nodes
							if(parts[2] != null && !parts[2].trim().equals(""))
							{
								String[] uses = parts[2].split(",");
								if(uses != null)
									for(int j = 0;j < uses.length;j++){
										
										if(uses[j] != null && !uses[j].trim().equals("") && uses[j].indexOf(".") == -1)
										{
											Node u = dfg.findNode(uses[j]);
											if(u != null)
												v.addUse(u);
										}
										else if(uses[j] != null && !uses[j].trim().equals("") && uses[j].indexOf(".") != -1){
											StringTokenizer defsST = new StringTokenizer(uses[j], ".");
											String srcString = defsST.nextToken();
											String destString = defsST.nextToken();
											Edge e = dfg.findEdge(srcString, destString);
											if(e != null)
												v.addUseOnEdges(e);
										}
									}
							}//end for loop and if structure

					/*		for(int k = 0; k < v.getDefsOnEdges().size();k++){
								System.out.println("defs: " + k + ": " + v.getDefsOnEdges().get(k).toString());
							}
							
							for(int k = 0; k < v.getDefsOnEdges().size();k++){
								System.out.println("uses: " + k + ": " + v.getUsesOnEdges().get(k).toString());
							}*/
						}//end if structure
							
					}
				}
			}
		
		return dfg;
	}
	
	/**
	 * Nodes in every path are delimited by ',' and each path is delimited by ':'
	 * @param paths
	 * @return a string including all nodes in every path
	 * For example: paths are [2,3,4],[2,3],[2,3,4],[3,4]
	 * a string like "2,3,4,:2,3,:2,3,4,:3,4" is supposed to be returned
	 */
	public static String outputPath(List<Path> paths)
	{
		String result = "";
		//get each path from the parameter paths
		for(int i = 0;i < paths.size();i++)
		{
			Iterator<Node> nodes = paths.get(i).getNodeIterator();
			int count = 0;//a counter for judge whether the for loop reaches the end of paths
			//if the for loop reaches the end of paths, stop putting a comma after each path
			while(nodes.hasNext())
			{
				if(count == paths.get(i).size()-1)
					result += nodes.next().toString();
				else
					result += nodes.next().toString()+",";
			}
			//if the for loop does not reach the end of paths, put a colon after each path
			if(i != paths.size()-1)
				result += ":";
		}
		//return "" if the result is null else return the corresponding result
		if(isNull(result))
			return "";
		else
			return result.substring(0, result.length()-1);
	}
	
	/**
	 * It is the converse method for outputPath(List<Path>) except the additional argument Graph.
	 * A path can not exist independently without Graph
	 * 
	 * @param s 
	 * @param g
	 * @return a list of paths that are included in the String
	 */
	public static Path[] inputPath(String s, Graph g)
	{
		String[] pstrs = s.split(":");
		Path[] paths = new Path[pstrs.length];
		//for each path included in the String, decompose the String and put each path in Paths
		for(int j = 0;j < pstrs.length;j++)
		{	
			String[] nstr = pstrs[j].split(",");
			Node[] nodes = new Node[nstr.length];
			for(int i = 0;i < nstr.length;i++)		
				nodes[i] = g.createNode(nstr[i]);
			
			paths[j] = new Path(nodes[0]);
			for(int i = 1;i < nodes.length;i++)
				paths[j].extendPath(nodes[i]);
		}
		
		return paths;
	}
	
	/**
	 * read strings including edges, the initial node, and final nodes of a graph 
	 * separate all edges, nodes with the space delimiter and put all information in a graph
	 * 
	 * @param edges
	 * @param initialNode
	 * @param endNodes
	 * @return a graph with all edges, the initial node, and final nodes
	 */
	public static Graph readGraph(String edges, String initialNode, String endNodes) throws IOException
	{
		char[] buf = edges.toCharArray();
		BufferedReader br = new BufferedReader(new CharArrayReader(buf));
		//a new graph
		Graph g = new Graph();
		try 
		{
			//a string for a next edge
			String str = null;
			//a StringTokinizer
			StringTokenizer newNodes = null;
			int count = 0;
			//get edges and set edges for the graph
			while((str = br.readLine()) != null)
			{
				//if the edge inputs are not in good format, throw the exception
				if(!Pattern.matches(edgePat, str.trim()))
					throw new IOException("An invalid input '" + str + "' for an edge. Please read the notes above the forms. ");
				//use a comma to separate tokens
				newNodes = new StringTokenizer (str, ", ");
				//get the value of source node of an edge
				String src = newNodes.nextToken();
				//when read the edges first time and initial node is null or empty, set the value of src to initialNode
				if(count == 0 && (initialNode == null ||initialNode.trim().equals("")))
					initialNode = src;
				//create the source node and destination node 
				Node srcNode = g.createNode(src);
				Node desNode = g.createNode(newNodes.nextToken());
				g.addEdge(new Edge(srcNode, desNode));
			}
			//get and set an initial node for the graph
		//	Node initial = g.createNode(initialNode);
		//	g.setInitialNode(initial);
			newNodes = null;
			newNodes = new StringTokenizer(initialNode,", ");
			while(newNodes.hasMoreTokens())
			{
				g.addInitialNode(g.createNode(newNodes.nextToken()));
			}
			//get the set final nodes for the graph
			newNodes = null;
			newNodes = new StringTokenizer(endNodes, ", ");
			while(newNodes.hasMoreTokens())
			{
				g.addEndingNode(g.createNode(newNodes.nextToken()));
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return g;
	}
	
	/**
	 * return a list<Path> type of infeasible sub-paths based on a user input String
	 * @param infeasibleSubpaths
	 * @return
	 * @throws IOException 
	 */
	public static List<Path> readInfeasibleSubpaths(String infeasibleSubpaths) throws  IOException{
		//a list of infeasible sub paths
		List<Path> subpaths = new ArrayList<Path>();
		
		char[] buf = infeasibleSubpaths.toCharArray();
		BufferedReader br = new BufferedReader(new CharArrayReader(buf));
		//a string for a next infeasible sub path
		String nextSubPath = null;
		
		if(infeasibleSubpaths != null && !infeasibleSubpaths.equals("")){
			
			while((nextSubPath = br.readLine()) != null){
				//if the edge inputs are not in good format, throw the exception
				if(!Pattern.matches(infeasibleSubpathsPat, nextSubPath.trim()))
					throw new IOException("An invalid input '" + infeasibleSubpaths + "' for the infeasible subpaths. Please read the notes above the forms. The software would be more error-tolerant if it ignored a trailing \";\". ");
				Path path1 = null;

				StringTokenizer nodes = new StringTokenizer(nextSubPath,",");
				
				//initiate the first node
				path1 = new Path(new Node(nodes.nextToken().trim()));
				
				//if there are more nodes in the path, add them in
				while(nodes.hasMoreTokens()){
					path1.extendPath(new Node(nodes.nextToken().trim()));		
				}
				//add one sub-path to the path list
				subpaths.add(path1);
			}
		}//end if
			
		return subpaths;
	}
	
	/**
	 * return a list<Path> type of infeasible sub-paths based on a user input String
	 * @param infeasibleSubpaths
	 * @return
	 * @throws IOException 
	 */
	public static List<Path> readInfeasibleSubpaths1(String infeasibleSubpaths) throws IOException{
		//a list of infeasible sub paths
		List<Path> subpaths = new ArrayList<Path>();
		
		StringTokenizer paths = null;
		if(infeasibleSubpaths != null && !infeasibleSubpaths.equals("")){
			if(!Pattern.matches(infeasibleSubpathsPat, infeasibleSubpaths.trim()))
				throw new IOException("An invalid input '" + infeasibleSubpaths + "' for the infeasible subpaths. Please read the notes above the forms. ");
			
			}
		//initialize sub paths
		paths = new StringTokenizer(infeasibleSubpaths, ";");
		while(paths.hasMoreTokens()){
			Path path1 = null;
			String path = paths.nextToken();
			StringTokenizer nodes = new StringTokenizer(path,",");
			
			//initiate the first node
			path1 = new Path(new Node(nodes.nextToken().trim()));
			
			//if there are more nodes in the path, add them in
			while(nodes.hasMoreTokens()){
				path1.extendPath(new Node(nodes.nextToken().trim()));		
			}
			//add one sub-path to the path list
			subpaths.add(path1);
		}
		return subpaths;
	}
	
	/**
	 * this method is mainly used for web form in servlet in data flow graph application
	 * Effects: read defs and uses from the users' inputs and check if they are correct
	 * if defs and uses are correct, put them in the variables of data flow graphs
	 * 
	 * @param d
	 * @param u
	 * @param dfg, which the data flow graph
	 */
	public static void readDefUse(String d, String u, DFGraph dfg) throws IOException
	{				
		try
		{
			//put a string of definitions of variables into a file
			char[] defs = d.toCharArray();
			BufferedReader br = new BufferedReader(new CharArrayReader(defs));
			
			String str = null;//a string storing one line of the data file
			StringTokenizer st = null;
			//read defs into variables
			while((str = br.readLine())!= null)
			{	
				//comments modified by Nan Li
				//if defs do not match the pattern, return an error message
				if(!Pattern.matches(defusePat, str.trim()))
					throw new IOException("Could not understand the format of the defs for '" + str+ "'. " +
							"Please check the syntax: a variable name followed by a space-separated sequence of nodes or edges where the varialbes are defined. ");
				int count = 0;
				st = new StringTokenizer (str, " ");
				Variable var = null;
				while(st.hasMoreTokens())
				{
					//set the first token as a variable
					//and get other nodes in defs
					if(count == 0)
					{
						var = dfg.createVariable(st.nextToken());
					}
					else
					{
						String defsString = st.nextToken();
						//If string contains a ",", consider it as an edge
						//otherwise, consider it as a node
						if(defsString.indexOf(",") != -1){
							StringTokenizer defsST = new StringTokenizer(defsString, ",");
								String srcString = defsST.nextToken();
								String destString = defsST.nextToken();
								Edge e = dfg.findEdge(srcString, destString);
								if(e == null)
									throw new IOException("Could not find the edges have defs. Please check the defs in the uses box");
								if(e != null)
									var.addDefOnEdges(e);
						}	
						else{
							Node n = dfg.findNode(defsString);
							if(n == null)
								throw new IOException("Could not find the nodes have defs. Please check the defs in the uses box");
							if(n != null)
								var.addDef(n);
						}					
					}
					count++;
				}//end while loop "st.hasMoreTokens()"
			}//end while loop "(str = br.readLine()) != null"		
		
			char[] uses = u.toCharArray();
			br = new BufferedReader(new CharArrayReader(uses));			
		
			str = null;
			st = null;
			//read uses into variables
			while((str = br.readLine()) != null)
			{
				//comments modified by Nan Li
				if(!Pattern.matches(defusePat, str.trim()))
					throw new IOException("Could not understand the format of the uses for '" + str + "'. " +
							"Please check the syntax: a variable name followed by a space-separated sequence of nodes or edges where the varialbes are defined. ");
				int count = 0;
				st = new StringTokenizer (str, " ");
				Variable var = null;
				while(st.hasMoreTokens())
				{
					//set the first token as a variable
					//and get other nodes in uses
					if(count == 0)
					{
						var = dfg.createVariable(st.nextToken());
					}
					else
					{
						String usesString = st.nextToken();					
						//If string contains a ",", consider it as an edge
						//otherwise, consider it as a node
						if(usesString.indexOf(",") != -1){

							StringTokenizer usesST = new StringTokenizer(usesString, ",");
							String srcString = usesST.nextToken();
							String destString = usesST.nextToken();
							Edge e = dfg.findEdge(srcString, destString);
							if(e == null)
								throw new IOException("Could not find the edges have uses. Please check the uses in the uses box");
							if(e != null)
								var.addUseOnEdges(e);
						}
						else{
							Node n = dfg.findNode(usesString);
							if(n == null)
								throw new IOException("Could not find the nodes have uses. Please check the uses in the uses box");
							if(n != null)
								var.addUse(n);
						}
						
					}//end if-else structure
					count++;
				}//end while loop "st.hasMoreTokens()"
			
			}//end while loop	"(str = br.readLine()) != null"		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
/*		Iterator<Variable> iv = dfg.getVariableIterator();
		while(iv.hasNext()){
			Variable v = iv.next();
			System.out.println("var: " + v.getName());
			Iterator<Node> nodeDef = v.getDefIterator();
			System.out.println("defs: ");
			while(nodeDef.hasNext()){
				System.out.println(nodeDef.next());			
			}
			List<Edge> edgeDef = v.getDefsOnEdges();
			for(int x = 0; x < edgeDef.size();x++){
				System.out.println(edgeDef.get(x));
			}
			
			Iterator<Node> nodeUse = v.getUseIterator();
			System.out.println("uses: ");
			while(nodeUse.hasNext()){
				System.out.println(nodeUse.next());			
			}
			List<Edge> edgeUse = v.getUsesOnEdges();
			for(int x = 0; x < edgeUse.size();x++){
				System.out.println(edgeUse.get(x));
			}			
		}*/
	}
	
	/**
	 * Generate a prefix graph based on a list of paths, 
	 * a vertex of the new prefix graph represents a path of the list
	 * a new edge is created in the new prefix graph if there is any overlapping between two vertices
	 * the weight of the edge is the prefix of the overlapping path
	 * @param g
	 * @return a prefix graph
	 */
	public static Graph getPrefixGraph(List<Path> listOfPaths){
		Graph prefix = new Graph();
		for(Path pathSrc: listOfPaths){
			for(Path pathDest: listOfPaths){
				Node srcNode = prefix.createNode(pathSrc);
				Node destNode = prefix.createNode(pathDest);
				//check another path that is not itself
				if(!pathSrc.equals(pathDest)){
					int index = 0;
					//jump out of the while loop if index is equal to -1
					while(index != -1){
						index = pathSrc.nextIndexOf(pathDest.get(0), index);
						//start generating the overlapping path
						if(index != -1){
							//a signal for the overlapping
							boolean signal = true;
							//System.out.println("path i: " + pathSrc);
							//System.out.println("path j: " + pathDest);
							//System.out.println("INDEX: " + index);
							//check whether the last k nodes in path i is the same as the first k nodes in path j
							for(int i = index; i < pathSrc.size(); i++){
								if(i - index >= pathDest.size()){
									signal = false;
									break;
								}
								if(!pathSrc.get(i).equals(pathDest.get(i - index))){
									signal = false;
									break;
								}
							}//end of for loop
							//System.out.println("signal: " + signal);
							//the last k nodes in path i is the same as the first k nodes in path j, 
							//get the new path that covers two paths i and j
							if(signal == true){
								Path prefixPath = pathSrc.subPath(0, index);
								//System.out.println("temp path: " + prefixPath);
								
								Edge newEdge = prefix.createEdge(srcNode, destNode, prefixPath, prefixPath.size(), 0);
							}								
						}//end of if statement
					}//end of while loop
	
				}//end of if statement
			}//end of for loop
		}//end of for loop

		return prefix;
	}
	
	/**
	 * Generate a bipartite graph based on a a prefix graph, 
	 * @param Graph prefixGraph
	 * @param String initialNodes
	 * @param String endNodes
	 * @author Nan
	 * @return a bipartite graph
	 */
	public static Graph getBipartiteGraph(Graph prefixGraph, String initialNodes, String endNodes){
		Graph bipartiteGraph = new Graph();
		//a StringTokinizer
		StringTokenizer newNodes = null;

		//get and set an initial node for the graph
		newNodes = new StringTokenizer(initialNodes,", ");
		while(newNodes.hasMoreTokens())
		{
			bipartiteGraph.addInitialNode(bipartiteGraph.createNode(newNodes.nextToken()));
		}
		//get the set final nodes for the graph
		newNodes = null;
		newNodes = new StringTokenizer(endNodes, ", ");
		while(newNodes.hasMoreTokens())
		{
			bipartiteGraph.addEndingNode(bipartiteGraph.createNode(newNodes.nextToken()));
		}	
			
		Iterator<Edge> edges = prefixGraph.getEdgeIterator();
	//	Node srcNode = bipartiteGraph.createNode("S");
	//	Node destNode = bipartiteGraph.createNode("T");
		Node leftNode = null;
		Node rightNode = null;
		//System.out.println("size :" + prefixGraph.sizeOfEdges());
		while(edges.hasNext()){
			Edge edge = edges.next();
			leftNode = edge.getSrc();
			rightNode = edge.getDest();
			bipartiteGraph.createNode(leftNode.getObject());
			bipartiteGraph.createNode(rightNode.getObject());
			bipartiteGraph.createEdge(leftNode, rightNode, edge.getWeight());
		//	bipartiteGraph.createEdge(srcNode, edge.getSrc(),  new Integer(1));
		//	bipartiteGraph.createEdge(edge.getDest(), destNode, new Integer(1));
		}
		System.out.println("size :" + bipartiteGraph.sizeOfEdges());
		return bipartiteGraph;
	}
	
	/**
	 * Generate a bipartite graph based on a a prefix graph, 
	 * @param Graph prefixGraph
	 * @param String initialNodes
	 * @param String endNodes
	 * @author Nan
	 * @return a bipartite graph
	 */
	public static Graph getBipartiteGraphWithST(Graph prefixGraph, String initialNodes, String endNodes){
		Graph bipartiteGraph = new Graph();
		//create an initial node and final node
		Node srcNode = bipartiteGraph.createNode("S");
		Node destNode = bipartiteGraph.createNode("T");

		bipartiteGraph.addInitialNode(bipartiteGraph.createNode(srcNode.getObject()));

		bipartiteGraph.addEndingNode(bipartiteGraph.createNode(destNode.getObject()));
			
		Iterator<Edge> edges = prefixGraph.getEdgeIterator();

		Node leftNode = null;
		Node rightNode = null;
		//System.out.println("size :" + prefixGraph.sizeOfEdges());
		while(edges.hasNext()){
			Edge edge = edges.next();
			leftNode = edge.getSrc();
			rightNode = edge.getDest();
			
			Path objectOfLeftNode = new Path((Path)leftNode.getObject());
			//System.out.println("left side: " + objectOfLeftNode);
			if(objectOfLeftNode.indexOf(new Node("L")) == -1 && objectOfLeftNode.indexOf(new Node("R")) == -1)
				objectOfLeftNode.extendPath(new Node("L"));			
		//	bipartiteGraph.createNode(objectOfLeftNode);
			
			Path objectOfRightNode = new Path((Path)rightNode.getObject());
			//System.out.println("right side: " + objectOfRightNode);
			if(objectOfRightNode.indexOf(new Node("R")) == -1 && objectOfRightNode.indexOf(new Node("L")) == -1 )
				objectOfRightNode.extendPath(new Node("R"));	
			
			boolean signForLeft = false;
			boolean signForRight = false;
			Iterator<Node> nodes = bipartiteGraph.getNodeIterator();
			while(nodes.hasNext()){
				Node node = nodes.next();
				if(!node.getObject().equals("S") && !node.getObject().equals("T")){
					//System.out.println(((Path)node.getObject()).equals(objectOfLeftNode));
					//System.out.println(((Path)node.getObject()));
					//System.out.println(objectOfLeftNode);

					if(((Path)node.getObject()).equals(objectOfLeftNode))
						signForLeft = true;
					if(((Path)node.getObject()).equals(objectOfRightNode))
						signForRight = true;
						
				}
			}
			if(signForLeft == false)
				bipartiteGraph.nodes.add(new Node(objectOfLeftNode));
			
			if(signForRight == false)
				bipartiteGraph.nodes.add(new Node(objectOfRightNode));
			
			//bipartiteGraph.createNode(objectOfRightNode);
			//System.out.println("right side: " + objectOfRightNode);
			
			leftNode = null;
			rightNode = null;
			//leftNode = new Node(objectOfLeftNode);
			//rightNode = new Node(objectOfRightNode);
			for(Node node: bipartiteGraph.nodes){
				if(!node.getObject().equals("S") && !node.getObject().equals("T")){
					if(((Path)node.getObject()).equals(objectOfLeftNode))
						leftNode = node;
					if(((Path)node.getObject()).equals(objectOfRightNode))
						rightNode = node;
					
				}
			}
			//System.out.println("find node: " + bipartiteGraph.findNode(objectOfLeftNode));
			bipartiteGraph.createEdge(leftNode, rightNode, edge.getWeight(), ((Path)edge.getWeight()).size(), 0);
		}
		//System.out.println("size :" + bipartiteGraph.sizeOfEdges());
		
		//add edges from the initial node to the nodes on the left side
		//add edges from the nodes on the right side to the final node
		for(Node node: bipartiteGraph.nodes){
			if(node.getObject().toString().indexOf("L") != -1)
				bipartiteGraph.createEdge(srcNode, node,  null, 1, 0);
			
			if(node.getObject().toString().indexOf("R") != -1)
				bipartiteGraph.createEdge(node, destNode,  null, 1, 0);
		}
		
		for(Node node: bipartiteGraph.nodes){
			System.out.println("bipartite nodes: " + node.toString());
		}
		
	      
		Iterator<Edge> edges1 = bipartiteGraph.getEdgeIterator();
		while(edges1.hasNext())
			System.out.println(edges1.next().toStringWithFlow());
		return bipartiteGraph;
	}
	
	/**
	 * 
	 * @param str
	 * @return true if a string is null or an empty string, otherwise return false
	 */
	public static boolean isNull(String str)
	{
		if(str == null || str.trim().equals(""))
			return true;
		else
			return false;
	}
}
