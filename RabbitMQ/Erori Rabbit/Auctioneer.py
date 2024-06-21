from RabbitMqConnection import RabbitMqConsumer, RabbitMqProducer


class Auctioneer:
    def __init__(self):
        self.producer = RabbitMqProducer(exchange="testbidder.direct",
                                         routing_key="messageprocessor.routingkey")
        self.producer_err = RabbitMqProducer(exchange="testbidder.direct",
                                             routing_key="errorprocessor.key")
        self.consumer = RabbitMqConsumer(rabbit_queue="bidder.queue")

    def receive_bids(self):
        print("[INFO-Auctioneer] Astept oferte pentru licitatie...")
        try:
            self.consumer.receive_message()
        except Exception as e:
            self.producer_err.send_message("except:{}".format(e))
        while len(self.consumer.list_msg) != 0:
            msg = self.consumer.list_msg.pop()
            msg = msg.split("_")
            identity = msg[0].split(":")[1]
            bid_amount = int(msg[1].split(":")[1])
            print("[INFO-Auctioneer] {} a licitat {}".format(identity, bid_amount))
            rabbit_msg = "_".join(msg)
            self.producer.send_message(rabbit_msg)
            try:
                self.consumer.receive_message()
            except Exception as e:
                self.producer_err.send_message("except:{}".format(e))

        # bids_consumer genereaza exceptia StopIteration atunci cand se atinge timeout-ul de 10 secunde
        # => licitatia se incheie dupa ce timp de 15 secunde nu s-a primit nicio oferta
        self.finish_auction()

    def finish_auction(self):
        print("[INFO-Auctioneer] Licitatia s-a incheiat!")
        # se notifica MessageProcessor ca poate incepe procesarea mesajelor
        auction_finished_message = "incheiat"
        self.producer.send_message(auction_finished_message)

    def run(self):
        try:
            self.receive_bids()
        except Exception as e:
            self.producer_err.send_message("except:{}".format(e))


if __name__ == '__main__':
    auctioneer = Auctioneer()
    auctioneer.run()
