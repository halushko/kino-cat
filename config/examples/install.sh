#!/bin/bash

apt-get update
apt-get upgrade -f -y
apt-get install -f -y docker docker.io docker-compose vim screen
mkdir -p /home/dima/workdir
chomd 777 -R /home/dima/workdir
echo "UUID=779DB7ED03DED64A   /home/dima/workdir      ntfs    defaults        0       0" >> /etc/fstab