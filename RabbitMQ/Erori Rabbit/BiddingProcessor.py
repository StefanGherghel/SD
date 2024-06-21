import time

from RabbitMqConnection import RabbitMqConsumer, RabbitMqProducer


class BiddingProcessor:
    def __init__(self):
        self.consumer = RabbitMqConsumer(rabbit_queue="biddingprocessor.queue")
        self.producer_err = RabbitMqProducer(exchange="testbidder.direct",
                                             routing_key="errorprocessor.key")
        self.producer = open("result.txt", "w")

    def get_processed_bids(self):
        print("[INFO-BiddingProcessor] Astept ofertele procesate de MessageProcessor...")
        time.sleep(5)
        # ofertele se stocheaza sub forma de perechi 'id:<ID>_amount:<SUMA>'
        bids = dict()
        try:
            self.consumer.receive_message()
        except Exception as e:
            self.producer_err.send_message("except:{}".format(e))
        while len(self.consumer.list_msg) != 0:
            msg = self.consumer.list_msg.pop()
            if msg == "incheiat":
                break
            msg = msg.split("_")
            bids[msg[0].split(":")[1]] = msg[1].split(":")[1]
            try:
                self.consumer.receive_message()
            except Exception as e:
                self.producer_err.send_message("except:{}".format(e))

        self.decide_auction_winner(bids)

    def decide_auction_winner(self, bids):
        print("[INFO-BiddingProcessor] Procesez ofertele...")

        if len(bids) == 0:
            print("[INFO-BiddingProcessor] Nu exista nicio oferta de procesat.")
            return

        # sortare dupa oferte, descrescator
        sorted_bids = sorted(bids.keys(), reverse=True)

        # castigatorul este ofertantul care a oferit pretul cel mai mare
        winner = bids[sorted_bids[0]]

        print("[INFO-BiddingProcessor] Castigatorul este:")
        print("\t{} - pret licitat: {}".format(sorted_bids[0], winner))

        # se trimite rezultatul licitatiei pentru ca entitatile Bidder sa il preia din topicul corespunzator
        self.producer.write("winner:{}".format(sorted_bids[0]))
        self.producer.close()

    def run(self):
        self.get_processed_bids()


if __name__ == '__main__':
    bidding_processor = BiddingProcessor()
    bidding_processor.run()
