/**
 * 
 */
package graph;

/**
 * @author Jeff Offutt, 
 *
 * Modified by Wuzhi, Date: Dec 12, 2006
 * 
 * Modified by Nan Li, Date: Nov 30, 2010
 */
public class Edge{
   Node src;  // source node
   Node dest; // destination node 
   Object weight; // weight of the edge could be a path such as [1, 2, 3]; could be used in prefix graph
   int capacity; // the capacity of the edge; only used for flow problems
   int flow; // the flow of the edge; only used for flow problems

   /**
    * if want to create edge outside of this package, please call GraphBase.createEdge
    * the constructor
    * @param s source node
    * @param d destination node
    */
   Edge(Node s, Node d)
   {
      src  = s;
      dest = d;
      weight = null;
      capacity = 0;
      flow = 0;
      src.addOutGoing(this); //add edge into outgoing edge list of source node
   }
   
   /**
    * the constructor
    * @param s
    * @param d
    * @param weight
    */
   Edge(Node s, Node d, Object weight)
   {
      src  = s;
      dest = d;
      this.weight = weight;
      capacity = 0;
      flow = 0;
      src.addOutGoing(this); //add edge into outgoing edge list of source node
   }
   
   /**
    * the constructor
    * @param s
    * @param d
    * @param capacity
    * @param flow
    */
   Edge(Node s, Node d, Object weight, int capacity, int flow)
   {
      src  = s;
      dest = d;
      this.weight = weight;
      this.capacity = capacity;
      this.flow = flow;
      src.addOutGoing(this); //add edge into outgoing edge list of source node
   }
   /**
    * 
    * @return the source node
    */
   public Node getSrc()
   {
      return src;
   }
   /**
    * 
    * @return the destination node
    */
   public Node getDest()
   {
      return dest;
   }
   /**
    * 
    * @return the weight of the edge
    */
   public Object getWeight()
   {
      return weight;
   }
   
   /**
    * 
    * @return the capacity of the edge
    */
   public int getCapacity()
   {
      return capacity;
   }
   
   /**
    * 
    * @return the flow of the edge
    */
   public int getFlow()
   {
      return flow;
   }
   
   /**
    * 
    * set the capacity property
    */
   public void setCapacity(int capacity)
   {
      this.capacity = capacity;
   }
   
   /**
    * 
    * set the flow property
    */
   public void setFlow(int flow)
   {
      this.flow = flow;
   }

   /**
    * override the object equals method, implementing the node equals. If two nodes of edges equals
    * return true, otherwise false;
    * 
    * @param e
    * @return true if two nodes of edges equals
    */
   public boolean equals(Edge e)
   {
	   if(src.equals(e.getSrc()) && dest.equals(e.getDest()))
		   return true;
	   else
		   return false;
   }
   
   /**
    * print as an ordered pair. This is only for printable objects, for example, string and integer 
    */
   public String toString ()
   {
      return ("(" + src.getObject() + ", " + dest.getObject() + ")");
   }
   
   /**
    * print as an ordered pair with flow and capacity
    */
   public String toStringWithFlow ()
   {
      return ("(" + src.getObject() + ", " + dest.getObject() + ")" + "; flow: " + getFlow() + "; capacity: " + getCapacity());
   }
}
