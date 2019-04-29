from selenium import webdriver
from selenium.webdriver.common.keys import Keys
import pymongo
from pymongo import MongoClient
import datetime
dataUrl="https://finance.yahoo.com/gainers"
mongoClient = MongoClient('localhost', 27017)
dataBase = mongoClient.FMS
sharesData=dataBase.shares
webDriver = webdriver.PhantomJS(executable_path="/usr/local/bin/phantomjs/bin/phantomjs")
shares=['AAPL','BABA','BMW/g','BNP/f','DBK/g','FB','GOOG','IBM','JPM','KO','MSFT','RBS/u','RMG/u','SIE/g','ABX']
quotes=[]
sharesDataByTime={}
webDriver.get(dataUrl)
html = webDriver.execute_script("return document.documentElement.innerHTML;")
webDriver.get(html)

#webDriver.switch_to.frame(webDriver.find_element_by_id('shares-iframe'))

div=webDriver.execute_script("return document.getElementsByClassName('Ovx(s) Ovy(h)');")
print(div[0].text)
htmlFile= open('output.html', 'w')
htmlFile.write(div[0].text.encode("utf8"))
htmlFile.close()

webDriver.close()
