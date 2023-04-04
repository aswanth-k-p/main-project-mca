import mysql.connector
import pymysql


class Db:
    def __init__(self):
        self.cnx = mysql.connector.connect(host="localhost",user="root",password="",database="2022_mock_interview")
        self.cur = self.cnx.cursor(dictionary=True)


    def select(self, q):
        self.cur.execute(q)
        return self.cur.fetchall()

    def selectOne(self, q):
        self.cur.execute(q)
        return self.cur.fetchone()


    def insert(self, q):
        self.cur.execute(q)
        self.cnx.commit()
        return self.cur.lastrowid

    def update(self, q):
        self.cur.execute(q)
        self.cnx.commit()
        return self.cur.rowcount

    def delete(self, q):
        self.cur.execute(q)
        self.cnx.commit()
        return self.cur.rowcount

def androidselectallnew(q):
    con=pymysql.connect(host='localhost',port=3306,user='root',passwd='',db='2022_mock_interview')
    cmd=con.cursor()
    cmd.execute(q)
    s=cmd.fetchall()

    row_headers = [x[0] for x in cmd.description]
    json_data = []

    for result in s:
        json_data.append(dict(zip(row_headers, result)))
    return json_data
def androidselectall(q,val):
    con=pymysql.connect(host='localhost',port=3306,user='root',passwd='',db='2022_mock_interview')
    cmd=con.cursor()
    cmd.execute(q,val)
    s=cmd.fetchall()
    row_headers = [x[0] for x in cmd.description]
    json_data = []
    print(json_data)
    for result in s:
        json_data.append(dict(zip(row_headers, result)))
    return json_data

def iud(q,val):
    con=pymysql.connect(host="localhost",user="root",password="",port=3306,db="2022_mock_interview")
    cmd=con.cursor()
    cmd.execute(q,val)
    id = con.insert_id()
    con.commit()
    return id
def selectonecond(q,val):
    con = pymysql.connect(host="localhost", user="root", password="", port=3306, db="2022_mock_interview")
    cmd = con.cursor()
    cmd.execute(q,val)
    s=cmd.fetchone()
    return s

def select(q):
    con = pymysql.connect(host="localhost", user="root", password="", port=3306, db="2022_mock_interview")
    cmd = con.cursor()
    cmd.execute(q)
    s=cmd.fetchone()
    return s


def selectall(q,val):
    con = pymysql.connect(host="localhost", user="root", password="", port=3306, db="2022_mock_interview")
    cmd = con.cursor()
    cmd.execute(q,val)
    s=cmd.fetchall()
    return s




def selects(q):
    con = pymysql.connect(host="localhost", user="root", password="", port=3306, db="2022_mock_interview")
    cmd = con.cursor()
    cmd.execute(q)
    s=cmd.fetchall()
    return s