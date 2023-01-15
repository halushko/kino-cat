# home_cinema

To run the home cinema please use https://github.com/halushko/kino-cat/blob/master/docker-compose.yml.
The images are located in https://hub.docker.com/repositories/halushko repositories

!! Do not forget to set BOT_TOKEN, BOT_NAME.

"-pi" postfix is used for arm64 architecture, for example for RasberryPi. Images without "-pi" are used for architecture amd64.

Use LATEST or LATEST-pi tags to pull the latest stable images

To build the your oun image please use dockerfiles from https://github.com/halushko/kino-cat/tree/master/config/dockerfiles.
You can use docker ARRG like BRANCH to use not master branch
You can use docker ARRG like REPO to use not https://github.com/halushko/kino-cat repository. For example if you want to fork this repo

Open issues:
1. There is aa bug with "network_mode: host" in Docker Desktop (Windows and MacOS), so that your media server will never be visible. The resolution is - run this project on Linux VM with Bridge adapter (do not use NAT)
2. There is a hardcoded values in https://github.com/halushko/kino-cat/blob/master/config/config_files/minidlna/minidlna.conf. 
"presentation_url" and "network_interface". You should use your values
