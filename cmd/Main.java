package cmd;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import graph.*;

public class Main{
	
    public static void main(String[] args)
	throws IOException, FileNotFoundException
	{
        boolean DEBUG=false;

        if(args.length !=2) {
            // No file or coverage type specified
            System.out.printf("Usage: java <executable> <coverage-type> <json file-name>\n",args[0]);
            throw new RuntimeException("Bad Arguments");
        }
        // Now load graphs
        Object file_obj = JsonParser.parseReader(new FileReader(args[1]));
        JsonObject file_jo = (JsonObject)file_obj;
        JsonArray function_arr = file_jo.getAsJsonArray("functions");
        ArrayList<parse_Function> functions = new ArrayList<parse_Function>();
        ArrayList<DFGraph> function_graphs = new ArrayList<DFGraph>();

        for(int i = 0; i<function_arr.size(); i++) {
            JsonObject function_jo = (JsonObject)function_arr.get(i);
            String function_name=function_jo.getAsJsonPrimitive("label").getAsString();
            parse_Function curr_function = new parse_Function(function_name);
            JsonArray node_arr=function_jo.getAsJsonArray("nodes");
            JsonArray edge_arr=function_jo.getAsJsonArray("edges");
            //for each node
            for(int j = 0; j<node_arr.size(); j++) {
                JsonPrimitive node_jo =(JsonPrimitive)node_arr.get(j);
                String node_str = node_jo.getAsString();
                //System.out.println(node_str);
                curr_function.nodes.add(node_str);
            }
            //for each set of edges coming fromo each node
            for(int j = 0; j<edge_arr.size(); j++) {
                JsonArray edge_j_arr =(JsonArray)edge_arr.get(j);
                for(int k = 0; k<edge_j_arr.size(); k++) {

                    JsonObject edge_jo =(JsonObject)edge_j_arr.get(k);
                    int edge_to = edge_jo.getAsJsonPrimitive("to_node").getAsInt();
                    String edge_text = edge_jo.getAsJsonPrimitive("condition").getAsString();
                    parse_Edge edge_obj = new parse_Edge(j,edge_to, edge_text);
                    curr_function.edges.add(edge_obj);
                }
            }
            
            /**
             * Now the Def-Use graph stuff
             * 
             */
            JsonObject defs_jo, uses_jo;
            Set<String> defs_targets, uses_targets;
            defs_jo=function_jo.getAsJsonObject("definitions");
            defs_targets=defs_jo.keySet();
            uses_jo=function_jo.getAsJsonObject("uses");
            uses_targets=uses_jo.keySet();
            for(String key: defs_targets) {
                JsonArray var_list =(JsonArray)defs_jo.get(key);
                for(int k=0; k<var_list.size(); k++) {
                    String curr_var = var_list.get(k).getAsString();
                    if(!curr_function.variables.contains(curr_var)) {
                        curr_function.variables.add(curr_var);
                    }
                    // If the element is a node:
                    if(!key.contains(",")) {
                        if(!curr_function.node_defs.containsKey(curr_var)) {
                            ArrayList<Integer> node_list = new ArrayList<Integer>();
                            curr_function.node_defs.put(curr_var,node_list);
                        }
                        curr_function.node_defs.get(curr_var).add(Integer.parseInt(key));
                    } else {
                        //Else if an edge
                        String[] parts = key.split(",");
                        Integer source = Integer.parseInt(parts[0]);
                        Integer target = Integer.parseInt(parts[1]);
                        parse_Edge curr_edge = new parse_Edge(source, target, "");

                        if(!curr_function.edge_defs.containsKey(curr_var)) {
                            ArrayList<parse_Edge> edge_list = new ArrayList<parse_Edge>();
                            curr_function.edge_defs.put(curr_var,edge_list);
                        }
                        curr_function.edge_defs.get(curr_var).add(curr_edge);
                    }

                }
            }
            for(String key: uses_targets) {
                JsonArray var_list =(JsonArray)uses_jo.get(key);
                for(int k=0; k<var_list.size(); k++) {
                    String curr_var = var_list.get(k).getAsString();
                    if(!curr_function.variables.contains(curr_var)) {
                        curr_function.variables.add(curr_var);
                    }
                    // If the element is a node:
                    if(!key.contains(",")) {
                        if(!curr_function.node_uses.containsKey(curr_var)) {
                            ArrayList<Integer> node_list = new ArrayList<Integer>();
                            curr_function.node_uses.put(curr_var,node_list);
                        }
                        curr_function.node_uses.get(curr_var).add(Integer.parseInt(key));
                    } else {
                        //Else if an edge
                        String[] parts = key.split(",");
                        Integer source = Integer.parseInt(parts[0]);
                        Integer target = Integer.parseInt(parts[1]);
                        parse_Edge curr_edge = new parse_Edge(source, target, "");

                        if(!curr_function.edge_uses.containsKey(curr_var)) {
                            ArrayList<parse_Edge> edge_list = new ArrayList<parse_Edge>();
                            curr_function.edge_uses.put(curr_var,edge_list);
                        }
                        curr_function.edge_uses.get(curr_var).add(curr_edge);
                    }

                }
            }


            functions.add(curr_function);
            //for now, use ints for simplicity
            String graph_input_string="";
            //System.out.println("Nodes: "+curr_function.edges.size());//XXXX
            for(int j=0; j<curr_function.edges.size(); j++) {
                parse_Edge curr_edge = curr_function.edges.get(j);
                graph_input_string+=curr_edge.source+","+curr_edge.target+";";
            }
            graph_input_string=graph_input_string.substring(0,graph_input_string.length()-1);
            graph_input_string+=":";
            int initial_node=function_jo.getAsJsonPrimitive("initial_nodes").getAsInt();
            graph_input_string+=initial_node+":";
            JsonArray final_nodes = function_jo.getAsJsonArray("final_nodes");
            for(int j = 0; j< final_nodes.size(); j++) {
                JsonPrimitive fn_obj = (JsonPrimitive)final_nodes.get(j);
                int final_node = fn_obj.getAsInt();
                graph_input_string+=final_node+",";
                //System.out.printf("Loading '%d' as a final node.\n",final_node);
            }
            graph_input_string=graph_input_string.substring(0,graph_input_string.length()-1);
            //System.out.printf("\nGraph Input String: %s\n",graph_input_string);

            //DU stuff again
            ArrayList<String> DU_statements=new ArrayList<String>();
            for(String var: curr_function.variables) {
                String s = var+";";
                ArrayList<String> def_strings = new ArrayList<String>();
                if(curr_function.node_defs.containsKey(var)) {
                    for(Integer node: curr_function.node_defs.get(var)) {
                        def_strings.add(node.toString());
                    }
                }
                if(curr_function.edge_defs.containsKey(var)) {
                    for(parse_Edge edge: curr_function.edge_defs.get(var)) {
                        def_strings.add(edge.source+"."+edge.target);
                    }
                }
                s+=String.join(",",def_strings)+";";
                ArrayList<String> use_strings = new ArrayList<String>();
                if(curr_function.node_uses.containsKey(var)) {
                    for(Integer node: curr_function.node_uses.get(var)) {
                        use_strings.add(node.toString());
                    }
                }
                if(curr_function.edge_uses.containsKey(var)) {
                    for(parse_Edge edge: curr_function.edge_uses.get(var)) {
                        use_strings.add(edge.source+"."+edge.target);
                    }
                }
                s+=String.join(",",use_strings);
                DU_statements.add(s);
            }
            String du_graph_input_string=String.join(":",DU_statements);
            //Graph fn_graph = GraphUtil.inputGraph(graph_input_string);
            DFGraph fn_graph = GraphUtil.inputDFGraph(graph_input_string,du_graph_input_string);
            function_graphs.add(fn_graph);
        }
        for(int k = 0; k<function_graphs.size(); k++){
            DFGraph g = function_graphs.get(k);
            if(DEBUG) {
                DFGraph.enableDebug();
                Variable.enableDebug();
            }
            
            try {
                List<Path> trs = new ArrayList<Path>();
                List<Path> var_trs;
                Iterator<Variable> variables=g.getVariableIterator();
                String coverage_type;
                System.out.printf("\t\tTest Requirements for Function %s\n", functions.get(k).name);
                switch(args[0].trim().toLowerCase()) {
                    case "definition":
                    case "defs":
                    case "def":
                    case "d":
                        while(variables.hasNext()) {
                            Variable curr_var=variables.next();
                            System.out.printf("\tTest Requirements for Variable %s:\n", curr_var.getName());
                            var_trs=g.findAllDef(curr_var);
                            trs.addAll(var_trs);
                            for(Path p: var_trs) {
                                System.out.println(p);
                            }
                        }
                        coverage_type="All Definitions";
                        break;
                    case "uses":
                    case "use":
                    case "u":
                        while(variables.hasNext()) {
                            Variable curr_var=variables.next();
                            System.out.printf("\tTest Requirements for Variable %s:\n", curr_var.getName());
                            var_trs=g.findAllUse(curr_var);
                            trs.addAll(var_trs);
                            for(Path p: var_trs) {
                                System.out.println(p);
                            }
                        }
                        coverage_type="All Uses";
                        break;
                    case "def-use":
                    case "def-use-pair":
                    case "du-pairs":
                    case "du":
                        while(variables.hasNext()) {
                            Variable curr_var=variables.next();
                            System.out.printf("\tTest Requirements for Variable %s:\n", curr_var.getName());
                            var_trs=g.findAllDUPath(curr_var);
                            trs.addAll(var_trs);
                            for(Path p: var_trs) {
                                System.out.println(p);
                            }
                        }
                        coverage_type="All Definition-Use Pairs";
                        break;
                    case "edge":
                    case "e":
                        trs = g.findEdgeCoverage();
                        coverage_type="All Edges";
                        break;
                    case "edge-pair":
                    case "ep":
                        trs = g.findEdgePairCoverage("");
                        coverage_type="All Edges-Pairs";
                        break;
                    case "prime-path":
                    case "pp":
                        trs = g.findPrimePathCoverage("");
                        coverage_type="All Prime-Paths";
                        break;
                    case "node":
                    case "n":
                    default:
                    coverage_type="All Nodes";
                        trs = g.findNodeCoverage();
                        coverage_type="All Nodes";
                        break;
            }
            System.out.printf("\tAll Test Requirements (%s)\n", coverage_type);
            for(int i = 0; i<trs.size(); i++) {
                System.out.println(trs.get(i));
            }
            } catch (InvalidGraphException err){
                System.out.printf("Could not perform %s Coverage: %s\n",args[0],err);
            }
        }
        return;
	}
}
