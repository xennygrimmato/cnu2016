import os
import xml.etree.ElementTree
from sys import argv

INCONSISTENCY_TYPES = []

def getfiles():
    filenames = []
    for i in os.listdir(os.getcwd()):
        if i.endswith(".xml"):
            filenames.append(i)
    return filenames

def parsefile(filename=None, csv_name=None):
    ret = []
    ret.append('SourcePath, Start Line, End Line, Violation String')
    e = xml.etree.ElementTree.parse(filename).getroot()
    for atype in e.findall('BugInstance'):
        tmp = atype.get('type')
        if tmp in INCONSISTENCY_TYPES:
            y = atype.find('SourceLine')
            methodname = atype.find('Method').get('name')
            path = y.get('classname') + '.' + methodname
            start = y.get('start')
            end = y.get('end')
            msg = atype.find('LongMessage').text
            try:
                ret.append(','.join(z.encode('utf8') for z in [path,start,end,msg]))
                #print ret
            except:
                pass
    return ret

def create_csv(csv_name, lines):
    if len(lines) > 1:
        with open(csv_name, 'w') as f:
            for line in lines:
                print line
                f.write(str(line) + '\n')

if __name__ == '__main__':
    # python xmltocsv.py [list of args]

    if len(argv) < 2:
        print 'Usage: python xmltocsv.py [LIST OF INCONSISTENCY TYPES]\nExample: python xmltocsv.py RUN_INCONSISTENCY THREAD_INCONSISTENCY'

    for i in xrange(1, len(argv)):
        print argv[i]
        INCONSISTENCY_TYPES.append(argv[i])

    filenames = getfiles()
    for f in filenames:
        print 'Parsing {0}'.format(f)
        csv_name = f.split('.')[0] + '.csv'
        create_csv(csv_name, parsefile(f))
        #parsefile(filename=f, csv_name=csv_name)
        print 'Written to {0}'.format(csv_name)
