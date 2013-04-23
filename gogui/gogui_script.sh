#!/bin/sh
BLACK="/usr/games/gnugo --mode gtp --level 15"
WHITE="/usr/games/gnugo --mode gtp --level 15"
TWOGTP="../gogui/bin/gogui-twogtp -black \"$BLACK\" -white \"$WHITE\" -numAgents 6 -agentsList -size 9 -iteration 0 -path ../gogui/output/tmp/0/ -initial 0 -games 3 -groupColor white -sgffile ../gogui/output/tmp/result -agentsList \"/usr/games/gnugo --mode gtp --level 15|/usr/games/gnugo --mode gtp --level 15|/usr/games/gnugo --mode gtp --level 15|/usr/games/gnugo --mode gtp --level 15\" -weightsList \"1.0|1.0|1.0|1.0|1.0|1.0\" "
../gogui/bin/gogui -size 9 -program "$TWOGTP"