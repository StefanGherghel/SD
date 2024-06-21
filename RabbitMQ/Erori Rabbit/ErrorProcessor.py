import time

from RabbitMqConnection import RabbitMqConsumer


class ErrorProcessor:

    def __init__(self):
        self.consumer = RabbitMqConsumer(rabbit_queue="errorprocessor.queue")
        self.reporting_file = None

    def catch_errors(self):
        print("[INFO-ErrorProcessor] Initializarea microserviciului de statistica.")
        time.sleep(5)
        self.reporting_file = open("erros.txt", "w")
        try:
            self.consumer.receive_message()
        except Exception as e:
            self.reporting_file.write(str(e) + "\n")
        while len(self.consumer.list_msg) != 0:
            msg = self.consumer.list_msg.pop()
            self.reporting_file.write(msg + "\n")
            try:
                self.consumer.receive_message()
            except Exception as e:
                self.reporting_file.write(str(e) + "\n")
        self.reporting_file.close()

    def run(self):
        self.catch_errors()
        print("[INFO-ErrorProcessor] Incheierea activitatii.")


if __name__ == "__main__":
    processor = ErrorProcessor()
    processor.run()
