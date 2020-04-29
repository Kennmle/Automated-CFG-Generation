import javalang

def_types=[javalang.tree.FieldDeclaration,javalang.tree.LocalVariableDeclaration,javalang.tree.VariableDeclaration]
dec_type=javalang.tree.VariableDeclarator
ref_type=javalang.tree.MemberReference
assign_type=javalang.tree.Assignment
exp_types=[javalang.parse.parse_expression, javalang.parse.parse_member_signature]

def get_variables(file_body):
    functions={}
    
    tree=javalang.parse.parse(file_body)
    for path, method in tree.filter(javalang.tree.MethodDeclaration):
        functions[method.name]={'parameters':[],'variables':[]}
        for parameter in method.parameters:
            functions[method.name]['parameters'].append(parameter.name)
        for dec_path, declaration in method.filter(dec_type):
            functions[method.name]['variables'].append(declaration.name)

    return functions

def find_method(name,file_body):
    tree=javalang.parse.parse(file_body)
    for p, method in tree.filter(javalang.tree.MethodDeclaration):
        if method.name == name:
            return method
    return None

def get_def_use(statement,variables):
    # tuple of lists, first for defs, second for uses
    ret=[[],[]]
    for f in exp_types:
        try:
            tree=f(statement)
            return extract_def_use(tree,variables)
        except javalang.parser.JavaSyntaxError as err:
            continue
    return ret

def extract_def_use(node, variables):
    ret=[[],[]]
    if isinstance(node, assign_type):
        if isinstance(node.expressionl,ref_type) and node.expressionl.member in variables:
            ret[0].append(node.expressionl.member)
        # Currently will not handle nested assignments with intermediate statements
        r=extract_def_use(node.value,variables)
        ret[0]=r[0]+ret[0]
        ret[1]=r[1]+ret[1]
    # If not an assignment, just get all member references (later, we should probably recurse on children)
    else:
        declarations=node.filter(dec_type)
        for path, dec in declarations:
            if dec.name in variables:
                ret[0].append(dec.name)
        references = node.filter(ref_type)
        for path, ref in references:
            if ref.member in variables:
                ret[1].append(ref.member)
    return ret
        
        