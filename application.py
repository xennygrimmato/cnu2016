from flask import Flask, jsonify, request
from flask_sqlalchemy import SQLAlchemy
import datetime
import dateutil.parser as parser
import ast

app = Flask(__name__)
app.config.from_pyfile('config.py')
db = SQLAlchemy(app)

def to_iso(date):
    ret = parser.parse(date)
    return ret.isoformat()

class queue_data(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    timestamp = db.Column(db.DATETIME)
    url = db.Column(db.String(1024))
    parameters = db.Column(db.String(1024))
    response_code = db.Column(db.String(255))
    ip = db.Column(db.String(255))
    method = db.Column(db.String(255))
    duration = db.Column(db.String(35))

    def __init__(self, timestamp, url, parameters, response_code, ip, method, duration):
        self.timestamp=timestamp
        self.url=url
        self.parameters=parameters
        self.response_code=response_code
        self.ip=ip
        self.method=method
        self.duration=duration

    def as_json(self):
        # get json form of object
        ts = datetime.datetime.strptime(to_iso(str(self.timestamp)), '%Y-%m-%dT%H:%M:%S').strftime('%m/%d/%yT%H:%M:%S')
        d = {'timestamp': ts, 'url': self.url, 'request_type':self.method, 'parameters':ast.literal_eval(self.parameters), 'request_duration_ms': int(self.duration), 'response_code': int(self.response_code), 'request_ip_address': self.ip}
        return d

@app.route("/api/auditLogs")
def get_logs():
    start_time = to_iso(request.args.get('startTime', '1970-01-01 00:00:00'))
    end_time = to_iso(request.args.get('endTime', '2020-12-12 23:59:59'))
    offset = max(0, int(request.args.get('offset'))) if ('offset' in request.args) else 0
    limit = max(0, min(10, int(request.args.get('limit')))) if ('limit' in request.args) else 10

    print start_time, end_time, limit, offset
    return jsonify(data=[obj.as_json() for obj in queue_data
                   .query
                   .order_by(queue_data.timestamp.desc())
                   .filter(queue_data.timestamp.between(start_time, end_time))
                   .limit(limit).offset(offset).all()])
if __name__ == "__main__":
    db.create_all()
    app.run()
