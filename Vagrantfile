Vagrant.configure("2") do |config|
  config.vm.box = "ubuntu/impish64"
#  config.vm.box = "ailispaw/barge"
#  config.vm.box = "ailispaw/docker-root"
  config.vm.synced_folder "./", "/home/vagrant/media_cat"
  config.vm.synced_folder "./volumes", "/home/vagrant/volumes"
  config.vm.network "public_network", bridge: "#$default_network_interface"
  config.vm.network "forwarded_port", guest: 15672, host: 15672, host_ip: "127.0.0.1"
  config.vm.network "forwarded_port", guest: 5672, host: 5672, host_ip: "127.0.0.1"
  config.vm.network "forwarded_port", guest: 5672, host: 2376
  config.vm.network "forwarded_port", guest: 80, host: 80
  config.vm.network "forwarded_port", guest: 8080, host: 8080
  

  config.vm.provider "virtualbox" do |vb|
    vb.memory = "1024"
  end

  config.vm.provision "shell", inline: <<-SHELL
    apt-get update
    apt-get -y upgrade
    apt-get -y install docker-compose git #docker docker.io docker-compose
    chmod -R 777 /home/vagrant
    #cd /home/vagrant/ && git clone https://github.com/halushko/kino-cat.git
  SHELL
end
