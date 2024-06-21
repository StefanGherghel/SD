from RabbitMqConnection import RabbitMqConsumer, RabbitMqProducer


class Auctioneer:
    def __init__(self):
        self.producer = RabbitMqProducer(exchange="bidder.direct",
                                         routing_key="messageprocessor.routingkey")
        self.consumer = RabbitMqConsumer(rabbit_queue="bidder.queue")
        

    def receive_bids(self):
        # se preiau toate ofertele din topicul bids_topic
        print("[Auctioneer] Astept oferte pentru licitatie...")
        try:
            self.consumer.receive_message()
        except Exception as e:
            print("except:{}".format(e))
        while len(self.consumer.list_msg) != 0:
            # mesajul e de tip identity:NUME_amount:SUMA
            msg = self.consumer.list_msg.pop()
            msg = msg.split("_")
            identity = msg[0].split(":")[1]
            amount = int(msg[1].split(":")[1])
            print("[Auctioneeer] {} a licitat {}".format(identity, amount))
            rabbit_msg = "_".join(msg)
            # trimit mesaje catre MessageProcessor
            self.producer.send_message(rabbit_msg)
            try:
                self.consumer.receive_message()
            except Exception as e:
                print("except:{}".format(e))

        # bids_consumer genereaza exceptia StopIteration atunci cand se atinge timeout-ul de 10 secunde
        # => licitatia se incheie dupa ce timp de 10 secunde nu s-a primit nicio oferta
        self.finish_auction()

    def finish_auction(self):
        print("[Auctioneer] Licitatia s-a incheiat!")
        # notificam MessageProcessor ca poate incepe procesarea mesajelor
        auction_finished_message = "incheiat"
        self.producer.send_message(auction_finished_message)

    def run(self):
        try:
            self.receive_bids()
        except Exception as e:
            print("except:{}".format(e))


if __name__ == '__main__':
    auctioneer = Auctioneer()
    auctioneer.run()
