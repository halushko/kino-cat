#!/bin/bash

apt-get update
apt-get upgrade -f -y
rpi-update
reboot now
apt install rpi-eeprom -y
sed -i 's/critical/stable/g' /etc/default/rpi-eeprom-update
rpi-eeprom-update -a
reboot now
#vcgencmd bootloader_config and set "BOOT_ORDER=0xf14"
shutdown now
#insert USB and power on the Raspberry PI
