import time

from RabbitMqConnection import RabbitMqConsumer, RabbitMqProducer


class MessageProcessor:
    def __init__(self):
        self.producer = RabbitMqProducer(exchange="testbidder.direct",
                                         routing_key="biddingprocessor.routingkey")
        self.consumer = RabbitMqConsumer(rabbit_queue="messageprocessor.queue")
        self.producer_err = RabbitMqProducer(exchange="testbidder.direct",
                                             routing_key="errorprocessor.key")

        # ofertele se pun in dictionar, sub forma de perechi <IDENTITATE_OFERTANT, MESAJ_OFERTA>
        self.bids = dict()

    def get_and_process_messages(self):
        # se asteapta notificarea de la Auctioneer pentru incheierea licitatiei
        print("[INFO-MessageProcessor] Astept notificare de la toate entitatile Auctioneer pentru incheierea " +
              "licitatiei...")
        time.sleep(2)
        try:
            self.consumer.receive_message()
        except Exception as e:
            self.producer_err.send_message("except:{}".format(e))
        while len(self.consumer.list_msg) != 0:
            msg = str(self.consumer.list_msg.pop())
            if msg == "incheiat":
                break
            split_message = msg.split("_")
            self.bids[split_message[0].split(":")[1]] = split_message[1].split(":")[1]

            try:
                self.consumer.receive_message()
            except Exception as e:
                self.producer_err.send_message("except:{}".format(e))

        # sortare dupa valoarea bid-ului
        sorted_bids = {k: v for k, v in sorted(self.bids.items(), key=lambda item: item[1])}

        self.finish_processing(sorted_bids)

    def finish_processing(self, sorted_bids: dict):
        print("[INFO-MessageProcessor] Procesarea s-a incheiat! Trimit urmatoarele oferte:")
        for idBidder in sorted_bids:
            identity = idBidder
            bid_amount = sorted_bids[idBidder]
            print("[INFO-MessageProcessor] {} a licitat {}.".format(identity, bid_amount))

            rabbit_msg = "id:{}_amount:{}".format(identity, bid_amount)
            # se stocheaza mesajele ordonate dupa timestamp si fara duplicate intr-un topic separat
            self.producer.send_message(rabbit_msg)
        self.producer.send_message("incheiat")

    def run(self):
        self.get_and_process_messages()


if __name__ == '__main__':
    message_processor = MessageProcessor()
    message_processor.run()
