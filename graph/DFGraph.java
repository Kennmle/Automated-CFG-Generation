/**
 * 
 */
package graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The graph for data flow. A additional data structure is a list of variables
 * 
 * @author wuzhi
 * Modified by Nan Li
 *
 */
public class DFGraph extends Graph {

	List<Variable> vars;
	static boolean DEBUG=false;
	
	/**
	 * It is for a CFGraph down cast to DFGraph
	 * 
	 * @param nodes
	 * @param edges
	 * @param start
	 * @param ends
	 */
	
	DFGraph(List<Node> nodes, List<Edge> edges, List<Node> starts, List<Node> ends)
	{
		this.nodes = nodes;
		this.edges = edges;
		this.ends = ends;
		this.starts = starts;
		vars = new ArrayList<Variable>();
	}
	
	public DFGraph() {
		super();
		vars = new ArrayList<Variable>();
	}

	/**
	 * It is better not to use this method, instead of createVariable(String name)
	 * @deprecated
	 * @param v
	 */
	@Deprecated
	void addVariable(Variable v)
	{
		vars.add(v);
	}
	
	/**
	 * return an iterator of defs on nodes
	 * @param v
	 */
	public Iterator<Node> getDefsOnNodes(Variable v){
		return v.getDefIterator();
	}
	
	/**
	 * return an iterator of defs on nodes
	 * @param v
	 */
	public Iterator<Node> getUsesOnNodes(Variable v){
		return v.getUseIterator();
	}
	
	/**
	 * 
	 * @param v
	 * @return a list of edges of have definitions
	 */
	public List<Edge> getDefsOnEdges(Variable v){
		return v.getDefsOnEdges();
	}
	
	/**
	 * 
	 * @param v
	 * @return a list of edges of have definitions
	 */
	public List<Edge> getUsesOnEdges(Variable v){
		return v.getUsesOnEdges();
	}
	
	/**
	 * 
	 * @param name
	 * @return the existed variable if the requested variable already exists in the Data Flow Graph 
	 * @return else return a new variable with the requested name.
	 */
	public Variable createVariable(String name)
	{
		//iterate all variables and see if the requested variable has existed 
		for(int i = 0;i < vars.size();i++)
			if(vars.get(i).getName().equals(name))
				return vars.get(i);
		//if the requested variable is not there, create a new variable with the requested name
		Variable v = new Variable(name);
		vars.add(v);
		return v;
	}
	/**
	 * 
	 * @return an iterator of variables
	 */
	public Iterator<Variable> getVariableIterator()
	{
		return vars.iterator();
	}
	
	/**
	 * @return number of variables
	 */
	public int sizeOfVariables()
	{
		return vars.size();
	}
	
	/**
	 * remove all variables in a data flow graph
	 *
	 */
	public void removeVariables()
	{		
		vars = new ArrayList<Variable>();
	}
	
	/**
	 * 
	 * @param v: Variable
	 * @return test paths of All Def Coverage
	 * @throws InvalidGraphException
	 */
	public List<Path> findAllDef(Variable v)
	throws InvalidGraphException
	{		
		if(!vars.contains(v))
			return new ArrayList<Path>();
		
		return generateTests(v.findAllDef());
	}
	
	/**
	 * 
	 * @param v: Variable
	 * @return test paths of All Use Coverage
	 * @throws InvalidGraphException
	 */
	public List<Path> findAllUse(Variable v)
	throws InvalidGraphException
	{
		if(!vars.contains(v))
			return new ArrayList<Path>();
	
		return generateTests(v.findAllUse());		
	}
	
	/**
	 * 
	 * @param v: Variable
	 * @return test paths of All DU Path Coverage
	 * @throws InvalidGraphException
	 */
	public List<Path> findAllDUPath(Variable v)
	throws InvalidGraphException
	{
		if(!vars.contains(v))
			return new ArrayList<Path>();	
		
		return generateTests(v.findDUPath());	
	
	}
	/**
	 * 
	 * @param v
	 * @return DU Pairs in a data flow graph
	 */
	public List<Path> findDuPairs(Variable v) throws InvalidGraphException{
		if(!vars.contains(v))
			return new ArrayList<Path>();

		List<Path> dup = new ArrayList<Path>();
		dup = v.findDuPairs();
		return dup;
	}
	/**
	 * 
	 * @param v
	 * @return DU paths in a data flow graph
	 */
	public List<Path> findDUPaths(Variable v) throws InvalidGraphException{
		if(!vars.contains(v))
			return new ArrayList<Path>();

		List<Path> duPaths = new ArrayList<Path>();
		duPaths = v.findDUPath();

		return duPaths;
	}
	/**
	 * 
	 * @param paths
	 * @return test paths for test requirements in data flow graphs
	 * @throws InvalidGraphException
	 */
	private List<Path> generateTests(List<Path> paths) throws InvalidGraphException
	{
		List<Path> result = new ArrayList<Path>();
		List<Path> tests = findTestPath();
		
		for(int i=0;i<paths.size();i++)
		{
			Path dp=paths.get(i);
			boolean addInitial=true;
			boolean addEnding=true;
			for(int j=0;j<tests.size();j++)
			{
				Path test=tests.get(j);
				if(addInitial)
				{	
					int index=test.indexOf(dp.get(0));
					if(index==0)
						addInitial=false;
					else if(index!=-1)
					{						
						Path sub=test.subPath(0, index);												
						sub.extendPath(dp);
						dp=sub;
						paths.set(i, dp);
						addInitial=false;
					}
				}
				
				if(addEnding)
				{
					int index=test.indexOf(dp.getEnd());
					
					if(index==test.size()-1)
						addEnding=false;
					else if(index!=-1)
					{													
						Path sub=test.subPath(index+1, test.size());										
						dp.extendPath(sub);					
						addEnding=false;
					}
				}				

				if(!addInitial && ! addEnding)
				{
					result.add(dp);
					break;
				}
			}
		}
		
		
		return minimize(result);
	}

	public static void enableDebug() {
		DEBUG=true;
	}
}
