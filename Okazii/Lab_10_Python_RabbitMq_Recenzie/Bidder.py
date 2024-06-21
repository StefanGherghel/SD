import os
from random import randint
from uuid import uuid4
from RabbitMqConnection import RabbitMqProducer


class Bidder:
    def __init__(self):
        self.producer = RabbitMqProducer(exchange="bidder.direct",
                                         routing_key="bidder.routingkey")
        self.producer_review = RabbitMqProducer(exchange="bidder.direct",
                                         routing_key="review.routingkey")
        self.consumer = open("result.txt", "r")  # ofertantul afla rezultatul licitatiei din fisierul respectiv
        self.my_bid = randint(1000, 10_000)  # se genereaza oferta ca numar aleator intre 1000 si 10.000
        self.my_id = uuid4()  # se genereaza un identificator unic pentru ofertant

    def bid(self):
        # se construieste mesajul pentru licitare
        print("[ID:{}]Trimit licitatia mea: {}...".format(self.my_id, self.my_bid))
        bid_message = "id:{}_amount:{}".format(self.my_id, self.my_bid)  # corpul contine id-ul si suma
        self.producer.send_message(bid_message)  # trimitem licitatia catre Auctioneer


        stars = randint(1,5)
        review_message = "id:{}_Stars:{}".format(self.my_id, stars)
        self.producer_review.send_message(review_message)
        print(review_message)

        # exista o sansa din 2 ca oferta sa fie trimisa de 2 ori pentru a simula duplicatele
        if randint(0, 1) == 1:
            self.producer.send_message(bid_message)

    def get_winner(self):
        # se asteapta raspunsul licitatiei
        print("[ID:{}] Astept rezultatul licitatiei...".format(self.my_id))

        # asteptam pana cand ne este scris in result.txt castigatorul
        ok = False
        while ok is False:
            if os.stat("result.txt").st_size != 0:
                ok = True
        result = self.consumer.readline()

        # se verifica identitatea castigatorului(ex: winner:ID)
        identity = result.split(":")[1]

        if identity == str(self.my_id):
            print("[ID:{}] Am castigat!!!".format(self.my_id))
        else:
            print("[ID:{}] Am pierdut...".format(self.my_id))

        self.consumer.close()

    def run(self):
        self.bid()
        self.get_winner()


if __name__ == '__main__':
    bidder = Bidder()
    bidder.run()
