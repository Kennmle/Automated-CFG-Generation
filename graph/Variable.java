/**
 * 
 */
package graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A variable that have some def and use node
 * 
 * @author wuzhi, spring 2007
 * Modified by Nan Li
 *
 */
public class Variable {

	List<Node> defs;
	List<Node> uses;
	List<Path> duPaths;
	List<Path> duPairs;
	List<Edge> defsOnEdges;
	List<Edge> usesOnEdges;
	String name;
	static boolean DEBUG=false;
	
	/**
	 * to create a variable outside of package, please use DFGraph.createVariable
	 * @param name
	 */
	Variable(String name)
	{
		this.name = name;
		defs = new ArrayList<Node>();
		uses = new ArrayList<Node>();
		defsOnEdges = new ArrayList<Edge>();
		usesOnEdges = new ArrayList<Edge>();
	}
	
	public String getName(){ return name;}
	
	public Iterator<Node> getDefIterator()
	{
		return defs.iterator();
	}
	
	public Iterator<Node> getUseIterator()
	{
		return uses.iterator();
	}
	
	public int sizeOfDefs()
	{
		return defs.size();
	}
	
	public int sizeOfUses()
	{
		return uses.size();
	}
	
	//return true if there is a definition on the node, otherwise return false
	public boolean isDef(Node n)
	{
		return defs.contains(n);
	}
	
	//return true if there is a use on the node, otherwise return false
	public boolean isUse(Node n)
	{
		return uses.contains(n);
	}
	
	//return true if there is a definition on the edge, otherwise return false
	public boolean isDefOnEdges(Edge e)
	{
		//get the edges having defs on
		//return true if one of the edges is equal to the specific edge
		Iterator<Edge> edge = defsOnEdges.iterator();
		while(edge.hasNext()){
			if(edge.next().equals(e))
				return true;
		}
		//by default, return false
		return false;
	}
	
	//return true if there is a use on the edge, otherwise return false
	public boolean isUseOnEdges(Edge e)
	{
		//get the edges having uses on
		//return true if one of the edges is equal to the specific edge
		Iterator<Edge> edge = usesOnEdges.iterator();
		while(edge.hasNext()){
			if(edge.next().equals(e))
				return true;
		}
		//by default, return false
		return false;
	}
	
	//add a node to defs of List<Node>
	public void addDef(Node n)
	{
		if(!isDef(n))
			defs.add(n);
	}
	
	//add a node to uses of List<Node>
	public void addUse(Node n)
	{
		if(!isUse(n))
			uses.add(n);
	}
	
	//add an edge to defs of List<Edge>
	public void addDefOnEdges(Edge e)
	{
		if(!isDefOnEdges(e))
			defsOnEdges.add(e);
	}
	
	//add an edge to uses of List<Edge>
	public void addUseOnEdges(Edge e)
	{
		if(!isUseOnEdges(e))
			usesOnEdges.add(e);
	}
	
	//return the defs on edges
	public List<Edge> getDefsOnEdges()
	{
		return defsOnEdges;
	}
	
	//return the uses on edges
	public List<Edge> getUsesOnEdges()
	{
		return usesOnEdges;
	} 
	
	/**
	 * 
	 * @return a list of paths of all uses
	 */
	public List<Path> findAllUse()
	{
		if(duPaths == null)
			duPaths = findDUPath();
		
		List<Path> result = new ArrayList<Path>();
		//from defs on nodes to uses on nodes
		for(int k = 0;k < defs.size();k++)
			for(int i = 0;i < uses.size();i++)
			{
				Node use = uses.get(i);
				for(int j = 0;j < duPaths.size();j++)
				{
					Path dp = duPaths.get(j);
					if(dp.get(0).equals(defs.get(k)) && dp.getEnd().equals(use))
					{
						boolean signForDuplicate = false;
						if(result.size() > 0){
							for(int z = 0; z < result.size();z++){
								if(result.get(z).equals(dp))
									signForDuplicate = true;
							}
						}
						if(signForDuplicate == false)
							result.add(dp);						
						break;
					}
				}
			}
		//from defs on nodes to uses on edges
		for(int k = 0;k < defs.size();k++)
			for(int i = 0;i < usesOnEdges.size();i++)
			{
				Edge use = usesOnEdges.get(i);
				for(int j = 0;j < duPaths.size();j++)
				{
					Path dp = duPaths.get(j);
					if(dp.get(0).equals(defs.get(k)) && dp.getEdgeList().get(dp.getEdgeList().size() - 1).equals(use))
					{
						boolean signForDuplicate = false;
						if(result.size() > 0){
							for(int z = 0; z < result.size();z++){
								if(result.get(z).equals(dp))
									signForDuplicate = true;
							}
						}
						if(signForDuplicate == false)
							result.add(dp);						
						break;
					}
				}
			}
		//from defs on edges to uses on edges
		for(int k = 0;k < defsOnEdges.size();k++)
			for(int i = 0;i < usesOnEdges.size();i++)
			{
				Edge use = usesOnEdges.get(i);
				for(int j = 0;j < duPaths.size();j++)
				{
					Path dp = duPaths.get(j);
					if(dp.getEdgeList().get(0).equals(defsOnEdges.get(k)) && dp.getEdgeList().get(dp.getEdgeList().size() - 1).equals(use))
					{
						boolean signForDuplicate = false;
						if(result.size() > 0){
							for(int z = 0; z < result.size();z++){
								if(result.get(z).equals(dp))
									signForDuplicate = true;
							}
						}
						if(signForDuplicate == false)
							result.add(dp);						
						break;
					}
				}
			}
		//from defs on edges to uses on nodes
		for(int k = 0;k < defsOnEdges.size();k++)
			for(int i = 0;i < uses.size();i++)
			{
				Node use = uses.get(i);
				for(int j = 0;j < duPaths.size();j++)
				{
					Path dp = duPaths.get(j);
					if(dp.getEdgeList().get(0).equals(defsOnEdges.get(k)) && dp.getEnd().equals(use))
					{
						boolean signForDuplicate = false;
						if(result.size() > 0){
							for(int z = 0; z < result.size();z++){
								if(result.get(z).equals(dp))
									signForDuplicate = true;
							}
						}
						if(signForDuplicate == false)
							result.add(dp);						
						break;
					}
				}
			}
		return result;
	}
	
	public List<Path> findAllDef()
	{
		if(duPaths == null)
			duPaths = findDUPath();
		
		List<Path> result = new ArrayList<Path>();
		//from defs on nodes
		for(int i = 0;i < defs.size();i++)
		{
			Node def = defs.get(i);
			for(int j = 0;j < duPaths.size();j++)
			{
				Path dp = duPaths.get(j);
				if(dp.get(0).equals(def))
				{
					boolean signForDuplicate = false;
					if(result.size() > 0){
						for(int z = 0; z < result.size();z++){
							if(result.get(z).equals(dp))
								signForDuplicate = true;
						}
					}
					if(signForDuplicate == false)
						result.add(dp);						
					break;
				}
			}				
		}
		//from defs on edges
		for(int i = 0;i < defsOnEdges.size();i++)
		{
			Edge def = defsOnEdges.get(i);
			for(int j = 0;j < duPaths.size();j++)
			{
				Path dp = duPaths.get(j);
				if(dp.getEdgeList().get(0).equals(def))
				{
					boolean signForDuplicate = false;
					if(result.size() > 0){
						for(int z = 0; z < result.size();z++){
							if(result.get(z).equals(dp))
								signForDuplicate = true;
						}
					}
					if(signForDuplicate == false)
						result.add(dp);						
					break;
				}
			}				
		}
		return result;
	}
	
	public int sizeOfDuPath()
	{
		if(duPaths==null)
			duPaths=findDuPath();
		
		return duPaths.size();
	}
	/*
	 * Effects: return an iterator of Du Paths
	 */
	public Iterator<Path> getDuPath()
	{
		if(duPaths == null)
			duPaths = findDuPath();
		
		return duPaths.iterator();
	}
	/*
	 * Effects: return an iterator of Du Pairs
	 */
	public Iterator<Path> getDuPairs(){
		if(duPairs == null)
			duPairs = findDuPairs();
		return duPairs.iterator();
	}
	
	/**
	 * 
	 * @return Du Pairs for each variable
	 */
	public List<Path> findDuPairs(){
		List<Path> defPath = new ArrayList<Path>();
		List<Path> DuPairs = new ArrayList<Path>();
		//a loop for each def
		for(int i = 0; i < defs.size(); i++){
			Node def = defs.get(i);
			//add each use to the def
			for(int j = 0;j < uses.size(); j++){
				//put defs and uses into a node list first
				List<Node> nodeList = new ArrayList<Node>();
				nodeList.add(def);
				nodeList.add(uses.get(j));
				//put defs and uses from a node list to a path list
				Path temp = null;
				Iterator<Node> it = nodeList.iterator();
				//put the def first to the path, if the def already exist, put the use in
				while(it.hasNext()){
					if(temp == null)
						temp = new Path(it.next());
					else
						temp.extendPath(it.next());
				}
				//add each du-pair to the path list
				DuPairs.add(temp);		
			}//end for loop of j
		}//end for loop of i	
		return DuPairs;
	}
	/**
	 * @deprecated
	 * @return a list of DU paths
	 */
	@Deprecated
	public List<Path> findDuPath()
	{	
		//variables
		List<Path> paths = new ArrayList<Path>();			
		List<Path> result = new ArrayList<Path>();
		for(int j = 0;j < defs.size();j++)
		{			
			
			Node def = defs.get(j);			
			paths.add(new Path(def));			
			
			while(paths.size() != 0)
			{				
				for(int i = 0;i < paths.size();i++)
				{
					
					Path path = paths.get(i);		
					Node end = path.getEnd();
					if(end.sizeOfOutEdges() == 0)
					{
						paths.remove(i);
						i--;
						continue;
					}
					
					Iterator<Edge> outEdges = end.getOutGoingIterator();
					int count = 0;
					while(outEdges.hasNext())
					{
						count++;
						if(count == end.sizeOfOutEdges())
							path.extendPath(outEdges.next().getDest());//extend path itself
						else
						{
							Path newPath = (Path)path.clone();//copy path and extend the copy
							newPath.extendPath(outEdges.next().getDest());
							paths.add(i+1, newPath);
							i++;
						}
					}
				}
				
				//check if the last node of paths are already included, def, use
				for(int i = 0;i < paths.size();i++)
				{
					Path path = paths.get(i);
					Node end = path.getEnd();
					if(isUse(end))
					{
						//assumption: in one node, def always is before a use
						//otherwise: need consider a cycle that has use before def in a node.
					//	if(path.indexOf(end) != path.size()-1)
					//	{							
							paths.remove(i);
							i--;
						//}else
					//	{
							//Path duPath=(Path)path.clone();
							Path duPath = new Path(path.get(0));
							if(path.size() > 1)
								for(int z = 1; z < path.size();z++)
									duPath.extendPath(path.get(z));
								
							result.add(duPath);
				//	}
					}else if(isDef(end))
					{
						paths.remove(i);
						i--;
					}else if(path.indexOf(end) != path.size()-1)
					{
						paths.remove(i);
						i--;
					}
				}
			}
		}
		
		for(int i = 0;i < result.size();i++){
			System.out.println(i + ": " + result.get(i));
		}
		return result;
	}

	/**
	 * 
	 * @return a list of DU Paths
	 */
	public List<Path> findDUPath()
	{	
		//variables
		List<Path> paths = new ArrayList<Path>();			
		List<Path> result = new ArrayList<Path>();
		
	/*	for(int j = 0;j < defs.size();j++){
			System.out.println("defs: " + defs.get(j));
		}
		for(int j = 0;j < uses.size();j++){
			System.out.println("uses: " + uses.get(j));
		}
		for(int j = 0;j < defsOnEdges.size();j++){
			System.out.println("defs: " + defsOnEdges.get(j));
		}
		for(int j = 0;j < usesOnEdges.size();j++){
			System.out.println("uses: " + usesOnEdges.get(j));
		}*/
		
		//generate DU Paths from defs on nodes to uses on nodes and edges
		for(int j = 0;j < defs.size();j++)
		{			
			
			Node def = defs.get(j);			
			paths.add(new Path(def));			
			//System.out.println("def: " + def);
			for(int z = 0;z < paths.size();z++){
				//System.out.println("path: " + paths.get(z));
			}
			
			while(paths.size() != 0)
			{				
				for(int i = 0;i < paths.size();i++)
				{					
					Path path = paths.get(i);		
					Node end = path.getEnd();
					//if the node is final, remove the path
					if(end.sizeOfOutEdges() == 0)
					{
						paths.remove(i);
						i--;
						continue;
					}
					//get rid of the cases that loops in the graph like 3,4,3,4
					//only simple path is allowed, so 3,4,3 is OK, 3,4,3,4 is bad
					if(path.indexOf(path.getEnd()) != (path.size() - 1)){
						paths.remove(i);
						i--;
						continue;
					}
											
					Iterator<Edge> outEdges = end.getOutGoingIterator();
					int count = 0;
					//add all outgoing edges of the final node of the path to the current path
					while(outEdges.hasNext())
					{
						Edge edge = outEdges.next();
						count++;
						if(count == end.sizeOfOutEdges())
							path.extendPath(edge.getDest());//extend path itself
						else
						{
							Path newPath = (Path)path.clone();//copy path and extend the copy
							newPath.extendPath(edge.getDest());
							//make sure the no duplicate Path is allowed to be added in
							boolean signForDuplicate = false;
							if(paths.size() > 0){
								for(int z = 0; z < paths.size();z++){
									if(paths.get(z).equals(newPath))
										signForDuplicate = true;
								}
							}
							if(signForDuplicate == false){
								paths.add(i+1, newPath);
								i++;
							}
						}
					
					}//end while loop
				}//end for loop with i
				
				//check if the last node of paths are already included, def, use
				for(int i = 0;i < paths.size();i++)
				{
					Path path = paths.get(i);
					Node end = path.getEnd();

					// first, check if the path is a def-clear path, which is, no duplicate definition
					boolean signForDefClear = true;
					for(int k = 1; k < path.size() - 1;k++){
					//	System.out.println("node " + "k: " + k + " " + path.get(k) + isDef(path.get(k)));
						if(isDef(path.get(k)))
							signForDefClear = false;
					}
					
					List<Edge> edgeList = path.getEdgeList();
					for(int k = 1; k < edgeList.size();k++){
						if(isDefOnEdges(edgeList.get(k)))
								signForDefClear = false;
					}
					
					//second, check if the path is simple
					boolean signForSimple = true;
					for(int k = 0;k < path.size();k++){
						if(path.indexOf(path.get(k)) != k && !path.get(0).equals(path.getEnd()) )
							signForSimple = false;
					}
				
					if(isUse(end) || isUseOnEdges(path.getEdgeList().get(path.getEdgeList().size() - 1)))
					{
						//assumption: in one node, def always is before a use
						//otherwise: need consider a cycle that has use before def in a node.
				
						//if the path is not def-clear, remove it
						if(signForDefClear == false)
						{
							paths.remove(i);
							i--;
						}
						//if the path is not simple, remove it
						else if(signForSimple == false)
						{
							paths.remove(i);
							i--;
						}
						else
						{
							//Path duPath=(Path)path.clone();
							Path duPath = new Path(path.get(0));
							if(path.size() > 1)
								for(int z = 1; z < path.size();z++)
									duPath.extendPath(path.get(z));
							//make sure the no duplicate DU Path is allowed to be added in
							boolean signForDuplicate = false;
							if(result.size() > 0){
								for(int z = 0; z < result.size();z++){
									if(result.get(z).equals(duPath))
										signForDuplicate = true;
								}
							}
							if(signForDuplicate == false)
								result.add(duPath);
						}
					}//end if-else if branch					
				}//end for loop	
			}//end while loop
		}//end for loop
		
		//generate DU Paths from defs on edges to uses on nodes and edges
		for(int j = 0;j < defsOnEdges.size();j++)
		{			
			
			Edge def = defsOnEdges.get(j);			
			paths.add(new Path(def));			

			while(paths.size() != 0)
			{				
				for(int i = 0;i < paths.size();i++)
				{					
					Path path = paths.get(i);	
					Node end = path.getEnd();
					System.out.println("path: " + path);
					//if the node is final, remove the path
					if(end.sizeOfOutEdges() == 0)
					{
						paths.remove(i);
						i--;
						continue;
					}
					//get rid of the cases that loops in the graph like 3,4,3,4
					//only simple path is allowed, so 3,4,3 is OK, 3,4,3,4 is bad
					if(path.indexOf(path.getEnd()) != (path.size() - 1)){
						paths.remove(i);
						i--;
						continue;
					}
											
					Iterator<Edge> outEdges = end.getOutGoingIterator();
					int count = 0;
					//add all outgoing edges of the final node of the path to the current path
					while(outEdges.hasNext())
					{
						Edge edge = outEdges.next();
						count++;
						if(count == end.sizeOfOutEdges())
							path.extendPath(edge.getDest());//extend path itself
						else
						{
							Path newPath = (Path)path.clone();//copy path and extend the copy
							newPath.extendPath(edge.getDest());
							//make sure the no duplicate Path is allowed to be added in
							boolean signForDuplicate = false;
							if(paths.size() > 0){
								for(int z = 0; z < paths.size();z++){
									if(paths.get(z).equals(newPath))
										signForDuplicate = true;
								}
							}
							if(signForDuplicate == false){
								paths.add(i+1, newPath);
								i++;
							}
						}
					
					}//end while loop
				}//end for loop with i
				
				//check if the last node of paths are already included, def, use
			for(int i = 0;i < paths.size();i++)
				{
					System.out.println("pathsSizehead: " + paths.size());
					Path path = paths.get(i);
					System.out.println("pathuse: " + path);
					Node end = path.getEnd();
					//Node endBefore = path.get(path.size() - 2);

					// first, check if the path is a def-clear path, which is, no duplicate definition
					boolean signForDefClear = true;
					for(int k = 1; k < path.size() - 1;k++){
					//	System.out.println("node " + "k: " + k + " " + path.get(k) + isDef(path.get(k)));
						if(isDef(path.get(k)))
							signForDefClear = false;
					}
					List<Edge> edgeList = path.getEdgeList();
					for(int k = 1; k < edgeList.size();k++){
						if(isDefOnEdges(edgeList.get(k)))
								signForDefClear = false;
					}

					//second, check if the path is simple
					boolean signForSimple = true;
					for(int k = 0;k < path.size();k++){
						if(path.indexOf(path.get(k)) != k && !path.get(0).equals(path.getEnd()) )
							signForSimple = false;
					}
					//get rid of all paths that do not have uses on end nodes but have defs on nodes in the middle
					if(!isUse(end) && (isDef(path.getEnd()) || signForDefClear == false) ){
						paths.remove(i);
						i--;
					}
					System.out.println("signForDefClear:" + signForDefClear);	
					System.out.println("signForSimple:" + signForSimple);
					//	System.out.println("path: " + path);
					if(isUse(end) || isUseOnEdges(path.getEdgeList().get(path.getEdgeList().size() - 1)))
					{
						//assumption: in one node, def always is before a use
						//otherwise: need consider a cycle that has use before def in a node.

						//if the path is not def-clear, remove it
						if(signForDefClear == false)
						{
							paths.remove(i);
							i--;
						}
						//if the path is not simple, remove it
						else if(signForSimple == false)
						{
							paths.remove(i);
							i--;
						}
						else
						{
							//Path duPath=(Path)path.clone();
							Path duPath = new Path(path.get(0));
							if(path.size() > 1)
								for(int z = 1; z < path.size();z++)
									duPath.extendPath(path.get(z));
								
							//make sure the no duplicate DU Path is allowed to be added in
							boolean signForDuplicate = false;
							if(result.size() > 0){
								for(int z = 0; z < result.size();z++){
									if(result.get(z).equals(duPath))
										signForDuplicate = true;
								}
							}
							if(signForDuplicate == false)
							{
								result.add(duPath);
								paths.remove(i);
								i--;
							}
							System.out.println("pathsSizeend: " + paths.size());
							System.out.println("resultsize: " + result.size());
						}
					}//end if-else if branch
					
				}//end for loop
				
			}//end while loop
		}//end for loop
		

		if(DEBUG) {
			for(int i = 0;i < result.size();i++){
				System.out.println(i + ": " + result.get(i));
			}
		}
		return result;
	}

	public static void enableDebug() {
		DEBUG=true;
	}
}
