/**
 * 
 */
package graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *  * 
 * 
 * The path is a list of nodes. The nodes can not be removed from a path. 
 * But the path can be extended. The path can generate a subpath from the path.
 * 
 * @author Jeff Offutt, modified by Wuzhi Xu, Date: Dec 12, 2006
 * modified by Nan Li
 * 
 */
public class Path {

   List<Node> path;
   
   public Path(){
   	path = new ArrayList<Node>();
   }
   /**
    * 
    * @param e
    */
   public Path (Edge e)
   {
      path = new ArrayList<Node>();
      path.add (e.getSrc());
      path.add (e.getDest());
   }

   /**
    * 
    * @param n
    */
   public Path (Node n)
   {
      path = new ArrayList<Node>();
      path.add(n);
   }
   
   /**
    * 
    * @param n
    */
   public Path (Path p)
   {
      this.path = new ArrayList<Node>();
      for(Node node:p.path)
      this.path.add(node);
   }
   
   /**
    * In order not to expose the inside 'list' data structure, return an iterator.
    * @return  
    */
   public Iterator<Node> getNodeIterator()
   {
	   return path.iterator();
   }
   
   /**
    * return a sequence of edges represented by the path. If size of the path is less than 2,
    * return an empty list
    * @return
    */
   public List<Edge> getEdgeList()
   {
	   List<Edge> edges = new ArrayList<Edge>();
	   if(size() > 1)
		   for(int i = 0;i < path.size()-1;i++)
			   edges.add(new Edge(path.get(i), path.get(i+1)));
	   
	   return edges;
   }
   
   /**
    * attach the argument path at the end of this path
    * @param p
    */
   public Path immutableExtendedPath(Path p)
   {
	   Iterator<Node> nodes = p.getNodeIterator();
	   Iterator<Node> nodes1 = path.iterator();
	   Path newPath = new Path();
	   
	   while(nodes1.hasNext()){
	   	Node node = nodes1.next();
	   	newPath.path.add(node); 
	   }
	   
	   while(nodes.hasNext())
	   {
		   newPath.path.add(nodes.next());
	   }
	   return newPath;
   }
   
   /**
    * attach the argument path at the end of this path
    * @param p
    */
   public void extendPath(Path p)
   {
	   Iterator<Node> nodes=p.getNodeIterator();
	   while(nodes.hasNext())
	   {
		   path.add(nodes.next());
	   }
   }

   /**
    * add the node at the end of this path
    * @param n
    */
   public void extendPath (Node n)
   {
      path.add(n);
   }

   /**
    *decide if the current path forms a cycle,that is, the first and the last nodes are the same
    *
    *@return true if it is a cycle
    */
   public boolean isCycle ()
   {
      if (path.get(0).equals (path.get(path.size()-1)))
         return true;
      else
         return false;
   }

   /**
    * @return a new path identical to the object
    */
   public Object clone ()
   {
      Path p = new Path(path.get(0));
      for (int i = 1; i < path.size(); i++)
         p.extendPath(path.get(i));
      return (p);
   }

   /**
    *@return number of nodes in the path
    */
   public int size ()
   {
      return (path.size());
   }

   /**
    *@return the node at the specified position
    */
   public Node get (int index)
   {
      return (path.get (index));
   }
   
   /**
    * remove a node from the path based on an index
    *@return void
    */
   public void remove (int index)
   {
      path.remove(index);
 
   }
   
   /**
    * remove a node
    *@return void
    */
   public void remove (Node node)
   {
      path.remove(node);
 
   }

   /**
    *@param Node n
    *@return index of node n at the first appeared position, -1 if not present
    */
   public int indexOf (Node n)
   {
	  for(int i = 0;i < path.size();i++)
		  if(path.get(i).equals(n))
			  return i;
	  
      return -1;
   }
   
   /**
    *@param Node n, index of node n at the last appeared position
    *@return index of node n at the next appeared position, -1 if not present
    */
   public int nextIndexOf (Node n, int index)
   {
   	if(index <= -1)
   		return -1;
   	if(index >= path.size())
   		return -1;
	   for(int i = index + 1;i < path.size();i++)
		   if(path.get(i).equals(n) && i != index)
			   return i;  
	   
      return -1;
   }
   
   /**
    * 
    * @param Node n
    * @return index of node n at the last appeared position, -1 if not present
    */
   public int lastIndexOf(Node n)
   {
	   for(int i = path.size() - 1;i > -1;i--)
		   if(path.get(i).equals(n))
			   return i;
	   
	   return -1;
   }
   
   /**
    * 
    * @return the last node of path
    */
   public Node getEnd()
   {
	   return (path.get(path.size()-1));
   }
   
   /**
    * It is exactly implementation of detour in the textbook.
    * Tour with Detours:
    * Test path p is said to tour subpath q with detours 
    * if and only if every node in q is also in p in the same order
    * p1.detour(p2) means that p1 tours p2 with Detours
    * @param p
    * @return true if p1 tours p2 with detours else return false
    */
   public boolean detour(Path p2)
   {
   	//get the iterator for the path p2
	   Iterator<Node> it=p2.getNodeIterator();	   
	   int pointer=0;
	   //if the path p2 has more node, check if path has the same node in the same order
	   //if the nodes are in the same order detour is assigned with a true value else a false value
	   while(it.hasNext())
	   {
		   boolean detour=false;
		   Node n=it.next();
		   for(;pointer<path.size();pointer++)
			   if(path.get(pointer).equals(n))
			   {
				   detour=true;
				   break;
			   }
				   
		   if(!detour)
			   return false;	
		   //warning!!! notice that do the increment for pointer
		   //pointer increment in the for loop does not work because the break statement
		   pointer++;
	   }
	   
	   return true;
   }
   
   /**
    * It is exactly the implementation of sidetrip in the textbook
    * Tour with Sidetrips:
    * Test path p is said to tour subpath q with sidetrips 
    * if and only if every edge in q is also in p in the same order
    * p1.sidetrip(p2) means that p1 tours p2 with Sidetrips
    * @param p
    * @return true if p1 tours p2 with sidetrips else return false
    */
   public boolean sidetrip(Path p)
   {
	   List<Edge> edges=getEdgeList();
	   List<Edge> e=p.getEdgeList();
	   int pointer=0;
	   //	 if the path p2 has more node, check if path has the same node in the same order
	   //if the nodes are in the same order detour is assigned with a true value else a false value
	   for(int i=0;i<e.size();i++)
	   {
		   boolean sidetrip=false;
		   for(;pointer<edges.size();pointer++)
			   if(edges.get(pointer).equals(e.get(i)))
			   {
				   sidetrip=true;
				   break;
			   }   
		   if(!sidetrip)
			   return false;
		   //warning!!! notice that do the increment for pointer
		   //pointer increment in the for loop does not work because the break statement
		   pointer++;
	   }
	   //
	   return true;
   }
   /**
    * a subpath from the position i to the end(position of a path from 0 to path.size()-1)
    * 
    * @param i
    * @return a subpath from position i to the end
    */
   public Path subPath(int i)
   {
   	//initialize the subpath with a node of position i
   	int counter = i;
   	Node newNode = path.get(counter);	
	   Path subPath = new Path(newNode);
	   //extend the path to the end
	   counter += 1;
	   for(;counter < path.size();counter++)
		   subPath.extendPath(path.get(counter));
	   
	   return subPath;
   }
   
   /**
    * a subpath from the position begin to the position end(position of a path from 0 to path.size()-1)
    * 
    * @param begin 
    * @param end
    * @return a subpath from a specified start position to a specified end position
    */
   public Path subPath(int begin, int end)
   {
   	//initialize the subpath with a node of position i
   	int counter = begin;
   	Node newNode = path.get(counter);
	   Path subPath = new Path(newNode);
	   //extend the path to the end
	   counter += 1;
	   for(;counter < end;counter++)
		   subPath.extendPath(path.get(counter));
	   
	   return subPath;
   }
      
   /**
    * 
    * @param p the short path 
    * @return the index number of the first node if a path p appears in the path, -1 if not present
    */
   public int indexOf(Path p)
   {
     final int NOTFOUND = -1; 
     int  iSub = 0, rtnIndex = NOTFOUND; 
     boolean isPat  = false;      
     int subjectLen = size();
     int patternLen = p.size(); 
  
     while (isPat == false && iSub + patternLen - 1 < subjectLen) 
     { 
       if (get(iSub).equals(p.get(0))) 
       {        
         rtnIndex = iSub; // Starting at zero 
         isPat = true;         
         for (int iPat = 1; iPat < patternLen; iPat ++) 
         {         
           if (!get(iSub + iPat).equals(p.get(iPat))) 
           { 
             rtnIndex = NOTFOUND; 
             isPat = false;      
             break;  // out of for loop 
           }
         } 
       }    
       iSub ++; 
     } 
     
     return (rtnIndex); 
   }
   
   /**
    * @deprecated replaced by indexOf(Path p)
    * @param s the path if contain this path
    * @return
    */
	@Deprecated
   public boolean isSubpath (Path s) 
   {    
     final int NOTFOUND = -1; 
     int  iSub = 0, rtnIndex = NOTFOUND; 
     boolean isPat  = false;      
     int subjectLen = s.size();
     int patternLen = size(); 
  
     while (isPat == false && iSub + patternLen - 1 < subjectLen) 
     { 
       if (s.get(iSub).equals(get(0))) 
       {        
         rtnIndex = iSub; // Starting at zero 
         isPat = true;         
         for (int iPat = 1; iPat < patternLen; iPat ++) 
         {         
           if (!s.get(iSub + iPat).equals(get(iPat))) 
           { 
             rtnIndex = NOTFOUND; 
             isPat = false;      
             break;  // out of for loop 
           }
         } 
       }    
       iSub ++; 
     } 
     
     if(rtnIndex==-1)
    	 return false;
     else
    	 return true; 
   } 
   
   /**
    * If path i and path j has any overlap k,
    * @return the index number of the first node of the overlap in the path i
    */
   public int hasOverlapWith(Path p){	
   	
   	return 0;   	
   }
   
   /**
    * @return the path with []
    * an example: [1, 2, 3] is represented for the path from node 1 to node 2 to node 3
    */
   public String toString()
   {
   	if(path.size() == 0)
   		return "[]";
	   String result = "[" + path.get(0);
	   for(int i = 1;i < path.size() - 1;i++)
		   result += "," + path.get(i).toString();
	   if(path.size() == 1)
	   	return result + "]";
	   else
	   	return result + "," + path.get(path.size() - 1) + "]";		   
   }
   
   /**
    * return true if two paths are the same, else return false
    * @param anotherPath
    * @return
    */
   public boolean equals(Path anotherPath){
   	
   	//get a local variable of another path
   	Path temp = anotherPath;
   	
   	//return false if the lengths of two paths are not the same
   	if(path.size() != temp.size() )
   		return false;
   	
   	//return false if one node of one path is not the same as the corresponding one of another path
   	for(int i = 0; i < path.size();i++){
   		if(!path.get(i).equals(temp.get(i)))
   			return false;
   	}
   	return true;
   }
}
