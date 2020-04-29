from .parse_graph import Graph
from .parse_def_use import get_def_use
from .parse_lib import find_next
class ParseFunction:
    import regex as re
    REGEX_FLAGS=re.U
    def __init__(self,label=None,body=None,return_type='void',params='',modifiers=[]):
        self.label=label
        self.ret_type=return_type
        self.params=[]
        self.variables=[] # For now, will also include params
        self.modifiers=modifiers
        self.graph=Graph()
        self.defs={}
        self.uses={}
        if isinstance(params, str):
            for s in params.split(','):
                x = s.strip()
                if not x.isspace() and len(x)>0:
                    self.add_parameter(x,False)
        elif isinstance(params, list):
            self.params=params
        else:
            raise TypeError("Parameters is not represented as a list or a string")
        self.set_parameters()
        self.graph.add_node()
        self.graph.add_edge(0,1)
        if body is not None:
            self.graph.handle_all(body)

    def prepend(self,text):
        self.graph.prepend(text)

    def add_parameter(self,text,reset=True):
        self.params.append(text)
        if reset:
            self.set_parameters()
        
    def set_parameters(self):
        if self.params is not None and len(self.params)>0:
            param_statement=''.join(s+';' for s in self.params)
            # Currently only runs at the beginning of the function, so okay
            self.graph.nodes[0]=param_statement

    def find_du(self):
        self.defs={}
        self.uses={}
        for ind,node in enumerate(self.graph.nodes):
            self.defs[ind]=[]
            self.uses[ind]=[]
            remaining_statements=node
            while len(remaining_statements)>0:
                #sc_ind is index of next immediate semi-colon
                sc_ind = find_next(remaining_statements,';')
                if sc_ind is False:
                    sc_ind=len(remaining_statements)
                statement=remaining_statements[:sc_ind+1]
                remaining_statements=remaining_statements[sc_ind+1:]
                dus=get_def_use(statement,self.variables)
                self.defs[ind]+=dus[0]
                self.uses[ind]+=dus[1]
        for ind, adj_list in enumerate(self.graph.edges):
            for edge in adj_list:
                dict_key=str(ind)+','+str(edge[0])
                self.defs[dict_key]=[]
                self.uses[dict_key]=[]
                remaining_statements=edge[1]
                while len(remaining_statements)>0:
                    #sc_ind is index of next immediate semi-colon
                    sc_ind = find_next(remaining_statements,';')
                    if sc_ind is False:
                        sc_ind=len(remaining_statements)
                    statement=remaining_statements[:sc_ind+1]
                    remaining_statements=remaining_statements[sc_ind+1:]
                    dus=get_def_use(statement,self.variables)
                    self.defs[dict_key]+=dus[0]
                    self.uses[dict_key]+=dus[1]



            

    def __str__(self):
        ret=""
        ret+="Function: "+self.label+'\n'
        ret+="Parameters: "+', '.join(self.params)+'\n'
        ret+="Return Type: "+self.ret_type+'\n'
        ret+="Modifiers: "+','.join(self.modifiers)+'\n'
        ret+=self.graph.__str__()
        ret+="Variables (for Def/Use): "+', '.join(self.variables)+'\n'
        ret+="Defs:\n"
        for def_node in self.defs.keys():
            if len(self.defs[def_node])>0:
                ret+="\tdef at "+str(def_node)+": {"+','.join(self.defs[def_node])+"}\n"
        ret+="Uses:\n"
        for use_node in self.uses.keys():
            if len(self.uses[use_node])>0:
                ret+="\tuse at "+str(use_node)+": {"+','.join(self.uses[use_node])+"}\n"
        return ret

    def map(self):
        ret={}
        ret['label']=self.label
        ret['return_type']=self.ret_type
        ret['parameters']=self.params
        ret['modifiers']=self.modifiers
        ret['nodes']=self.graph.nodes
        ret['edges']=[
            [
                {
                    'to_node': n[0],
                    'condition':n[1]
                } for n in lst
            ]
            for lst in self.graph.edges]
        ret['initial_nodes']=self.graph.initial
        ret['final_nodes']=self.graph.final
        ret['definitions']=self.defs
        ret['uses']=self.uses
        return ret
