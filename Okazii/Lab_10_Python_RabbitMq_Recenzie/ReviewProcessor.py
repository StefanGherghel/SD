import time

from RabbitMqConnection import RabbitMqConsumer


class ReviewProcessor:
    def __init__(self):
        self.consumer = RabbitMqConsumer(rabbit_queue="review.queue")
        self.file = None  # fisierul in care scriem erori

    def reviews(self):
        print("[ReviewProcessor] Initializare microserviciu de review.")
        # time.sleep()
        self.file = open("review.txt", "w")
        try:
            self.consumer.receive_message()
        except Exception as e:
            print(str(e) + "\n")
        while len(self.consumer.list_msg) != 0:
            msg = self.consumer.list_msg.pop()
            self.file.write(msg + "\n")
            try:
                self.consumer.receive_message()
            except Exception as e:
                print(str(e) + "\n")
        self.file.close()

    def run(self):
        self.reviews()
        print("[ReviewProcessor] Incheierea activitatii.")


if __name__ == "__main__":
    processor = ReviewProcessor()
    processor.run()
