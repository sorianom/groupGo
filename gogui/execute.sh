#!/bin/sh
BLACK="/home/soriano/pesquisa/desenvolvimento/go/fuego-1.1c/fuegomain/fuego --nobook --config /home/soriano/pesquisa/desenvolvimento/go/configGroupGo/configSlowOneThread"
WHITE="/home/soriano/pesquisa/desenvolvimento/go/fuego-1.1c/fuegomain/fuego --nobook --config /home/soriano/pesquisa/desenvolvimento/go/configGroupGo/configSlowOneThread"
ALICE="/home/soriano/pesquisa/desenvolvimento/go/fuego-1.1c/fuegomain/fuego --nobook --config /home/soriano/pesquisa/desenvolvimento/go/configGroupGo/configSlowOneThread"
#ALBERT="/home/soriano/pesquisa/desenvolvimento/go/gnugo-3.8/interface/gnugo --mode gtp --level 15"
ALBERT="/home/soriano/pesquisa/desenvolvimento/go/groupGoSimpleDemo/userClient/Vegos/bin/userClient"
#JOHN="/home/soriano/pesquisa/desenvolvimento/go/fuego-1.1-mine2/fuegomain/fuego --nobook --config /home/soriano/pesquisa/desenvolvimento/go/configGroupGo/configSlowOneThread-ag5"
JOHN="/home/soriano/pesquisa/desenvolvimento/go/groupGoSimpleDemo/userClient/Vegos/bin/userClient"
FRANCESCO="/home/soriano/pesquisa/desenvolvimento/go/MoGo_release3/mogo"

TWOGTP="/home/soriano/pesquisa/desenvolvimento/go/groupGoSimpleDemo/bin/gogui-twogtp -black \"$BLACK\" -white \"$WHITE\" -alice \"$ALICE\" -aliceWeight 1.3 -albert \"$ALBERT\" -albertWeight 0.61 -john \"$JOHN\" -johnWeight 1.2 -francesco \"$FRANCESCO\" -francescoWeight 0.78 -size 9 -iteration 0 -path /home/soriano/tmp/0/ -initial 0 -games 10 -groupColor white -sgffile /home/soriano/tmp/result"
rm /home/soriano/pesquisa/desenvolvimento/go/groupGoSimpleDemo/votes_text_file.txt
/home/soriano/pesquisa/desenvolvimento/go/groupGoSimpleDemo/bin/gogui -size 9 -program "$TWOGTP"
#/home/soriano/pesquisa/desenvolvimento/go/groupGoSimpleDemo/bin/gogui-twogtp -black "$BLACK" -white "$WHITE" -alice "$ALICE" -aliceWeight 1.3 -albert "$ALBERT" -albertWeight 0.61 -john "$JOHN" -johnWeight 1.2 -francesco "$FRANCESCO" -francescoWeight 0.78 -size 9 -iteration 0 -path /home/soriano/tmp/0/ -initial 0 -games 10 -groupColor white -sgffile /home/soriano/tmp/result -verbose -auto