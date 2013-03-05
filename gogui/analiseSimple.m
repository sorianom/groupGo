function analiseSimple()
    % ex 30
    %wonWhite = [];
    %wonBlack = [0, 1, 2, 3, 4, 6, 10, 11, 12, 13, 15, 17, 19, 20, 21, 22, 23, 27, 28, 31, 33, 35, 36, 38, 39, 42, 46, 47, 50, 51, 53, 54, 57, 59, 60, 61, 64, 65, 67, 68, 69, 71, 72, 75, 77, 78, 85, 86, 88, 89, 96, 97];
    
    % ex 31
    wonWhite = [];
    wonBlack = [0, 3, 4, 7, 8, 9, 10, 11, 12, 13, 14, 15, 17, 19, 20, 21, 23, 24, 25, 26, 29, 30, 31, 33, 34, 35, 36, 39, 41, 42, 46, 47, 48, 52, 53, 58, 59, 61, 62, 63, 64, 66, 67, 69, 71, 72, 73, 74, 75, 77, 78, 80, 81, 82, 84, 85, 86, 87, 89, 91, 95, 99];
    
    %lost = [2, 3, 4, 6, 7, 8, 12, 13, 14, 15, 16, 17, 18, 19];
    
    %lost = [2, 3, 4, 6, 7, 8];
    names = {'alice','albert','john','francesco'};
    
    exPrefix = 'ex31';
    
    numGamesWhite = 0;
    numGamesBlack = 100;
    numGamesTotal = numGamesWhite + numGamesBlack;
    
    won = [wonBlack, numGamesBlack + wonWhite];
    
    %won = wonWhite;
    
    changed = 0;

    nExperts = 4;
    
     % Loading black files
    for n = 1 : nExperts
        for i = 1 : numGamesBlack
            disp(strcat('experiments/',exPrefix,'/',names(n),'-',int2str(i-1),'.log'));
            fname = strcat('experiments/',exPrefix,'/',names(n),'-',int2str(i-1),'.log');
            fid = fopen(char(fname));

            j = 1;
            while ~feof(fid)
                line = fgetl(fid);

                if (strfind(line, '-1'))
                    tmp = sscanf(line, '%d, %d, %*2c');
                    data(:,j,i,n) = tmp;
                    if (changed == 1)
                        endOfFile(i, n) = j - 1;
                    end
                    changed = 0;                    
                else
                    data(:,j,i,n) = sscanf(line, '%d, %d, %*2c');
                    changed = 1;
                end
                
                j = j + 1;
            end

            if (changed == 1)
                endOfFile(i, n) = j - 1;
            end
            
            fclose(fid);
        end
    end
    
    changed = 0;
        
    %endOfFile(i, :) = min(endOfFile(i,:));
    endOfFile = min(endOfFile(:,:)');

    
    for n = 1 : nExperts
        wonPos = 1;
        lostPos = 1;
        for i = 1 : numGamesTotal
            numAccept(i, n) = 0;
            for j = 1 : endOfFile(i)                
                numAccept(i, n) = numAccept(i,n) + data(2,j,i,n);
            end
            numAccept(i, n) = numAccept(i, n)/endOfFile(i);

            if (wonPos <= size(won,2))
                if (i == (won(wonPos) + 1))
                    numAcceptWon(wonPos, n) = numAccept(i, n);
                    wonPos = wonPos + 1;
                else
                   numAcceptLost(lostPos, n) = numAccept(i, n);
                   lostPos = lostPos + 1; 
                end
            else
                numAcceptLost(lostPos, n) = numAccept(i, n);
                lostPos = lostPos + 1;
            end            
        end    
    end

    bar(mean(numAccept),'r');
    hold;
    errorbar(mean(numAccept), std(numAccept),'d');
    Labels = {'Fuego', 'GnuGo', 'Pachi', 'MoGo'};
    set(gca, 'XTick', 1:4, 'XTickLabel', Labels);
    xlabel('Agent');
    ylabel('Acceptance Rate');
    ylim([0 1.0]);
    title('All depths - All games');
    print('-dpng',strcat('experiments/graphs/',exPrefix,'/acceptsAllGames.png'));
    
    figure;
    bar(mean(numAcceptWon),'r');
    hold;
    errorbar(mean(numAcceptWon), std(numAcceptWon),'d');
    set(gca, 'XTick', 1:4, 'XTickLabel', Labels);    
    xlabel('Agent');
    ylabel('Acceptance Rate');
    ylim([0 1.0]);
    title('All depths - Won games');
    print('-dpng',strcat('experiments/graphs/',exPrefix,'/acceptsWonGames.png'));

    figure;
    bar(mean(numAcceptLost),'r');
    hold;
    errorbar(mean(numAcceptLost), std(numAcceptLost),'d');
    set(gca, 'XTick', 1:4, 'XTickLabel', Labels);
    xlabel('Agent');
    ylabel('Acceptance Rate');
    ylim([0 1.0]);
    title('All depths - Lost games');
    print('-dpng',strcat('experiments/graphs/',exPrefix,'/acceptsLostGames.png'));

end