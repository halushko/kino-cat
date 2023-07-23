#!/bin/bash
apt-get update
apt-get upgrade -f -y
apt-get install -f -y docker docker.io docker-compose vim screen