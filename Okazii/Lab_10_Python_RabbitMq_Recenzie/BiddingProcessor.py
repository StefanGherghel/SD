import time

from RabbitMqConnection import RabbitMqConsumer, RabbitMqProducer


class BiddingProcessor:
    def __init__(self):
        self.producer = open("result.txt", "w")
        self.consumer = RabbitMqConsumer(rabbit_queue="biddingprocessor.queue")
    def get_processed_bids(self):
        # se preiau toate ofertele procesate din topicul processed_bids_topic
        print("[BiddingProcessor] Astept ofertele procesate de MessageProcessor...")
        # sa aibe timp restu microserviciilor sa-si incheie munca
        time.sleep(30)
        # ofertele se stocheaza sub forma de perechi <id:<ID>_amount:<SUMA>>
        bids = dict()
        try:
            self.consumer.receive_message()
        except Exception as e:
            print("except:{}".format(e))
        while len(self.consumer.list_msg) != 0:
            msg = self.consumer.list_msg.pop()
            if msg == "incheiat":
                break
            msg = msg.split("_")
            bids[msg[0].split(":")[1]] = msg[1].split(":")[1]
            try:
                self.consumer.receive_message()
            except Exception as e:
                print("except:{}".format(e))

        self.decide_auction_winner(bids)

    def decide_auction_winner(self, bids):
        print("[BiddingProcessor] Procesez ofertele...")

        if len(bids) == 0:
            print("[BiddingProcessor] Nu exista nicio oferta de procesat.")
            return

        # sortare dupa oferte
        sorted_bids = sorted(bids.keys())

        # castigatorul este ofertantul care a oferit pretul cel mai mare
        winner = bids[sorted_bids[-1]]

        print("[BiddingProcessor] Castigatorul este:")
        print("\t{} - pret licitat: {}".format(winner, sorted_bids[-1]))

        # se trimite rezultatul licitatiei pentru ca entitatile Bidder sa il preia din topicul corespunzator
        self.producer.write("winner:{}".format(sorted_bids[-1]))
        self.producer.close()

    def run(self):
        self.get_processed_bids()


if __name__ == '__main__':
    bidding_processor = BiddingProcessor()
    bidding_processor.run()
