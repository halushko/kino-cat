#!/bin/bash
V_REMOVE_FILES=false

if [ $# -eq 0 ]; then
    V_REMOVE_FILES=false
else
  while [ "$1" != "" ]; do
      case $1 in
      --full)
          V_REMOVE_FILES=true
          ;;
      *)
          ;;
      esac
      shift
  done
fi

if [[ $V_REMOVE_FILES == true ]]; then
    transmission-remote -t $1 -rad
else
    transmission-remote -t $1 -r
fi

exit 1
