#!/bin/bash

systemctl stop bot.service
systemctl stop bot_debug_down.service
systemctl stop bot_down.service
systemctl stop bot_pull.service
systemctl stop media_server.service
systemctl stop media_server_down.service
systemctl stop media_server_pull.service
systemctl stop message_broker.service
systemctl stop message_broker_down.service
systemctl stop message_broker_pull.service
systemctl stop torrent.service
systemctl stop torrent_down.service
systemctl stop torrent_pull.service
systemctl stop torrent-hdd.service
systemctl stop torrent-hdd-down.service
systemctl stop torrent-hdd-pull.service

systemctl disable bot.service
systemctl disable bot_debug_down.service
systemctl disable bot_down.service
systemctl disable bot_pull.service
systemctl disable media_server.service
systemctl disable media_server_down.service
systemctl disable media_server_pull.service
systemctl disable message_broker.service
systemctl disable message_broker_down.service
systemctl disable message_broker_pull.service
systemctl disable torrent.service
systemctl disable torrent_down.service
systemctl disable torrent_pull.service
systemctl disable torrent-hdd.service
systemctl disable torrent-hdd-down.service
systemctl disable torrent-hdd-pull.service


systemctl enable bot.service
systemctl enable bot_debug_down.service
systemctl enable bot_down.service
systemctl enable bot_pull.service
systemctl enable media_server.service
systemctl enable media_server_down.service
systemctl enable media_server_pull.service
systemctl enable message_broker.service
systemctl enable message_broker_down.service
systemctl enable message_broker_pull.service
systemctl enable torrent.service
systemctl enable torrent_down.service
systemctl enable torrent_pull.service
systemctl enable torrent-hdd.service
systemctl enable torrent-hdd-down.service
systemctl enable torrent-hdd-pull.service