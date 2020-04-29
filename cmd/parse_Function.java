package cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
public class parse_Function{
    public String name;
    public ArrayList<String> nodes;
    public ArrayList<parse_Edge> edges;
    public ArrayList<String> variables;
    public HashMap<String, ArrayList<Integer>> node_defs;
    public HashMap<String, ArrayList<parse_Edge>> edge_defs;
    public HashMap<String, ArrayList<Integer>> node_uses;
    public HashMap<String, ArrayList<parse_Edge>> edge_uses;

    parse_Function(){
        name="None";
        nodes= new ArrayList<String>();
        edges= new ArrayList<parse_Edge>();
        variables = new ArrayList<String>();
        node_defs= new HashMap<String, ArrayList<Integer>>();
        node_uses= new HashMap<String, ArrayList<Integer>>();
        edge_defs= new HashMap<String, ArrayList<parse_Edge>>();
        edge_uses= new HashMap<String, ArrayList<parse_Edge>>();
        return;
    }


    parse_Function(String n){
        this();
        name=n;
    }

    @Override
    public String toString() {
        String ret="";
        for(int i = 0; i<nodes.size(); i++) {
            ret+=i+": "+nodes.get(i)+"\n";
        }
        return ret;
    }
}