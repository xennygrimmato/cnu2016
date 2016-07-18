import json
import MySQLdb
import datetime
from random import randrange as rr
from string import lowercase as lc
import time


class Database:
    def __init__(self, propertiesFile):
        data = self.getProperties(propertiesFile)
        self.username = data['username']
        self.password = data['password']
        self.host = data['host']
        self.db_name = data['db_name']

    def getProperties(self, propertiesFile):
        try:
            with open(propertiesFile) as f:
                data = json.load(f)
                return data
        except Exception:
            return None

    def getConnection(self):
        conn = MySQLdb.connect(self.host, self.username, self.password, self.db_name)
        self.connection = conn

    def getCursor(self):
        try:
            self.cursor = self.connection.cursor()
            return self.cursor
        except Exception:
            self.getConnection()
            self.cursor = self.connection.cursor()
            return self.cursor


def random_string(length=10):
    # returns a random lowercase Latin alphabet string
    return "".join(lc[rr(len(lc))] for i in xrange(length))

def random_product():
    return [0, random_string(), 10**7, random_string(), random_string(), 0]

def random_order():
    return [0, rr(1, 123), 0, datetime.datetime(rr(2003, 2005), rr(1, 13), rr(1, 29), rr(24), rr(60), rr(60)), "Created"]

def random_order_product():
    return [rr(1, 1000000), rr(1051000, 2051000), rr(1, 100), 3.14 * rr(1,1000), 1.23 * rr(1, 1000), 0]

def get_rows(random_function=None, num_rows=10**6):
    if not random_function:
        print 'Function not provided. Exiting...'
        exit(0)

    # runs the specified function, creates a list of objects returned and returns the list
    return [tuple(random_function()) for i in xrange(num_rows)]

def generic_insert(db, table_name, rows):
    if (not table_name) or (not rows):
        print "Table Name not specified. Exiting..."
        exit(0)
    s = ", ".join("%s" for i in xrange(len(rows[0])))
    query_str = "INSERT INTO {0} VALUES ({1})".format(table_name, s)

    print len(rows)

    print 'Starting insertion into DB.'
    start_time = time.time()
    K = 1000
    for i in xrange(len(rows)/K):
        db.cursor.executemany(query_str, rows[i*K: min(len(rows), (i+1)*K)])
        db.connection.commit()
    end_time = time.time()
    print 'Completed insertion.'
    print end_time - start_time


def main():
    db = Database('properties.json')
    cursor = db.getCursor()

    # Add 1 million products
    # (id, name, remaining, code, description, deleted)
    # (0, random, 10^7, random, random, 0)
    generic_insert(db, "product", get_rows(random_function=random_product))


    # Add 1 million orders
    # (oid, uid, amount, timestamp, status)
    # (0, random, 0, random, "Created")
    generic_insert(db, "orders", get_rows(random_function=random_order))


    # Add 1 million order products
    # (order_id, product_id, quantity, buying_cost, selling_cost, orders_oid)
    # (0, random, random, 10^2, random, random, 0)
    generic_insert(db, "order_product", get_rows(random_function=random_order_product))


if __name__ == '__main__':
    main()
