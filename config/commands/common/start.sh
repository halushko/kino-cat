#!/bin/bash
function usage() {
    cat <<USAGE

    Run media server
    Usage: $0 [--full] [-m --mount] [-c --clear] [-h --help]

    Options:
        --full                   :   stop + clear containers + clear images + unmount + build + mount + run
        -m, --mount              :   mount USB
        -rmc, --clear_containers :   prune docker containers
        -rmi, --clear_images     :   prune docker images
        -h, --help               :   help
USAGE
    exit 1
}

if [ $# -eq 0 ]; then
    usage
    exit 1
fi

V_MOUNT=false
V_CLEAR_CONTAINERS=false
V_CLEAR_IMAGES=false

while [ "$1" != "" ]; do
    case $1 in
    -h | --help)
        usage
        exit 1
        ;;
    -rmc | --clear_containers)
        V_CLEAR_CONTAINERS=true
        ;;
    -rmi | --clear_images)
        V_CLEAR_IMAGES=true
        ;;
    -m | --mount)
        V_MOUNT=true
        ;;
    --full)
        V_CLEAR_IMAGES=true
        V_MOUNT=true
        ;;
    *)
        usage
        exit 1
        ;;
    esac
    shift
done

docker-compose down

if [[ $V_CLEAR_IMAGES == true ]]; then
    docker system prune -fa
elif [[ $V_CLEAR_CONTAINERS == true ]]; then
    docker container prune -f
fi

if [[ $V_MOUNT == true ]]; then
    umount -f ./workdir/
    mount -t ntfs /dev/sda1 ./workdir/
#    UUID=779DB7ED03DED64A /home/dima/workdir ntfs defaults 0 0
fi

docker-compose up
exit 1
