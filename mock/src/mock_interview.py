
from flask import Flask, request, render_template, session, redirect, jsonify
from flask_mail import Mail
# from tensorflow.keras.layers import Conv2D
# from tensorflow.keras.layers import Dense, Dropout, Flatten
# from tensorflow.keras.layers import MaxPooling2D
# from tensorflow.keras.models import Sequential

from DBConnection import *

app=Flask(__name__)
app.secret_key="hi"
import smtplib
from email.mime.text import MIMEText

mail=Mail(app)
app.config['MAIL_SERVER']='smtp.gmail.com'
app.config['MAIL_PORT'] = 587
app.config['MAIL_USERNAME'] = 'acetheinterview99@gmail.com'
app.config['MAIL_PASSWORD'] = 'Acetheinterview@99'
app.config['MAIL_USE_TLS'] = False
app.config['MAIL_USE_SSL'] = True
static_path="D:\\riss kannur\\workspace\\mock interview\\static\\"
import functools

def login_required(func):
    @functools.wraps(func)
    def secure_function():
        if "lid" not in session:
            return login()
        return func()
    return secure_function
@app.route("/")
def login():
    return render_template("start.html")
@app.route("/lg")
def lg():
    return render_template("index.html")

@app.route("/logout")
def logout():
    session.clear()
    return render_template("index.html")
@app.route("/forgot")
def forgot():

    return render_template("forgotpassword.html")


@app.route('/forpassword', methods=['POST','GET'])
def forpassword():
    emails=request.form['textfield']
    if '@gmail.com' in emails:

        q="SELECT `lid` FROM `interviewer` WHERE `email`=%s"
        v=emails
        res=selectonecond(q,v)
        if res is None:

            return "<script>alert('Email not valid');window.location='/forgot';</script>"
        else:
            lid=res[0]
            print("lid"+str(lid))
            qq="SELECT `password` FROM `login` WHERE `lid`=%s"
            res=selectonecond(qq,lid)
            print(res)
            try:
                gmail = smtplib.SMTP('smtp.gmail.com', 587)
                gmail.ehlo()
                gmail.starttls()
                gmail.login('acetheinterview99@gmail.com', 'Acetheinterview@99')
            except Exception as e:
                print("Couldn't setup email!!" + str(e))
            msg = MIMEText("your restored password from mock interview site")
            print(msg)
            msg['Subject'] = 'your password is '+str(res[0])
            msg['To'] = emails
            msg['From'] = 'acetheinterview99@gmail.com'
            try:
                gmail.send_message(msg)
                return "<script>alert('you can check your password in your email....');window.location='/forgot';</script>"

            except Exception as e:
                print("COULDN'T SEND EMAIL", str(e))
                return "<script>alert('Error');window.location='/forgot';</script>"
    else:
        uname = request.form['textfield']
        q = "select * from login where username=%s"
        v = uname
        ee = selectonecond(q, v)
        pa = ee[2]
        print()
        return render_template("forgotpassword.html", val="your password is " + pa)





@app.route("/login_post", methods=['post'])
def login_post():
    print(request.form)
    uname=request.form['textfield']
    pswd=request.form['textfield2']
    db=Db()
    res=db.selectOne("select * from login where username='"+uname+"' and password='"+pswd+"'")
    if res is None:
        return "<script>alert('Invalid details');window.location='/';</script>"
    else:
        session['lg']='yes'
        session['lid']=res['lid']
        if res['type']=='admin':
            return redirect('/home')
        elif res['type']=="interviewer":
            return redirect("/inter_home")
        else:
            return "<script>alert('Unauthorised access');window.location='/';</script>"

@app.route("/signup", methods=['get', 'post'])
def signup():
    if request.method=="POST":
        try:
            name=request.form['textfield']
            email=request.form['textfield2']
            phone=request.form['textfield3']
            about=request.form['textarea']
            password=request.form['textfield4']
            db=Db()
            lid=db.insert("INSERT INTO login(username, PASSWORD, TYPE) VALUES('"+email+"','"+password+"','pending')")
            db.insert("INSERT INTO interviewer(NAME, email, phone, about, lid) VALUES('"+name+"','"+email+"','"+phone+"','"+about+"','"+str(lid)+"')")
            return "<script>alert('Registered');window.location='/';</script>"
        except Exception as e:
            return "<script>alert('Already Registered');window.location='/';</script>"

    return render_template("interviewer/signup.html")


@app.route("/home")
@login_required

def home():
    return render_template("admin/home.html")


@app.route("/adm_view_interviewers")
@login_required
def adm_view_interviewers():
    db=Db()
    res=db.select("SELECT `interviewer`.* FROM `interviewer`, login WHERE `interviewer`.lid=`login`.lid AND login.type='pending'")
    return render_template("admin/view_interviewers.html", data=res)

@app.route("/adm_approve_inter/<id>")
def adm_approve_inter(id):
    db=Db()
    print(id)
    db.update("update login set type='interviewer' where lid='"+id+"'")
    return ("<script>alert('Approved');window.location='/adm_view_interviewers';</script>")

@app.route("/adm_reject_inter/<lid>")
def adm_reject_inter(lid):
    db=Db()
    db.update("update login set type='rejected' where lid='"+lid+"'")
    return ("<script>alert('Rejected');window.location='/adm_view_interviewers';</script>")


@app.route("/adm_view_approved_interviewers")
@login_required

def adm_view_approved_interviewers():
    db=Db()
    res=db.select("SELECT `interviewer`.* FROM `interviewer`, login WHERE `interviewer`.lid=`login`.lid AND login.type='interviewer'")
    return render_template("admin/view_approved_interviewers.html", data=res)


@app.route("/adm_view_users")
@login_required

def adm_view_users():
    db=Db()
    res=db.select("select * from candidate")
    return render_template("admin/view_candidates.html", data=res)




################################        INTERVIEWER

@app.route("/inter_home")
@login_required
def inter_home():
    return render_template("interviewer/home.html")

@app.route("/add_test", methods=['get', 'post'])
@login_required

def add_test():
    if request.method=="POST":
        test_name=request.form['textfield']
        descr=request.form['textarea']
        db=Db()
        db.insert("INSERT INTO test(test_name, description, interviewer_id) VALUES('"+test_name+"','"+descr+"','"+str(session['lid'])+"')")
        return "<script>alert('Test added');window.location='/add_test';</script>"
    return render_template("interviewer/add_test.html")

@app.route("/view_test")
@login_required

def view_test():
    db=Db()
    res=db.select("select * from test where interviewer_id='"+str(session['lid'])+"'")
    return render_template("interviewer/view_tests.html", data=res)

@app.route("/delete_test/<id>")

def delete_test(id):
    db=Db()
    db.delete("delete from test where test_id='"+id+"'")
    return redirect("/view_test")

@app.route("/edit_test/<id>", methods=['get', 'post'])

def edit_test(id):
    db=Db()
    if request.method=="POST":
        test_name = request.form['textfield']
        descr = request.form['textarea']
        id=request.form['hid']
        db.update("update test set test_name='"+test_name+"', description='"+descr+"' where test_id='"+id+"'")
        return redirect("/view_test")
    res=db.selectOne("select * from test where test_id='"+id+"'")
    return render_template("interviewer/edit_test.html", data=res)

@app.route("/question/<id>", methods=['get', 'post'])
def question(id):
    db=Db()
    if request.method=="POST":
        qn = request.form['textfield']
        ans = request.form['textarea']
        db.insert("insert into questions(test_id, question, answer) values('"+id+"','"+qn+"', '"+ans+"')")
        return "<script>alert('Question added');window.location='/question/"+id+"';</script>"
    res=db.select("select * from questions where test_id='"+id+"'")
    return render_template("interviewer/question.html", data=res, tid=id)

@app.route("/delete_qn/<id>/<tid>")

def delete_qn(id, tid):
    db=Db()
    db.delete("delete from questions where qn_id='"+id+"'")
    return redirect("/question/"+tid)

@app.route("/view_candidates/<tid>")
def view_candidates(tid):
    db=Db()
    print("SELECT * FROM `candidate` INNER JOIN `scores_main` ON `candidate`.lid=`scores_main`.user_id WHERE `scores_main`.test_id='"+tid+"' GROUP BY `scores_main`.user_id")
    res=db.select("SELECT * FROM `candidate` INNER JOIN `scores_main` ON `candidate`.lid=`scores_main`.user_id WHERE `scores_main`.test_id='"+tid+"' GROUP BY `scores_main`.user_id")
    return render_template("interviewer/view_candidates.html", data=res)

@app.route("/view_emotions/<score_id>")
def view_emotions(score_id):
    db=Db()
    res=db.select("SELECT * FROM `questions` INNER JOIN `score_sub` ON `questions`.qn_id=`score_sub`.qn_id WHERE `score_sub`.scores_id='"+score_id+"'")
    return render_template("interviewer/view_emotions.html", data=res)




@app.route("/adm1_view_users")
@login_required

def adm1_view_users():
    db=Db()
    res=db.select("select * from candidate")
    return render_template("interviewer/view_candidatess.html", data=res)



###########################             CANDIDATE

# @app.route("/and_login", methods=['post'])
# @login_required
#
# def and_login():
#     uname=request.form['uname']
#     password=request.form['password']
#     db=Db()
#     res=db.selectOne("SELECT * FROM login WHERE username='"+uname+"' AND PASSWORD='"+password+"'")
#     if res is None:
#         return jsonify(status="no")
#     else:
#         if res['type']=="user":
#             return jsonify(status="ok", lid=res['lid'])
#         else:
#             return jsonify(status="no")
#
# @app.route("/and_view_interviewers", methods=['post'])
#
# def and_view_interviewers():
#
#     q=("SELECT `interviewer`.* FROM `interviewer`, login WHERE `interviewer`.lid=`login`.lid AND login.type='interviewer'")
#     print(q)
#     res=androidselectall(q)
#     print(res)
#
#     if len(res)>0:
#         print(res)
#         return jsonify(res)
#     else:
#         return jsonify(status="no")
#
# @app.route("/and_view_tests", methods=['post'])
# @login_required
#
# def and_view_tests():
#     int_id=request.form['int_id']
#     db=Db()
#     res=db.select("SELECT * FROM `test` WHERE interviewer_id='"+int_id+"'")
#     if len(res)>0:
#         return jsonify(status="ok", data=res)
#     else:
#         return jsonify(status="no")
#
# @app.route("/and_view_questions", methods=['post'])
# @login_required
#
# def and_view_questions():
#     test_id=request.form['tid']
#     db=Db()
#     res=db.select("SELECT * FROM questions WHERE test_id='"+test_id+"'")
#     if len(res)>0:
#         return jsonify(status="ok", data=res)
#     else:
#         return jsonify(status="no")
#
#
# @app.route("/and_insert_scores", methods=['post'])
# @login_required
#
# def and_insert_scores():
#     qid=request.form['qid']
#     tid=request.form['tid']
#     answer=request.form['answer']
#     lid=request.form['lid']
#     image=request.form['image']
#
#     db=Db()
#     ress=db.selectOne("select * from questions where qn_id='"+qid+"'")
#     import base64
#     a = base64.b64decode(image)
#     fh = open(static_path+"a.jpg", "wb")
#     fh.write(a)
#     fh.close()
#     import numpy as np
#     import cv2
#     model = Sequential()
#
#     model.add(
#         Conv2D(32, kernel_size=(3, 3), activation='relu', input_shape=(48, 48, 1)))
#     model.add(Conv2D(64, kernel_size=(3, 3), activation='relu'))
#     model.add(MaxPooling2D(pool_size=(2, 2)))
#     model.add(Dropout(0.25))
#
#     model.add(Conv2D(128, kernel_size=(3, 3), activation='relu'))
#     model.add(MaxPooling2D(pool_size=(2, 2)))
#     model.add(Conv2D(128, kernel_size=(3, 3), activation='relu'))
#     model.add(MaxPooling2D(pool_size=(2, 2)))
#     model.add(Dropout(0.25))
#
#     model.add(Flatten())
#     model.add(Dense(1024, activation='relu'))
#     model.add(Dropout(0.5))
#     model.add(Dense(7, activation='softmax'))
#
#     model.load_weights(r'D:\riss kannur\workspace\mock interview\model.h5')
#
#     # prevents openCL usage and unnecessary logging messages
#     cv2.ocl.setUseOpenCL(False)
#
#     # dictionary which assigns each label an emotion (alphabetical order)
#     emotion_dict = {0: "Angry", 1: "Disgusted", 2: "Fearful", 3: "Happy", 4: "Neutral",
#                     5: "Sad",
#                     6: "Surprised"}
#
#     frame = cv2.imread(static_path+"a.jpg")
#
#     facecasc = cv2.CascadeClassifier(
#         r'D:\riss kannur\workspace\mock interview\haarcascade_frontalface_default.xml')
#     gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
#     faces = facecasc.detectMultiScale(gray, scaleFactor=1.3, minNeighbors=5)
#
#     for (x, y, w, h) in faces:
#         cv2.rectangle(frame, (x, y - 50), (x + w, y + h + 10), (255, 0, 0), 2)
#         roi_gray = gray[y:y + h, x:x + w]
#         cropped_img = np.expand_dims(np.expand_dims(cv2.resize(roi_gray, (48, 48)), -1),
#                                      0)
#         prediction = model.predict(cropped_img)
#         # print(prediction)
#         maxindex = int(np.argmax(prediction))
#         print(emotion_dict[maxindex])
#         res_emo = emotion_dict[maxindex]
#
#     #   sentence similarity
#     import spacy
#     nlp = spacy.load("en_core_web_lg")
#     doc1 = nlp(ress['answer'])
#     doc2 = nlp(answer)
#     simi=doc1.similarity(doc2)
#     score=0
#     if float(simi)>80.00:
#         score=1
#
#
#     db=Db()
#     res=db.selectOne("SELECT * FROM `scores_main` WHERE test_id='"+tid+"' AND user_id='"+lid+"' AND DATE=CURDATE()")
#     res2=db.selectOne("SELECT max(qn_id) as qn_id FROM questions WHERE test_id='"+tid+"'")
#     if res is None:
#         if res2['qn_id']==qid:
#             db.insert("INSERT INTO `scores_main`(test_id, user_id, DATE, score) VALUES('"+tid+"', '"+lid+"' ,CURDATE(), '"+score+"')")
#         main_id=db.selectOne("SELECT MAX(score_id) AS score_id FROM `scores_main` WHERE test_id='"+tid+"'")
#         mid=int(main_id['score_id'])+1
#         db.insert("")


@app.route('/graph/<tid>')
def graph(tid):
    print("tid",tid)
    import datetime
    fn=datetime.datetime.now().strftime("%Y%m%d%H%M%S")+".png"
    qry="SELECT * FROM `test`"
    res=selects(qry)
    resultclg=[]
    yfv = []
    ypv = []
    ynv = []
    xvalue=[]
    j=1
    for i in res:
        qry="SELECT COUNT(*) FROM `scores_main` WHERE `test_id`=%s AND `user_id`=3 and score=50.0"
        val=(str(i[0]))
        resfv=selectonecond(qry,val)
        yfv.append(int(resfv[0]))
        print("yfv",yfv)
        qry="SELECT COUNT(*) FROM `scores_main` WHERE `test_id`=%s AND `user_id`=3 and score>20.0"
        val=(i[1])
        respv=selectonecond(qry,val)
        ypv.append(int(respv[0]))
        #
        #
        qry="SELECT COUNT(*) FROM `scores_main` WHERE `test_id`=%s AND `user_id`=3 and score=0"
        val=(i[1])
        resnv=selectonecond(qry,val)
        ynv.append(int(resnv[0]))
        xvalue.append(j)
        print("xvalue",xvalue)
        j=j+1
        resultclg.append(i[1])
    import matplotlib
    matplotlib.use('Agg')
    import matplotlib.pyplot as plt1
    fig, axs = plt1.subplots(3)
    fig.suptitle('mock test ')
    axs[0].plot(xvalue, yfv, 'o-g', label="exam attend")
    axs[1].plot(xvalue, ypv, 'o-y', label="Partially  ")
    axs[2].plot(xvalue, ynv, 'o-r', label="Not attend")
    axs[0].legend()
    axs[1].legend()
    plt1.legend()
    plt1.savefig("static/graph/" + fn)
    plt1.close()
    return render_template("interviewer/admin_vi.html",val=resultclg,fn="../static/graph/"+fn)



if __name__== "__main__":
    app.run(debug=True, host='0.0.0.0', port=5000)
