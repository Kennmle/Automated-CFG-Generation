
from .parse_lib import norm, match_encase, get_statement
import regex as re
REGEX_FLAGS=re.U
VARIABLE_REGEX=r'[a-zA-Z][a-zA-Z1-9]*'

class Graph:
    # label=function name
    def __init__(self,text=None):
        # List of strings: represents the contents of the node
        self.nodes=[""]
        # List of List of (int, str) tuples: each list is an adjacency list, int is node number, string is condition
        self.edges=[[]]
        # Initial node is always 0 (for now)
        self.initial=0
        # List of ints: numbers corresponding to all final nodes
        self.final=[]
        # If given text, parse it
        self.statement_types=[self.handle_if, self.handle_for, self.handle_statement]
        if text:
            if self.handle_all(text) is False:
                raise Exception("Could not parse file")
            # Band-aid fix
            if not len(self.nodes)-1 in self.final:
                self.final.append(len(self.nodes)-1)
            self.text=text
            if self.handle_def_use() is False:
                raise Exception("Could not parse graph")

    # Appends text to the last node in list
    def append_last(self,text):
        self.nodes[-1]+=text
    
    # Appends text to specified node
    def append_ind(self,ind,text):
        self.nodes[ind]+=text
    # Prepends to top of first node
    def prepend(self,text):
        self.nodes[0]=text+self.nodes[0]
    # Appends new node
    def add_node(self,text=''):
        self.nodes.append(text)
        self.edges.append([])

    # Adds edge to non-final node (for now, silently fail)
    def add_edge(self,start,end,condition=''):
        if start not in self.final:
            self.edges[start].append((end,condition))

    # Adds to list of final nodes
    # Will also remove all edges from this node
    def add_final(self,n):
        self.final.append(n)
        self.edges[n]=[]

    def set_final(self):
        self.add_final(len(self.nodes)-1)

    def get_last(self):
        return len(self.nodes)-1
    
    def __str__(self):
        ret=""
        ret+='Nodes:\n'
        for i in range(len(self.nodes)):
            ret+='\t'+str(i)+': '+self.nodes[i].replace(';',';\n\t   ')+'\n'
        ret+='Final nodes:\n'
        ret+='\t'+','.join([str(n) for n in self.final])+'\n'
        ret+='Edges:\n'
        for k in range(len(self.edges)):
            ret+='\t'+str(k)+': \n\t\t'+'\n\t\t'.join([str(p[0])+'('+p[1]+')' for p in self.edges[k]])+'\n'
        return ret

    

    def handle_all(self,text_body):
        remaining_text=text_body
        while len(remaining_text)!=0:
            matched=False
            for f in self.statement_types:
                k_ret = f(remaining_text)
                if k_ret!=False:
                    matched=True
                    remaining_text=k_ret
                    break
            if not matched:
                print("Couldn't match: "+remaining_text)
                return False
        
        #print(str(self))
        return True
    
    

    def handle_if(self, text_body):
        if type(text_body)!=str:
            print("TYPE ERROR: ",text_body)
        i = self.get_last()
        # Check to see if it matches
        text=norm(text_body)
        top=text[0:2]
        if not norm('if')==norm(top):
            return False
        # Find condition
        condition=text[2:].lstrip()
        cond_split=match_encase('(',')', condition)
        if not cond_split or cond_split[0]!='':
            #print('Not if-statement (2)')
            return False
        condition=cond_split[1]
        # Find statement
        statement_split=match_encase('{','}', cond_split[2])
        if not statement_split or statement_split[0]!='':
            return False
        statement=statement_split[1]
        remaining_text=statement_split[2]
        #Create new node for positive branch (don't worry about else yet)
        self.add_node()
        self.add_edge(i,i+1,condition)
        # Now check to see if there's an if
        if self.handle_all(statement) is False:
            print('Bad if-body')
            return False
        # Look for else
        j=self.get_last()
        text=norm(remaining_text.lstrip())
        top=text[0:4]
        if norm('else')==norm(top):
            else_remaining=text[4:].lstrip()
            else_split=match_encase('{','}', else_remaining)
            if else_split and else_split[0]=='':
                else_body=else_split[1]
                remaining_text=else_split[2]
                # Create new node for start of else
                
                self.add_node()
                self.add_edge(i,j+1,'!('+condition+')')
                if self.handle_all(else_body) is False:
                    print('Bad else-body')
                    return False
                k = self.get_last()
                self.add_node()
                self.add_edge(j,k+1)
                self.add_edge(k,k+1)
                return remaining_text
            else:
                print('bad else split')

        #At this point, inside if-statement should already be handled; create new node for continuing
        j = self.get_last()
        self.add_node()
        self.add_edge(i,j+1,'!('+condition+')')
        self.add_edge(j,j+1)
        return remaining_text
    

    def handle_for(self, text_body):
        if type(text_body)!=str:
            print("TYPE ERROR: ",text_body)
        i = self.get_last()
        # Check to see if it matches
        text=norm(text_body)
        top=text[0:3]
        if not norm('for')==norm(top):
            return False
        # Find condition
        condition=text[3:].lstrip()
        cond_split=match_encase('(',')', condition)
        if not cond_split or cond_split[0]!='':
            return False
        condition=cond_split[1]
        # For loop: split into init, check, and inc
        try:
            cond_init, cond_rem=get_statement(condition)
            cond_check,cond_inc=get_statement(cond_rem)
        except:
            #Couldn't find for conditions
            return False
        # Find statement
        statement_split=match_encase('{','}', cond_split[2])
        if not statement_split or statement_split[0]!='':
            return False
        statement=statement_split[1]
        remaining_text=statement_split[2]
        #Create new node for positive branch
        self.append_last(cond_init)
        # Junction Node
        self.add_node()
        self.add_edge(i,i+1)
        # For-loop Body
        self.add_node()
        self.add_edge(i+1,i+2,cond_check)

        if self.handle_all(statement) is False:
            print('Bad for-body')
            return False

        #At this point, inside for-statement should already be handled; 
        #append for incrementing (j)
        #create new node for continuing (j+1)
        j = self.get_last()
        self.append_last(cond_inc)
        self.add_node()
        self.add_edge(i+1,j+1, '!('+cond_check[:-1]+')')
        self.add_edge(j,i+1)
        return remaining_text

    def handle_statement(self, text_body):
        return_regex=r'return(\s|;)'
        statement_split=get_statement(text_body)
        if statement_split is False:
            return False
        self.append_last(statement_split[0])
        remaining_text=statement_split[1]
        if re.match(return_regex,statement_split[0],REGEX_FLAGS):
            self.set_final()
            return ''
        return remaining_text



