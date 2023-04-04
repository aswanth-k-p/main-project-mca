import os

import datetime
import speech_recognition as sr
from flask import Flask, request, jsonify
from flask_mail import Mail
from werkzeug.utils import secure_filename
from src.DBConnection import *
from src.emotion import detect_emotion
from src.test import checkans
app=Flask(__name__)
app.secret_key="hi"
import language_tool_python

# using the tool
my_tool = language_tool_python.LanguageTool('en-US')

import smtplib
from email.mime.text import MIMEText

mail=Mail(app)
app.config['MAIL_SERVER']='smtp.gmail.com'
app.config['MAIL_PORT'] = 587
app.config['MAIL_USERNAME'] = 'acetheinterview99@gmail.com'
app.config['MAIL_PASSWORD'] = 'Acetheinterview@99'
app.config['MAIL_USE_TLS'] = False
app.config['MAIL_USE_SSL'] = True

static_path="C:\\Users\\HP\\PycharmProjects\\mock\\src\\static\\emotion\\"
@app.route("/logincode", methods=['post'])

def logincode():
    uname=request.form['username']
    password=request.form['password']
    db=Db()
    res=db.selectOne("SELECT * FROM login WHERE username='"+uname+"' AND PASSWORD='"+password+"'")
    print(res)
    if res is None:
        return jsonify({'task': 'error'})

    else:
        if res['type']=="user":
            q="select * from `candidate` WHERE `lid`=%s"
            rr=selectonecond(q,res['lid'])
            nm=rr[1]
            return jsonify({'task': 'success','lid':res['lid'],'type':res['type'],'nm':nm})
        else:
            return jsonify({'task': 'error'})

@app.route("/and_view_interviewers", methods=['post'])
def and_view_interviewers():
    q="SELECT `interviewer`.* FROM `interviewer`, login WHERE `interviewer`.lid=`login`.lid AND login.type='interviewer'"


    res = androidselectallnew(q)
    print(res)
    if len(res)>0:
        return jsonify(res)
    else:
        return jsonify(status="no")

@app.route("/and_view_tests", methods=['post'])
def and_view_tests():
    int_id=request.form['int_id']
    print(request.form)
    db=Db()
    a="SELECT `test`.*,`interviewer`.`name` FROM `test` JOIN `interviewer` ON `interviewer`.`lid`=`test`.`interviewer_id`WHERE `test`.interviewer_id='"+int_id+"'"
    res=androidselectallnew(a)
    print(res)
    if len(res)>0:
        return jsonify(res)
    else:
        return jsonify(status="no")




@app.route("/and_view_tests1", methods=['post'])
def and_view_tests1():
    int_id=request.form['lid']
    print(request.form)
    db=Db()
    a="SELECT DISTINCT `test`.*,`grp_plot`.`date`,`interviewer`.`name` FROM `test` JOIN `grp_plot` ON `grp_plot`.`testid`=`test`.`test_id` JOIN `interviewer` ON `interviewer`.`lid`=`test`.`interviewer_id` WHERE `grp_plot`.`lid`='"+int_id+"' "
    res=androidselectallnew(a)
    print(a)
    print(res)
    if len(res)>0:
        return jsonify(res)
    else:
        return jsonify(status="no")

@app.route("/and_view_questions", methods=['post'])
def and_view_questions():
    print(request.form)
    test_id=request.form['tid']
    db=Db()
    q="SELECT * FROM questions WHERE test_id='"+test_id+"'"
    res=androidselectallnew(q)
    print(res)
    if len(res)>0:
        return jsonify(res)
    else:
        return jsonify(status="no")




@app.route("/register", methods=['post'])

def register():
    db=Db()
    try:
        name=request.form['name']
        email=request.form['email']
        phone=request.form['phone']
        dob=request.form['dob']
        uname=request.form['username']

        password=request.form['password']
        lid=db.insert("INSERT INTO login(username, PASSWORD, TYPE) VALUES('"+uname+"','"+password+"','user')")
        db.insert("INSERT INTO `candidate`(NAME, email, phone, dob, lid) VALUES('"+name+"','"+email+"','"+phone+"','"+dob+"','"+str(lid)+"')")
        return jsonify({'task':'success'})

    except Exception as e:
        return jsonify({'task':str(e)})



# @app.route('/capture', methods=['POST','GET'])
# def capture():
#     print(request.files)
#     img = request.files['files']
#     im = secure_filename(img.filename)
#     import time
#
#     fl1 = im.split('.')
#     req1 = "pic" + "." + fl1[1]
#     print(req1)
#
#     img.save(os.path.join('F:/MainProject/Project_updated/mock/src/static/emotion/', req1))
#     return jsonify({'task': "ok"})


@app.route('/voice', methods=['POST','GET'])
def voice():
        try:
            print(request.form)
            print(request.files)
            scrid=request.form['scid']
            lid = request.form['lid']
            qid=request.form['qid']
            tid=request.form['tid']
            file=request.files['file']
            im=request.files['file1']
            image = secure_filename(im.filename)
            import time

            ffl = time.strftime("%Y%m%d_%H%M%S")
            reg = time.strftime("%Y%m%d_%H%M%S") + ".jpg"
            kk = "pic.jpg"
            im.save(os.path.join(r'D:\project\project-source\mock\src/static/emotion/', reg))
            ff=secure_filename(file.filename)
            print(ff)
            fl = ff.split('.')
            print(fl[1])
            import time
            ffl=time.strftime("%Y%m%d_%H%M%S")
            req = time.strftime("%Y%m%d_%H%M%S") + "." + fl[1]
            file.save(os.path.join('D:\project\project-source\mock\src/static/audio/', req))
            print(req)
            print(ffl)
            os.system('ffmpeg -i D:\project\project-source\mock\src\\static\\audio\\'+req+' D:\project\project-source\mock\src\\static\\audio\\'+ffl+".wav")
            ans="no answer"
            try:
                ans=silence_based_conversion(ffl)
            except:
                pass

            print("ans",ans)


            q="SELECT * FROM `questions` WHERE `qn_id`=%s"
            res=selectonecond(q,qid)
            oans = res[3]
            print(oans)
            mark=10
            sim = checkans(oans, ans)
            omark = sim * mark
            print("omark",omark)
            #
            if omark!=0.0:
                my_matches =[]# my_tool.check(ans)

                print(len(my_matches))
                if len(my_matches)>5 and len(my_matches)<10:
                    omark=omark-0.3
                elif len(my_matches)>10 and len(my_matches)<20:
                    omark=omark-0.6



            qqqq="select * from grp_plot where lid=%s and date=curdate() and testid=%s and qid=%s "
            vq=lid,tid,qid
            sd=selectonecond(qqqq,vq)
            gpid=0
            if sd is None:

                qe="insert into grp_plot values(null,%s,curdate(),%s,%s,%s)"
                v=lid,tid,qid,omark
                gpid=iud(qe,v)
            else:
                qa="update grp_plot set mark=%s where id=%s"
                b=omark,str(sd[0])
                gpid=sd[0]
                iud(qa,b)
            qw="select * from score_sub where scores_id=%s and qn_id=%s"
            v=scrid,qid
            res=selectonecond(qw,v)
            print("rrr"+str(res))
            if res is None:
                print(reg,"==============")
                em=detect_emotion(reg)
                print("emotion",em)
                qw="INSERT INTO `answer_details` VALUES (NULL,%s,%s,%s,%s,'pending',%s,curdate())"
                val=(lid,qid,ans,em,tid)
                iud(qw, val)



                qq = "insert into score_sub values(null,%s,%s,%s)"
                v = scrid, qid, em
                iud(qq, v)
                print(omark)

            return jsonify({'task': omark})
        except Exception as e:

            print(e,"qeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")

            return jsonify({'task': "failed"})








@app.route('/mark', methods=['POST','GET'])
def mark():
    scid=request.form['scid']
    mark=request.form['mark']
    q="update  scores_main set score=%s where score_id=%s"
    v=mark,scid
    iud(q,v)
    return jsonify({'task':'success'})



@app.route('/forgot', methods=['POST','GET'])
def forgot():
    email=request.form['email']
    q="SELECT `lid` FROM `candidate` WHERE `email`=%s"
    v=email
    res=selectonecond(q,v)
    if res is None:

        return jsonify({'task':'email does not valid'})
    else:
        lid=res[0]
        qq="SELECT `password` FROM `login` WHERE `lid`=%s"
        res=selectonecond(qq,lid)
        try:
            gmail = smtplib.SMTP('smtp.gmail.com', 587)
            gmail.ehlo()
            gmail.starttls()
            gmail.login('regionalmails@gmail.com', 'mails2020')
        except Exception as e:
            print("Couldn't setup email!!" + str(e))
        msg = MIMEText("your restored password from mock interview site")
        print(msg)
        msg['Subject'] = 'your password is '+str(res[0])
        msg['To'] = email
        msg['From'] = 'acetheinterview99@gmail.com'
        try:
            gmail.send_message(msg)
            return jsonify({'task':'success'})

        except Exception as e:
            print("COULDN'T SEND EMAIL", str(e))


            return jsonify({'task':'success'})







def silence_based_conversion(fl):
    path = r'./static/audio/'+fl+'.wav'



    r = sr.Recognizer()
    file=path
    # recognize the chunk
    with sr.AudioFile(file) as source:
        # remove this if it is not working
        # correctly.
        r.adjust_for_ambient_noise(source)
        audio_listened = r.listen(source)

    try:
        # try converting it to text
        rec = r.recognize_google(audio_listened)
        # write the output to the file.
        print(rec)
        return rec


    except sr.UnknownValueError:
        print("Could not understand audio")
        # return "Could not understand audio"

    except sr.RequestError as e:
        print("Could not request results. check your internet connection")


@app.route('/view_score',methods=['post'])
def view_score():
    print(request.form)
    lid=request.form['lid']
    tid=request.form['tid']
    q="insert into scores_main values(null,%s,%s,curdate(),0)"
    v=tid,lid
    id=iud(q,v)
    return jsonify({'task':str(id)})



@app.route('/viewresult1',methods=['post'])
def viewresult1():
    print(request.form)
    lid=request.form['lid']
    tid=request.form['tid']
    dt=request.form['dt']
    q="select * from `graph` WHERE `lid`=%s AND `tid`=%s AND `date`=%s"
    v=lid,tid,dt
    print(q,v)
    res=androidselectall(q,v)
    print(res)
    return jsonify(res)


@app.route('/viewresult2',methods=['post'])
def viewresult2():
    print(request.form)
    lid=request.form['lid']
    tid=request.form['tid']
    dt=request.form['dt']
    q="SELECT `answer_details`.*,`questions`.`question` FROM `questions` JOIN `answer_details` ON `answer_details`.`qid`=`questions`.`qn_id` WHERE `answer_details`.`uid`=%s AND `answer_details`.`tid`=%s AND `answer_details`.`date`=%s"
    v=lid,tid,dt
    print(q,v)
    res=androidselectall(q,v)
    print(res)
    return jsonify(res)


@app.route('/viewresult',methods=['post'])
def viewresult():
    lid=request.form['lid']
    tid=request.form['tid']

    import matplotlib.pyplot as plt

    # x-coordinates of left sides of bars
    left = []

    # heights of bars
    height = []

    q="SELECT  qid FROM `grp_plot` WHERE `testid`=%s and lid=%s and date=curdate()"
    f=tid,lid
    rr=selectall(q,f)
    print("rr",rr)


    tick_label = []
    for i in range(0,len(rr)):
        tick_label.append(i+1)
        left.append(i+1)

    for i in rr:
        q="select mark from grp_plot where lid=%s and testid=%s and date=curdate()and qid=%s "
        print("i",str(i[0]))
        v=lid,tid,str(i[0])
        print(v)
        h=selectonecond(q,v)
        print(h)
        height.append(int(h[0]))
    print("eee",height)



    # labels for bars

    # plotting a bar chart
    plt.bar(left, height, tick_label=tick_label,
            width=0.8, color=['red', 'green'])
    # naming the x-axis
    plt.xlabel('x - axis-question')
    # naming the y-axis
    plt.ylabel('y - axis-mark')
    # plot title
    plt.title('Result')

    # function to show the plot
    fn = datetime.datetime.now().strftime("%Y%m%d_%H%M%S")+".png"
    print(fn)
    try:
        plt.savefig("static/pltgraph/" + fn)
        q="insert into graph values(null,%s,%s,curdate(),%s)"
        v=lid,tid,fn
        iud(q,v)
        plt.close()
    except Exception as e:
        print(e)
        plt.savefig("static/pltgraph/" + fn)
        plt.close()
        q = "insert into graph values(null,%s,%s,curdate(),%s)"
        v = lid, tid, fn
        iud(q, v)
    return jsonify({'task':str(fn)})


if __name__ =='__main__':
    app.run(port=5000,host="0.0.0.0")
    # res=detect_emotion()
    # print(res)
