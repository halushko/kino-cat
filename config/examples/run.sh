#!/bin/bash
COMPOSE_HTTP_TIMEOUT=10000 docker-compose down
COMPOSE_HTTP_TIMEOUT=10000 docker-compose pull
COMPOSE_HTTP_TIMEOUT=10000 docker-compose up