# home_cinema

To run the home cinema please use https://github.com/halushko/kino-cat/blob/master/docker-compose.yml.
The images are located in https://hub.docker.com/repositories/halushko repositories

!! Do not forget to set BOT_TOKEN, BOT_NAME.

Open issues:
1. There is aa bug with "network_mode: host" in Docker Desktop (Windows and MacOS), so that your media server will never be visible. The resolution is - run this project on Linux VM with Bridge adapter (do not use NAT)

HOWTO install (Linux)
1. Update system: 
```
sudo apt-get update
sudo apt-get -y -f upgrade
```
2. Install Docker and docker-compose:
```
sudo apt-get -f -y install docker docker.io docker-compose
```
3. Copy [docker-compose.yml](https://github.com/halushko/kino-cat/blob/master/config/dockerfiles/docker-compose.yml) file to the server in the empty directory
4. Run program:
```
sudo docker-compose up
```
5. If you need, you can add to autorun
  - Create a new file named docker-compose.service in /etc/systemd/system/ directory:
```
sudo vi /etc/systemd/system/docker-compose.service
```
  - Add the following contents to the file:
```
[Unit]
Description=Docker Compose Application Service
Requires=docker.service
After=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=/path/to/docker-compose/directory
ExecStart=/usr/local/bin/docker-compose up -d
ExecStop=/usr/local/bin/docker-compose down
TimeoutStartSec=0

[Install]
WantedBy=default.target

```
Make sure to replace /path/to/docker-compose/directory with the path to your directory containing the docker-compose.yml file.
  - Save and close the file.
  - Reload the systemd daemon to pick up the new service file. Enable the service to start on boot and tart the service:
```
sudo systemctl daemon-reload
sudo systemctl enable docker-compose.service
sudo systemctl start docker-compose.service
```
  - Now, docker-compose.up will be executed automatically on startup. You can check the status of the service by running:
```
sudo systemctl status docker-compose.service
```
