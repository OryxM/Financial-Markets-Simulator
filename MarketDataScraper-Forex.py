from selenium import webdriver
from selenium.webdriver.common.keys import Keys
import pymongo
import thread
from pymongo import MongoClient
import datetime
mongoClient = MongoClient('localhost', 27017)
dataBase = mongoClient.FMS
sharesData=dataBase.shares
forexData = dataBase.forex
forexData1 = dataBase.forex1
webDriver = webdriver.PhantomJS(executable_path="/usr/local/bin/phantomjs/bin/phantomjs")
webDriver1 = webdriver.PhantomJS(executable_path="/usr/local/bin/phantomjs/bin/phantomjs")
currencies=['EURUSD','USDJPY','GBPUSD','USDCHF','USDCAD','AUDUSD','NZDUSD','USDBGN','EURRON','USDTRY']
shares=['AAPL','BABA','BMW/g','BNP/f','DBK/g','FB','GOOG','IBM','JPM','KO','MSFT','RBS/u','RMG/u','SIE/g']
#scraping last prices
def scrapeLastPrice():
	quotes=[]
	ForexDataByTime={}
	for i in range (len (currencies)):
		quotation={}
		quotation['symbol']=currencies[i]
		url='https://finance.yahoo.com/quote/'+currencies[i]+'%3DX?p='+currencies[i]+'%3DX&guccounter=1'
		webDriver.get(url)
		html = webDriver.execute_script("return document.documentElement.innerHTML;")
		webDriver.get(html)
		lastPrice=webDriver.execute_script("return document.getElementsByClassName('Trsdu(0.3s) Fw(b) Fz(36px) Mb(-4px) D(ib)');")
		quotation['lastPrice']=lastPrice[0].text.encode("utf8")
		quotes.append(quotation)
	webDriver.close()
	ForexDataByTime['quotes']=quotes
	ForexDataByTime['date']=datetime.datetime.utcnow()
	print(ForexDataByTime)
	data_id = forexData.insert_one(ForexDataByTime).inserted_id
	webDriver.close()
# scraping bid , ask and change
def scrapeBidAsk():
	quotes=[]
	ForexDataByTime={}
	webDriver1.get("https://www.deltastock.com/english/resources/quotes.asp")
	webDriver1.switch_to.frame(webDriver1.find_element_by_id('forex-iframe'))
	for i in range (len (currencies)):
		quotation={}
		quotation['symbol']=currencies[i]
		bidId='b'+str(i)
		askId='a'+str(i)
		changeId='c'+str(i)
		quotation['bid']=webDriver1.execute_script("return document.getElementById('"+bidId+"');").text.encode("utf8")
		quotation['ask']=webDriver1.execute_script("return document.getElementById('"+askId+"');").text.encode("utf8")
		quotation['change']=webDriver1.execute_script("return document.getElementById('"+changeId+"');").text.encode("utf8")
		quotes.append(quotation)
	webDriver1.close()
	ForexDataByTime['quotes']=quotes
	ForexDataByTime['date']=datetime.datetime.utcnow()
	print(ForexDataByTime)
	data_id = forexData1.insert_one(ForexDataByTime).inserted_id
	webDriver1.close()
try:
   thread.start_new_thread( scrapeLastPrice, () )
   thread.start_new_thread( scrapeBidAsk, () )
except:
   print "Error: unable to start thread"
while 1:
	pass

