from .parse_function import ParseFunction
from .parse_graph import Graph
from .parse_def_use import get_variables
from .parse_lib import norm, match_encase, get_statement,is_balanced, find_next
import re
import json
class ParseClass:
    def __init__(self,file_body=None):
        if file_body is None:
            return
        self.class_pre,function_bodies,self.class_post=match_encase('{','}',file_body)
        self.functions={}
        self.fields=[]
        
        items = re.split('\s+|;',self.class_pre)
        for i, it in enumerate(items):
            if it=="class":
                self.class_name=items[i+1]
        if self.class_name is None:
            raise Exception("Could not find class name")
        try:
            # Parse body of class
            while not function_bodies.isspace() and not len(function_bodies)==0:
                # First check for field declaration
                while True:
                    i=find_next(function_bodies,';')
                    if i is False or not is_balanced(function_bodies[:i+1]):
                        break
                    self.fields.append(function_bodies[:i+1])
                    function_bodies=function_bodies[i+1:]
                function_header,params,remaining=match_encase('(',')',function_bodies)
                bad,function_body,function_bodies=match_encase('{','}',remaining)
                if len(bad)>0 and not bad.isspace():
                    raise Exception()
                function_modifiers=function_header.split()
                function_name=function_modifiers.pop()
                return_type=function_modifiers.pop()
                
                self.functions[function_name]=ParseFunction(
                    label=function_name,
                    body=function_body,
                    return_type=return_type,
                    params=params,
                    modifiers=function_modifiers
                )
            # At this point, class generation is done; now we do def/use
            variables=get_variables(file_body)
            for func in self.functions.values():
                func_vars=variables[func.label]
                # For now, we treat parameters and variables as the same
                func.variables=func_vars['parameters']+func_vars['variables']
                func.find_du()
        except:
            raise Exception("Could not parse function at",function_bodies)
        # Now go back and add class fields to graphs
        field_statements=[]
        for field in self.fields:
            i = find_next(field,'=')
            if i is False:
                i=len(field)-1
            v=field[:i].split()
            v[-1]='this.'+v[-1]
            field_statements.append(' '.join(v)+field[i:])

        for func in self.functions.values():
            for field in field_statements:
                func.add_parameter(field[:-1],False)
            func.set_parameters()


    def __str__(self):
        ret=""
        ret+="Class Name: "+self.class_name+"\n"
        ret+="Class Fields: \n\t"+'\n\t'.join(self.fields)+"\n"
        ret+="Functions:\n"
        for k in self.functions.keys():
            ret+='\t'+k+'\n'
        return ret

    def pack(self):
        ret={}
        ret['class_name']=self.class_name
        ret['class_pre']=self.class_pre
        ret['class_post']=self.class_post
        ret['fields']=self.fields
        ret['functions']=[f.map() for f in self.functions.values()]
        return ret

    def unpack(self,img):
        self.class_name=img['class_name']
        self.class_pre=img['class_pre']
        self.class_post=img['class_post']
        self.fields=img['fields']
        self.functions={}
        for f in img['functions']:
            curr_function=ParseFunction(
                label=f['label'],
                return_type=f['return_type'],
                params=f['parameters'],
                modifiers=f['modifiers']
            )
            curr_graph=Graph()
            curr_graph.nodes=f['nodes']
            curr_graph.edges=[
                [
                    (e['to_node'],e['condition'])
                for e in lst]
            for lst in f['edges']]
            curr_graph.initial=f['initial_nodes']
            curr_graph.final=f['final_nodes']
            curr_function.graph=curr_graph
            curr_function.uses=f['uses']
            curr_function.defs=f['definitions']
            self.functions[f['label']]= curr_function


    def write(self, file_name):
        with open(file_name, 'w') as w_file:
            json.dump(self.pack(),w_file)
        

    def read(self,file_name):
        with open(file_name, 'r') as r_file:
            self.unpack(json.load(r_file))

