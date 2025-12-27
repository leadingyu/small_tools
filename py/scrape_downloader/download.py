import requests, os

os.makedirs("images", exist_ok=True)

with open("urls.txt") as f:
    for i, url in enumerate(f):
        r = requests.get(url.strip(), timeout=10)
        with open(f"images/{i}.jpg", "wb") as img:
            img.write(r.content)
