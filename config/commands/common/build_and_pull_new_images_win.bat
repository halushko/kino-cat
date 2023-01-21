docker build -t halushko/cinema-bot:0.3 -f Dockerfile-bot .
docker build -t halushko/cinema-file:0.3 -f Dockerfile-file .
docker build -t halushko/cinema-media:0.3 -f Dockerfile-minidlna .
docker build -t halushko/cinema-text:0.3 -f Dockerfile-text .
docker build -t halushko/cinema-torrent:0.3 -f Dockerfile-torrent .

docker build -t halushko/cinema-bot:LATEST -f Dockerfile-bot .
docker build -t halushko/cinema-file:LATEST -f Dockerfile-file .
docker build -t halushko/cinema-media:LATEST -f Dockerfile-minidlna .
docker build -t halushko/cinema-text:LATEST -f Dockerfile-text .
docker build -t halushko/cinema-torrent:LATEST -f Dockerfile-torrent .

docker push halushko/cinema-bot:0.3
docker push halushko/cinema-file:0.3
docker push halushko/cinema-media:0.3
docker push halushko/cinema-text:0.3
docker push halushko/cinema-torrent:0.3

docker push halushko/cinema-bot:LATEST
docker push halushko/cinema-file:LATEST
docker push halushko/cinema-media:LATEST
docker push halushko/cinema-text:LATEST
docker push halushko/cinema-torrent:LATEST
