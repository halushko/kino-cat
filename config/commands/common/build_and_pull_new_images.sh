#!/bin/bash
function usage() {
    cat <<USAGE

    Run media server
    Usage: $0 [--full] [-b] [-d] [-p] [--not_pi] [-t] [--branch]

    Options:
        --full                   :   download dockerfiles + build images + push images to dockerhub + execute for RaspberryPi
USAGE
    exit 1
}

V_BUILD=false
V_DOWNLOAD=false
V_PUSH=false
V_PI=true
V_TAG="trash"
V_BRANCH="master"

while [ "$1" != "" ]; do
    case $1 in
    -h | --help)
        usage
        exit 1
        ;;
    -b)
        V_BUILD=true
        ;;
    -d)
        V_DOWNLOAD=true
        ;;
    -p)
        V_PUSH=true
        ;;
    -t)
        shift
        V_TAG=$1
        ;;
    --branch)
        shift
        V_BRANCH=$1
        ;;
    --not_pi)
        V_PI=false
        ;;
    --full)
        V_BUILD=true
        V_DOWNLOAD=true
        V_PUSH=true
        ;;
    *)
        usage
        exit 1
        ;;
    esac
    shift
done

mkdir tmp
cd tmp

if [[ $V_DOWNLOAD == true ]]; then
    git init kino-cat && \
    cd kino-cat && \
    git remote add origin https://github.com/halushko/kino-cat.git && \
    git config core.sparseCheckout true && \
    echo "config/dockerfiles/" >> .git/info/sparse-checkout && \
    git pull origin $BRANCH
else
    mkdir -p ./config/dockerfiles
    cp ../Dockerfile-* ./config/dockerfiles
fi

if [[ $V_PI == true ]]; then
    V_TAG="$V_TAG-pi"
fi

if [[ $V_BUILD == true ]]; then
    cd ./config/dockerfiles
    docker build --build-arg BRANCH=$BRANCH -t halushko/cinema-bot:$V_TAG -f Dockerfile-bot
    docker build --build-arg BRANCH=$BRANCH -t halushko/cinema-file:$V_TAG -f Dockerfile-file
    docker build --build-arg BRANCH=$BRANCH -t halushko/cinema-media:$V_TAG -f Dockerfile-minidlna
    docker build --build-arg BRANCH=$BRANCH -t halushko/cinema-text:$V_TAG -f Dockerfile-text
    docker build --build-arg BRANCH=$BRANCH -t halushko/cinema-torrent:$V_TAG -f Dockerfile-torrent
    cd ../..
fi

if [[ $V_PUSH == true ]]; then
    docker push halushko/cinema-bot:$V_TAG
    docker push halushko/cinema-file:$V_TAG
    docker push halushko/cinema-media:$V_TAG
    docker push halushko/cinema-text:$V_TAG
    docker push halushko/cinema-torrent:$V_TAG
fi

#cd ..
#rm -rf tmp
