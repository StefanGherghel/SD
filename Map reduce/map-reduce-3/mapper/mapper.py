import string

import requests
from bs4 import BeautifulSoup


def main():
    urls = [
        "https://ro.wikipedia.org/wiki/Pandemia_de_COVID-19_%C3%AEn_Rom%C3%A2nia",
        "https://en.wikipedia.org/wiki/Microservices",
        "http://mike.tuiasi.ro/",
        "https://microservices.io/patterns/microservices.html",
        "https://dzone.com/articles/design-patterns-for-microservices-1",
        "https://docs.python.org/3.3/library/stdtypes.html?highlight=maketrans#str.maketrans"
    ]
    for url in urls:
        page_content = requests.get(url).content
        soup = BeautifulSoup(page_content, "lxml")
        for tag in soup.find_all("div"):
            table = str.maketrans("", "", string.punctuation)
            words = list(map(lambda s: s.strip().translate(table), tag.text.split(" ")))
            for word in words:
                if word not in string.punctuation:
                    print("{} {} 1".format(word, url))


if __name__ == "__main__":
    main()
