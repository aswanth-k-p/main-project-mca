import time

import language_tool_python
from src.DBConnection import *
# using the tool
my_tool = language_tool_python.LanguageTool('en-US')

# given text
my_text = """LanguageTool provides utility to check grammar and spelling errors. We just have to paste the text here and click the 'Check Text' button. Click the colored phrases for information on potential errors. Or we can use this text to see some of the issues that LanguageTool can detect. Whot do someone thinks of grammar checkers? Please not that they are not perfect. Style problems get a blue marker: It is 7 P.M. in the evening. The weather was nice on Monday, 22 November 2021"""
while True:
    qry="SELECT `aid`,`ans` FROM `answer_details` WHERE `details`='pending'"
    res=selects(qry)
    for i in res:

        # getting the matches
        my_matches = my_tool.check(i[1])

        # print(my_matches)
        # printing matches
        # print(type(my_matches))
        print(len(my_matches))
        qry="UPDATE `answer_details` SET `details`=%s WHERE `aid`=%s"
        val=(str(len(my_matches)),i[0])
        iud(qry,val)

        for i in my_matches:
            ii=str(i)
            print(type(ii))
            print(ii)
            print("+++++++++++++++++++++++++++++++++++++")
            # for ii in i:
            #     print(ii)
            #     print("========================")
    time.sleep(10)
    print("==========")