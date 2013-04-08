#!/bin/sh

BLACK="gnugo --mode gtp --level 1"
WHITE="gnugo --mode gtp --level 3"
ALICE="gnugo --mode gtp --level 5"
ALBERT="gnugo --mode gtp --level 7"
JOHN="gnugo --mode gtp --level 9"
FRANCESCO="gnugo --mode gtp --level 10"

TWOGTP="./src/net/sf/gogui/tools/gogui-twogtp -black \"$BLACK\" -white \"$WHITE\" -alice \"$ALICE\" -aliceWeight 1 -albert \"$ALBERT\" -albertWeight 1 -john \"$JOHN\" -johnWeight 1 -francesco \"$FRANCESCO\" -francescoWeight 1 -size 9 -iteration 0 -path /home/cs102/test_gogui/0/ -initial 0 -games 10 -groupColor white -sgffile /home/cs102/test_gogui_result/"

./src/net/sf/gogui/gogui -size 9 -program "$TWOGTP" -computer-both -auto

#TWOGTP="./bin/gogui-twogtp -black \"$BLACK\" -white \"$WHITE\" -alice \"$ALICE\" -aliceWeight 1 -albert \"$ALBERT\" -albertWeight 1 -john \"$JOHN\" -johnWeight 1 -francesco \"$FRANCESCO\" -francescoWeight 1 -size 9 -iteration 0 -path /home/soriano/tmp/0/ -initial 0 -games 10 -groupColor white -sgffile /home/soriano/tmp/result"

#./bin/gogui -size 9 -program "$TWOGTP" -computer-both -auto
