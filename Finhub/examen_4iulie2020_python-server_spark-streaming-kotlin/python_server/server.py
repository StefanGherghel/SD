import json
import socket
import time
from typing import List

#import requests


class Api3rdParty:
    def __init__(self):
        self.__api_uri = "https://finnhub.io/api/v1/stock/"
        self.__token = "bs03ehfrh5rbvs9fgsag"
        self.api_url_stock_symbols = '{}symbol?exchange=US&token={}'.format(self.__api_uri, self.__token)

    def getData(self) -> List[dict]:
        symbols_response = requests.get(self.api_url_stock_symbols)
        symbols_data = symbols_response.content
        return json.loads(symbols_data)

    def getSpecificData(self, json_obj: dict) -> dict:
        company_profile_url = '{}profile2?symbol={}&token={}'.format(self.__api_uri, json_obj["symbol"],
                                                                     self.__token)
        data = json.loads(requests.get(company_profile_url).content)
        return data if data.__ne__({}) else None


class ApiNetworkService:
    __host: str = None
    __port: int = None
    __socket: socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    __client_socket: socket = None
    __client_address: str = None

    def __init__(self, host: str, port: int):
        self.__host = host
        self.__port = port

        self.__socket.bind((self.__host, self.__port))
        self.__socket.listen(1)
        self.__client_socket, self.__client_address = self.__socket.accept()

    def sendJSON(self, json_data: str) -> None:
        self.__client_socket.sendall(json_data.encode())

    def closeConnection(self) -> None:
        self.__client_socket.close()


if __name__ == "__main__":
    partyCon = Api3rdParty()
    serviceCon = ApiNetworkService('localhost', 1502)
    try:
        values = partyCon.getData()
        for item in values:
            profile_json_data = partyCon.getSpecificData(item)
            if profile_json_data is not None:
                print(json.dumps(profile_json_data))
                serviceCon.sendJSON(json.dumps(profile_json_data) + "\n")
                time.sleep(3)
    except Exception:
        serviceCon.closeConnection()
        exit(1)
    finally:
        serviceCon.closeConnection()
