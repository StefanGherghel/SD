from bs4 import BeautifulSoup
import requests

urls = ["https://rapidapi.com/blog/sms-websites/", "https://readspike.com/", "https://hugotunius.se/"]


blacklist = [
    '[doument]',
    'script',
    'header',
    'html',
    'meta',
    'head',
    'input',
    'style'
]


def mapper():
    for url in urls:
        output = ""
        soup = BeautifulSoup(requests.get(url).content, "html.parser").find_all(text=True)
        
        for txt in soup:
            if txt.parent.name not in blacklist:
                output += '{} '.format(txt)
        
        words = output.split()
        for word in words:
            print('%s, {%s: %s}' % (word, url, 1))

mapper()