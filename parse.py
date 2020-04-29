import sys
from parsing_lib.parse_graph import Graph
from parsing_lib.parse_class import ParseClass
from parsing_lib.parse_lib import norm, match_encase, get_statement

def t_parse(file_name):
    f= open(file_name, "r")

    file_body=f.read()
    file_body=''.join([l.strip() for l in file_body.split('\n')]).strip()
    single=ParseClass(file_body)
    return single

def t_write(parse_class,file_name):
    parse_class.write(file_name)

def t_load(file_name):
    single=ParseClass()
    single.read(file_name)
    return single

def t_print(single):
    print(single)
    for f in single.functions.values():
        print('-----------------')
        print(f)
        print('-----------------')

def parse_and_write(mode,args):
    if len(args)!=4:
        print("Improper usage, {} expects two arguments".format(mode))
        exit(0)
    file_input=args[2]
    file_output=args[3]
    class_instance=t_parse(file_input)
    t_write(class_instance,file_output)

def load_and_print(mode,args):
    if len(args)!=3:
        print("Improper usage, {} expects one argument".format(mode))
        exit(0)
    file_input=args[2]
    class_instance=t_load(file_input)
    t_print(class_instance)

def parse_and_print(mode,args):
    if len(args)!=3:
        print("Improper usage, {} expects one argument".format(mode))
        exit(0)
    file_input=args[2]
    class_instance=t_parse(file_input)
    t_print(class_instance)

def list_modes(x,y):
    print("Available modes:")
    for m in MODES.keys():
        print(m,':',MODES.get(m)[1],'\n')


MODES={
    'write': (
        parse_and_write,
        """
        Generates control-flow graph for a java file and saves it as a json
        USAGE: {} write <java-file> <output-file>""".format(sys.argv[0])
        
    ),
    'load' : (
        load_and_print,
        """
        Loads a json file storing a control-flow graph and prints it
        USAGE: {} load <json-file>""".format(sys.argv[0])
    ),
    'read': (
        parse_and_print,
        """
        Loads a java file prints the control-flow graph for it
        USAGE: {} read <java-file>""".format(sys.argv[0])
    ),
    'help':  (
        list_modes,
        """
        Prints a list of commands for this file
        USAGE: {} help """.format(sys.argv[0])
    ),
}
if len(sys.argv)<=1:
    print("Improper usage: no mode specified")
    exit(0)
mode=sys.argv[1]
f=MODES.get(mode,None)[0]
if f is None:
    print("Improper usage: invalid mode specified")
    print("Run {} help for a list of options".format(sys.argv[0]))
    exit(0)
f(mode,sys.argv)


