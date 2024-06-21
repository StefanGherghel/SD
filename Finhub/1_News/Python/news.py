import requests
from datetime import date
from datetime import timedelta
import time
import socket 

class StockSymbolGenerator:
	def __init__(self, API_key):
		self.__API_key = API_key
		self.__link = 'https://finnhub.io/api/v1/stock/symbol?exchange=US&token=' + self.__API_key

	def get_stock_symbols_from_US(self):
		r = requests.get(self.__link)
		
		return r

class CompanyNewsGenerator:

	def __init__(self, API_key):
			self.__API_key = API_key

	def get_news(self, company_symbol ):
		today = date.today()
		current_date = today.strftime("%Y-%m-%d")
		
		yesterday = today - timedelta(days=1)
		yesterday_date = yesterday.strftime("%Y-%m-%d")


		link = 'https://finnhub.io/api/v1/company-news?symbol=' + company_symbol + '&from=' + yesterday_date + '&to=' + current_date + '&token=' + self.__API_key
		r = requests.get(link)
		
		return r

class NewsSender:

	def __init__(self):
		self.__API_key = 'c38sivqad3ido5ak9ibg'
		self._ssp = StockSymbolGenerator(self.__API_key)
		self._cng = CompanyNewsGenerator(self.__API_key)
		
		self.__HOST = '127.0.0.1'
		self.__PORT = 65432
				 	
	def run(self):
		with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
			s.bind((self.__HOST, self.__PORT))
			s.listen()
			print("Waiting for a connection...")
			conn, addr = s.accept()			
			print('Connected by ', addr)
			for company in self._ssp.get_stock_symbols_from_US().json():
				news = self._cng.get_news(company['symbol']).text

				if len(news) > 2:
					print(news)
					conn.sendall(bytes(news + "\n", 'utf-8'))
					time.sleep(3)
				else:
					print("Nu sunt stiri pentru " + company['symbol'])

if __name__ == '__main__':
	ns = NewsSender()
	ns.run()
