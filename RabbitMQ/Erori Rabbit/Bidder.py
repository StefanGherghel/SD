import os
from random import randint
from uuid import uuid4

from RabbitMqConnection import RabbitMqProducer


class Bidder:
    def __init__(self):
        self.producer = RabbitMqProducer(exchange="testbidder.direct",
                                         routing_key="testbidder.routingkey")
        self.consumer = open("result.txt", "r")

        self.my_bid = randint(1000, 10_000)  # se genereaza oferta ca numar aleator intre 1000 si 10.000
        self.my_id = uuid4()  # se genereaza un identificator unic pentru ofertant

    def bid(self):
        print("[INFO~id:{}]Trimit licitatia mea: {}...".format(self.my_id, self.my_bid))
        bid_message = "id:{}_amount:{}".format(self.my_id, self.my_bid)

        self.producer.send_message(bid_message)

        # exista o sansa din 2 ca oferta sa fie trimisa de 2 ori pentru a simula duplicatele
        if randint(0, 1) == 1:
            self.producer.send_message(bid_message)

    def get_winner(self):
        print("[INFO~id:{}]Astept rezultatul licitatiei...".format(self.my_id))
        ok = False
        while ok is False:
            if os.stat("result.txt").st_size != 0:
                ok = True
        result = self.consumer.readline()
        # se verifica identitatea castigatorului
        identity = result.split(":")[1]

        if identity == str(self.my_id):
            print("[INFO~id:{}] Am castigat!".format(self.my_id))
        else:
            print("[INFO~id:{}] Am pierdut!".format(self.my_id))

    def run(self):
        self.bid()
        self.get_winner()


if __name__ == '__main__':
    bidder = Bidder()
    bidder.run()
