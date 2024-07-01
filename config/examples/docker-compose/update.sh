#!/bin/bash

update_broker() {
    /usr/bin/docker-compose -f /home/dima/media/docker-compose-broker.yml pull
}

update_media() {
    /usr/bin/docker-compose -f /home/dima/media/docker-compose-media.yml pull
}

update_bot() {
    /usr/bin/docker-compose -f /home/dima/media/docker-compose-bot.yml pull
}

update_torrent() {
    /usr/bin/docker-compose -f /home/dima/media/docker-compose-torrent.yml pull
}

keys_specified=false

while (( "$#" )); do
    case "$1" in
        -r) update_broker; keys_specified=true ;;
        -m) update_media; keys_specified=true ;;
        -b) update_bot; keys_specified=true ;;
        -t) update_torrent; keys_specified=true ;;
        -h) echo "Tool for kino-cat docker-containers pull:" ;
            echo "            -h   print help" ;
            echo "            -r   update broker" :
            echo "            -m   update media" ;
            echo "            -b   update bot" ;
            echo "            -t   update torrent clients"; keys_specified=true ;;
        *) echo "Unexpected key: $1" >&2; exit 1 ;;
    esac
    shift
done

if ! $keys_specified; then
    update_broker
    update_media
    update_bot
    update_torrent
fi

echo "All images files have been updated."
