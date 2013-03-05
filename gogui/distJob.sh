#!/bin/sh
cd $PBS_O_WORKDIR
PATH=$PBS_O_PATH
#only for white for now...
sh /home/rcf-proj/ls/sorianom/go/white/scripts/ex-$PBS_VNODENUM.sh