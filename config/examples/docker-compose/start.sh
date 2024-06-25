#!/bin/bash

start_broker() {
    /usr/bin/docker-compose -f /home/dima/media/docker-compose-broker.yml up -d
    while ! curl -s http://localhost:15672 > /dev/null; do
        echo "Waiting for RabbitMQ to be available..."
        sleep 5
    done
}

start_media() {
    /usr/bin/docker-compose -f /home/dima/media/docker-compose-media.yml up -d
}

start_bot() {
    /usr/bin/docker-compose -f /home/dima/media/docker-compose-bot.yml up -d
    sudo chmod 777 -R /home/dima/media/workdir/config/transmission*
}

start_torrent() {
    start_bot
    /usr/bin/docker-compose -f /home/dima/media/docker-compose-torrent.yml up -d
}

start_torrent_hdd() {
    start_bot
    /usr/bin/docker-compose -f /home/dima/media/docker-compose-torrent-hdd.yml up -d
}

start_torrent_low() {
    start_bot
    /usr/bin/docker-compose -f /home/dima/media/docker-compose-torrent-low.yml up -d
}

keys_specified=false

while (( "$#" )); do
    case "$1" in
        -r) start_broker; keys_specified=true ;;
        -m) start_media; keys_specified=true ;;
        -b) start_bot; keys_specified=true ;;
        -t) start_torrent; start_torrent_hdd; start_torrent_low; keys_specified=true ;;
        -tt) start_torrent; keys_specified=true ;;
        -th) start_torrent_hdd; keys_specified=true ;;
        -tl) start_torrent_low; keys_specified=true ;;
        *) echo "Unexpected key: $1" >&2; exit 1 ;;
    esac
    shift
done

if ! $keys_specified; then
    start_broker
    start_bot
    start_media
    start_torrent
    start_torrent_hdd
    start_torrent_low
fi

echo "Services have been started."
