#!/bin/bash

echo 'rc_provide="loopback net"' >> /etc/rc.conf
rc-service transmission-daemon stop
rc-service transmission-daemon start
java -Dlog4j.configuration=file:/home/app/log4j-${LOGS_LEVEL}.properties -jar /home/app/app.jar