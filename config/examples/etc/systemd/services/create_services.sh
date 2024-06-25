#!/bin/bash

systemctl stop mount-hdd.service
systemctl stop mount-smb.service
systemctl stop kino-down.service
systemctl stop kino-pull.service
systemctl stop kino.service

systemctl disable mount-hdd.service
systemctl disable mount-smb.service
systemctl disable kino-down.service
systemctl disable kino-pull.service
systemctl disable kino.service

cp -f *.service /etc/systemd/system

systemctl enable mount-hdd.service
systemctl enable mount-smb.service
systemctl enable kino-down.service
systemctl enable kino-pull.service
systemctl enable kino.service
