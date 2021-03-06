#!/usr/bin/python

import os
import sys
import subprocess

numGames = 1000;
numNodes = 10;
color = int(sys.argv[1]);

if (color == 0):
    base = "/home/rcf-proj/ls/sorianom/go/black/";
else:
    base = "/home/rcf-proj/ls/sorianom/go/white/";

baseScripts = "/home/rcf-proj/ls/sorianom/go/";

gamesPerNode = numGames/numNodes;
rest = numGames - gamesPerNode*numNodes;

gogui = baseScripts + "/groupGoSimpleTest/bin/gogui-twogtp";
baseExec = base + "/scriptsTest";

experiments = base + "/experiments/testing/" + sys.argv[2] + "/";

arg = "-alice \"" + baseScripts + "/fuego-1.1/fuegomain/fuego --config " + baseScripts + "/configGroupGo/configSlowOneThread\" -aliceWeight " + sys.argv[3] + " -albert \"" + baseScripts + "/gnugo-3.8/interface/gnugo --mode gtp\" -albertWeight " + sys.argv[4] + " -john \"" + baseScripts + "/pachi/pachi\" -johnWeight " + sys.argv[5] + " -francesco \"" + baseScripts + "/MoGo_release3/mogo\" -francescoWeight " + sys.argv[6] + " -white \"" + baseScripts + "/fuego-1.1/fuegomain/fuego --config " + baseScripts + "/configGroupGo/configSlowOneThread\" -black \"" + baseScripts + "/fuego-1.1/fuegomain/fuego --config " + baseScripts + "/configGroupGo/configSlowOneThread\" -size 9 -auto -iteration " + sys.argv[2];

subprocess.call(["mkdir",experiments]);

p = [];

initial = 0;

for n in range(numNodes):
    f = open(baseExec + "/ex-" + str(n) + ".sh",'w');
    f.write("#!/bin/sh\n\n");
    
    f.write('cd $PBS_O_WORKDIR\n');

    if (rest > 0):
        exGames = gamesPerNode + 1;
    else:
        exGames = gamesPerNode;

    if (color == 0):
        f.write(gogui + " " + arg + " -initial " + str(initial) + " -games " + str(exGames) + " -groupColor black -sgffile " + experiments + "result\n");
    else:
        f.write(gogui + " " + arg + " -initial " + str(initial) + " -games " + str(exGames) + " -groupColor white -sgffile " + experiments + "result\n");

    if (rest > 0):
        initial = initial + gamesPerNode + 1;
        rest = rest - 1;
    else:
        initial = initial + gamesPerNode;

    f.close();

    os.system("chmod +x " + baseExec + "/ex-" + str(n) + ".sh");
    
    # for single job execution
    #if (n > 0):
    #    subprocess.Popen("qsub -l walltime=24:00:00,nodes=1:ppn=1,arch=x86_64 " + baseExec + "/ex-" + str(n) + ".sh", shell=True);
    #else:
    #    subprocess.Popen(baseExec + "/ex-" + str(n) + ".sh", shell=True);
    p.append(subprocess.Popen(baseExec + "/ex-" + str(n) + ".sh", shell=True));

#subprocess.Popen("pbsdsh -v " + baseScripts + "/groupGoSimple/distJob.sh " + str(color), shell=True);

for n in numNodes:
    p.wait();
