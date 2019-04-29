from selenium import webdriver
from selenium.webdriver.common.keys import Keys
import pymongo
import thread
from pymongo import MongoClient
import datetime
mongoClient = MongoClient('localhost', 27017)
dataBase = mongoClient.FMS
forexData = dataBase.forex2
with open('CurrencySymbols') as fp:
    lines = fp.readlines()
currencies=[]
for i in range (len(lines)):
    currencies.append(lines[i][0:6])
webDriver = webdriver.PhantomJS(executable_path="/usr/local/bin/phantomjs/bin/phantomjs")
quotes=[]
ForexDataByTime={}
url='https://www.dailyfx.com/forex-rates'
webDriver.get(url)
html = webDriver.execute_script("return document.documentElement.innerHTML;")
webDriver.get(html)
for i in range (len (currencies)):
	quotation={}
	quotation['symbol']=currencies[i]
	bid=webDriver.find_elements_by_xpath("//tr[@id='"+currencies[i]+"']/td[@class='text-right rates-row-td']/span[@data-type='bid']")
	ask=webDriver.find_elements_by_xpath("//tr[@id='"+currencies[i]+"']/td[@class='text-right rates-row-td']/span[@data-type='ask']")
	quotation['bid']=bid[0].get_attribute('data-value').encode("utf8")
	quotation['ask']=ask[0].get_attribute('data-value').encode("utf8")
	quotes.append(quotation)
webDriver.close()
ForexDataByTime['quotes']=quotes
ForexDataByTime['date']=datetime.datetime.utcnow()
print(ForexDataByTime)
data_id = forexData.insert_one(ForexDataByTime).inserted_id
