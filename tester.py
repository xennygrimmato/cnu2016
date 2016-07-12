import urllib2
import time
import json
from sys import argv
from string import lowercase as lc
from random import randrange as rr

def send_post_req():
    data = json.dumps({"code": "".join(str(x) for x in xrange(10)),
                       "name": "".join(lc[rr(len(lc))] for i in xrange(10))
                        })
    print 'HTTP/1.1 POST /api/products'
    print 'Request Body: {0}'.format(data)
    req = urllib2.Request('http://localhost:8080/api/products/', data, {'Content-Type': 'application/json'})
    f = urllib2.urlopen(req)
    response = f.read()
    f.close()

def send_get_req():
    response = urllib2.urlopen('http://localhost:8080/api/products')

if len(argv) < 3:
    print 'Usage: python tester.py <iterations> <requests per iteration>'
    print 'Exiting...'
    exit(0)

#TODO: Can use argument parser with flags instead of this method
MAX_ITERATIONS = int(argv[1])
MAX_REQUESTS = int(argv[2])

for i in xrange(MAX_ITERATIONS):
    for j in xrange(MAX_REQUESTS):
        if j&1:
            send_post_req()
        else:
            print 'HTTP/1.1 GET /api/products'
            send_get_req()
        time.sleep(0.005)
