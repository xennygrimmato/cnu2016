import boto3
import json
import MySQLdb
import csv
import datetime
import time
import ast
import threading

'''
'''
#TODO: Get properties.json from s3

MAX_POLL_TIME = 15.0

'''
    queueing service
'''
def getMessages(queue_name):

    sqs = boto3.resource('sqs', region_name='us-east-1')
    queue = sqs.get_queue_by_name(QueueName = queue_name)

    messages = []

    start_time = time.time()
    while True:
        for message in queue.receive_messages(WaitTimeSeconds=20, MaxNumberOfMessages=10):
            content = (message.body)
            if len(content) == 0 or ('This is' in content):
                continue
            try:
                # convert string to python dict
                data = ast.literal_eval(content)
                messages.append(data)
                print data
                message.delete()
            except:
                pass

        time.sleep(1)

        if time.time() - start_time > MAX_POLL_TIME:
            return messages

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
        #conn = mysql.connector.connect(user=self.username, password=self.password, host=self.host, database=self.db_name)
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

    def executeQueryFromFile(self, filePath):
        with open(filePath, 'r') as f:
            sql_command = f.read()
            sql_command = sql_command.strip()
            self.cursor.execute(sql_command)

def createOrderedRow(row):
    ret = []
    ret.append(row['timestamp'])
    ret.append(str(row['url']))
    ret.append(str(row['data']))
    ret.append(str(row['responseCode']))
    ret.append(str(row['ipAddress']))
    ret.append('')
    ret.append(0)
    return tuple(ret)

def addRows(db, rows):
    statement = """INSERT INTO queue_data (timestamp, url, parameters, response_code, ip, data, id) VALUES (%s, %s, %s, %s, %s, %s, %s)"""
    db.cursor.executemany(statement, rows)
    db.connection.commit()

'''
Generic functions
'''
def select(db, tableName):
    # returns list of rows in table in the form of list of tuples
    query = """SELECT * FROM %s""" % (tableName)
    db.cursor.execute(query)
    rows = db.cursor.fetchall()
    #print rows
    return rows

def main():
    print 'Running thread.'
    db = Database('properties.json')
    cursor = db.getCursor()
    messages = getMessages('cnu2016_vtulsyan_log_queue') # fetch from properties file
    addRows(db, [createOrderedRow(row) for row in messages])

if __name__ == '__main__':
    threading.Timer(3, main).start()
    main()
