import schedule
import time
from datetime import datetime, timedelta
import json
import os
import pymongo
from pymongo import MongoClient
from bson.objectid import ObjectId

client = MongoClient('localhost', 27017)
db = client.FMS
orders = db.orders
def job():
    cursor = orders.find({})
    for order in cursor:
        if (order["state"] == "PENDING" and (order["time"] + timedelta(seconds=2)) < (datetime.utcnow())):
            if (order["duration"] == "FOK" or (order["duration"] == "IOC" and order["filled"] > 0)):
                orders.update_one({'_id':order["_id"] },{"$set":{"state":"KILLED"}})
            elif (order["duration"] == "IOC" and order["filled"] == 0):
                orders.update_one({'_id':order["_id"] },{"$set":{"state":"FILLEDPARTIALLY"}})



schedule.every(1).seconds.do(job)
#get the current PID
ProcessPid= os.getpid()
#save the PID in a file named as user id
pidfilename = os.path.join('/home/maha/pids/orderKiller.pid')
pidfile = open(pidfilename, 'w')
pidfile.write(str(ProcessPid))
pidfile.close()

while 1:
    schedule.run_pending()
    time.sleep(1)
