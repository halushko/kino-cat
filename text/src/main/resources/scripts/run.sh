#!/bin/bash

java -Dlog4j.configuration=file:/home/app/log4j-${LOGS_LEVEL}.properties -jar /home/app/app.jar