#!/bin/bash
function usage() {
  cat <<USAGE

    Build docker images
    Usage: $0 [--full] [-b] [-d] [-p] [-t] [--branch] [--release]

    Options:
        --full        :   download dockerfiles + build images + push images to dockerhub
        -b            :   build Dockerfiles
        -d            :   use this flag to download Dockerfiles from github repo. Without this flag the Dockerfiles
                          from current directory will be used. No need to use with also see flag [--branch]
        --branch      :   see flag [-d]. This flag used to specify which branch of github is need to be used. By
                          default the "master" branch is used
        -p            :   pull images to DockerHub after completion
        -t            :   tag (name) of images that will be built
        -m            :   crate manifest for arm46 and amd64 architectures
        --release     :   build images with tag [-t] and also build with tag "latest"
USAGE
  exit 1
}

V_BUILD=false
V_DOWNLOAD=false
V_PUSH=false
V_TAG="latest"
V_BRANCH="master"
V_LATEST=true
V_RELEASE=false
V_MANIFEST=false

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
  -m)
    V_MANIFEST=true
    ;;
  --branch)
    V_DOWNLOAD=true
    shift
    V_BRANCH=$1
    ;;
  --release)
    V_LATEST=true
    ;;
  --full)
    V_BUILD=true
    V_DOWNLOAD=true
    V_PUSH=true
    ;;
  *)
    usage
    echo "$1 not found"
    exit 1
    ;;
  esac
  shift
done

echo "Build started"

rm -rf tmp
mkdir tmp
cd tmp || exit 0
echo "tmp directory created"

if [[ $V_LATEST == $V_TAG ]]; then
  V_LATEST = false
fi
echo "The TAG is $V_TAG"

if [[ $V_LATEST == true ]]; then
  echo "The latest TAG will be built too"
fi

if [[ $V_DOWNLOAD == true ]]; then
  echo "Download Dockerfiles from repo"
  git init kino-cat
  cd kino-cat || exit 0
  git remote add origin https://github.com/halushko/kino-cat.git
  git config core.sparseCheckout true
  echo "config/dockerfiles/" >>.git/info/sparse-checkout
  git pull origin "$V_BRANCH"
  cd ..
else
  echo "Copy Dockerfiles from current directory"
  mkdir -p ./kino-cat/config/dockerfiles
  cp ../Dockerfile-* ./kino-cat/config/dockerfiles
fi

if [[ $V_BUILD == true ]]; then
  echo "Start build tag = $V_TAG"
  cd ./kino-cat/config/dockerfiles || exit 0

#  docker build --build-arg BRANCH="$V_BRANCH" --build-arg ARCH=arm64v8/ --platform linux/arm64 -t halushko/cinema-middleware:tmp- $V_TAG-arm64 -f Dockerfile-middleware .
  #docker build --build-arg BRANCH="$V_BRANCH" --build-arg ARCH=amd64/ --platform=linux/amd64 -t halushko/cinema-middleware:tmp- $V_TAG-amd64 -f Dockerfile-middleware .
#  docker build --build-arg BRANCH="$V_BRANCH" --build-arg ARCH=arm64v8/ --platform=linux/arm64 -t halushko/cinema-middleware:$V_TAG-arm64 -f Dockerfile-middleware .
  #docker build --build-arg BRANCH="$V_BRANCH" --build-arg ARCH=amd64/ --platform=linux/amd64 -t halushko/cinema-middleware:$V_TAG-amd64 -f Dockerfile-middleware .

#  docker push halushko/cinema-middleware:tmp-$V_TAG-arm64
  #docker push halushko/cinema-middleware:tmp-$V_TAG-amd64
#  docker manifest create halushko/cinema-middleware:tmp -a halushko/cinema-middleware:$V_TAG-amd64 -a halushko/cinema-middleware:$V_TAG-arm64
#  docker manifest push halushko/cinema-middleware:tmp

  docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-bot:$V_TAG-arm64 --build-arg ARCH=arm64v8/ -f Dockerfile-bot .
  #docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-bot:$V_TAG-amd64 --build-arg ARCH=amd64/ -f Dockerfile-bot .
  docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-file:$V_TAG-arm64 --build-arg ARCH=arm64v8/ -f Dockerfile-file .
  #docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-file:$V_TAG-amd64 --build-arg ARCH=amd64/ -f Dockerfile-file .
  docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-media:$V_TAG-arm64 --build-arg ARCH=arm64v8/ -f Dockerfile-minidlna .
  #docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-media:$V_TAG-amd64 --build-arg ARCH=amd64/ -f Dockerfile-minidlna .
  docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-text:$V_TAG-arm64 --build-arg ARCH=arm64v8/ -f Dockerfile-text .
  #docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-text:$V_TAG-amd64 --build-arg ARCH=amd64/ -f Dockerfile-text .
  docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-torrent:$V_TAG-arm64 --build-arg ARCH=arm64v8/ -f Dockerfile-torrent .
  #docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-torrent:$V_TAG-amd64 --build-arg ARCH=amd64/ -f Dockerfile-torrent .
  if [[ $V_LATEST == true ]]; then
    echo "Start build 'latest' tag"
#    docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-middleware:latest-arm64 --build-arg ARCH=arm64v8/ -f Dockerfile-middleware .
    #docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-middleware:latest-amd64 --build-arg ARCH=amd64/ -f Dockerfile-middleware .
    docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-bot:latest-arm64 --build-arg ARCH=arm64v8/ -f Dockerfile-bot .
    #docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-bot:latest-amd64 --build-arg ARCH=amd64/ -f Dockerfile-bot .
    docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-file:latest-arm64 --build-arg ARCH=arm64v8/ -f Dockerfile-file .
    #docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-file:latest-amd64 --build-arg ARCH=amd64/ -f Dockerfile-file .
    docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-media:latest-arm64 --build-arg ARCH=arm64v8/ -f Dockerfile-minidlna .
    #docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-media:latest-amd64 --build-arg ARCH=amd64/ -f Dockerfile-minidlna .
    docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-text:latest-arm64 --build-arg ARCH=arm64v8/ -f Dockerfile-text .
    #docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-text:latest-amd64 --build-arg ARCH=amd64/ -f Dockerfile-text .
    docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-torrent:latest-arm64 --build-arg ARCH=arm64v8/ -f Dockerfile-torrent .
    #docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-torrent:latest-amd64 --build-arg ARCH=amd64/ -f Dockerfile-torrent .
  fi

  cd ../../..
  echo "Finish build"
fi

if [[ $V_PUSH == true ]]; then
  echo "Start push to Docker Hub"
#  docker push halushko/cinema-middleware:$V_TAG-arm64
  #docker push halushko/cinema-middleware:$V_TAG-amd64
#  docker manifest create halushko/cinema-middleware: $V_TAG -a halushko/cinema-middleware:$V_TAG-amd64 -a halushko/cinema-middleware:$V_TAG-arm64
#  docker manifest push halushko/cinema-middleware: $V_TAG
  docker push halushko/cinema-bot:$V_TAG-arm64
  #docker push halushko/cinema-bot:$V_TAG-amd64
  docker manifest create halushko/cinema-bot: $V_TAG -a halushko/cinema-bot:$V_TAG-amd64 -a halushko/cinema-bot:$V_TAG-arm64
  docker manifest push halushko/cinema-bot: $V_TAG
  docker push halushko/cinema-file:$V_TAG-arm64
  #docker push halushko/cinema-file:$V_TAG-amd64
  docker manifest create halushko/cinema-file: $V_TAG -a halushko/cinema-file:$V_TAG-amd64 -a halushko/cinema-file:$V_TAG-arm64
  docker manifest push halushko/cinema-file: $V_TAG
  docker push halushko/cinema-media:$V_TAG-arm64
  #docker push halushko/cinema-media:$V_TAG-amd64
  docker manifest create halushko/cinema-media: $V_TAG -a halushko/cinema-media:$V_TAG-amd64 -a halushko/cinema-media:$V_TAG-arm64
  docker manifest push halushko/cinema-media: $V_TAG
  docker push halushko/cinema-text:$V_TAG-arm64
  #docker push halushko/cinema-text:$V_TAG-amd64
  docker manifest create halushko/cinema-text: $V_TAG -a halushko/cinema-text:$V_TAG-amd64 -a halushko/cinema-text:$V_TAG-arm64
  docker manifest push halushko/cinema-text: $V_TAG
  docker push halushko/cinema-torrent:$V_TAG-arm64
  #docker push halushko/cinema-torrent:$V_TAG-amd64
  docker manifest create halushko/cinema-torrent: $V_TAG -a halushko/cinema-torrent:$V_TAG-amd64 -a halushko/cinema-torrent:$V_TAG-arm64
  docker manifest push halushko/cinema-torrent: $V_TAG

  if [[ $V_LATEST == true ]]; then
    echo "Start push latest to Docker Hub"
#    docker push halushko/cinema-middleware:latest-arm64
    #docker push halushko/cinema-middleware:latest-amd64
#    docker manifest create halushko/cinema-middleware:latest -a halushko/cinema-middleware:latest-amd64 -a halushko/cinema-middleware:latest-arm64
#    docker manifest push halushko/cinema-middleware:latest
    docker push halushko/cinema-bot:latest-arm64
    #docker push halushko/cinema-bot:latest-amd64
    docker manifest create halushko/cinema-bot:latest -a halushko/cinema-bot:latest-amd64 -a halushko/cinema-bot:latest-arm64
    docker manifest push halushko/cinema-bot:latest
    docker push halushko/cinema-file:latest-arm64
    #docker push halushko/cinema-file:latest-amd64
    docker manifest create halushko/cinema-file:latest -a halushko/cinema-file:latest-amd64 -a halushko/cinema-file:latest-arm64
    docker manifest push halushko/cinema-file:latest
    docker push halushko/cinema-media:latest-arm64
    #docker push halushko/cinema-media:latest-amd64
    docker manifest create halushko/cinema-media:latest -a halushko/cinema-media:latest-amd64 -a halushko/cinema-media:latest-arm64
    docker manifest push halushko/cinema-media:latest
    docker push halushko/cinema-text:latest-arm64
    #docker push halushko/cinema-text:latest-amd64
    docker manifest create halushko/cinema-text:latest -a halushko/cinema-text:latest-amd64 -a halushko/cinema-text:latest-arm64
    docker manifest push halushko/cinema-text:latest
    docker push halushko/cinema-torrent:latest-arm64
    #docker push halushko/cinema-torrent:latest-amd64
    docker manifest create halushko/cinema-torrent:latest -a halushko/cinema-torrent:latest-amd64 -a halushko/cinema-torrent:latest-arm64
    docker manifest push halushko/cinema-torrent:latest
  fi
fi

cd ..
rm -rf tmp
echo "tmp directory removed"

echo "Build finished"
