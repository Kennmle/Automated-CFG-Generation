/**
 * 
 */
package graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TODO: The base for all kinds of graph. It is for extension in the future.
 * 
 * @author Wuzhi Xu
 * 
 * Date: Jan 5, 2007
 * 
 */
public abstract class GraphBase {

	List<Edge> edges;	
	List<Node> nodes;
	//constructor
	public GraphBase()
	{
		edges = new ArrayList<Edge>();
		nodes = new ArrayList<Node>();
	}
	
	public Iterator<Node> getNodeIterator()
	{
		return nodes.iterator();
	}
	
	/**
	 * 
	 * @return the number of nodes
	 */
	public int sizeOfNodes()
	{
		return nodes.size();
	}
	
	/**
	 * 
	 * @return the number of edges
	 */
	public int sizeOfEdges()
	{
		return edges.size();
	}

	/**
	 * return the node if it is in the graph
	 * @param obj
	 * @return null if this node does not exist
	 */
	public Node findNode(Object obj)
	{
		for(int i = 0;i < nodes.size();i++)
			if(nodes.get(i).getObject().equals(obj))
				return nodes.get(i);
		
		return null;
	}
	
	/**
	 * return the edge if it is in the graph
	 * @param obj
	 * @return null if this edge does not exist
	 */
	public Edge findEdge(Object objSrc, Object objDest)
	{
		for(int i = 0;i < edges.size();i++)
			if(edges.get(i).getSrc().getObject().equals(objSrc) && edges.get(i).getDest().getObject().equals(objDest))
				return edges.get(i);
		
		return null;
	}
	
	
	public Iterator<Edge> getEdgeIterator()
	{
		return edges.iterator();
	}
	
	/**
	 * 
	 * @param obj
	 * @return existed node if it existed in graph, otherwise return a new node and add it into graph
	 */
	public Node createNode(Object obj)
	{
		//check whether the node has existed in the graph
		for(int i = 0;i < nodes.size();i++)	{	
			//System.out.println(nodes.get(i).getObject().toString() + nodes.get(i).getObject().getClass());
		//	System.out.println("obj" + obj.toString() + obj.getClass());
			//System.out.println(nodes.get(i).getObject().equals(obj));
			if(nodes.get(i).getObject().equals(obj))	
				return nodes.get(i);
		}
		//return a new node
		Node node = new Node(obj);
		nodes.add(node);
		return node;
	}
	
	/**
	 * 
	 * @param s
	 * @param d
	 * @return existed edge if it exists in graph, otherwise create a new edge and add it into graph
	 */
	public Edge createEdge(Node s, Node d)
	{
		//check whether the edge has existed in the graph
		Iterator<Edge> outEdges = null;
		Node src = null;
		for(Node node: nodes){
			if(node.equals(s)){
				src = node;
				outEdges = node.getOutGoingIterator();
				break;
			}
		}
		
		if(outEdges != null){
			while(outEdges.hasNext())
			{
				Edge e = outEdges.next();
				if(d.equals(e.getDest()))
					return e;
			}
		}
		
		//return a new edge
		Edge e = null;
		if(src != null)
			e = new Edge(src, d);
		else
			e = new Edge(s, d);
		edges.add(e);
		
		return e;
	}
	
	/**
	 * 
	 * @param s
	 * @param d
	 * @return existed edge if it exists in graph, otherwise create a new edge and add it into graph
	 */
	public Edge createEdge(Node s, Node d, Object weight)
	{
		Iterator<Edge> outEdges = null;
		Node src = null;
		//looking for a existing node which is the same as the node s
		for(Node node: nodes){		
			if(node.equals(s)){			
				src = node;
				//return a list of edges whose source node is the same as the node s
				outEdges = node.getOutGoingIterator();		
				break;
			}
		}
		//go through each edge and check whether the node e is equal to one of the destination nodes
		if(outEdges != null){
			while(outEdges.hasNext())
			{
				Edge e = outEdges.next();
				if(d.equals(e.getDest()))
					return e;
			}
		}

		//return a new edge if no existing edge is found
		Edge e = null;
		if(src != null)
			e = new Edge(src, d, weight);
		else
			e = new Edge(s, d, weight);
		edges.add(e);
		return e;
	}
	
	/**
	 * 
	 * @param s
	 * @param d
	 * @param flow
	 * @param capacity
	 * @return the existing edge if it has existed in the graph, otherwise a new edge is created and added into the graph
	 */
	public Edge createEdge(Node s, Node d, Object weight, int capacity, int flow)
	{
		Iterator<Edge> outEdges = null;
		Node src = null;
		//looking for a existing node which is the same as the node s
		for(Node node: nodes){		
			if(node.equals(s)){			
				src = node;
				//return a list of edges whose source node is the same as the node s
				outEdges = node.getOutGoingIterator();		
				break;
			}
		}
		//go through each edge and check whether the node e is equal to one of the destination nodes
		if(outEdges != null){
			while(outEdges.hasNext())
			{
				Edge e = outEdges.next();
				if(d.equals(e.getDest()))
					return e;
			}
		}

		//return a new edge if no existing edge is found
		Edge e = null;
		if(src != null)
			e = new Edge(src, d, weight, capacity, flow);
		else
			e = new Edge(s, d, weight, capacity, flow);
		edges.add(e);
		return e;
	}
	
	/**
	 * 
	 * @param Edge edge
	 * @return the existing edge if it has existed in the graph, otherwise a new edge is created and added into the graph
	 */
	public Edge createEdge(Edge edge)
	{
		Iterator<Edge> outEdges = null;
		Node src = null;
		//looking for a existing node which is the same as the node s
		for(Node node: nodes){		
			if(node.equals(edge.src)){			
				src = node;
				//return a list of edges whose source node is the same as the node s
				outEdges = node.getOutGoingIterator();		
				break;
			}
		}
		//go through each edge and check whether the node e is equal to one of the destination nodes
		if(outEdges != null){
			while(outEdges.hasNext())
			{
				Edge e = outEdges.next();
				if(edge.dest.equals(e.getDest()))
					return e;
			}
		}

		//return a new edge if no existing edge is found
		Edge e = null;
		if(src != null)
			e = new Edge(src, edge.dest, edge.getWeight(), edge.getCapacity(), edge.getFlow());
		else
			e = new Edge(edge.src, edge.dest, edge.getWeight(), edge.getCapacity(), edge.getFlow());
		edges.add(e);
		return e;
	}
	
	/**
	 * @deprecated replaced by createEdge(Node s, Node d)
	 * @param e
	 */
	@Deprecated
	public void addEdge(Edge e)
	{
		edges.add(e);
	}

	abstract public void validate()
	throws InvalidGraphException;

	public static List<Path> minimize(List<Path> paths)
	{
		List<Path> result=new ArrayList<Path>();
		for(int i=0;i<paths.size();i++)
			result.add(paths.get(i));
		
		for(int i=0;i<result.size();i++)
			for(int j=i+1;j<result.size();j++)
			{
				if(result.get(i).indexOf(result.get(j))!=-1)
				{
					result.remove(j);
					break;
				}
				
				if(result.get(j).indexOf(result.get(i))!=-1)
				{
					result.remove(i);
					i--;
					break;
				}					
			}
		
		return result;
	}
}
