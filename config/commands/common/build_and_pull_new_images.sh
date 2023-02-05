#!/bin/bash
function usage() {
  cat <<USAGE

    Build docker images
    Usage: $0 [--full] [-b] [-d] [-p] [--not_pi] [-t] [--branch] [--release]

    Options:
        --full        :   download dockerfiles + build images + push images to dockerhub + execute for RaspberryPi
        -b            :   build Dockerfiles
        -d            :   use this flag to download Dockerfiles from github repo. Without this flag the Dockerfiles
                          from current directory will be used. No need to use with also see flag [--branch]
        --branch      :   see flag [-d]. This flag used to specify which branch of github is need to be used. By
                          default the "master" branch is used
        -p            :   pull images to DockerHub after completion
        -t            :   tag (name) of images that will be built
        --not_pi      :   suffix PI is used for arm64 architecture. If you will use this flag then images will not
                                  have suffix "PI"
        --release     :   build images with tag [-t] and also build with tag "LATEST" or "LATEST-PI"
USAGE
  exit 1
}

V_BUILD=false
V_DOWNLOAD=false
V_PUSH=false
V_PI=true
V_TAG=""
V_BRANCH="master"
V_LATEST=false
V_PUSH_LATEST=true
V_LATEST_TAG=""

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
  --not_pi)
    V_PI=false
    ;;
  --full)
    V_BUILD=true
    V_DOWNLOAD=true
    V_PUSH=true
    ;;
  --release)
    V_LATEST=true
    ;;
  *)
    usage
    echo "$1 not found"
    exit 1
    ;;
  esac
  shift
done

rm -rf tmp
mkdir tmp
cd tmp || exit 0

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

if [[ $V_LATEST == true && $V_TAG == "" ]]; then
  V_TAG="LATEST"
  V_PUSH_LATEST=false
fi

if [[ $V_PI == true ]]; then
  V_LATEST_TAG=":LATEST-pi"
fi

if [[ $V_TAG == "" ]]; then
  V_PI=false
fi
if [[ $V_PI == true ]]; then
  V_TAG=":$V_TAG-pi"
fi

echo "The TAG is $V_TAG"

if [[ $V_BUILD == true ]]; then
  echo "Start build from branch $V_BRANCH"
  cd ./kino-cat/config/dockerfiles || exit 0

  docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-bot$V_TAG -f Dockerfile-bot .
  docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-file$V_TAG -f Dockerfile-file .
  docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-media$V_TAG -f Dockerfile-minidlna .
  docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-text$V_TAG -f Dockerfile-text .
  docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-torrent$V_TAG -f Dockerfile-torrent .

  if [[ $V_LATEST == true ]]; then
    echo "Start build LATEST tag $V_LATEST_TAG"
    docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-bot$V_LATEST_TAG -f Dockerfile-bot .
    docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-file$V_LATEST_TAG -f Dockerfile-file .
    docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-media$V_LATEST_TAG -f Dockerfile-minidlna .
    docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-text$V_LATEST_TAG -f Dockerfile-text .
    docker build --build-arg BRANCH="$V_BRANCH" -t halushko/cinema-torrent$V_LATEST_TAG -f Dockerfile-torrent .
  fi

  cd ../../..
  echo "Finish build"
fi

if [[ $V_PUSH == true && $V_TAG != "LATEST" ]]; then
  echo "Start push to Docker Hub"
  docker push halushko/cinema-bot$V_TAG
  docker push halushko/cinema-file$V_TAG
  docker push halushko/cinema-media$V_TAG
  docker push halushko/cinema-text$V_TAG
  docker push halushko/cinema-torrent$V_TAG

  if [[ $V_PUSH_LATEST == true ]]; then
    docker push halushko/cinema-bot$V_LATEST_TAG
    docker push halushko/cinema-file$V_LATEST_TAG
    docker push halushko/cinema-media$V_LATEST_TAG
    docker push halushko/cinema-text$V_LATEST_TAG
    docker push halushko/cinema-torrent$V_LATEST_TAG
  fi
fi

cd ..
rm -rf tmp
