#!/bin/bash

TORRENT_IP_ARRAY=$(echo $TORRENT_IP | jq -r '.[]')

for ITEM in $TORRENT_IP_ARRAY
do
  NAME=$(echo $ITEM | jq -r '.name // empty')

  if [ -n "$NAME" ]; then
    CONFIG_PATH="/home/app/transmission_config_$NAME"
  else
    CONFIG_PATH="/home/app/transmission_config"
  fi

  mkdir -p $CONFIG_PATH

  if [ -e $CONFIG_PATH/.settings.lock ]; then
    echo "The transmission service was already configured by this service"
  else
    cp /home/app/settings.json $CONFIG_PATH/settings.json
    echo "The transmission service configured by this service"
    echo "The transmission service configured by this service" > $CONFIG_PATH/.settings.lock
  fi
done

java -Dlog4j.configuration=file:/home/app/log4j-${LOGS_LEVEL}.properties -jar /home/app/app.jar