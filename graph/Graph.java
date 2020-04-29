
package graph;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * 
 * Three graph properties must be satisfied for computing coverage: 
 * 		<li> one initial node and  at least one ending node.
 * 		<li>exsit a path from the inital node to any node 
 * 		<li>exist an path from any node to at least one of the ending nodes.
 *<br>The last two properties also called in the class 'connected'
 *  * @author Wuzhi Xu, Date: Dec 12, 2006
 * Modified by Nan Li
 * 
 */
public class Graph extends GraphBase{
	//Node start;
	List<Node> starts;//initial nodes
	List<Node> ends; //final nodes
	String[] infeasiblePrimePathsString;//store infeasible prime paths
	String[] infeasibleEdgePairsString;//store infeasible edge-pairs
	
	//get infeasible prime paths
	public String[] getInfeasiblePrimePathsString(){
		return infeasiblePrimePathsString;
	}
	//set initial nodes
	public void setInitialNode(List<Node> starts)
	{
		this.starts = starts;
	}
	//get the initial nodes
	public List<Node> getInitialNode()
	{
		return starts;
	}
	/**
	 * 
	 * @param n
	 * @return true if the node n is one of the initial nodes else return false
	 */
	public boolean isInitialNode(Node n)
	{
		if(starts == null || starts.size() == 0)
			return false;
		
		return starts.contains(n);
	}
	/**
	 * 
	 * @param n
	 * @return true if the node n is one of the final nodes else return false
	 */
	public boolean isEndingNode(Node n)
	{
		if(ends == null || ends.size() == 0)
			return false;
		
		return ends.contains(n);
	}
	/**
	 * 
	 * @return an iterator of final nodes
	 */
	public Iterator<Node> getEndingNodeIterator()
	{
		if(ends == null)
			ends = new ArrayList<Node>();
		
		return ends.iterator();
	}
	/**
	 * 
	 * @return an iterator of initial nodes
	 */
	public Iterator<Node> getInitialNodeIterator()
	{
		if(starts == null)
			starts = new ArrayList<Node>();
		
		return starts.iterator();
	}
	/**
	 * 
	 * @return the size of edges
	 */
	public int sizeOfEdges()
	{
		return edges.size();
	}
	/**
	 * 
	 * @return the size of all nodes in a graph
	 */
	public int sizeOfNodes()
	{
		return nodes.size();
	}
	/**
	 * 
	 * @return the number of final nodes if final nodes are not null else return 0
	 */
	public int sizeOfEndingNode()
	{
		if(ends != null)
			return ends.size();
		else
			return 0;
	}
	/**
	 * 
	 * @return the number of initial nodes if initial nodes are not null else return 0
	 */
	public int sizeOfInitialNode()
	{
		if(starts != null)
			return starts.size();
		else
			return 0;
	}
	/**
	 * add a node n to the final nodes set
	 * @param n
	 */
	public void addEndingNode(Node n)
	{
		if(ends == null)
			ends = new ArrayList<Node>();
		ends.add(n);
	}
	/**
	 * add a node n to the initial nodes set
	 * @param n
	 */
	public void addInitialNode(Node n)
	{
		if(starts == null)
			starts = new ArrayList<Node>();
		starts.add(n);
	}
	
	/**
	 * After generating the spanning tree for graph. This method extends the paths in the 
	 * spanning tree to  the paths having ending nodes.
	 * @return
	 * @throws InvalidGraphException if the graph is invalid, or there is a dead loop 
	 */
	public List<Path> findTestPath()
	throws InvalidGraphException
	{
		List<Path> paths = findSpanningTree();
		List<Path> result = new ArrayList<Path>();
		
		//part 1: find all 
		//find all possible test path existing in the spanning tree
		for(int i = 0;i < paths.size();i++)
		{
			Path p = paths.get(i);
			//if the last node of the path is one of the final nodes, add it to result, ignore the rest, and jump to the next path
			if(ends.contains(p.getEnd()))
			{
				result.add(p);
				paths.remove(i);
				i--;
				continue;
			}
			
			//for other paths, which are, paths contains at least one node that is one of the final nodes
			//if the path contains one of the final nodes, get the subpath from the initial node to this node
			//and check if this subpath has been included in the result, if not, add it to the result
			for(int j = 0;j < ends.size();j++)
			{	
				//check if the path p has one of the final nodes
				int index = p.indexOf(ends.get(j));
				if(index != -1)
				{
					//get a subpath from one initial node to this final node
					Path subpath = p.subPath(0, index+1);
					boolean exist = false;//a sign whether a path is a subpath of any path of result
					//if the subpath is a sub path of the result, do nothing and exit from the loop
					for(int k = 0;k < result.size();k++)
						if(subpath.isSubpath(result.get(k)))
						{
							exist = true;
							break;
						}
					//if the path is not a subpath of any path of the result, add it to result	
					if(!exist)
						result.add(subpath);
				}//end if					
			}//end for loop with variable j
		}//end for loop with variable i
		
		
		//throw an exception if there is not any test path in a spanning tree, which means there is no ending node
		//or ending node is not connected
		if(result.size()==0)
			throw new InvalidGraphException("No ending node.");	
		
		//part 2: extend all paths in spanning tree to test paths, that is, all paths will have final nodes.
		int oldsize = 0;
		do
		{
			oldsize = paths.size();
			for(int j = 0;j < result.size();j++)
				for(int i = 0;i < paths.size();i++)
				{
					Path testPath = result.get(j);
					Path path = paths.get(i);
					//if the last node of path is included in the test path, put the rest subpath from this node right after path
					int index = testPath.indexOf(path.getEnd());
					if(index != -1)
					{
						Path subpath = testPath.subPath(index+1);
						path.extendPath(subpath);
						result.add(path);
						paths.remove(i);
						i--;
					}
				}		
		}while(oldsize-paths.size() > 0);//after one loop, the size of paths should be reduced; the while loop will be stopped if no path is removed from paths
		
		//throw an exception if there exists some paths that never reach an ending node.
		if(paths.size() != 0)
		{
			String pathStr = "";
			for(Path p:paths)
				pathStr += " " + p.toString();
			throw new InvalidGraphException("some paths, " + pathStr + ", never reach an ending node.");
		}
	
		return result;
	}
	
	/**
	 * Create the spanning tree for a graph. The tree is represented by a list of paths that start from the 
	 * initial nodes
	 * 
	 * @return a list of paths starting from each initial node and all nodes have been reached in a graph
	 * @throws InvalidGraphException
	 */
	public List<Path> findSpanningTree()
	throws InvalidGraphException
	{
		//validate whether the graph is valid
		validate();
		//a list of paths to return the expected paths			
		List<Path> result = new ArrayList<Path>();
		
		//go through all paths from each initial node
		for(int j = 0; j < starts.size();j++)
		{
			//add all nodes to nodesCopy
			List<Node> nodesCopy = new ArrayList<Node>();
			for(int i = 0;i < nodes.size();i++)
				nodesCopy.add(nodes.get(i));
			//remove all initial nodes from nodesCopy
			for(int i = starts.size() - 1;i >= 0;i--)
			{
				int index = nodes.indexOf(starts.get(i));
				nodesCopy.remove(index);
			}
			//initialize each path with one initial node
			List<Path> paths = new ArrayList<Path>();		
			paths.add(new Path(starts.get(j)));
			
			//create paths
			//get paths from one initial node
			//paths starts from an initial node, keep expanding itself, and check if all nodes except initial nodes have been reached
			//put paths into result if the corresponding node has been reached
			//stop while looping when all paths have been removed from paths, which means all nodes have been reached in a graph
			while(paths.size() != 0)
			{
				//from each initial node, go through each possible path
				for(int i = 0;i < paths.size();i++)
				{
					Path path = paths.get(i);
					Node end = path.getEnd();
					Iterator<Edge> outEdges = end.getOutGoingIterator();
					int count = 0;
					while(outEdges.hasNext())
					{
						count++;
						//if the edge is the last edge to go through
						if(count == end.sizeOfOutEdges())
							path.extendPath(outEdges.next().getDest());//extend path itself
						else
						{
							Path newPath = (Path)path.clone();//copy path and extend the copy
							newPath.extendPath(outEdges.next().getDest());
							paths.add(i+1, newPath);
							i++;//add one to variable i to avoid keeping running in the for loop
						}//end if-else
					}//end while loop
				}//end for loop
				
				//put paths in List<Path> result when the expected node has been reached
				for(int i = 0;i < paths.size();i++)
				{
					Path path = paths.get(i);
					Node end = path.getEnd();
					//if a node is just being reached, remove it from nodesCopy
					if(nodesCopy.contains(end))
					{
						nodesCopy.remove(end);
					}
					//if a node has been reached, put the path to which the node belongs to result
					else
					{
						paths.remove(i);
						i--;//subtract one from i to keep looping the paths because one path has been removed from paths
						result.add(path);
					}
				}
			}//end while loop
		}//end for loop with variable j
		return result;
		
	}
	
	/**
	 * find prime path coverage that do not cover infeasible prime paths. But it is not ensured that the set of test path is minimal
	 * 
	 * @return
	 * @throws InvalidGraphException
	 */
	public List<Path> findPrimePathCoverage(String infeasiblePrimePathsString)
	throws InvalidGraphException
	{
		
		List<Path> primes = findPrimePaths1(infeasiblePrimePathsString);
		long start = System.nanoTime();
		List<Path> testPaths = findTestPath();
		//List<Path> primes = findPrimePathsWithSidetrips(infeasiblePrimePathsString);
		for(int i = 0;i <primes.size();i++)
		{
			//get a prime path
			Path prime = primes.get(i);
			boolean extendHead = false;
			boolean extendTail = false;
			//get the first node and last node of a prime path
			Node head = prime.get(0);
			Node tail = prime.getEnd();
			//if the first node is the initial node of the graph
			//set true to extendHead
			if(starts.indexOf(head) != -1)
				extendHead = true;
			//if the last node is one of the final nodes of the graph
			//set true to extendTail
			if(ends.contains(tail))
				extendTail = true;
			//System.out.println("prime path: " + prime);			
			for(int j = 0;j < testPaths.size();j++)
			{
				Path test = testPaths.get(j);
				//System.out.println("test " + j + ": " + test);
				//if the last node of the prime path is not one of the final nodes, 
				//extend it such that the last node is a final node of the graph
				if(!extendTail)
				{	
					//get the index of the last tail node
					int index = test.lastIndexOf(tail);
					//System.out.println("index" + ": " + index);
					//extend the prime path such that the last node is one of the final nodes of the graph
					if(index != -1)
					{
						Path sub = test.subPath(index+1, test.size());	
					//	System.out.println("path: " + sub);
						prime.extendPath(sub);
					//	System.out.println("prime: " + prime);
						extendTail = true;
					}										
				}
				
				//if the first node of the prime path is not the initial node, 
				//extend it such that the first node is the initial node
				if(!extendHead)
				{
					//get the index of the first head node
					int index = test.indexOf(head);
					//extend the prime path such that the first node is the initial node 
					if(index != -1)
					{
						Path sub = test.subPath(0, index);
						sub.extendPath(prime);
						prime = sub;
						extendHead = true;
					//	System.out.println("prime after head: " + prime);
					}
				}
				
				//if extend both, finish extending this path
				if(extendHead && extendTail)
				{
					primes.set(i, prime);
				//	System.out.println("prime after head and tail: " + prime);
					break;
				}
			}//end for loop of j
			
			//if either of two ends can not extend, 
			if(!(extendHead & extendTail))
				throw new InvalidGraphException("Can't find test path for "+prime.toString());
		}//end for loop of i
		
		//attempt to minimize the set of test path,
		for(int i = 0;i < primes.size();i++)
		{
			Path prime = primes.get(i);
			for(int j = i+1;j < primes.size();j++)
			{
				if(primes.get(j).isSubpath(prime))
				{
					primes.remove(j);
					j--;
				}
			}
		}
		
		for(int i = 0;i < primes.size();i++)
		{
			Path prime = primes.get(i);
		//	System.out.println("prime list: " + "i " + prime);
		}
		//minimize the coverage set----step2
		//for a test path, if all prime paths that are toured by it directly are also toured by another test path directly
		//delete it from the test paths set
		
		//for one test path
		for(int i = 0;i < primes.size();i++)
		{
			//get prime paths and prime paths with sidetrips
			List<Path> primePathsList = findPrimePaths();
			List<Path> primePathsWithSidetripsList = findPrimePaths1(infeasiblePrimePathsString);
			//initialization
			List<Path> selectedPrimePaths = new ArrayList<Path>();	
			List<Path> selectedPrimePathsWithSidetrips = new ArrayList<Path>();
			//put all prime paths that are toured by this path directly into selectedPrimePaths
			for(int z = 0;z < primePathsList.size();z++)
			{
				if(primePathsList.get(z).isSubpath(primes.get(i)))
					selectedPrimePaths.add(primePathsList.get(z));
			}
			//put all prime paths that are toured by this path with sidetrips into selectedPrimePathsWithSidetrips
			for(int z = 0;z < primePathsWithSidetripsList.size();z++)
			{
				if(primePathsWithSidetripsList.get(z).isSubpath(primes.get(i)))
					selectedPrimePathsWithSidetrips.add(primePathsWithSidetripsList.get(z));
			}
			//System.out.println("test path: " + "i " + primes.get(i));
			for(int z = 0; z < selectedPrimePaths.size();z++)
			{
			//	System.out.println("selectedPrimePaths: " + selectedPrimePaths.get(z));
			}
			for(int z = 0; z < selectedPrimePaths.size();z++)
			{
			//	System.out.println("selectedPrimePathsWithSidetrips: " + selectedPrimePathsWithSidetrips.get(z));
			}
			
			//for the other test paths
			for(int j = 0;j < primes.size();j++)
			{
				boolean sign = false;//a sign for determine whether selected prime paths are also toured by the other test paths
				//if exists one prime path is not toured by the other test path, set true to the sign
				for(int x = 0;x < selectedPrimePaths.size();x++)
				{
					if(!selectedPrimePaths.get(x).isSubpath(primes.get(j)))
						sign = true;
				}
				for(int x = 0;x < selectedPrimePathsWithSidetrips.size();x++)
				{
					if(!selectedPrimePathsWithSidetrips.get(x).isSubpath(primes.get(j)))
						sign = true;
				}
			//	System.out.println("test path: " + "j " + primes.get(j));
			//	System.out.println("sign: " + sign);
				//remove the test path if it is redundant
				if(sign == false && !primes.get(i).equals(primes.get(j)))
				{
					primes.remove(i);
					i--;
					break;
				}
			}
		}
		
		long end = System.nanoTime();
     	long duration = end - start;
     	//System.out.println("running time of prime path coverage = " + duration);
     	
     	/*
		for(int i = 0;i < primes.size();i++)
		{
			Path prime = primes.get(i);
			System.out.println("second prime list: " + "i " + prime);
		}*/
		return primes;
	}
	
	
	public List<Path> findPrimePathCoverageWithInfeasibleSubPath(String infeasiblePrimePathsString, List<Path> infeasibleSubpaths) throws InvalidGraphException{
		List<Path> result = new ArrayList<Path>();
		List<Path> testPathsForPrimePaths = findPrimePathCoverage(infeasiblePrimePathsString);
		List<Path> tempTestPathsForPrimePaths = new ArrayList<Path>();
		List<Path> tempPrimes = new ArrayList<Path>();
		
		/*List<Path> subPaths = new ArrayList<Path>();
		Path path1 = new Path(new Node("2"));
		path1.extendPath(new Node("3"));
		path1.extendPath(new Node("1"));
		path1.extendPath(new Node("2"));
		path1.extendPath(new Node("3"));
		subPaths.add(path1);*/
		
		List<Path> subPaths = new ArrayList<Path>();
		if(infeasibleSubpaths != null){
			for(int a = 0;a < infeasibleSubpaths.size();a++){
				subPaths.add(infeasibleSubpaths.get(a));
			}
		}
	/*	Path path1 = new Path(new Node("3"));
		path1.extendPath(new Node("4"));
		path1.extendPath(new Node("6"));
		path1.extendPath(new Node("2"));
		path1.extendPath(new Node("3"));
		path1.extendPath(new Node("4"));
		path1.extendPath(new Node("6"));
		path1.extendPath(new Node("2"));
	//	path1.extendPath(new Node("1"));
		Path path2 = new Path(new Node("3"));
		path2.extendPath(new Node("5"));
		path2.extendPath(new Node("6"));
		path2.extendPath(new Node("2"));
		path2.extendPath(new Node("3"));
		path2.extendPath(new Node("4"));
		path2.extendPath(new Node("6"));
	//	path2.extendPath(new Node("7"));
	//	path2.extendPath(new Node("1"));
		subPaths.add(path1);
		subPaths.add(path2);*/
		
	/*	Path path2 = new Path(new Node("2"));
		path2.extendPath(new Node("3"));
		path2.extendPath(new Node("1"));
		path2.extendPath(new Node("2"));
		path2.extendPath(new Node("3"));
		System.out.println("path1: " + path1);
		System.out.println("path2: " + path2);
	   System.out.println("path1 is equal to path2: " + path1.equals(path2));*/
		
		for(int i = 0; i < testPathsForPrimePaths.size();i++){
			Path testPath = testPathsForPrimePaths.get(i);
			//System.out.println("test path: " + testPath);
			for(int j = 0; j < subPaths.size();j++){
				Path infeasibleSubPath = subPaths.get(j);
			//	System.out.println("infeasibleSubPath: " + infeasibleSubPath);
				if(infeasibleSubPath.isSubpath(testPath)){
					testPathsForPrimePaths.remove(i);
					i--;
					//break;
			
					//check the prime paths that are covered by the deleted test path can be covered by other test paths
					//if not, try to generate another test path to cover it; else, give up
					//tempPrimes stores prime paths that are toured by testPath
		//for(int i = 0; i < testPathsForPrimePaths.size();i++){	
		//	Path testPath = testPathsForPrimePaths.get(i);
				List<Path> primes = findPrimePaths1(infeasiblePrimePathsString);
					for(int k = 0; k < primes.size();k++){
						boolean exist = false; //a sign for distinguishing existed prime paths
						boolean exist1 = false; //a sign for whether a prime path is a subpath of an infeasible sub path
						//set exist to true if there exists the same path in tempPrimes as the one in primes
						//get rid of redundant prime paths stored in tempPrimes
						for(int k1 = 0; k1 < tempPrimes.size();k1++){
							if(tempPrimes.get(k1).equals(primes.get(k)))
								exist = true;
						}
						for(int k2 = 0; k2 < subPaths.size(); k2++){
							if(primes.get(k).isSubpath(subPaths.get(k2))){
								exist1 = true;
								break;
							}
						}
						if(primes.get(k).isSubpath(testPath) && !exist && !exist1)
							tempPrimes.add(primes.get(k));
					}//end for loop of variable k
					
					//check if paths in tempPrimes can be toured by other test paths
					//if yes, remove it from tempPrimes
				//	System.out.println("test path: " + testPath);
					for(int l = 0; l < tempPrimes.size();l++){
						Path tempPrimes1 = tempPrimes.get(l);
					//	System.out.println("temp prime: " + tempPrimes1);
						for(int m = 0; m < testPathsForPrimePaths.size();m++){
					//		System.out.println("test paths1: " + testPathsForPrimePaths.get(m));
							if(tempPrimes1.isSubpath(testPathsForPrimePaths.get(m)) && !testPathsForPrimePaths.get(m).equals(testPath)){
								tempPrimes.remove(l);
								l--;
								break;
							}
						}
					}//end for loop of variable l
					
					//try to generate another test path that can tour paths in tempPrimes
			
					// one way to generate test path
						for(int n = 0; n < tempPrimes.size(); n++){
							Path tempPrime = tempPrimes.get(n);
							
						//	for(int x = 0; x < testPathsForPrimePaths.size(); x++){
							
							for(int x = 0; x < findTestPath().size(); x++){
								Path pathForHead = findTestPath().get(x);
								Path subPathForHead = null;
								int index1 = pathForHead.lastIndexOf(tempPrime.get(0));
								if(index1 != -1 && starts.indexOf(pathForHead.get(0)) != -1){
									//System.out.println("n:" + n);
									subPathForHead = pathForHead.subPath(0, index1);
									//System.out.println("subPathForHead:" + subPathForHead.toString());
									if(index1 == 0)
										subPathForHead.extendPath(tempPrime.subPath(1, tempPrime.size()));
									else
										subPathForHead.extendPath(tempPrime);
									//tempPrime = subPathForHead;
								}
								
								for(int y = 0; y < testPathsForPrimePaths.size(); y++){
									//System.out.println("y:" + y);
									Path pathForTail = testPathsForPrimePaths.get(y);
									Path subPathForTail =  null, finalPath = null, finalPath1 = null;
									//System.out.println("PathForTail: " + pathForTail.toString());
									if(subPathForHead != null){
										//System.out.println("subPathForHead: " + subPathForHead.toString());
										finalPath = subPathForHead;
									
									
										
										if(ends.indexOf(finalPath.getEnd()) == -1){
											int index2 = pathForTail.indexOf(finalPath.getEnd());
											if(index2 != -1){
												subPathForTail = pathForTail.subPath(index2 + 1, pathForTail.size());
												finalPath.extendPath(subPathForTail);
											//	finalPath1 = finalPath;
											//	finalPath = subPathForTail;
											}
										}
									//	if(finalPath1 != null)
									//		System.out.println("finalPath1: " + finalPath1.toString());
										
										boolean is = true, existed = false;
										//if an infeasible subpath is a sub-path of the finalPath, set is to false
										for(int z = 0; z < subPaths.size();z++)
											if(subPaths.get(z).isSubpath(finalPath))
												is = false;
										//if the finalPath is the same as any one in the test paths, set existed to true
										for(int z1 = 0;z1 < testPathsForPrimePaths.size();z1++){
											if(testPathsForPrimePaths.get(z1).equals(finalPath)){
												existed = true;
									//			System.out.println("existed: " + existed);
												break;
											}
											
										}
									//	System.out.println("finalPath: " + finalPath.toString());
									//	System.out.println("is: " + is);
										//if a final path does not include any infeasible sub-path and is not equal to any existing test path
										//and the last node is one of the final nodes, add it to test paths list
										boolean existed1 = false;
										if(is == true && existed == false && ends.indexOf(finalPath.getEnd()) != -1){
											//if the testPrime has been included in other existing test path, set extisted1 to true 
											for(int z2 = 0;z2 < testPathsForPrimePaths.size();z2++){
												if(tempPrime.isSubpath(finalPath) && tempPrime.isSubpath(testPathsForPrimePaths.get(z2))){
														existed1 = true;
												}					
											}
											//if existed1 is equal to false, i.e. the testPrime has not been toured by any other test path, add the finalPath to test paths list
											if(existed1 == false){
												testPathsForPrimePaths.add(finalPath);
											//	tempTestPathsForPrimePaths.add(finalPath);
											}
											
											//System.out.println("finalPath: " + finalPath.toString());
										}
											
											
								//		}
									}//end if
							}//end for loop of varaile y
								
							}// end for loop of variable x
							//check if the new generated path contains infeasible sub path
							
						}//end for loop of variable n
						
						//another way to generate test path
						
					
				}//end if
			}//end for loop of variable j
		}//end for loop of variable i

	//return tempTestPathsForPrimePaths;
	return testPathsForPrimePaths;
	//return tempPrimes;
	}
	/*
	 * Effects: get a minimal set of test paths for Edge-Pair Coverage that do not cover infeasible edge-pairs
	 * 
	 */
	public List<Path> findEdgePairCoverage(String infeasibleEdgePairs) throws InvalidGraphException{
		//get test paths
		List<Path> testPaths = findTestPath();
		//get edge pairs
		List<Path> edgePairs = findEdgePairs(infeasibleEdgePairs);
		//get a copy of edge-pairs
	//	List<Path> edgePairsCopy = new ArrayList<Path>();
	////	for(int i = 0;i < edgePairs.size();i++)
	//		edgePairsCopy.add(edgePairs.get(i));
		//new array list to keep test paths for Edge-Pair Coverage
		List<Path> result = new ArrayList<Path>();
		jumpofloop:
		for(int i = 0; i < edgePairs.size();i++){
//			get a prime path
			Path edgePair = edgePairs.get(i);
			for(int k = 0;k < result.size();k++){
				if(edgePair.isSubpath(result.get(k)))
					continue jumpofloop;
			}
			boolean extendHead = false;
			boolean extendTail = false;
			//get the first node and last node of a prime path
			Node head = edgePair.get(0);
			Node tail = edgePair.getEnd();
			//if the first node is the initial node of the graph
			//set true to extendHead
			if(starts.indexOf(head) != -1)
				extendHead = true;
			//if the last node is one of the final nodes of the graph
			//set true to extendTail
			if(ends.contains(tail))
				extendTail = true;
						
			for(int j=0;j<testPaths.size();j++)
			{
				Path testPath = testPaths.get(j);
				//if the last node of the prieme path is not one of the final nodes, 
				//extend it such that the last node is a final node of the graph
				if(!extendTail)
				{	
					//get the index of the last tail node
					int index = testPath.lastIndexOf(tail);
					//extend the prime path so that the last node is one of the final nodes of the graph
					if(index!=-1)
					{
						Path sub = testPath.subPath(index+1, testPath.size());						
						edgePair.extendPath(sub);
						extendTail = true;
					}										
				}
				
				//if the first node of the prieme path is not the intitial node, 
				//extend it such that the first node is the initial ndoe
				if(!extendHead)
				{
					//get the index of the first head node
					int index = testPath.indexOf(head);
					//extend the prime path such that the first node is the initial node 
					if(index != -1)
					{
						Path sub = testPath.subPath(0, index);
						sub.extendPath(edgePair);
						edgePair = sub;
						extendHead = true;
					}
				}
				
				//if extend both, finish extending this path
				if(extendHead && extendTail)
				{
					result.add(edgePair);
					break;
				}
			}//end for loop of j
			
			//if either of two ends can not extend, 
			if(!(extendHead & extendTail))
				throw new InvalidGraphException("Can't find test path for " + edgePair.toString());
		}//end for loop of i
		
		//attempt to minimize the set of test path, step1
		//get rid of test paths that are subpaths of any other test path
		for(int i = 0;i < result.size();i++)
		{
			Path edgePair = result.get(i);
			for(int j = i+1;j < result.size();j++)
			{
				if(result.get(j).isSubpath(edgePair))
				{
					result.remove(j);
					j--;
				}
			}
		}
		
		//minimize the coverage set----step2
		//for a test path, if all edge pairs that are toured by it directly are also toured by another test path directly
		//delete it from the test paths set
		
		//for one test path
		for(int i = 0;i < result.size();i++)
		{
			//get edge-pairs and edge-pairs with sidetrips
			List<Path> edgePairsList = findEdgePairs();
		   List<Path> edgePairsWithSidetripsList = findEdgePairs(infeasibleEdgePairs);
			//initialization
			List<Path> selectedEdgePairs = new ArrayList<Path>();
			List<Path> selectedEdgePairsWithSidetrips = new ArrayList<Path>();
		
			//put all edge pairs that are toured by this path directly into selectedEdgePairs
			for(int z = 0;z < edgePairsList.size();z++)
			{
				if(edgePairsList.get(z).isSubpath(result.get(i)))
					selectedEdgePairs.add(edgePairsList.get(z));
			}
			//put all edge-pairs that are toured by this path with sidetrips into selectedPrimePathsWithSidetrips
			for(int z = 0;z < edgePairsWithSidetripsList.size();z++)
			{
				if(edgePairsWithSidetripsList.get(z).isSubpath(result.get(i)))
					selectedEdgePairsWithSidetrips.add(edgePairsWithSidetripsList.get(z));
			}
			//for the other test paths
			for(int j = 0;j < result.size();j++)
			{
				boolean sign = false;//a sign for determine whether selected edge paris are also toured by the other
				//if exists one edge pair is not toured by the other test path, set true to the sign
				for(int x = 0;x < selectedEdgePairs.size();x++)
				{
					if(!selectedEdgePairs.get(x).isSubpath(result.get(j)))
						sign = true;
				}
				for(int x = 0;x < selectedEdgePairsWithSidetrips.size();x++)
				{
					if(!selectedEdgePairsWithSidetrips.get(x).isSubpath(result.get(j)))
						sign = true;
				}
				//remove the test path if it is redundant
				if(sign == false && !result.get(i).equals(result.get(j)))
					result.remove(i);
			}
		}
		/* this part is not complete
		//attempt to minimize the set of test path, step2
		//get rid of test paths that are subpaths of any other test path
		List<Path> edgePairsCopy = findEdgePairs();
		List<Path> finalResult = new ArrayList<Path>();
		//edgePairsSet storing test paths for edge pairs
		List<Path> edgePairsSet = new ArrayList<Path>();
		for(int i = 0; i < edgePairsCopy.size();i++)
			edgePairsSet.add(edgePairsCopy.get(i));
		//resultSet storing test paths for edge-pair coverage
		List<Path> resultSet = new ArrayList<Path>();
		for(int i = 0; i < result.size();i++)
			resultSet.add(result.get(i));
		
		for(int i = 0;i < edgePairsSet.size();i++)
		{
			int counter = 0;
			Path edgePair = edgePairsSet.get(i);
			List<Path> temp = new ArrayList<Path>();
			for(int j = 0;j < resultSet.size();j++)
			{
				if(edgePair.isSubpath(resultSet.get(j)))
				{
					counter++;
					temp.add(resultSet.get(j));
				}
			}
			if(counter == 1){
				finalResult.add(temp.get(0));
				edgePairsSet.remove(i);
			}
		}
		*/
		return result;
	}
	
	/**
	 * @return: a minimal set of test paths for Edge coverage using sidetrips
	 * 
	 */
	public List<Path> findEdgeCoverage()
	throws InvalidGraphException
	{
		List<Path> result = findTestPath();
		
		List<Path> resultCopy = new ArrayList<Path>();
		for(int i = 0;i < result.size();i++)
			resultCopy.add(result.get(i));
			
		//use sidetrip to minimize the set of test path
		for(int i = result.size() - 1;i > -1;i--)
			for(int j = 0;j < resultCopy.size();j++)
			{			
				Path p1 = result.get(i);
				Path p2 = resultCopy.get(j);
				if(p1 != p2 && p1.sidetrip(p2))
					resultCopy.remove(j);	
			}
		
		//to minimize the test paths set
		//remove redundant paths if all edges have been reached by any other path
		List<Path> resultCopy3 = new ArrayList<Path>();
		//make a copy of resultCopy
		List<Path> resultCopy2 = new ArrayList<Path>();
		for(int i = 0;i < resultCopy.size();i++)
			resultCopy2.add(resultCopy.get(i));
		//make a copy of nodeCopy
		List<Edge> edgeCopy = new ArrayList<Edge>();
		for(int i = 0;i < edges.size();i++)
			edgeCopy.add(edges.get(i));
		//add paths one by one from resultCopy2 to resultCopy3 until all nodes have been reached
		for(int i = 0;i < resultCopy2.size();i++)
		{
			Path path = resultCopy2.get(i);
			resultCopy3.add(path);
			//compare each node in all paths to all nodes in the graph, remove a node from nodeCopy if the node has been reached by one path
			for(int j = 0;j < path.getEdgeList().size();j++)
			{			
				for(int z = 0;z < edgeCopy.size();z++)
				{
					//if an edge in edgeCopy is being reached by one path, remove it from edgeCopy
					boolean sign;
					if(edgeCopy.get(z).equals(path.getEdgeList().get(j)))
					{
						sign = edgeCopy.remove(edgeCopy.get(z));
					
					}
				}
			}
			//when all edges have been reached, jump out of the for loops
			if(edgeCopy.size() == 0)
				break;
		}
		return resultCopy3;
	}

	
	/**
	 * Goal:Find a minimal set of test paths for node coverage using detours
	 * Actually it is really hard to achieve it.
	 * For instance: we have paths from findTestPath()
	 * p1: [0,1,2,3,1,5,6]
	 * p2: [0,1,5,6]
	 * p3: [0,4,4,6]
	 * p4: [0,4,6]
	 * The minimal set of test paths for node coverage should be p1 and p4
	 * However, using touring with detours, we get p1 and p3
	 */
	public List<Path> findNodeCoverage()
	throws InvalidGraphException
	{
		//get all possible test paths
		List<Path> result = findTestPath();
		//make a copy to resultCopy
		List<Path> resultCopy = new ArrayList<Path>();
		for(int i = 0;i < result.size();i++)
			resultCopy.add(result.get(i));
		//for each path in result check if exists any path in resultCopy tours it with Detours
		//if p2 tours p1 with Detours, remove the p2 from the resultCopy
		for(int i = 0;i < result.size();i++)
			for(int j=0;j < resultCopy.size();j++)
			{			
				Path p1 = result.get(i);
				Path p2 = resultCopy.get(j);
				
				if(p1 != p2 && p1.detour(p2)){
					resultCopy.remove(j);
				}
			}
		//remove redundent nodes from paths
		//e.g. all nodes should appear once in paths of node coverage
		//thus,the result is [0,4,6] rather than [0,4,4,6]
		for(int i = 0;i < resultCopy.size();i++){
			Path p = resultCopy.get(i);
	
			for(int j = 0;j < p.size()-1;j++){
				if (p.get(j).equals(p.get(j+1)))
					p.remove(j);
			}
		
		}
		
		//the last step to minimize the set
		//clear up the test paths that can tour any other path with Detours again
		
		//make another copy of current prime paths
		List<Path> resultCopy1 = new ArrayList<Path>();
		for(int i = 0; i < resultCopy.size();i++)
			resultCopy1.add(resultCopy.get(i));
		//for each path in result check if exists any path in resultCopy tours it with Detours
		//if p2 tours p1 with Detours, remove the p2 from the resultCopy
		for(int i = 0;i < resultCopy.size();i++)
			for(int j=0;j < resultCopy1.size();j++)
			{			
				Path p1 = resultCopy.get(i);
				Path p2 = resultCopy1.get(j);
				
				if(p1 != p2 && p1.detour(p2)){
					resultCopy1.remove(j);
				}
			}
		List<Path> resultCopy3 = new ArrayList<Path>();
		//make a copy of resultCopy
		List<Path> resultCopy2 = new ArrayList<Path>();
		for(int i = 0;i < resultCopy1.size();i++)
			resultCopy2.add(resultCopy1.get(i));
		//make a copy of nodeCopy
		List<Node> nodeCopy = new ArrayList<Node>();
		for(int i = 0;i < nodes.size();i++)
			nodeCopy.add(nodes.get(i));
		//add paths one by one from resultCopy2 to resultCopy3 until all nodes have been reached
		for(int i = 0;i < resultCopy2.size();i++)
		{
			Path path = resultCopy2.get(i);
			resultCopy3.add(path);
			//compare each node in all paths to all nodes in the graph, remove a node from nodeCopy if the node has been reached by one path
			for(int j = 0;j < path.size();j++)
			{
				for(int z = 0;z < nodeCopy.size();z++)
				{
					int index =	nodeCopy.indexOf(path.get(j));
					if(index >= 0)
						nodeCopy.remove(index);			
				}
			}
			//check if all nodes have been reached
			if(nodeCopy.size() == 0)
				break;
		}
		
		return resultCopy3;
		
	}
	
	
	/**
	 * A valid graph must have the following three properties :
	 * <li>at least one initial node and at least one final node
	 * <li>exist a path for any node that can be reached by the initial node(connected)
	 * <li>exist a path for any node that can reach at least one ending node(dead loop)
	 * This method check the first and second properties.
	 * 
	 */
	public void validate() throws InvalidGraphException
	{
		//throw InvalidGraphException if no initial nodes
		/*
		int index = nodes.indexOf(start); 
		if(start == null || index == -1)
			throw new InvalidGraphException("No initial node.");
		*/
		if(starts == null || starts.size()== 0)
			throw new InvalidGraphException("No initial nodes.");
		//throw InvalidGraphException if no final nodes
		if(ends == null || ends.size()== 0)
			throw new InvalidGraphException("No ending nodes.");
				
		//check if one node has not outgoing edge but not an ending node.
//		for(int i=0;i<nodes.size();i++)
//			if(!nodes.get(i).getOutGoingIterator().hasNext())
//				if(!ends.contains(nodes.get(i)))
//					throw new InvalidGraphException("The node "+nodes.get(i)+" has no outgoing edge");
			
		
		//if graph is connected
		List<Node> linkedNodes = new ArrayList<Node>();
		List<Node> nodesCopy = new ArrayList<Node>();
		//put all nodes into nodesCopy List
		for(int i = 0;i < nodes.size();i++)
			nodesCopy.add(nodes.get(i));
		//add the initial node into the linkedNodes 
		//and remove the initial node from the nodesCopy
		for(int i = 0; i < starts.size();i++)
			linkedNodes.add(starts.get(i));
		for(int i = starts.size() - 1;i >= 0;i--)
		{
			int index = nodes.indexOf(starts.get(i));
			
			if(index >= 0 && index < nodesCopy.size()){
				nodesCopy.remove(index);				
			}
		}
		//for each node that is connected in the graph, 
		//if the node is included in the linkedNodes, remove it from the nodesCopy and add it to the linkedNodes
		for(int i = 0;i < linkedNodes.size();i++)
		{
			Iterator<Edge> outEdge = linkedNodes.get(i).getOutGoingIterator();
			
			while(outEdge.hasNext())
			{
				Node des = outEdge.next().getDest();
				if(nodesCopy.contains(des))
				{
					nodesCopy.remove(des);
					linkedNodes.add(des);
				}
			}	
		}
		//if there is any node left in the nodesCopy, print them with a InvalidGraphException
		if(nodesCopy.size() != 0 && nodesCopy.size() != 1)			
		{
			String nodeStr = "";
			for(Node node:nodesCopy)
				nodeStr += " " + node.toString();
			throw new InvalidGraphException("The Nodes: " + nodeStr + " are not connected.");		
		}
		else if(nodesCopy.size() == 1)
		{
			String nodeStr = "";
			for(Node node:nodesCopy)
				nodeStr += " " + node.toString();
			throw new InvalidGraphException("The Node: " + nodeStr + " is not connected.");	
		}
	}
	
	/**
	 * return simple paths
	 */
	public List<Path> findSimplePaths()
	{
	   Edge e;
	   Path p, pnew;
	   int lastStart = 0;
	   int curSize;
	   List<Path> simplePaths = new ArrayList<Path>();

	   // Initialize the paths list with the edges
	   for (int i=0; i<edges.size(); i++)
	   {
	      e = (Edge) edges.get(i);
	      p = new Path (e);
	      simplePaths.add (p);
	   }

	   // Continue until an iteration adds no new paths
	   boolean newPaths = true;
	   while (newPaths)
	   {
	      newPaths = false;
	      // Get paths of length the next length
	      curSize = simplePaths.size();
	      for (int i=lastStart; i<curSize; i++)
	      {  // Check each path created in the last iteration
	         p = (Path) simplePaths.get(i);
	         if (! p.isCycle())
	         {  // The path is not already a cycle (extendible)
	            for (int j=0; j<edges.size(); j++)
	            {  // Check each edge
	               e = (Edge) edges.get(j);
	               // if the edge src is the last of the path
	               if(e.getSrc().equals(p.get(p.size()-1)))//if (e.getSrc().equals (p.getEnd()))
	               { // The src of this edge matches the end of the path
	                 // Extend the path with the destination
	                  if (p.indexOf (e.getDest()) == -1 ||
	                      p.indexOf (e.getDest()) == 0)
	                  {  // The destination node is not already in the list
	                     // except possibly at the first spot
	                     newPaths = true;
	                     pnew = (Path) p.clone();
	                     pnew.extendPath (e.getDest());
	                     simplePaths.add (pnew);
	                  }
	               } // end if
	            } // end for
	         } // end if
	      } // end for
	      lastStart = curSize;
	   } // end while
	   
	   return simplePaths;
	}
	/**
	 *
	 * Effects: return nodes of a graph
	 */
	public List<Path> findNodes(){
		List<Path> nodesPath = new ArrayList<Path>();
		for (int i=0; i<nodes.size(); i++)
	   {
			Path p = new Path(nodes.get(i));
	      nodesPath.add(p);
	   }
		return nodesPath;
	}
	/**
	 * 
	 * @return a list of edges of a graph
	 */
	public List<Path> findEdges(){
		List<Path> edgesPath = new ArrayList<Path>();
		for (int i = 0; i < edges.size(); i++)
	   {
			Path p = new Path(edges.get(i));
	      edgesPath.add(p);
	   }
		return edgesPath;
	}
	/**
	 * Effects: return edge-pair requirements of a graph
	 */
	public List<Path> findEdgePairs(){
		long start = System.nanoTime();
		List<Path> edgesPath = new ArrayList<Path>();
		//for each edge, if its destination node has an outgoing edge
		//add this outgoing edge to it
		for (int i = 0; i < edges.size(); i++)
	   {
			
			if(edges.get(i).dest.sizeOfOutEdges() != 0)
			{
				Iterator<Edge> ie = edges.get(i).dest.getOutGoingIterator();
				while(ie.hasNext()){
					Path p = new Path(edges.get(i));
					Path p1 = new Path(ie.next().dest);
					p.extendPath(p1);
					edgesPath.add(p);
				}
			}
			//cover edges that are not covered by paths of length of 2
			else if(edges.get(i).dest.sizeOfOutEdges() == 0 && starts.indexOf(edges.get(i).src) != -1)
			{
				Path p = new Path(edges.get(i));
				edgesPath.add(p);
			}
	   }
		/*
		long end = System.nanoTime();
		long duration = end - start;
		System.out.println("time taken: " + duration);*/
		return edgesPath;
	}
	
	/**
	 * Effects: return a path list of prime paths
	 */
	public List<Path> findPrimePaths()
	{
		//long start = System.nanoTime();
		//get all simple paths
		List<Path> simplePaths = findSimplePaths();
		//generate prime paths only if there is one simple path at least
		if(simplePaths.size() > 0){
			//sort list in term of the size of path
			quickSort(simplePaths, 1, simplePaths.size());
			List<Path> primePaths = new ArrayList<Path>();
			primePaths.add(simplePaths.get(simplePaths.size()-1));
			for(int i = simplePaths.size() - 2;i > -1;i--)
			{
				boolean isSubpath = false;
				for(int j = 0;j < primePaths.size();j++)
					if(simplePaths.get(i).isSubpath(primePaths.get(j)))
					{
						isSubpath=true;
						break;
					}
				
				if(!isSubpath)
					primePaths.add(simplePaths.get(i));
			}	
			return primePaths;
		}	
	//	long end = System.nanoTime();
 	 //  long duration = end - start;
 	 //  System.out.println("The running time: " + duration);
		//if there is no simple path, return an empty List<path>	
		return simplePaths;
	}
	/**
	 * a list of paths including all feasible prime paths and paths touring infeasible paths with sidetrips
	 * the sidetrips are some of feasible prime paths
	 * @param infeasiblePrimePaths
	 * @return
	 */
	public List<Path> findPrimePaths1(String infeasiblePrimePaths)
	{
		List<Path> simplePaths=findSimplePaths();
		
		//sort list in term of the size of path
		quickSort(simplePaths, 1, simplePaths.size());
		List<Path> primePaths = new ArrayList<Path>();
		primePaths.add(simplePaths.get(simplePaths.size()-1));
		for(int i = simplePaths.size()-2;i >- 1;i--)
		{
			boolean isSubpath = false;
			for(int j = 0;j < primePaths.size();j++)
				if(simplePaths.get(i).isSubpath(primePaths.get(j)))
				{
					isSubpath = true;
					break;
				}
			
			if(!isSubpath)
				primePaths.add(simplePaths.get(i));
		}	
		
		//get a copy of prime paths
		List<Path> primePathsCopy = new ArrayList<Path>();
		for(int i = 0; i < primePaths.size();i++)
			primePathsCopy.add(primePaths.get(i));
		
      //remove infeasible prime paths from prime path list
		//only if primePaths String is not empty nor null
		if(!infeasiblePrimePaths.equals("") && !infeasiblePrimePaths.equals(" ") && !infeasiblePrimePaths.equals(null)){
			infeasiblePrimePathsString = infeasiblePrimePaths.trim().split(",");
			//remove the paths from back to the front
			for(int i = primePaths.size() - 1; i >= 0; i--){
				for(int j = 0; j < infeasiblePrimePathsString.length; j++){
					//	subtract 1 because user input number starting from 1 rather than 0
					Integer tempInt = new Integer(infeasiblePrimePathsString[j]) - 1;
					//remove the infeasible prime path and add a prime path that tours that infeasible prime path with sidetrips
					if(tempInt.intValue() == i)
					{
						Path tempPath = primePaths.get(i);
						primePaths.remove(i);
						//remove an infeasible path from primePathsCopy to ensure that only feasible and userful prime paths are left
						primePathsCopy.remove(i);
						//put a test path that tours tempPath with sidetrips to the primePaths
						//for each infeasible path in tempPath, try to find one path that can tour it with sidetrips
						for(int z = 0;z < tempPath.size();z++){
							//use primePathsCopy rather than primePaths to make sure that no prime paths with sidetrips are included in primePaths
							for(int k = 0; k < primePathsCopy.size();k++){
								Path firstPartTempPath = null, secondPartTempPath = null;
								Path tempPrimePath = primePathsCopy.get(k);
								//for one node in the infeasible path, check if there exists a feasible prime path whose both first and last node are the same as it
								//if they are the same, create a one new path that tours the infeasible prime path with sidetrips
								//and add it to the prime paths list
								if(tempPath.get(z).equals(tempPrimePath.get(0)) && tempPath.get(z).equals(tempPrimePath.getEnd()) && !tempPath.equals(tempPrimePath))
								{
									if(z == 0)
									{
										secondPartTempPath = tempPath.subPath(z+1, tempPath.size());
										firstPartTempPath = tempPath.subPath(0, z);
										firstPartTempPath.extendPath(tempPrimePath.subPath(1, tempPrimePath.size()));
										firstPartTempPath.extendPath(secondPartTempPath);
									}
									else if(z > 0 )
									{
										secondPartTempPath = tempPath.subPath(z, tempPath.size());
										firstPartTempPath = tempPath.subPath(0, z);
										firstPartTempPath.extendPath(tempPrimePath.subPath(0, tempPrimePath.size()-1));
										firstPartTempPath.extendPath(secondPartTempPath);
									}
								/*	else if(z > 0 && tempPath.size() == 3)
									{
										secondPartTempPath = tempPath.subPath(z, tempPath.size());
										firstPartTempPath = tempPath.subPath(0, z);
										firstPartTempPath.extendPath(tempPrimePath.subPath(0,tempPrimePath.size()));
										Path path1 = tempPath.subPath(z+1, tempPath.size());
										firstPartTempPath.extendPath(secondPartTempPath);
									}*/
									if(firstPartTempPath.size() < 100)
										primePaths.add(firstPartTempPath);
								}//end if
							}//end for loop of variable k
						}//end for loop of variable z
					}//end if
				}//end for loop of variable j
			}//end for loop of varialbe i
		}//end if
		return primePaths;
	}
	/**
	 * Effects: return a path list of prime paths with sidetrips but no marked infeasible prime paths
	 */
	public List<Path> findPrimePathsWithSidetrips(String infeasiblePrimePaths)
	{
		List<Path> simplePaths = findSimplePaths();
		
		//sort list in term of the size of path
		quickSort(simplePaths, 1, simplePaths.size());
		List<Path> primePaths = new ArrayList<Path>();
		
		primePaths.add(simplePaths.get(simplePaths.size()-1));
		for(int i = simplePaths.size()-2;i >- 1;i--)
		{
			boolean isSubpath = false;
			for(int j = 0;j < primePaths.size();j++)
				if(simplePaths.get(i).isSubpath(primePaths.get(j)))
				{
					isSubpath = true;
					break;
				}
			
			if(!isSubpath)
				primePaths.add(simplePaths.get(i));
		}	
		List<Path> primePathsCopy = new ArrayList<Path>(primePaths.size());
		//remove infeasible prime paths from prime path list
		//only if primePaths String is not empty nor null
		if(!infeasiblePrimePaths.equals("") && !infeasiblePrimePaths.equals(" ") && !infeasiblePrimePaths.equals(null)){
			infeasiblePrimePathsString = infeasiblePrimePaths.trim().split(",");
			//remove the paths from back to the front
			for(int i = primePaths.size() - 1; i >= 0; i--){
				for(int j = 0; j < infeasiblePrimePathsString.length; j++){
					//	subtract 1 because user input number starting from 1 rather than 0
					Integer tempInt = new Integer(infeasiblePrimePathsString[j]) -1;
					//remove the infeasible prime path and add a prime path that tours that infeasible prime path with sidetrips
					if(tempInt.intValue() == i)
					{
						Path tempPath = primePaths.get(i);
						primePaths.remove(i);
						//put a test path that tours tempPath with sidetrips to the primePaths
						for(int z = 0;z < tempPath.size();z++){
							Path firstPartTempPath = null, secondPartTempPath = null;
							for(int k = 0; k < primePaths.size();k++){
								Path tempPrimePath = primePaths.get(k);
								//check the current node of this prime path is same as the initial and final node of another prime path
								if(tempPath.get(z).equals(tempPrimePath.get(0)) && tempPath.get(z).equals(tempPrimePath.getEnd()) && !tempPath.equals(tempPrimePath))
								{
									//final path: firstPartTempPath extends tempPrimePath and also extends secondPartTempPath
									//secondPartTempPath = tempPath.subPath(z+1, tempPath.size());
									secondPartTempPath = tempPath.subPath(z, tempPath.size());
									if(z == 0)
										{
										firstPartTempPath = tempPath.subPath(0, z);
										firstPartTempPath.extendPath(tempPrimePath.subPath(1, tempPrimePath.size()));
										firstPartTempPath.extendPath(secondPartTempPath);
										}
									else if(z > 0)
										{
										firstPartTempPath = tempPath.subPath(0, z);
										//firstPartTempPath.extendPath(tempPrimePath);
										firstPartTempPath.extendPath(tempPrimePath.subPath(0, tempPrimePath.size()-1));
										firstPartTempPath.extendPath(secondPartTempPath);
										}
									if(firstPartTempPath != null)
										primePathsCopy.add(i, tempPath);
									
								}//end if
							}//end for loop of variable k
						}//end for loop of variable z
					}//end if
				}//end for loop of variable j
			}//end for loop of varialbe i
		}//end if
		//take care the case when no infeasible prime path 
		else if(infeasiblePrimePaths.equals("")||infeasiblePrimePaths.equals(" ")){
			//for a prime path 'i', check other prime paths to see if this prime path can be toured by a test path with sidetrips
			for(int i = 0; i < primePaths.size(); i++){
				Path tempPath = primePaths.get(i);
				//iterate each node of this prime path
				for(int z = 0;z < tempPath.size();z++){
					Path firstPartTempPath = null, secondPartTempPath = null;
					//iterate other prime paths
					for(int k = 0; k < primePaths.size();k++){
						Path tempPrimePath = primePaths.get(k);
						//check the current node of this prime path is same as the initial and final node of another prime path
						if(tempPath.get(z).equals(tempPrimePath.get(0)) && tempPath.get(z).equals(tempPrimePath.getEnd()) && !tempPath.equals(tempPrimePath))
						{
							//final path: firstPartTempPath extends tempPrimePath and also extends secondPartTempPath
							secondPartTempPath = tempPath.subPath(z, tempPath.size());
							if(z == 0)
							{
								firstPartTempPath = tempPath.subPath(0, z);
								firstPartTempPath.extendPath(tempPrimePath.subPath(1, tempPrimePath.size()));
								firstPartTempPath.extendPath(secondPartTempPath);
							}
							else if(z > 0)
							{
								firstPartTempPath = tempPath.subPath(0, z);
								firstPartTempPath.extendPath(tempPrimePath.subPath(0, tempPrimePath.size()-1));
								firstPartTempPath.extendPath(secondPartTempPath);
							}
							primePathsCopy.add(i, firstPartTempPath);
							//primePathsCopy.add(tempPath);
						}//end if
					}//end for loop of variable k
				}//end for loop of variable z				
			}//end for loop of varialbe i
		}//end if
		return primePathsCopy;
	}
	/*
	 * Effects: return edge-pair requirements of a graph without marked infeasible edge-pairs
	 */
	public List<Path> findEdgePairs(String infeasibleEdgePairs){
		List<Path> edgesPath = new ArrayList<Path>();
		//for each edge, if its destination node has an outgoing edge
		//add this outgoing edge to it
		for (int i = 0; i < edges.size(); i++)
	   {
			if(edges.get(i).dest.sizeOfOutEdges() != 0)
			{
				Iterator<Edge> ie = edges.get(i).dest.getOutGoingIterator();
				while(ie.hasNext()){
					Path p = new Path(edges.get(i));
					Path p1 = new Path(ie.next().dest);
					p.extendPath(p1);
					edgesPath.add(p);
				}
			}
			//cover edges that are not covered by paths of length of 2
			else if(edges.get(i).dest.sizeOfOutEdges() == 0 && starts.indexOf(edges.get(i).src) != -1)
			{
				Path p = new Path(edges.get(i));
				edgesPath.add(p);
			}
	   }
	/*	
		//remove infeasible prime paths from prime path list
		//only if primePaths String is not empty nor null
		if(!infeasibleEdgePairs.equals("") && !infeasibleEdgePairs.equals(" ") && !infeasibleEdgePairs.equals(null)){
			infeasibleEdgePairsString = infeasibleEdgePairs.trim().split(",");
			for(int i = edgesPath.size() - 1; i >= 0; i--){
				for(int j = 0; j < infeasibleEdgePairsString.length; j++){
					Integer tempInt = new Integer(infeasibleEdgePairsString[j]);
					if(tempInt.intValue() == i)
						edgesPath.remove(i);
				}
			}
		}
		*/
		//get a copy of prime paths
		List<Path> edgePairsCopy = new ArrayList<Path>();
		for(int i = 0; i < edgesPath.size();i++)
			edgePairsCopy.add(edgesPath.get(i));
		
      //remove infeasible edge pairs from edge pairs list
		//only if edge pairs String is not empty nor null
		if(!infeasibleEdgePairs.equals("") && !infeasibleEdgePairs.equals(" ") && !infeasibleEdgePairs.equals(null)){
			infeasibleEdgePairsString = infeasibleEdgePairs.trim().split(",");
			//remove the paths from back to the front
			for(int i = edgesPath.size() - 1; i >= 0; i--){
				for(int j = 0; j < infeasibleEdgePairsString.length; j++){
					//	subtract 1 because user input number starting from 1 rather than 0
					Integer tempInt = new Integer(infeasibleEdgePairsString[j]) - 1;
					//remove the infeasible prime path and add a prime path that tours that infeasible edge pairs with sidetrips
					if(tempInt.intValue() == i)
					{
						Path tempPath = edgesPath.get(i);
						edgesPath.remove(i);
						//remove an infeasible path from edgePairsCopy to ensure that only feasible and userful edge pairs are left
						edgePairsCopy.remove(i);
						//put a test path that tours tempPath with sidetrips to the edgePairs
						for(int z = 0;z < tempPath.size();z++){
							//use edgePairsCopy rather than edgePairs to make sure that no prime paths with sidetrips are included in edgePairs
							for(int k = 0; k < edgePairsCopy.size();k++){
								Path firstPartTempPath = null, secondPartTempPath = null;
								Path tempPrimePath = edgePairsCopy.get(k);
								if(tempPath.get(z).equals(tempPrimePath.get(0)) && tempPath.get(z).equals(tempPrimePath.getEnd()) && !tempPath.equals(tempPrimePath))
								{
									if(z == 0)
									{
										secondPartTempPath = tempPath.subPath(z+1, tempPath.size());
										firstPartTempPath = tempPath.subPath(0, z);
										firstPartTempPath.extendPath(tempPrimePath.subPath(1, tempPrimePath.size()));
										firstPartTempPath.extendPath(secondPartTempPath);
									}
									else if(z > 0 )
									{
										secondPartTempPath = tempPath.subPath(z, tempPath.size());
										firstPartTempPath = tempPath.subPath(0, z);
										firstPartTempPath.extendPath(tempPrimePath.subPath(0, tempPrimePath.size()-1));
										firstPartTempPath.extendPath(secondPartTempPath);
									}
								/*	else if(z > 0 && tempPath.size() == 3)
									{
										secondPartTempPath = tempPath.subPath(z, tempPath.size());
										firstPartTempPath = tempPath.subPath(0, z);
										firstPartTempPath.extendPath(tempPrimePath.subPath(0,tempPrimePath.size()));
										Path path1 = tempPath.subPath(z+1, tempPath.size());
										firstPartTempPath.extendPath(secondPartTempPath);
									}*/
									if(firstPartTempPath.size() < 100)
										edgesPath.add(firstPartTempPath);
								}//end if
							}//end for loop of variable k
						}//end for loop of variable z
					}//end if
				}//end for loop of variable j
			}//end for loop of varialbe i
		}//end if
		return edgesPath;
	}
	/**
	 * @author nli1
	 * @return a minimum test path that covers all prime paths
	 * This algorithm uses the shortest superstring via set cover
	 */
	public List<Path> findMinimumPrimePathCoverageViaSetCover(List<Path> listOfPaths){
		//get the prime paths
		List<Path> primePathsList = new ArrayList<Path>();
		primePathsList = listOfPaths;
		
		//initialize the a list of paths for the set of overlapping paths
		List<Path> overlappingPaths = new ArrayList<Path>();
		
		//initialize the minimum test path
		Path minimumPath = new Path();
		//Path tempPath = new Path();
		
		//initialize the a list of paths for the set of overlapping paths
		List<Path> finalSet = new ArrayList<Path>();
		long start1 = System.nanoTime();

		//find the set that consists of the path covering any two paths that have overlaps.
		for(Path pathI: primePathsList){
			for(Path pathJ: primePathsList)
			{
				//check another path that is not itself
				if(!pathI.equals(pathJ)){
					int index = 0;
					//jump out of the while loop if index is equal to -1
					while(index != -1){
						index = pathI.nextIndexOf(pathJ.get(0), index);
						//start generating the overlapping path
						if(index != -1){
							//a signal for the overlapping
							boolean signal = true;
							//System.out.println("path i: " + pathI);
							//System.out.println("path j: " + pathJ);
							//System.out.println("INDEX: " + index);
							//check whether the last k nodes in path i is the same as the first k nodes in path j
							for(int i = index; i < pathI.size(); i++){
								if(i - index >= pathJ.size()){
									signal = false;
									break;
								}
								if(!pathI.get(i).equals(pathJ.get(i - index))){
									signal = false;
									break;
								}
							}//end of for loop
							
							//the last k nodes in path i is the same as the first k nodes in path j, 
							//get the new path that covers two paths i and j
							if(signal == true){
								Path tempPath = pathI.subPath(0, index);
								//System.out.println("temp path: " + tempPath);
								tempPath = tempPath.immutableExtendedPath(pathJ);
								overlappingPaths.add(tempPath);
							}								
						}//end of if statement
					}//end of while loop
				}//end of if statement
			}//end of for loop
		}//end of for loop
		long end1 = System.nanoTime();
  	   long duration1 = end1 - start1;
		//System.out.println("Time for constructing set covers: " + duration1);
		
		
		long start2 = System.nanoTime();
		//add prime paths to the final set
		for(Path path: primePathsList){
			finalSet.add(path);
		}
		
		//add overlapping paths to the final set
		for(Path path: overlappingPaths){
			finalSet.add(path);
			//System.out.println("super-test requirement for two prime paths " + "#" + overlappingPaths.indexOf(path) + ": "+ path.toString());
		}
		int numberOfSetsSelected = 0;
		int numberOfSets = finalSet.size();
		//initialize the number of prime paths that are the subpaths of one path in the final set
		List<Integer> numberOfSubs = new ArrayList<Integer>();
		for(int i = 0 ; i < finalSet.size();i++)
			numberOfSubs.add(new Integer(0));
		
		//generate the minimum test set
		while(primePathsList.size() > 0){
			double ratio = 100;
			int index = 0;
			for(int i = 0; i < finalSet.size(); i++){
				//calculate how many prime paths are in one path of final set 
				Integer num = 0;
				for(Path primePath: primePathsList){
					if(finalSet.get(i).indexOf(primePath) != -1){
						num++;
					}
				}
				//System.out.println("numberOfSubssize" + numberOfSubs.size());
				//System.out.println("i" + i);
				numberOfSubs.set(i, num);
				//the following statements are not executed
				//if no prime paths are the sub paths of this path in the final set
				if(numberOfSubs.get(i) != 0){
					//only keep track of the minimum effect/cost
					if((finalSet.get(i).size() / numberOfSubs.get(i)) < ratio){
						ratio = (double)finalSet.get(i).size() / (double)numberOfSubs.get(i);
						index = i;	
					}
				}
			}
			
			for(int i = 0;i < primePathsList.size(); i++){
				int index1 = finalSet.get(index).indexOf(primePathsList.get(i));
				if(index1 != -1){
					primePathsList.remove(i);
					i--;
				}
			}
			//System.out.println("size of prime paths: " + primePathsList.size());
		//	System.out.println("extended path: " + finalSet.get(index) + " ;size: " + finalSet.get(index).size());
			//tempPath = minimumPath.immutableExtendedPath(finalSet.get(index));
			minimumPath.extendPath(finalSet.get(index));
			//System.out.println("Selected super-test requirement: " + finalSet.get(index));
			numberOfSetsSelected++;
		//	System.out.println("minimumPath: " + minimumPath);
			finalSet.remove(index);
			numberOfSubs.remove(index);
			
		}//end while loop
		long end2 = System.nanoTime();
  	    long duration2 = end2 - start2;
		//System.out.println("Time for greedy algorithm: " + duration2);
		//System.out.println("final minimumPath: " + minimumPath);
		//System.out.println("size: " + minimumPath.size());
		//System.out.println("number of sets selected: " + numberOfSetsSelected);
		//System.out.println("number of sets: " + numberOfSets);
		overlappingPaths.removeAll(overlappingPaths);
		overlappingPaths.add(minimumPath);
		
		return overlappingPaths;
	}
	
	/**
	 * @author nli1
	 * @return a minimum test path that covers all prime paths
	 * This algorithm uses the shortest superstring via set cover
	 */
	public List<Path> findMinimumPrimePathCoverageViaPrefixGraph(List<Path> listOfPaths){
		List<Path> minimumPaths = new ArrayList<Path>();
		Iterator<Edge> edgesIterator = edges.iterator();
		List<Path> primePaths = listOfPaths;
			
		List<Node> leftSide = new ArrayList<Node>();
		List<Node> rightSide = new ArrayList<Node>();

		Path minimumPath = new Path();

		//construct the vertices of left side and of the right side
		while(edgesIterator.hasNext()){
			Edge edge = edgesIterator.next();
			boolean signForLeft = true;
			boolean signForRight = true;
			for(Node node: leftSide){
				if(edge.getSrc().equals(node))
					signForLeft = false;				
			}
			if(signForLeft)
				leftSide.add(edge.getSrc());
			
			for(Node node: rightSide){
				if(edge.getDest().equals(node))
					signForRight = false;
					
			}	
			if(signForRight)
				rightSide.add(edge.getDest());
		}

	/*	for(int i = 0;i < leftSide.size();i++)
			System.out.println(leftSide.get(i));
		System.out.println("...........");
		for(int i = 0;i < rightSide.size();i++)
			System.out.println(rightSide.get(i));
		System.out.println("...........");*/
		
		List<Edge> perfectMatching = new ArrayList<Edge>();

		Iterator<Edge> edgesLeft = null;
		for(int i = 0; i < leftSide.size(); i++){
			Node node = leftSide.get(i);
			edgesLeft = node.getOutGoingIterator();
			//size of the outgoing edges for this node
			int size = node.sizeOfOutEdges();
			int counter = 0;
			//go through all outgoing edges
			while(edgesLeft.hasNext()){
				Edge edge = edgesLeft.next();
				counter++;
				//decide whether this edge is incident to the same node with another edge
				boolean sign = false;
				for(int j = 0; j < perfectMatching.size();j++){
					if(perfectMatching.get(j).getDest().equals(edge.getDest())){
						sign = true;
						break;
					}
				}//end of for loop of variable j
				if(sign == false){
					perfectMatching.add(edge);
					break;
				}
				else{
					if(size != counter){
						continue;
					}
					//if all outgoing edges share a node that is incident to another edge
					else{
						//System.out.println("The thing: " + node + " " + edge);
						edgesLeft = node.getOutGoingIterator();
						outer:
						while(edgesLeft.hasNext()){
							if(i == (perfectMatching.size() - 1))
								break;
							Edge edge1 = edgesLeft.next();
						//	System.out.println("edge1: " + edge1);
							Node nodeDest = edge1.getDest();
						//	System.out.println("edge1's dest " + edge1.getDest());
							Node nodeSrc = null;
							int position = 0;
							for(int x = 0; x < perfectMatching.size(); x++){
								if(perfectMatching.get(x).getDest().equals(nodeDest)){
									nodeSrc = perfectMatching.get(x).getSrc();
									position = x;
									break;
								}
							}
							//System.out.println("position " + position);
							if(nodeSrc != null){
								Iterator<Edge> edges = nodeSrc.getOutGoingIterator();
								while(edges.hasNext()){
									Edge edge2 = edges.next();
									boolean sign1 = false;
							//		System.out.println("edge2: " + edge2);
									for(int y = 0; y < perfectMatching.size(); y++){
										if(edge2.getDest().equals(perfectMatching.get(y).getDest())){
											sign1 = true;
											break;
										}
									
									}//end for loop with variable y
									if(sign1 == false){
										perfectMatching.set(position, edge2);
										perfectMatching.add(edge1);
							//			System.out.println("edge2: " + edge2 + "edge1: " + edge1);
										break outer;
									}
								}//end while loop
							}//end if 					
						}//end while loop
						
					}//end if-else
				}//end if-else				
			}//end while loop
			
		}//end of for loop of variable i
	//	for(int i = 0;i < perfectMatching.size();i++){
	//		System.out.println(perfectMatching.get(i));
	//	}
		
		List<Path> paths = null;
	


				
				//System.out.println(result);
			//	for(Edge edge: perfectMatching)
			//		System.out.print(edge);
				
				Path path = new Path();
				paths = new ArrayList<Path>();
				boolean signForMatching = false;
				path.extendPath(perfectMatching.get(0).getSrc());
				path.extendPath(perfectMatching.get(0).getDest());
				perfectMatching.remove(0);
				
				while(perfectMatching.size() > 0){				
					if(path.size() == 0){
						path.extendPath(perfectMatching.get(0).getSrc());
						path.extendPath(perfectMatching.get(0).getDest());
						perfectMatching.remove(0);;
					}
					signForMatching = false;
				//	System.out.print("path: " + path);
					
					for(int y = 0; y < perfectMatching.size(); y++){
						if(path.get(0).equals(perfectMatching.get(y).getDest())){
							Path p = new Path();
							p.extendPath(perfectMatching.get(y).getSrc());
							p.extendPath(path);	
							path = new Path();
							path = p;
							perfectMatching.remove(y);	
					//		System.out.print("pathdest: " + path + "size: " + perfectMatching.size());
							if(perfectMatching.size() == 0){
								paths.add(path);
								path = new Path();
							}
							y--;
							signForMatching = true;
							
							break;
						}
						
						if(path.getEnd().equals(perfectMatching.get(y).getSrc())){
							path.extendPath(perfectMatching.get(y).getDest());
							perfectMatching.remove(y);
					//		System.out.print("pathsrc: " + path + "size: " + perfectMatching.size());
							if(perfectMatching.size() == 0){
								paths.add(path);
								path = new Path();
							}
							y--;
							signForMatching = true;
							break;
						}
					}//end for loop
					if(signForMatching != true){
						//if(path.size() == 2){
							paths.add(path);
						//}
						path = new Path();
					}
				}//end while loop	
				
			//	System.out.println();
				int tempValue = 0;		
				for(Path path1: paths){
					//System.out.println("finalpath: " + path1);
					path = new Path();
					List<Node> nodes = path1.path;
					for(int m = 0; m < nodes.size() - 1; m++){					
						Node nSrc = nodes.get(m);
						Node nDest = nodes.get(m + 1);
						Edge e = new Edge(nSrc, nDest);
						boolean signForEdge = false;
						for(int l = 0; l < edges.size(); l++){
							if(e.equals(edges.get(l))){
								tempValue = tempValue + ((Path)edges.get(l).getWeight()).size();
								path.extendPath((Path)edges.get(l).getWeight());
								signForEdge = true;
							}							
						}//end for loop
						if(m == nodes.size() - 2){
							tempValue = tempValue + ((Path)nodes.get(m + 1).getObject()).size();
							path.extendPath((Path)nodes.get(m + 1).getObject());
						}						
					}//end for loop
					minimumPath.extendPath(path);
					//System.out.println("minimumpath: " + minimumPath);
				}//end for loop

			
		for(Path path1: primePaths){
			//System.out.println("path: " + path1);
			if(minimumPath.indexOf(path1) == -1)
				minimumPath.extendPath(path1);
		}
		
		//System.out.println("minimumpath: " + minimumPath);
		minimumPaths.add(minimumPath);
		return minimumPaths;
	}
	
	/**
	 * @author nli1
	 * @return a minimum test path that covers all prime paths
	 * This algorithm uses the shortest superstring via set cover
	 * @throws InvalidGraphException 
	 */
	public List<Path> findMinimumPrimePathCoverageViaPrefixGraphOptimized(List<Path> listOfPaths) throws InvalidGraphException{
		//a list of paths to be returned
		List<Path> minimumPaths = new ArrayList<Path>();
		//get the edges of the bipartite graph
		Iterator<Edge> edgesIterator = edges.iterator();
		//get the prime paths from the formal parameter listOfPaths
		List<Path> primePaths = listOfPaths;
		//a set for the greedy algorithm in the last step
		List<Path> finalPathsSet = new ArrayList<Path>();
		//a list of nodes to store the nodes on the left side	
		List<Node> leftSide = new ArrayList<Node>();
		//a list of nodes to store the nodes on the right side
		List<Node> rightSide = new ArrayList<Node>();
		//the minimum path
		Path minimumPath = new Path();

		//construct vertices on the left side and on the right side
		while(edgesIterator.hasNext()){
			Edge edge = edgesIterator.next();
			//a sign for redundant vertices of the left side
			boolean signForLeft = true;
			//a sign for redundant vertices of the right side
			boolean signForRight = true;
			//if the source node of an edge is not on the list of the left side, add it to the left side
			for(Node node: leftSide){
				if(edge.getSrc().equals(node))
					signForLeft = false;				
			}
			if(signForLeft)
				leftSide.add(edge.getSrc());
			//if the destination node of an edge is not on the list of the left side, add it to the right side
			for(Node node: rightSide){
				if(edge.getDest().equals(node))
					signForRight = false;
					
			}	
			if(signForRight)
				rightSide.add(edge.getDest());
		}
		long start1 = System.nanoTime();
		//a list of edges to store the perfect matching
		List<Edge> perfectMatching = new ArrayList<Edge>();
		int counterOne = 0;
		int counterTwo = 0;
		//an iterator of edges for each vertex on the left side
		Iterator<Edge> edgesLeft = null;
		for(int i = 0; i < leftSide.size(); i++){
			//if the vertices of the right side are fewer than those of the left side
			//and the number of edges in the perfect matching is bigger than the number of vertices of the right side
			//break from the for loop since the perfect matching has been completed
			if(perfectMatching.size() >= rightSide.size() && rightSide.size() < leftSide.size())
				break;
			//	System.out.println("i: "+ i);
			//get the iterator of edges for one vertex
			Node node = leftSide.get(i);
			edgesLeft = node.getOutGoingIterator();
			//size of the outgoing edges for this vertex
			int size = node.sizeOfOutEdges();
			int counter = 0;
			//go through all outgoing edges
			while(edgesLeft.hasNext()){
				Edge edge = edgesLeft.next();
				counter++;
				//decide whether this edge is incident to the same vertex with another edge
				boolean sign = false;
				for(int j = 0; j < perfectMatching.size();j++){
					if(perfectMatching.get(j).getDest().equals(edge.getDest())){
						sign = true;
						break;
					}
				}//end of for loop of variable j
				//if the destination node of this edge has not been in any other matchings that have been selected for the perfect matching
				//add this edge to the perfect matching
				if(sign == false){
					perfectMatching.add(edge);
					//jump out of the loop and go to the next vertex
					break;
				}
				//if the destination node of this edge has been in any other matchings that have been selected for the perfect matching
				//switch the edge with another edge that has been selected 
				else{
					//try the next edge if this edge is not the last one for this vertex
					if(size != counter){
						continue;
					}
					//if each outgoing edge shares a node that is incident to another edge that has been in the matchings
					else{
						edgesLeft = node.getOutGoingIterator();
						boolean signForSwitch = false; // check if the edge have been switched with another
						//go through all edges for this vertex
						outer:
						while(edgesLeft.hasNext()){
							if(i == (perfectMatching.size() - 1))
								break;
							Edge edge1 = edgesLeft.next();
							Node nodeDest = edge1.getDest();
							Node nodeSrc = null;
							int position = 0;
							//System.out.println("one edge1: " + edge1);
							counterOne++;
							//find the edge that has conflict with the current edge in the perfect matching
							for(int x = 0; x < perfectMatching.size(); x++){
								if(perfectMatching.get(x).getDest().equals(nodeDest)){
									//find the vertex that is the source node of the edge that conflicts
									nodeSrc = perfectMatching.get(x).getSrc();
									position = x;
									break;
								}
							}
							
							if(nodeSrc != null){
								Iterator<Edge> edges = nodeSrc.getOutGoingIterator();
								while(edges.hasNext()){
									Edge edge2 = edges.next();
									//System.out.println("one edge2: " + edge2);
									counterTwo++;
									boolean sign1 = false;
									//see whether the edge has any conflict with the current perfect matching
									for(int y = 0; y < perfectMatching.size(); y++){
										if(edge2.getDest().equals(perfectMatching.get(y).getDest())){
											sign1 = true;
											break;
										}									
									}//end for loop with variable y
									//if no conflicts, add the edges
									if(sign1 == false){
										perfectMatching.set(position, edge2);
										perfectMatching.add(edge1);
										signForSwitch = true;
										break outer;
									}
								}//end while loop							
							}//end if 					
						}//end while loop
						//System.out.println("sign: " + signForSwitch);
						if(signForSwitch == false)
							break;
					}//end if-else
				}//end if-else				
			}//end while loop
			
		}//end of for loop of variable i
	/*	for(int i = 0;i < leftSide.size();i++)
			System.out.println(leftSide.get(i));
		System.out.println("...........");
		for(int i = 0;i < rightSide.size();i++)
			System.out.println(rightSide.get(i));
		System.out.println("...........");
		for(Edge edge: perfectMatching)
			System.out.println(edge);*/
		int sizeOfPerfectMatching = perfectMatching.size();
		List<Path> paths =new ArrayList<Path>();
		// for the perfect matching, find the all cycle covers and store all paths to the finalPathsSet	
		Path path = new Path();
		boolean signForMatching = false;
		if(perfectMatching.size() >= 1){
			path.extendPath(perfectMatching.get(0).getSrc());
			path.extendPath(perfectMatching.get(0).getDest());
			perfectMatching.remove(0);
		}
				
		while(perfectMatching.size() > 0){				
			if(path.size() == 0){
				path.extendPath(perfectMatching.get(0).getSrc());
				path.extendPath(perfectMatching.get(0).getDest());
				perfectMatching.remove(0);;
			}
			signForMatching = false;
					
			for(int y = 0; y < perfectMatching.size(); y++){
				//if the first node of the path is the same as the last node of one path in the perfect matching
				if(path.get(0).equals(perfectMatching.get(y).getDest())){
					Path p = new Path();
					p.extendPath(perfectMatching.get(y).getSrc());
					p.extendPath(path);	
					path = new Path();
					path = p;
					perfectMatching.remove(y);	
					if(perfectMatching.size() == 0){
						paths.add(path);
						path = new Path();
					}
					y--;
					signForMatching = true;						
					break;
				}
				//if the last node of the path is the same as the first node of one path in the perfect matching	
				if(path.getEnd().equals(perfectMatching.get(y).getSrc())){
							path.extendPath(perfectMatching.get(y).getDest());
							perfectMatching.remove(y);
							
							if(perfectMatching.size() == 0){
								paths.add(path);
								path = new Path();
							}
							y--;
							signForMatching = true;
							break;
						}
			}//end for loop
			// if the current path does not have any overlapping with any path in the perfect matchings, add this path to paths 
			if(signForMatching != true){
				paths.add(path);
				path = new Path();
			}
		}//end while loop	
		
		//int tempValue = 0;		
		for(Path path1: paths){
		//	System.out.println("finalpath: " + path1);
			path = new Path();
			List<Node> nodes = path1.path;
			boolean sign = false;
			if(nodes.get(0).equals(nodes.get(nodes.size() - 1))){
				sign = true;
			}
			int size = nodes.size();
			for(int m = 0; m < size - 1; m++){					
				Node nSrc = nodes.get(m);
				Node nDest = nodes.get(m + 1);
				Edge e = new Edge(nSrc, nDest);
			//	boolean signForEdge = false;
				for(int l = 0; l < edges.size(); l++){
					if(e.equals(edges.get(l))){
						//tempValue = tempValue + ((Path)edges.get(l).getWeight()).size();
						path.extendPath((Path)edges.get(l).getWeight());
			//			signForEdge = true;
					}							
				}//end for loop
				if(m == size - 2 && sign == false){
					//tempValue = tempValue + ((Path)nodes.get(m + 1).getObject()).size();
					path.extendPath((Path)nodes.get(m + 1).getObject());
				}
				if(m == size - 3 && sign == true){
					path.extendPath((Path)nodes.get(m + 1).getObject());
					break;
				}
			}//end for loop of variable m
		//	System.out.println("path: " + path);
			minimumPath.extendPath(path);
			finalPathsSet.add(path);
		//	System.out.println("minimumpath: " + minimumPath);
		}//end for loop
	//	System.out.println("middle minimumPath: " + minimumPath + "; size: " + minimumPath.size());
		long end1 = System.nanoTime();
		long duration1 = end1 - start1;
		//System.out.println("Time for constructing cycle covers: " + duration1);
		
		long start2 = System.nanoTime();
		//add prime paths to the final paths set
		minimumPath = new Path();
		for(Path tempPath: primePaths){
			finalPathsSet.add(tempPath);
		}
		int numberOfFinalSets = finalPathsSet.size();
		int numberOfSetsSelected = 0;
		//run the greedy algorithm
		//initialize the number of prime paths that are the subpaths of one path in the final set
		List<Integer> numberOfSubs = new ArrayList<Integer>();
		for(int i = 0 ; i < finalPathsSet.size();i++)
			numberOfSubs.add(new Integer(0));
		
		//generate the minimum test set
		while(primePaths.size() > 0){
		//	if(finalPathsSet.size() == 0)
		//		break;
			double ratio = 100;
			int index = 0;
			for(int i = 0; i < finalPathsSet.size(); i++){
				//calculate how many prime paths are in one path of final set 
				Integer num = 0;
				for(Path primePath: primePaths){
					if(finalPathsSet.get(i).indexOf(primePath) != -1){
						num++;
					}
				}
				//System.out.println("numberOfSubssize" + numberOfSubs.size());
				//System.out.println("i" + i);
				numberOfSubs.set(i, num);
				//the following statements are not executed
				//if no prime paths are the sub paths of this path in the final set
				if(numberOfSubs.get(i) != 0){
					//only keep track of the minimum effect/cost
					if((finalPathsSet.get(i).size() / numberOfSubs.get(i)) < ratio){
						ratio = (double)finalPathsSet.get(i).size() / (double)numberOfSubs.get(i);
						index = i;	
						
					}
				}
			}
			
			for(int i = 0;i < primePaths.size(); i++){
			//	System.out.println("index: " + index);
			//	System.out.println("size: " + finalPathsSet.size());
				int index1 = finalPathsSet.get(index).indexOf(primePaths.get(i));
				if(index1 != -1){
					primePaths.remove(i);
					i--;
				}
			}
			//System.out.println("size of prime paths: " + primePathsList.size());
		//	System.out.println("extended path: " + finalPathsSet.get(index) + " ;size: " + finalPathsSet.get(index).size());
			//tempPath = minimumPath.immutableExtendedPath(finalSet.get(index));
			minimumPath.extendPath(finalPathsSet.get(index));
			numberOfSetsSelected++;
		//	System.out.println("minimumPath: " + minimumPath);
			finalPathsSet.remove(index);
			numberOfSubs.remove(index);
			
		}//end while loop
		
		long end2 = System.nanoTime();
		long duration2 = end2 - start2;
		//System.out.println("Time for greedy algorithm: " + duration2);	
	/*	for(Path path1: primePaths){
			if(minimumPath.indexOf(path1) == -1)
				minimumPath.extendPath(path1);
		}*/
		
		//System.out.println("final minimumpath: " + minimumPath);
		//System.out.println("length: " + minimumPath.size());
	/*	System.out.println("number of sets selected: " + numberOfSetsSelected);
		System.out.println("number of sets: " + numberOfFinalSets);
		System.out.println("left side size: " + leftSide.size());
		System.out.println("right side size: " + rightSide.size());
		System.out.println("perfect matching: " + sizeOfPerfectMatching);
		System.out.println("edge1: " + counterOne);
		System.out.println("edge2: " + counterTwo);*/
		minimumPaths.add(minimumPath);
		
		return minimumPaths;
	}
	
	public List<Path> splittedPathsFromSuperString(Path superString, List<Path> testPaths){
	//	System.out.println("super string: " + superString.toString());
	/*	System.out.println("-----test paths------");
		for(Path path: testPaths){
			System.out.println(path.toString());
		}
		System.out.println("-----test paths------");*/
		long start = System.nanoTime();
		/*
		 * Breaking a long superstring into shorter test paths starts here:
		 * 
		 */
		List<Path> paths = new ArrayList<Path>();
//		System.out.println("size of starts: " + starts.size());
		//List<Path> localTestPaths = testPaths;
		Path testPath = new Path(superString.get(0));
		for(int i = 1; i < superString.size();i++){
			Node node = superString.get(i);
			//if the preceding node is not connected to this node, create a new test path starting from //this node
			if(isInitialNode(node) && !isEdge(superString.get(i - 1), node)){
					testPath = new Path(node);	
			}
			else if(testPath == null){
				testPath = new Path(node);
			}
			else{
				testPath.extendPath(node);
			}
			//System.out.println("first: " + testPath);
			//check whether the current path has been disconnected from the adjacent nodes in the minimumPath
			if(i == superString.size() - 1){
				if(!isInitialNode(testPath.get(0))){
					//find the smallest index of the node in each existing test path
					int indexInt = 32255;
					int index = 0;
					for(int j = 0;j < testPaths.size();j++){
						if(testPaths.get(j).indexOf(testPath.get(0)) < indexInt && testPaths.get(j).indexOf(testPath.get(0))>= 0 ){
							indexInt = testPaths.get(j).indexOf(testPath.get(0));
							index = j;
						}
					}
					//construct a test path by using a starting node from existing test paths
					Path thePath = new Path(testPaths.get(index).get(0));
					for(int j = 1;j < indexInt;j++){
						thePath.extendPath(testPaths.get(index).get(j));
					}
					
					for(int j = 0;j < testPath.size();j++){
						thePath.extendPath(testPath.get(j));
					}
					testPath = new Path();
					
					for(int j = 0;j < thePath.size();j++){
						testPath.extendPath(thePath.get(j));
					}
				}
				if(!isEndingNode(testPath.getEnd())){
					//find the smallest index of the node in each existing test path
					int indexInt = 0;
					int index = 0;
					for(int j = 0;j < testPaths.size();j++){
						if(testPaths.get(j).lastIndexOf(testPath.getEnd()) > indexInt){
							indexInt = testPaths.get(j).lastIndexOf(testPath.getEnd());
							index = j;
						}
					}
					//this piece of code is commented out because it adds redundant nodes to the very last split test path
					//a test path should be [1,2,4,6,7,9,10] but this code results in [1,2,4,6,7,9,9,9,10] but no [9,9] exists
					//construct a test path by using a starting node from existing test paths
				//	Path thePath = new Path(testPaths.get(index).get(indexInt));
				//	for(int j = indexInt;j < testPaths.get(index).size();j++){
				//		thePath.extendPath(testPaths.get(index).get(j));
				//	}
					
					for(int j = indexInt + 1;j < testPaths.get(index).size();j++){
						testPath.extendPath(testPaths.get(index).get(j));
					}
				}
				//System.out.println("testPathlast: " + testPath);
				paths.add(testPath);
				
			}
			//if the current node is not connected to the next node, generate a test path for the previously selected nodes
			else if(!isEdge(node, superString.get(i + 1))){
				if(!isInitialNode(testPath.get(0))){
					//find the smallest index of the node in each existing test path
					int indexInt = 32255;
					int index = 0;
					for(int j = 0;j < testPaths.size();j++){
						if(testPaths.get(j).indexOf(testPath.get(0)) < indexInt && testPaths.get(j).indexOf(testPath.get(0))>= 0 ){
							indexInt = testPaths.get(j).indexOf(testPath.get(0));
							index = j;
						}
					}
					//System.out.println("indexInt: " + indexInt);
					//System.out.println("index: " + index);
					//construct a test path by using a starting node from existing test paths
					Path thePath = new Path(testPaths.get(index).get(0));
					for(int j = 1;j < indexInt;j++){
						thePath.extendPath(testPaths.get(index).get(j));
					}
					
					for(int j = 0;j < testPath.size();j++){
						thePath.extendPath(testPath.get(j));
					}
					//System.out.println("thePath: " + thePath);
					testPath = new Path();
					
					for(int j = 0;j < thePath.size();j++){
						testPath.extendPath(thePath.get(j));
					}
				}
				if(!isEndingNode(testPath.getEnd())){
					//find the smallest index of the node in each existing test path
					int indexInt = 0;
					int index = 0;
					for(int j = 0;j < testPaths.size();j++){
						if(testPaths.get(j).lastIndexOf(testPath.getEnd()) > indexInt){
							indexInt = testPaths.get(j).lastIndexOf(testPath.getEnd());
							index = j;
						}
					}
//					System.out.println("indexInt: " + indexInt);
//					System.out.println("index: " + index);
					//construct a test path by using a starting node from existing test paths
					Path thePath = new Path(testPaths.get(index).get(indexInt + 1));
					for(int j = indexInt + 2;j < testPaths.get(index).size();j++){
						thePath.extendPath(testPaths.get(index).get(j));
					}
					
					for(int j = 0;j < thePath.size();j++){
						testPath.extendPath(thePath.get(j));
					}
				}
			//	System.out.println("testPath: " + testPath);
				paths.add(testPath);
//				System.out.println("testPath: " + testPath);
				testPath = null;
			}	
			/*
			else{
				if(!isInitialNode(testPath.get(0))){
					//find the smallest index of the node in each existing test path
					int indexInt = 32255;
					int index = 0;
					for(int j = 0;j < testPaths.size();j++){
						if(testPaths.get(j).indexOf(testPath.get(0)) < indexInt && testPaths.get(j).indexOf(testPath.get(0))>= 0 ){
							indexInt = testPaths.get(j).indexOf(testPath.get(0));
							index = j;
						}
					}
					//construct a test path by using a starting node from existing test paths
					Path thePath = new Path(testPaths.get(index).get(0));
					for(int j = 1;j < indexInt;j++){
						thePath.extendPath(testPaths.get(index).get(j));
					}
					
					for(int j = 0;j < testPath.size();j++){
						thePath.extendPath(testPath.get(j));
					}
					testPath = new Path();
					
					for(int j = 0;j < thePath.size();j++){
						testPath.extendPath(thePath.get(j));
					}
				}
				if(!isEndingNode(testPath.getEnd())){
					//find the smallest index of the node in each existing test path
					int indexInt = 0;
					int index = 0;
					for(int j = 0;j < testPaths.size();j++){
						if(testPaths.get(j).lastIndexOf(testPath.getEnd()) > indexInt){
							indexInt = testPaths.get(j).lastIndexOf(testPath.getEnd());
							index = j;
						}
					}
					//construct a test path by using a starting node from existing test paths
					Path thePath = new Path(testPaths.get(index).get(indexInt));
					for(int j = indexInt;j < testPaths.get(index).size();j++){
						thePath.extendPath(testPaths.get(index).get(j));
					}
					
					for(int j = 0;j < thePath.size();j++){
						testPath.extendPath(thePath.get(j));
					}
				}
				System.out.println("testPath: " + testPath);
				paths.add(testPath);
			}
			*/
		}//end for loop
		
		//remove duplicated paths in the path list
		for(int i = 0; i < paths.size(); i++){
			Path path = paths.get(i);
			for(int j = i + 1; j < paths.size(); ){
				Path anotherPath = paths.get(j);
				if(path.equals(anotherPath)){
					paths.remove(j);
				}
				else{
					j++;
				}
			}
		}
		
		long end = System.nanoTime();
     	long duration = end - start;
     	//System.out.println("running time of splitting super string = " + duration);
     	
		return paths;
	}
	/**
	 * 
	 * @param starting
	 * @param target
	 * @return a list of all augmenting paths
	 * @throws InvalidGraphException 
	 */
	public List<Path> fordFulkerson(Node starting, Node target) throws InvalidGraphException{
		List<Path> augmentingPaths = new ArrayList<Path>();
		//a residual graph
		Graph residualGraph = new Graph();
		//create a residual graph having the same nodes and edges as the original graph
		for(Node node: this.nodes){
			residualGraph.createNode(node.getObject());
		}
		
		//assign the capacities for all edges
	/*	List<Edge> edges = this.edges;
		edges.get(0).setCapacity(16);
		edges.get(1).setCapacity(13);
		edges.get(2).setCapacity(12);
		edges.get(3).setCapacity(4);
		edges.get(4).setCapacity(14);
		edges.get(5).setCapacity(9);
		edges.get(6).setCapacity(20);
		edges.get(7).setCapacity(7);
		edges.get(8).setCapacity(4);*/
		
		//System.out.println("flow graph edges: " + this.edges.size());
		
		//add edges between left side and right side
		for(Edge edge: this.edges){
			Node leftNode = edge.src;
			Node rightNode = edge.dest;
			for(Node node: residualGraph.nodes){
				if(!node.getObject().equals("S") && !node.getObject().equals("T")){
					if(((Path)node.getObject()).equals(leftNode.getObject()))
						leftNode = node;
					if(((Path)node.getObject()).equals(rightNode.getObject()))
						rightNode = node;
				}
			}
			residualGraph.createEdge(leftNode, rightNode, null, edge.getCapacity(), edge.getFlow());
		}
		//add the starting and final node
		if(starting != null)
			residualGraph.starts.add(starting);
		else{
			for(Node node: this.starts)
				residualGraph.addInitialNode(residualGraph.createNode(node.getObject()));
		}
		if(target != null)
			residualGraph.ends.add(target);
		else{
			for(Node node: this.ends)
				residualGraph.addEndingNode(residualGraph.createNode(node.getObject()));
		}	
		
	//	for(Node node: residualGraph.nodes)
	//		System.out.println(node.toString());
		//System.out.println("residual graph edges: " + residualGraph.edges.size());
		//for(Edge edge: residualGraph.edges)
		//	System.out.println(edge.toStringWithFlow());
		
		Path augmentingPath = null;
		//System.out.println("nodes' size: " + residualGraph.nodes.size());
		
		int numberLeft = 0;
		int numberRight = 0;
		int numberForStop = 0;
		for(int i = 0; i < nodes.size();i++){
			if(!ends.contains(nodes.get(i))){
				if(nodes.get(i).toString().indexOf("L") != -1)
					numberLeft++;
				if(nodes.get(i).toString().indexOf("R") != -1)
					numberRight++;
			}
		}
		if(numberLeft < numberRight)
			numberForStop = numberLeft;
		else
			numberForStop = numberRight;
		
		while((augmentingPath = residualGraph.nextAugmentingPath()) != null || augmentingPaths.size() < numberForStop){
		//if(augmentingPaths == null)
		//	break;
		augmentingPaths.add(augmentingPath);
		//get all edges from the augmenting path
		List<Edge> edgesOfAugmentingPath  = new ArrayList<Edge>();
		for(int i = 0; i < augmentingPath.size();){
			Edge edge = residualGraph.findEdge(augmentingPath.get(i).getObject(), augmentingPath.get(++i).getObject());
			edgesOfAugmentingPath.add(edge);
			//jump out of the loop if all edges are reached
			if(i == (augmentingPath.size() - 1))
				break;
		}
		//get the minimum flow for the augmenting path
		int minimumFlow = 200000;
		for(Edge edge:edgesOfAugmentingPath){
			//System.out.println("edge: " + edge.toString());
			if(edge.getCapacity() < minimumFlow)
				minimumFlow = edge.getCapacity();
		}
		//update the flows in the flow graph
		for(Edge edge: this.edges){
			for(Edge edge1: edgesOfAugmentingPath){
				if(edge.equals(edge1) && (edge.getCapacity() >= edge.getFlow()  + minimumFlow))
					edge.setFlow(edge.getFlow() + minimumFlow);				
			}		
		}
		//update the residual graph
		for(Edge edge: edgesOfAugmentingPath){
		//	for(Edge edge1: residualGraph.edges){
		//		if(edge.equals(edge1)){
					if(minimumFlow < edge.getCapacity()){
						edge.setCapacity(edge.getCapacity() - minimumFlow);
						if(residualGraph.findEdge(edge.dest.getObject(), edge.src.getObject()) == null)
							residualGraph.addEdge(new Edge(residualGraph.createNode(edge.dest.getObject()), residualGraph.createNode(edge.src.getObject()), null, minimumFlow, 0));
						else
							residualGraph.findEdge(edge.dest.getObject(), edge.src.getObject()).setCapacity(residualGraph.findEdge(edge.dest.getObject(), edge.src.getObject()).getCapacity() + minimumFlow);
					}
					else if(minimumFlow == edge.getCapacity()){
						residualGraph.edges.remove(edge);
						residualGraph.findNode(edge.src.getObject()).removeOutGoing(edge);
						if(residualGraph.findEdge(edge.dest.getObject(), edge.src.getObject()) == null)
							residualGraph.addEdge(new Edge(residualGraph.createNode(edge.dest.getObject()), residualGraph.createNode(edge.src.getObject()), null, minimumFlow, 0));
						else
							residualGraph.findEdge(edge.dest.getObject(), edge.src.getObject()).setCapacity(residualGraph.findEdge(edge.dest.getObject(), edge.src.getObject()).getCapacity() + minimumFlow);
					}
		//		}
		//	}
		}
		
		//for(Edge edge: this.edges)
		//	System.out.println("flow edges: " + edge.toStringWithFlow());
		//System.out.println("----------------------------------------------------");
		//for(Edge edge: residualGraph.edges)
		//	System.out.println("residual edges: " + edge.toStringWithFlow());
		}
		return augmentingPaths;
		
	}
	
	/**
	 * 
	 * @return the next augmenting path from the residual graph
	 * @throws InvalidGraphException 
	 */
	public Path nextAugmentingPath() throws InvalidGraphException{
		//validate whether the graph is valid
		// validate();
		//create result to return the expected paths			
		List<Path> result = new ArrayList<Path>();
		
		//compute the possible number for the paths
	/*	long possibleNumber = 1L;
		for(int i = 0; i < nodes.size();i++){
			if(!ends.contains(nodes.get(i))){
				System.out.println(nodes.get(i));
				System.out.println(nodes.get(i).sizeOfOutEdges());
				possibleNumber *= nodes.get(i).sizeOfOutEdges();
			}
		}
		System.out.println("possibleNumber: " + possibleNumber);*/
		
		//go through all paths from each initial node
		//
		for(int j = 0; j < starts.size();j++)
		{
			//add all nodes to nodesCopy
			List<Node> nodesCopy = new ArrayList<Node>();
			for(int i = 0;i < nodes.size();i++)
				nodesCopy.add(nodes.get(i));
			//remove all initial nodes from nodesCopy
			for(int i = starts.size() - 1;i >= 0;i--)
			{
				int index = nodes.indexOf(starts.get(i));
				nodesCopy.remove(index);
			}
			//initialize each path with one initial node
			List<Path> paths = new ArrayList<Path>();		
			paths.add(new Path(starts.get(j)));
			
			//for(Edge edge: edges){
			//	System.out.println("edge: " + edge.toStringWithFlow());
			//}
			
			//create paths
			//get paths from one initial node
			//paths starts from an initial node, keep expanding itself, and check if all nodes except initial nodes have been reached
			//put paths into result if the corresponding node has been reached
			//stop while looping when all paths have been removed from paths, which means all nodes have been reached in a graph
			while(paths.size() != 0)
			{
				//from each initial node, go through each possible path
				for(int i = 0;i < paths.size();i++)
				{
					Path path = paths.get(i);
					Node end = path.getEnd();
					Iterator<Edge> outEdges = end.getOutGoingIterator();
				
					int count = 0;
					while(outEdges.hasNext())
					{
						count++;
						//if the edge is the last edge to go through
						if(count == end.sizeOfOutEdges())
							path.extendPath(outEdges.next().getDest());//extend path itself
						else
						{
							Path newPath = (Path)path.clone();//copy path and extend the copy
							newPath.extendPath(outEdges.next().getDest());
							paths.add(i+1, newPath);
							i++;//add one to variable i to avoid keep looping in the for loop
						}//end if-else
					}//end while loop
				}//end for loop
				
				//if(paths.size() > possibleNumber)
				//	return null;
				
					
				//put paths in result when the expected node has been reached
				for(int i = 0;i < paths.size();i++)
				{
					//System.out.println("path " + i + ": " + paths.get(i));
					Path path = paths.get(i);
					Node end = path.getEnd();
					//Node start = path.get(0);
					boolean sign = false;
					/*for(Node node: starts)
						if(node.equals(start)){
							sign = true;
							break;
						}
						else
							sign = false;*/
					
					for(Node node: ends){
						if(node.equals(end)){
							sign = true;
							break;
						}
					}
				//	System.out.println(ends.contains(end) && starts.contains(start));
				//	System.out.println("sign: " + sign);
				//	System.out.println(starts.get(0).equals(start));
					//if a node is just being reached, remove it from nodesCopy
					if(end.sizeOfOutEdges() <= 0 && sign == false)
					{
						paths.remove(i);
						i--;//subtract one from i to keep looping the paths because one path has been removed from paths
					}
					//if a node has been reached, put the path to which the node belongs to result
				//	else if (ends.contains(end) && starts.contains(start))
					else if(sign == true)
					{
				//		System.out.println("path: " + path);
						paths.remove(i);
						i--;//subtract one from i to keep looping the paths because one path has been removed from paths
						result.add(path);
						return path;
					}
				}
			}//end while loop
		}//end for loop with variable j
		return null;
	}
	//a quicksort algorithm
	private void quickSort(List<Path> paths, int p, int len)
	{
		int pivot = -1;
		if(p < len)
		{
			Path x = paths.get(p);
			int i = p-1;
			int j = len;
			while(true)
			{
				j -= 1;
				for(;true;j--)
					if(paths.get(j).size()<= x.size())
						break;
				i += 1;		
				for(;true;i ++ )
					if(paths.get(i).size() >= x.size())
						break;
				
				if(i < j)
				{
					Path temp = paths.get(i);
					paths.set(i, paths.get(j));
					paths.set(j, temp);
				}else
				{	
					pivot = j;
					break;
				}
			}
			
			quickSort(paths,p,pivot);
			quickSort(paths,pivot+1, len);
		}
	}
	
	/**
	 * 
	 * @return true if there is an edge between the two nodes;otherwise, return false
	 */
	public boolean isEdge(Node start, Node end){
		Iterator<Edge> ie = start.getOutGoingIterator();
		Edge e = null;
		while(ie.hasNext()){
			e = ie.next();
			if(e.getDest().equals(end))
				return true;
		}
		return false;	
	}
	
	/**
	 * down cast to DFGraph
	 * 
	 * @return
	 */
	public DFGraph createDFGraph()
	{
		return new DFGraph(nodes, edges, starts, ends);
	}
	

}

