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

  docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-middleware:$V_TAG -f Dockerfile-middleware .
  docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-bot:$V_TAG -f Dockerfile-bot .
  docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-file:$V_TAG -f Dockerfile-file .
  docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-media:$V_TAG -f Dockerfile-minidlna .
  docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-text:$V_TAG -f Dockerfile-text .
  docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-torrent:$V_TAG -f Dockerfile-torrent .

  if [[ $V_LATEST == true ]]; then
    echo "Start build 'latest' tag"
    docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-middleware:latest -f Dockerfile-middleware .
    docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-bot:latest -f Dockerfile-bot .
    docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-fil:latest -f Dockerfile-file .
    docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-media:latest -f Dockerfile-minidlna .
    docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-text:latest -f Dockerfile-text .
    docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-torrent:latest -f Dockerfile-torrent .
  fi

  cd ../../..
  echo "Finish build"
fi

if [[ $V_PUSH == true ]]; then
  echo "Start push to Docker Hub"
  docker push halushko/cinema-middleware:$V_TAG
  docker push halushko/cinema-bot:$V_TAG
  docker push halushko/cinema-file:$V_TAG
  docker push halushko/cinema-media:$V_TAG
  docker push halushko/cinema-text:$V_TAG
  docker push halushko/cinema-torrent:$V_TAG

  if [[ $V_LATEST == true ]]; then
    docker push halushko/cinema-middleware:latest
    docker push halushko/cinema-bot:latest
    docker push halushko/cinema-file:latest
    docker push halushko/cinema-media:latest
    docker push halushko/cinema-text:latest
    docker push halushko/cinema-torrent:latest
  fi
fi

cd ..
rm -rf tmp
echo "tmp directory removed"

echo "Build finished"
