#!/bin/bash

mkdir -p /home/app/transmission_config
if [ -e /home/app/transmission_config/.settings.lock ]; then
  echo "The transmission service was already configured by this service"
else
  cp /home/app/settings.json /home/app/transmission_config/settings.json
  echo "The transmission service configured by this service"
  echo "The transmission service configured by this service" > /home/app/transmission_config/.settings.lock
fi

java -Dlog4j.configuration=file:/home/app/log4j-${LOGS_LEVEL}.properties -jar /home/app/app.jar