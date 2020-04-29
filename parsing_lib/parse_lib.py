import unicodedata

def norm(text):
        return unicodedata.normalize("NFKD", text.casefold())

def find_matching(s1,s2, s):
    sstack=[s1]
    s=s[1:]
    for i,c in enumerate(s):
        if c==s1:
            sstack.append(c)
        if c==s2:
            if len(sstack)==0 or sstack.pop()!=s1:
                return False
        if len(sstack)==0:
            return i
    print(len(sstack))#XX
    return False

def is_balanced(s,d=[('{','}'),('(',')')]):
    st=[]
    for c in s:
        for p in d:
            if c==p[0]:
                st.append(c)
                break
            if c==p[1]:
                if st[-1]!=p[0]:
                    return False
                st.pop()
                break
    return len(st)==0

def find_next(s,v):
    for i,c in enumerate(s):
        if c==v:
            return i
    return False

def match_encase(s1,s2, s):
    s=s.lstrip()
    for i in range(len(s)):
        if s[i]==s1:
            break
    if i == len(s):
        return False
    remain_s=s[i:]
    end_i=find_matching(s1,s2,remain_s)
    if end_i is False:
        return False
    return (s[:i],s[i+1:i+end_i+1],s[i+end_i+2:])

# Split on the first semi-colon
def get_statement(statement):
    state_i=statement.find(';')
    if state_i==-1:
        #print("Could not find semi-colon: ",text_body)
        return False
    return statement[:state_i+1],statement[state_i+1:]

