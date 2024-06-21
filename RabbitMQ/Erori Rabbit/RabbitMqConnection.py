import pika
from retry import retry


class RabbitMqInterface:
    def __init__(self):
        self.config = {
            'host': '0.0.0.0',
            'port': 5678,
            'username': 'student',
            'password': 'student'
        }
        self.list_msg = []
        self.credentials = pika.PlainCredentials(self.config['username'], self.config['password'])
        self.parameters = (pika.ConnectionParameters(host=self.config['host']),
                           pika.ConnectionParameters(port=self.config['port']),
                           pika.ConnectionParameters(credentials=self.credentials))


class RabbitMqProducer(RabbitMqInterface):
    def __init__(self, exchange: str, routing_key: str):
        super().__init__()
        self.config["exchange"] = exchange
        self.config["routing_key"] = routing_key

    def send_message(self, message):
        # automatically close the connection
        with pika.BlockingConnection(self.parameters) as connection:
            # automatically close the channel
            with connection.channel() as channel:
                channel.basic_publish(exchange=self.config['exchange'],
                                      routing_key=self.config['routing_key'],
                                      body=message)


class RabbitMqConsumer(RabbitMqInterface):
    def __init__(self, rabbit_queue: str):
        super().__init__()
        self.config["queue"] = rabbit_queue
        self.connection = pika.BlockingConnection(self.parameters)
        self.channel = self.connection.channel()
        self.channel.queue_purge(self.config["queue"])

    @retry(Exception, delay=1, tries=15)
    def receive_message(self):
        try:
            result_msg = self.channel.basic_get(self.config['queue'])
            if result_msg[2]:
                self.list_msg.append(result_msg[2].decode("utf-8"))
                self.channel.basic_ack(result_msg[0].delivery_tag)
            else:
                raise Exception("nici o valoare noua in coada.")
        # Don't recover connections closed by server
        except pika.exceptions.ConnectionClosedByBroker:
            print("Connection closed by broker.")
        # Don't recover on channel errors
        except pika.exceptions.ConnectionClosedByBroker:
            print("AMQP Channel Error")
        except ValueError:
            print("Value error!")
        # Don't recover from KeyboardInterrupt
        except KeyboardInterrupt:
            print("Application closed.")
