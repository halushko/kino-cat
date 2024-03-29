#!/bin/bash

systemctl stop bot.service
systemctl stop bot-debug-down.service
systemctl stop bot-down.service
systemctl stop bot-pull.service
systemctl stop media-server.service
systemctl stop media-server-down.service
systemctl stop media-server-pull.service
systemctl stop message-broker.service
systemctl stop message-broker-down.service
systemctl stop message-broker-pull.service
systemctl stop torrent.service
systemctl stop torrent-down.service
systemctl stop torrent-pull.service
systemctl stop torrent-hdd.service
systemctl stop torrent-hdd-down.service
systemctl stop torrent-hdd-pull.service
systemctl stop mount-hdd.service
systemctl stop mount-smb.service

systemctl disable bot.service
systemctl disable bot-debug-down.service
systemctl disable bot-down.service
systemctl disable bot-pull.service
systemctl disable media-server.service
systemctl disable media-server-down.service
systemctl disable media-server-pull.service
systemctl disable message-broker.service
systemctl disable message-broker-down.service
systemctl disable message-broker-pull.service
systemctl disable torrent.service
systemctl disable torrent-down.service
systemctl disable torrent-pull.service
systemctl disable torrent-hdd.service
systemctl disable torrent-hdd-down.service
systemctl disable torrent-hdd-pull.service
systemctl disable mount-hdd.service
systemctl disable mount-smb.service

cp -f *.service /etc/systemd/system

systemctl enable bot.service
systemctl enable bot-debug-down.service
systemctl enable bot-down.service
systemctl enable bot-pull.service
systemctl enable media-server.service
systemctl enable media-server-down.service
systemctl enable media-server-pull.service
systemctl enable message-broker.service
systemctl enable message-broker-down.service
systemctl enable message-broker-pull.service
systemctl enable torrent.service
systemctl enable torrent-down.service
systemctl enable torrent-pull.service
systemctl enable torrent-hdd.service
systemctl enable torrent-hdd-down.service
systemctl enable torrent-hdd-pull.service
systemctl enable mount-hdd.service
systemctl enable mount-smb.service
