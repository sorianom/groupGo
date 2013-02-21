// TwoGtp.java
// from Leandro

package net.sf.gogui.tools.twogtp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;

import net.sf.gogui.game.ConstNode;
import net.sf.gogui.game.ConstGameTree;
import net.sf.gogui.game.Game;
import net.sf.gogui.game.NodeUtil;
import net.sf.gogui.game.TimeSettings;
import net.sf.gogui.go.BlackWhiteSet;
import net.sf.gogui.go.ConstBoard;
import net.sf.gogui.go.GoColor;
import static net.sf.gogui.go.GoColor.BLACK;
import static net.sf.gogui.go.GoColor.WHITE;
import net.sf.gogui.go.GoPoint;
import net.sf.gogui.go.InvalidKomiException;
import net.sf.gogui.go.Komi;
import net.sf.gogui.go.Move;
import net.sf.gogui.gtp.GtpCommand;
import net.sf.gogui.gtp.GtpEngine;
import net.sf.gogui.gtp.GtpError;
import net.sf.gogui.gtp.GtpResponseFormatError;
import net.sf.gogui.gtp.GtpUtil;
import net.sf.gogui.sgf.SgfError;
import net.sf.gogui.sgf.SgfReader;
import net.sf.gogui.sgf.SgfWriter;
import net.sf.gogui.util.ErrorMessage;
import net.sf.gogui.util.ObjectUtil;
import net.sf.gogui.util.Platform;
import net.sf.gogui.util.StringUtil;
import net.sf.gogui.util.Table;
import net.sf.gogui.version.Version;
import net.sf.gogui.xml.XmlWriter;

/** GTP adapter for playing games between two Go programs. */
@SuppressWarnings("all")
public class TwoGtp
    extends GtpEngine
{
	private Random generator;
	private FileWriter[] fstream;
	// Doug Chen - ADDED:
	private FileWriter fw_votes;
	private BufferedWriter[] expertLog;
	private FileWriter fw_acceptedOpinions;
	private BufferedWriter acceptedOpinions;
	
	private FileWriter fw_freqAgreed;
	private BufferedWriter freqAgreed;
	
	String path;
	
	private Integer[] numAccepted = {0, 0, 0, 0};
	private Integer[] numAgreed = {0, 0, 0, 0};
	
	private GoColor myColor;
	private String myColorShort;
	private GoColor oponentColor;
	private String oponentColorShort;	
	
	boolean m_useOpeningDB;
	
    /** Constructor.
        @param komi The fixed komi. See TwoGtp documentation for option -komi
    */
    public TwoGtp(int iteration, String path, String black, String white, String alice, String albert, String john, String francesco, double [] weights, String groupColor, String referee, String observer,
                  int size, Komi komi, int initialGame, int numberGames, boolean alternate,
                  String filePrefix, boolean force, boolean verbose,
                  Openings openings, TimeSettings timeSettings,
                  boolean useXml, boolean openingDB)
        throws Exception
    {
        super(null);
        assert size > 0;
        assert size <= GoPoint.MAX_SIZE;
        assert komi != null;
        
        /*if (groupColor.equals("black"))
        {
        	path = "/home/rcf-proj/ls/sorianom/go/black/experiments/current/";
        	path = path + Integer.toString(iteration) + "/";
        }
        else
        {
        	path = "/home/rcf-proj/ls/sorianom/go/white/experiments/current/";
        	//path = "/home/soriano/pesquisa/desenvolvimento/go/groupGoSimple/experiments/current/";
        	path = path + Integer.toString(iteration) + "/";
        }*/
        
        this.path = path + Integer.toString(iteration) + "/";
        
        if (black.equals(""))
            throw new ErrorMessage("No black program set");
        if (white.equals(""))
            throw new ErrorMessage("No white program set");
        if (alice.equals(""))
            throw new ErrorMessage("Where is Alice?..");
        if (albert.equals(""))
            throw new ErrorMessage("Sorry, can't work without Albert");
        if (john.equals(""))
            throw new ErrorMessage("Miss John...");
        if (francesco.equals(""))
            throw new ErrorMessage("Gosh, where is Francesco?..");
        m_filePrefix = filePrefix;
        m_useXml = useXml;
        File resultFile = getResultFile();
        if (force && resultFile.exists())
            if (! resultFile.delete())
                throw new ErrorMessage("Could not delete file '" + resultFile
                                       + "'");
        m_allPrograms = new ArrayList<Program>();
        m_black = new Program(black, "Black", "B", verbose);
        m_allPrograms.add(m_black);
        Thread.sleep(2500);
        m_white = new Program(white, "White", "W", verbose);
        m_allPrograms.add(m_white);
        Thread.sleep(2500);
        //m_white = m_black;
        /*m_alice = new Program(alice, "Alice", "Ali", verbose);
        m_allPrograms.add(m_alice);*/
        if (groupColor.equals("black"))
        	m_alice = m_black;
        else
        	m_alice = m_white;
        m_albert = new Program(albert, "Albert", "Alb", verbose);
        m_allPrograms.add(m_albert);
        Thread.sleep(2500);
        m_john = new Program(john, "John", "J", verbose);
        m_allPrograms.add(m_john);
        Thread.sleep(2500);
        m_francesco = new Program(francesco, "Francesco", "F", verbose);
        m_allPrograms.add(m_francesco);        
        if (referee.equals(""))
            m_referee = null;
        else
        {
            m_referee = new Program(referee, "Referee", "R", verbose);
            m_allPrograms.add(m_referee);
        }
        if (observer.equals(""))
            m_observer = null;
        else
        {
            m_observer = new Program(observer, "Observer", "O", verbose);
            m_allPrograms.add(m_observer);
        }
        for (Program program : m_allPrograms)
            program.setLabel(m_allPrograms);
        m_weights = weights;		
        m_size = size;
        m_komi = komi;
        m_alternate = alternate;
        m_initialGame = initialGame;
        m_numberGames = numberGames;
        m_openings = openings;
        m_verbose = verbose;
        m_timeSettings = timeSettings;
        m_useOpeningDB = openingDB;
        initTable();
        readGames();
        initGame(size);
        generator = new Random();
        
    	fstream = new FileWriter[4];
    	expertLog = new BufferedWriter[4];
    	
    	if (groupColor.equals("black"))
    	{
    		myColor = BLACK;
    		myColorShort = "B";
    		oponentColor = WHITE;
    		oponentColorShort = "W";
    	}
    	else
    	{
    		myColor = WHITE;
    		myColorShort = "W";
    		oponentColor = BLACK;
    		oponentColorShort = "B";
    	}
    }

    public void autoPlay() throws Exception
    {
        try
        {
            System.in.close();
            StringBuilder response = new StringBuilder(256);
            // Check if we can still play game (still have games left)
            while (m_gameIndex < m_initialGame + m_numberGames)
            {
                try
                {
                    newGame(m_size);
                    while (! gameOver())
                    {
                        response.setLength(0);
                        sendGenmove(getToMove(), response);
                    }
                }
                catch (GtpError e)
                {
                    //handleEndOfGame(true, e.getMessage());
                	if (m_black.isProgramDead())
                        throw new ErrorMessage("Black program died");
                    if (m_white.isProgramDead())
                        throw new ErrorMessage("White program died");
                    if (m_alice.isProgramDead())
                        throw new ErrorMessage("Can't see Alice anymore");
                    if (m_albert.isProgramDead())
                        throw new ErrorMessage("Can't see Albert anymore");
                    if (m_john.isProgramDead())
                    {
                    	FileWriter warningFile = new FileWriter(path + "warning");
                    	BufferedWriter warning = new BufferedWriter(warningFile);
                    	
                    	warning.write("Can't see John anymore");                    	
                    	
                    	warning.close();
                    	
                        throw new ErrorMessage("Can't see John anymore");
                    }
                    if (m_francesco.isProgramDead())
                        throw new ErrorMessage("Can't see Francesco anymore");
                    
                    // Well, one of the experts died... But let's go back to work!..
                    // ** Does not seem to work... Everything still stops if some expert die
                    while (! gameOver())
                    {
                        response.setLength(0);
                        sendGenmove(getToMove(), response);
                    }
                }
                if (m_black.isProgramDead())
                    throw new ErrorMessage("Black program died");
                if (m_white.isProgramDead())
                    throw new ErrorMessage("White program died");
                if (m_alice.isProgramDead())
                    throw new ErrorMessage("Can't see Alice anymore");
                if (m_albert.isProgramDead())
                    throw new ErrorMessage("Can't see Albert anymore");
                if (m_john.isProgramDead())
                {
                	FileWriter warningFile = new FileWriter(path + "warning");
                	BufferedWriter warning = new BufferedWriter(warningFile);
                	
                	warning.write("Can't see John anymore");                    	
                	
                	warning.close();
                	
                    throw new ErrorMessage("Can't see John anymore");
                }
                if (m_francesco.isProgramDead())
                    throw new ErrorMessage("Can't see Francesco anymore");
            }
        }
        finally
        {
            close();
        }
    }

    /** Get games left to play.
        @return number of games left of -1 if no maximum set.
    */
    public int gamesLeft()
    {
        if (m_numberGames <= 0)
            return -1;
        return m_numberGames + m_initialGame - m_gameIndex;
    }

    public void handleCommand(GtpCommand cmd) throws GtpError
    {
        String command = cmd.getCommand();
        if (command.equals("boardsize"))
            cmdBoardSize(cmd);
        else if (command.equals("clear_board"))
            cmdClearBoard(cmd);
        else if (command.equals("final_score"))
            finalStatusCommand(cmd);
        else if (command.equals("final_status"))
            finalStatusCommand(cmd);
        else if (command.equals("final_status_list"))
            finalStatusCommand(cmd);
        else if (command.equals("gogui-interrupt"))
            ;
        else if (command.equals("gogui-title"))
            cmd.setResponse(getTitle());
        else if (command.equals("gogui-twogtp-black"))
            twogtpColor(m_black, cmd);
        else if (command.equals("gogui-twogtp-white"))
            twogtpColor(m_white, cmd);
        else if (command.equals("gogui-twogtp-referee"))
            twogtpReferee(cmd);
        else if (command.equals("gogui-twogtp-observer"))
            twogtpObserver(cmd);
        else if (command.equals("quit"))
        {
            close();
            setQuit();
        }
        else if (command.equals("play"))
            cmdPlay(cmd);
        else if (command.equals("undo"))
            cmdUndo(cmd);
        else if (command.equals("genmove"))
            cmdGenmove(cmd);
        else if (command.equals("komi"))
            komi(cmd);
        else if (command.equals("scoring_system"))
            sendIfSupported(command, cmd.getLine());
        else if (command.equals("name"))
            cmd.setResponse("GoGuiTwoGtp");
        else if (command.equals("version"))
            cmd.setResponse(Version.get());
        else if (command.equals("protocol_version"))
            cmd.setResponse("2");
        else if (command.equals("list_commands"))
            cmd.setResponse("boardsize\n" +
                            "clear_board\n" +
                            "final_score\n" +
                            "final_status\n" +
                            "final_status_list\n" +
                            "genmove\n" +
                            "gogui-interrupt\n" +
                            "gogui-title\n" +
                            "komi\n" +
                            "list_commands\n" +
                            "name\n" +
                            "play\n" +
                            "quit\n" +
                            "scoring_system\n" +
                            "time_settings\n" +
                            "gogui-twogtp-black\n" +
                            "gogui-twogtp-observer\n" +
                            "gogui-twogtp-referee\n" +
                            "gogui-twogtp-white\n" +
                            "undo\n" +
                            "version\n");
        else if (GtpUtil.isStateChangingCommand(command))
            throw new GtpError("unknown command");
        else if (command.equals("time_settings"))
            sendIfSupported(command, cmd.getLine());
        else
        {
            boolean isExtCommandBlack = m_black.isSupported(command);
            boolean isExtCommandWhite = m_white.isSupported(command);
            boolean isExtCommandAlice = m_alice.isSupported(command);
            boolean isExtCommandAlbert = m_albert.isSupported(command);
            boolean isExtCommandJohn = m_john.isSupported(command);
            boolean isExtCommandFrancesco = m_francesco.isSupported(command);            
            boolean isExtCommandReferee = false;
            if (m_referee != null)
                isExtCommandReferee = m_referee.isSupported(command);
            boolean isExtCommandObserver = false;
            if (m_observer != null)
                isExtCommandObserver = m_observer.isSupported(command);
            if (isExtCommandBlack && ! isExtCommandObserver
                && ! isExtCommandWhite && ! isExtCommandAlice
                && ! isExtCommandAlbert   && !isExtCommandJohn
                && ! isExtCommandFrancesco && ! isExtCommandReferee)
                forward(m_black, cmd);
            if (isExtCommandWhite && ! isExtCommandObserver
                && ! isExtCommandBlack && ! isExtCommandAlice
                && ! isExtCommandAlbert   && !isExtCommandJohn
                && ! isExtCommandFrancesco && ! isExtCommandReferee)
                forward(m_white, cmd);
            if (isExtCommandAlice && ! isExtCommandObserver
                && ! isExtCommandBlack && ! isExtCommandWhite
                && ! isExtCommandAlbert && ! isExtCommandJohn
                && isExtCommandFrancesco && ! isExtCommandReferee)
                forward(m_alice, cmd);
            if (isExtCommandAlbert && ! isExtCommandObserver
                && ! isExtCommandBlack && ! isExtCommandAlice
                && ! isExtCommandJohn && ! isExtCommandFrancesco
                && ! isExtCommandWhite && ! isExtCommandReferee)
                forward(m_albert, cmd);
            if (isExtCommandJohn && ! isExtCommandObserver
                    && ! isExtCommandBlack && ! isExtCommandAlice
                    && ! isExtCommandAlbert && ! isExtCommandFrancesco
                    && ! isExtCommandWhite && ! isExtCommandReferee)
            	forward(m_john, cmd);
            if (isExtCommandFrancesco && ! isExtCommandObserver
                    && ! isExtCommandBlack && ! isExtCommandAlice
                    && ! isExtCommandJohn && ! isExtCommandAlbert
                    && ! isExtCommandWhite && ! isExtCommandReferee)
                 forward(m_francesco, cmd);
            if (isExtCommandReferee && ! isExtCommandObserver
                && ! isExtCommandBlack && ! isExtCommandWhite
                && ! isExtCommandAlice && ! isExtCommandAlbert
                && ! isExtCommandJohn && ! isExtCommandFrancesco)
                forward(m_referee, cmd);
            if (isExtCommandObserver && ! isExtCommandReferee
                && ! isExtCommandBlack && ! isExtCommandWhite
                && ! isExtCommandAlice && ! isExtCommandAlbert
                && ! isExtCommandJohn && ! isExtCommandFrancesco)
                forward(m_observer, cmd);
            if (! isExtCommandReferee
                && ! isExtCommandBlack
                && ! isExtCommandObserver
                && ! isExtCommandWhite
                && ! isExtCommandAlice
                && ! isExtCommandAlbert
                && ! isExtCommandJohn
                && ! isExtCommandFrancesco)
                throw new GtpError("unknown command");
            throw new GtpError("use gogui-twogtp-black/white/referee/observer");
        }
    }

    public void interruptCommand()
    {
        for (Program program : m_allPrograms)
            program.interruptProgram();
    }

    /** Limit number of moves.
        @param maxMoves Maximum number of moves after which genmove will fail,
        -1 for no limit.
    */
    public void setMaxMoves(int maxMoves)
    {
        m_maxMoves = maxMoves;
    }

    private final boolean m_alternate;

    private final boolean m_useXml;

    private boolean m_gameSaved;

    private int m_maxMoves = 1000;

    private boolean m_resigned;

    private final boolean m_verbose;

    private int m_gameIndex;
    
    private final int m_initialGame;

    private final int m_numberGames;

    private final int m_size;

    /** Fixed komi. */
    private final Komi m_komi;

    private Game m_game;

    private GoColor m_resignColor;

    private final Openings m_openings;

    private final Program m_black;

    private final Program m_white;
    
    private final Program m_alice;
    
    private final Program m_albert;
    
    private final Program m_john;
    
    private final Program m_francesco;
    
    private final Program m_referee;

    private final Program m_observer;

    private double [] m_weights;
    
    private final ArrayList<Program> m_allPrograms;

    private final BlackWhiteSet<Double> m_realTime =
        new BlackWhiteSet<Double>(0., 0.);

    private String m_openingFile;

    private final String m_filePrefix;

    private final ArrayList<ArrayList<Compare.Placement>> m_games
        = new ArrayList<ArrayList<Compare.Placement>>(100);

    private Table m_table;

    private final TimeSettings m_timeSettings;

    private ConstNode m_lastOpeningNode;

    private void checkInconsistentState() throws GtpError
    {
        for (Program program : m_allPrograms)
            if (program.isOutOfSync())
                throw new GtpError("Inconsistent state");
    }

    private void close()
    {
        for (Program program : m_allPrograms)
            program.close();
    }

    private void cmdBoardSize(GtpCommand cmd) throws GtpError
    {
        cmd.checkNuArg(1);
        int size = cmd.getIntArg(0, 1, GoPoint.MAX_SIZE);
        if (size != m_size)
            throw new GtpError("Size must be " + m_size);
    }

    private void cmdClearBoard(GtpCommand cmd) throws GtpError
    {
        cmd.checkArgNone();
        if (gamesLeft() == 0)
            throw new GtpError("Maximum number of " + m_numberGames +
                               " games reached");
        newGame(m_size);
    }

    private void cmdGenmove(GtpCommand cmd) throws GtpError
    {
        try
        {
            sendGenmove(cmd.getColorArg(), cmd.getResponse());
        }
        catch (ErrorMessage e)
        {
            throw new GtpError(e.getMessage());
        }
        catch (GtpResponseFormatError e)
        {
        	throw new GtpError(e.getMessage());
        }
    }

    private void cmdPlay(GtpCommand cmd) throws GtpError
    {
        cmd.checkNuArg(2);
        checkInconsistentState();
        GoColor color = cmd.getColorArg(0);
        GoPoint point = cmd.getPointArg(1, m_size);
        Move move = Move.get(color, point);
        m_game.play(move);
        synchronize();
    }

    private void cmdUndo(GtpCommand cmd) throws GtpError
    {
        cmd.checkArgNone();
        int moveNumber = m_game.getMoveNumber();
        if (moveNumber == 0)
            throw new GtpError("cannot undo");
        m_game.gotoNode(getCurrentNode().getFatherConst());
        assert m_game.getMoveNumber() == moveNumber - 1;
        synchronize();
    }

    private void finalStatusCommand(GtpCommand cmd) throws GtpError
    {
        checkInconsistentState();
        if (m_referee != null)
            forward(m_referee, cmd);
        else if (m_black.isSupported("final_status"))
            forward(m_black, cmd);
        else if (m_white.isSupported("final_status"))
            forward(m_white, cmd);
        else
            throw new GtpError("neither player supports final_status");
    }

    private void initTable() throws ErrorMessage
    {
        File file = getResultFile();
        if (file.exists())
        {
            m_table = new Table();
            try
            {
                m_table.read(getResultFile());
                int lastRowIndex = m_table.getNumberRows() - 1;
                m_gameIndex =
                    Integer.parseInt(m_table.get("GAME", lastRowIndex)) + 1;
                if (m_gameIndex < 0)
                    throw new ErrorMessage("Invalid file format: " + file);
            }
            catch (NumberFormatException e)
            {
                throw new ErrorMessage("Invalid file format: " + file);
            }
            catch (FileNotFoundException e)
            {
                throw new ErrorMessage(e.getMessage());
            }
            catch (IOException e)
            {
                throw new ErrorMessage("Read error: " + file);
            }
            return;
        }
        else
        {
        	m_gameIndex = m_initialGame;
        }
        ArrayList<String> columns = new ArrayList<String>();
        columns.add("GAME");
        columns.add("RES_B");
        columns.add("RES_W");
        columns.add("RES_R");
        columns.add("ALT");
        columns.add("DUP");
        columns.add("LEN");
        columns.add("TIME_B");
        columns.add("TIME_W");
        columns.add("CPU_B");
        columns.add("CPU_W");
        columns.add("ERR");
        columns.add("ERR_MSG");
        m_table = new Table(columns);
        m_black.setTableProperties(m_table);
        m_white.setTableProperties(m_table);
        if (m_referee == null)
            m_table.setProperty("Referee", "-");
        else
            m_referee.setTableProperties(m_table);
        m_table.setProperty("Size", Integer.toString(m_size));
        m_table.setProperty("Komi", m_komi.toString());
        if (m_openings != null)
            m_table.setProperty("Openings",
                                m_openings.getDirectory() + " ("
                                + m_openings.getNumber() + " files)");
        m_table.setProperty("Date", StringUtil.getDate());
        m_table.setProperty("Host", Platform.getHostInfo());
        m_table.setProperty("Xml", m_useXml ? "1" : "0");
    }

    private void forward(Program program, GtpCommand cmd) throws GtpError
    {
        cmd.setResponse(program.send(cmd.getLine()));
    }

    private boolean gameOver()
    {
        return (getBoard().bothPassed() || m_resigned);
    }

    private ConstBoard getBoard()
    {
        return m_game.getBoard();
    }

    private ConstNode getCurrentNode()
    {
        return m_game.getCurrentNode();
    }

    private File getFile(int gameIndex)
    {
        if (m_useXml)
            return new File(m_filePrefix + "-" + gameIndex + ".xml");
        else
            return new File(m_filePrefix + "-" + gameIndex + ".sgf");
    }

    private GoColor getToMove()
    {
        return m_game.getToMove();
    }

    private ConstGameTree getTree()
    {
        return m_game.getTree();
    }

    private File getResultFile()
    {
        return new File(m_filePrefix + "-" + Integer.toString(m_initialGame) + ".dat");
    }

    private String getTitle()
    {
        StringBuilder buffer = new StringBuilder();
        String nameBlack = m_black.getLabel();
        String nameWhite = m_white.getLabel();
        if (isAlternated())
        {
            String tmpName = nameBlack;
            nameBlack = nameWhite;
            nameWhite = tmpName;
        }
        buffer.append(nameWhite);
        buffer.append(" vs ");
        buffer.append(nameBlack);
        buffer.append(" (B)");
        if (! m_filePrefix.equals(""))
        {
            buffer.append(" (");
            buffer.append(m_gameIndex + 1);
            buffer.append(')');
        }
        return buffer.toString();
    }

    private void handleEndOfGame(boolean error, String errorMessage)
        throws ErrorMessage
    {
        try
        {
            String resultBlack;
            String resultWhite;
            String resultReferee;
            if (m_resigned)
            {
                String result = (m_resignColor == BLACK ? "W" : "B");
                result = result + "+R";
                resultBlack = result;
                resultWhite = result;
                resultReferee = result;
            }
            else
            {
                resultBlack = m_black.getResult();
                resultWhite = m_white.getResult();
                resultReferee = "?";
                if (m_referee != null)
                    resultReferee = m_referee.getResult();
            }
            double cpuTimeBlack = m_black.getAndClearCpuTime();
            double cpuTimeWhite = m_white.getAndClearCpuTime();
            double realTimeBlack = m_realTime.get(BLACK);
            double realTimeWhite = m_realTime.get(WHITE);
            if (isAlternated())
            {
                resultBlack = inverseResult(resultBlack);
                resultWhite = inverseResult(resultWhite);
                resultReferee = inverseResult(resultReferee);
                realTimeBlack = m_realTime.get(WHITE);
                realTimeWhite = m_realTime.get(BLACK);
            }
            ArrayList<Compare.Placement> moves
                = Compare.getPlacements(getTree().getRootConst());
            String duplicate =
                Compare.checkDuplicate(getBoard(), moves, m_games,
                                       m_alternate, isAlternated());
            // If a program is dead we wait for a few seconds, because it
            // could be because the TwoGtp process was killed and we don't
            // want to write a result in this case.
            if (m_black.isProgramDead() || m_white.isProgramDead()
            		|| m_alice.isProgramDead() || m_albert.isProgramDead()
            		|| m_john.isProgramDead() || m_francesco.isProgramDead())
            {
                try
                {
                    Thread.sleep(3000);
                }
                catch (InterruptedException e)
                {
                    assert false;
                }
            }
            int moveNumber = NodeUtil.getMoveNumber(getCurrentNode());
            saveResult(resultBlack, resultWhite, resultReferee,
                       isAlternated(), duplicate, moveNumber, error,
                       errorMessage, realTimeBlack, realTimeWhite,
                       cpuTimeBlack, cpuTimeWhite);
            saveGame(resultBlack, resultWhite, resultReferee);
            ++m_gameIndex;
            m_games.add(moves);
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Could not save game: " + e.getMessage());
        }
    }

    private void initGame(int size) throws GtpError
    {
        m_game = new Game(size, m_komi, null, null, null);
        m_realTime.set(BLACK, 0.);
        m_realTime.set(WHITE, 0.);
        // Clock is not needed
        m_game.haltClock();
        m_resigned = false;
        if (m_openings != null)
        {
            int openingFileIndex;
            if (m_alternate)
                openingFileIndex = (m_gameIndex / 2) % m_openings.getNumber();
            else
                openingFileIndex = m_gameIndex % m_openings.getNumber();
            try
            {
                m_openings.loadFile(openingFileIndex);
            }
            catch (Exception e)
            {
                throw new GtpError(e.getMessage());
            }
            m_openingFile = m_openings.getFilename();
            if (m_verbose)
                System.err.println("Loaded opening " + m_openingFile);
            if (m_openings.getBoardSize() != size)
                throw new GtpError("Wrong board size: " + m_openingFile);
            m_game.init(m_openings.getTree());
            m_game.setKomi(m_komi);
            m_lastOpeningNode = NodeUtil.getLast(getTree().getRootConst());
        }
        else
            m_lastOpeningNode = null;
        synchronizeInit();
    }

    private String inverseResult(String result)
    {
        if (result.indexOf('B') >= 0)
            return result.replaceAll("B", "W");
        else if (result.indexOf('W') >= 0)
            return result.replaceAll("W", "B");
        else
            return result;
    }

    private boolean isAlternated()
    {
        return (m_alternate && m_gameIndex % 2 != 0);
    }

    private boolean isInOpening()
    {
        if (m_lastOpeningNode == null)
            return false;
        for (ConstNode node = getCurrentNode().getChildConst(); node != null;
             node = node.getChildConst())
            if (node == m_lastOpeningNode)
                return true;
        return false;
    }

    private void komi(GtpCommand cmd) throws GtpError
    {
        String arg = cmd.getArg();
        try
        {
            Komi komi = Komi.parseKomi(arg);
            if (! ObjectUtil.equals(komi, m_komi))
                throw new GtpError("komi is fixed at " + m_komi);
        }
        catch (InvalidKomiException e)
        {
            throw new GtpError("invalid komi: " + arg);
        }
    }

    private void newGame(int size) throws GtpError
    {
    	// Set up programs (?)
        m_black.getAndClearCpuTime();
        m_white.getAndClearCpuTime();
        m_alice.getAndClearCpuTime();
        m_albert.getAndClearCpuTime();
        m_john.getAndClearCpuTime();
        m_francesco.getAndClearCpuTime();        
        initGame(size);
        m_gameSaved = false;
        if (m_timeSettings != null)
            sendIfSupported("time_settings",
                            GtpUtil.getTimeSettingsCommand(m_timeSettings));
        
        try
        {
        	// Set up the file writers for each of the program's log files
        	if (myColor == BLACK)
        	{
        		fstream[0] = new FileWriter(path + "alice-b-" + m_gameIndex + ".log");
        		fstream[1] = new FileWriter(path + "albert-b-" + m_gameIndex + ".log");
        		fstream[2] = new FileWriter(path + "john-b-" + m_gameIndex + ".log");
        		fstream[3] = new FileWriter(path + "francesco-b-" + m_gameIndex + ".log");
        	}
        	else
        	{
        		fstream[0] = new FileWriter(path + "alice-w-" + m_gameIndex + ".log");
        		fstream[1] = new FileWriter(path + "albert-w-" + m_gameIndex + ".log");
        		fstream[2] = new FileWriter(path + "john-w-" + m_gameIndex + ".log");
        		fstream[3] = new FileWriter(path + "francesco-w-" + m_gameIndex + ".log");
        	}
        }
        catch (IOException e)
        {
        	System.out.println("Error: Could not create the log files");
        	System.exit(0);
        }
    	
    	for(int i = 0; i < 4; i++)
    	{
	    	expertLog[i] = new BufferedWriter(fstream[i]);	    
    	}
    }

    private void readGames()
    {
        for (int n = m_initialGame; n < m_gameIndex; ++n)
        {
            File file = getFile(n);
            if (! file.exists())
            {
                System.err.println("Game " + file + " not found");
                continue;
            }
            if (! file.exists())
                return;
            try
            {
                FileInputStream fileStream = new FileInputStream(file);
                SgfReader reader =
                    new SgfReader(fileStream, file, null, 0);
                ConstNode root = reader.getTree().getRoot();
                m_games.add(Compare.getPlacements(root));
            }
            catch (SgfError e)
            {
                System.err.println("Error reading " + file + ": " +
                                   e.getMessage());
            }
            catch (Exception e)
            {
                System.err.println("Error reading " + file + ": " +
                                   e.getMessage());
            }
        }
    }

    private void saveGame(String resultBlack, String resultWhite,
                          String resultReferee)
        throws FileNotFoundException
    {
        if (m_filePrefix.equals(""))
            return;
        String nameBlack = m_black.getLabel();
        String nameWhite = m_white.getLabel();
        String blackCommand = m_black.getProgramCommand();
        String whiteCommand = m_white.getProgramCommand();
        String blackVersion = m_black.getVersion();
        String whiteVersion = m_white.getVersion();
        if (isAlternated())
        {
            nameBlack = m_white.getLabel();
            nameWhite = m_black.getLabel();
            blackCommand = m_white.getProgramCommand();
            whiteCommand = m_black.getProgramCommand();
            blackVersion = m_white.getVersion();
            whiteVersion = m_black.getVersion();
            String resultTmp = inverseResult(resultWhite);
            resultWhite = inverseResult(resultBlack);
            resultBlack = resultTmp;
            resultReferee = inverseResult(resultReferee);
        }
        m_game.setPlayer(BLACK, nameBlack);
        m_game.setPlayer(WHITE, nameWhite);
        if (m_referee != null)
            m_game.setResult(resultReferee);
        else if (resultBlack.equals(resultWhite) && ! resultBlack.equals("?"))
            m_game.setResult(resultBlack);
        String host = Platform.getHostInfo();
        StringBuilder comment = new StringBuilder();
        comment.append("Black command: ");
        comment.append(blackCommand);
        comment.append("\nWhite command: ");
        comment.append(whiteCommand);
        comment.append("\nBlack version: ");
        comment.append(blackVersion);
        comment.append("\nWhite version: ");
        comment.append(whiteVersion);
        if (m_openings != null)
        {
            comment.append("\nOpening: ");
            comment.append(m_openingFile);
        }
        comment.append("\nResult[Black]: ");
        comment.append(resultBlack);
        comment.append("\nResult[White]: ");
        comment.append(resultWhite);
        if (m_referee != null)
        {
            comment.append("\nReferee: ");
            comment.append(m_referee.getProgramCommand());
            comment.append("\nResult[Referee]: ");
            comment.append(resultReferee);
        }
        comment.append("\nHost: ");
        comment.append(host);
        comment.append("\nDate: ");
        comment.append(StringUtil.getDate());
        m_game.setComment(comment.toString(), getTree().getRootConst());
        File file = getFile(m_gameIndex);
        if (m_verbose)
            System.err.println("Saving " + file);
        OutputStream out = new FileOutputStream(file);
        if (m_useXml)
            new XmlWriter(out, getTree(), "GoGuiTwoGtp:" + Version.get());
        else
            new SgfWriter(out, getTree(), "GoGuiTwoGtp", Version.get());
    }

    private void saveResult(String resultBlack, String resultWhite,
                            String resultReferee, boolean alternated,
                            String duplicate, int numberMoves, boolean error,
                            String errorMessage, double timeBlack,
                            double timeWhite, double cpuTimeBlack,
                            double cpuTimeWhite)
        throws ErrorMessage
    {
        if (m_filePrefix.equals(""))
            return;
        NumberFormat format = StringUtil.getNumberFormat(1);
        m_table.startRow();
        m_table.set("GAME", Integer.toString(m_gameIndex));
        m_table.set("RES_B", resultBlack);
        m_table.set("RES_W", resultWhite);
        m_table.set("RES_R", resultReferee);
        m_table.set("ALT", alternated ? "1" : "0");
        m_table.set("DUP", duplicate);
        m_table.set("LEN", numberMoves);
        m_table.set("TIME_B", format.format(timeBlack));
        m_table.set("TIME_W", format.format(timeWhite));
        m_table.set("CPU_B", format.format(cpuTimeBlack));
        m_table.set("CPU_W", format.format(cpuTimeWhite));
        m_table.set("ERR", error ? "1" : "0");
        m_table.set("ERR_MSG", errorMessage);
        try
        {
            m_table.save(getResultFile());
        }
        catch (IOException e)
        {
            throw new ErrorMessage("Could not write to: " + getResultFile());
        }
    }

	final int OCCUPIED = -999999;
    
    private String lastOponentMovement = "";
    
    private void undo(Program expert) // Special undo function for Pachi
    		throws GtpError
    {
    	ConstGameTree tree = getTree();
    	ConstNode node = tree.getRootConst();
    	Move move;
    	
    	expert.send("clear_board");
    	
    	while (node.getNumberChildren() == 1)
    	{
    		move = node.getMove();

    		if (move != null)
    		{
    			//String point = getPoint(move.getPoint()); // What about pass?
    			GoPoint pointTmp = move.getPoint();
    			String point;
    			
    			if (pointTmp != null)
    				point = pointTmp.toString();
    			else
    				point = "pass";
    			
    			if (move.getColor() == BLACK)
    				expert.send("play B " + point);
    			else
    				expert.send("play W " + point);
    		}
            
            node = node.getChildConst();
    	}
    	
    	if (!lastOponentMovement.equals("")) // For some reason, the last oponent movement seemed to be missing...
    	{
    		expert.send("play " + oponentColorShort + " " + lastOponentMovement);
    	}
    }
    
    private boolean zeroMap(float map[][], int beginX, int endX, int beginY, int endY)
    {
    	for(int i = beginX; i < endX; i++)
    	{
    		for(int j = beginY; j < endY; j++)
    		{
    			if (map[i][j] != 0 && map[i][j] != OCCUPIED)
    				return false;
    		}    		
    	}
    	
    	return true;
    }
    
    private int moveNumber = -1;
    
    private int systemMoveNumber = 0;
    
    private void sendGenmove(GoColor color, StringBuilder response)
        throws GtpError, ErrorMessage, GtpResponseFormatError //Adding "GtpResponseFormatError" here for a while... 
    {
        checkInconsistentState();
        int moveNumber = m_game.getMoveNumber();
        if (m_maxMoves >= 0 && moveNumber > m_maxMoves)
            throw new GtpError("move limit exceeded");
        if (isInOpening())
        {
            ConstNode child = getCurrentNode().getChildConst();
            Move move = child.getMove();
            if (move.getColor() != color)
                throw new GtpError("next opening move is " + move);
            m_game.gotoNode(child);
            synchronize();
            response.append(GoPoint.toString(move.getPoint()));
            return;
        }
        Program program;
        boolean exchangeColors =
            (color == BLACK && isAlternated())
            || (color == WHITE && ! isAlternated());
        if (exchangeColors)
            program = m_white;
        else
            program = m_black;
        long timeMillis = System.currentTimeMillis();

        // SORIANO: MODIFICATION POINT
        String responseGenmove = "-1";
        
		int[][] vote = new int[4][2];
		int[][] votedMovs = new int[4][2];
		int numVotedMovs = 0;
		double [] numVotes = new double[4];
		String[] names = {"Alice", "Albert", "John", "Francesco"};
		int playX = -1;
		int playY = -1;
		GoPoint bestMove;		
		boolean ignoreExpert[] = {false, false, false, false};
		int votesResign = 0;
		int votesPass = 0;
		boolean pass = false;
		boolean resign = false;
		int numExpertsAlive = 0;
		boolean openingBook = false;		
		int nExperts = 4;
		boolean foundVote = false;
		//float[] moveValue = {-1, -1, -1, -1};
		
		int maxVotesPos = 0;
		double maxNumVotes = -1;
		int finalMaxVote = -1;
		int [] maxVotes = {-1, -1, -1, -1};
		
		moveNumber++;
		
		/*if (moveNumber >= 54) // Test for an arbitrary weight towards the end of the game
		{
			System.out.println("Playing with different weights now!..");
			m_weights[0] = 1.2;
			m_weights[1] = 0.5;
			m_weights[2] = 1.2;
			m_weights[3] = 0.5;
		}*/
		
		if (color == myColor)
		{			
			systemMoveNumber++;
			
			// The repetition looks terrible...
			if (m_weights[0] != 0)
			{
				if (!m_alice.isProgramDead())	
				{					
					numExpertsAlive++;
					
					String testFirstMove = m_alice.sendCommandGenmove(color);
					
					if (testFirstMove.equalsIgnoreCase("resign") || testFirstMove.equalsIgnoreCase("pass"))
					{
						responseGenmove = testFirstMove;
						
						System.out.println(testFirstMove);
						
						m_alice.send("showboard"); // For debug
						
						ignoreExpert[0] = true;
						
						if (testFirstMove.equalsIgnoreCase("resign")) // ** I guess I can't undo a resign...
							votesResign++;
						if (testFirstMove.equalsIgnoreCase("pass"))
						{
							votesPass++;
							m_alice.send("undo");
						}
					}
					else
					{
						//map[0] = buildQuickMap(m_alice, beginX, endX, beginY, endY, color);
						//hasMap[0] = true;
						//moveValue[0] = getValue(m_alice,color);
						if (m_alice.m_command.equals("/home/soriano/pesquisa/desenvolvimento/go/pachi/pachi"))
							undo(m_alice);
						else
							m_alice.send("undo");
						//map[0] = buildMap(m_alice, color);
						GoPoint tmp = GtpUtil.parsePoint(testFirstMove, getBoard().getSize());					
						vote[0][0] = tmp.getX();
						vote[0][1] = tmp.getY();
						
						// Old version for opening book test
						/*if (zeroMap(map[0], 0, 9, 0, 9)) // Is Alice playing opening book?..
						{
							openingBook = true;
							
							responseGenmove = testFirstMove;
							
							System.out.println("Playing opening book move " + responseGenmove);
							
							program.send("play " + myColorShort + " " + responseGenmove);	
						}*/
						
						// New version for opening book test
						if (((myColor == BLACK && moveNumber <= 14) ||
							(myColor == WHITE && moveNumber <=13)) && m_useOpeningDB) // Is Alice playing opening book?.. // Should be 14 for black?.. And 13 for white?..
						{
							openingBook = true;
							
							responseGenmove = testFirstMove;
							
							System.out.println("This might be an opening book move... Playing " + responseGenmove);
							
							program.send("play " + myColorShort + " " + responseGenmove);	
						}
					}
				}
				else
					ignoreExpert[0] = true;
			}
			else
			{
				if (((myColor == BLACK && moveNumber <= 14) ||
						(myColor == WHITE && moveNumber <=13)) && m_useOpeningDB) // Is Alice playing opening book?.. // Should be 14 for black?.. And 13 for white?..
					{						
						openingBook = true;
						
						responseGenmove =  m_alice.sendCommandGenmove(color);
						m_alice.send("undo");
												
						System.out.println("This might be an opening book move... Playing " + responseGenmove);
						
						program.send("play " + myColorShort + " " + responseGenmove);	
					}
				else
					ignoreExpert[0] = true;
			}			
			
			if (!openingBook) // I will only play the other experts if it is not an opening book move...
			{
				if (m_weights[1] != 0)
				{
					if (!m_albert.isProgramDead())
					{
						numExpertsAlive++;
						
						String testFirstMove = m_albert.sendCommandGenmove(color);
						
						if (testFirstMove.equalsIgnoreCase("resign") || testFirstMove.equalsIgnoreCase("pass"))
						{
							responseGenmove = testFirstMove;
							
							System.out.println(testFirstMove);
							
							//m_albert.send("showboard"); // For debug
							
							ignoreExpert[1] = true;
							
							if (testFirstMove.equalsIgnoreCase("resign")) // ** I guess I can't undo a resign...
								votesResign++;
							if (testFirstMove.equalsIgnoreCase("pass"))
							{
								votesPass++;
								m_albert.send("undo");
							}
						}
						else
						{
							//map[1] = buildQuickMap(m_albert, beginX, endX, beginY, endY, color);
							//hasMap[1] = true;
							//undo(m_albert);
							//moveValue[1] = getValue(m_albert,color);
							if (m_albert.m_command.equals("/home/soriano/pesquisa/desenvolvimento/go/pachi/pachi"))
								undo(m_albert);
							else
								m_albert.send("undo");
							//map[1] = buildMap(m_albert, color);
							GoPoint tmp = GtpUtil.parsePoint(testFirstMove, getBoard().getSize());					
							vote[1][0] = tmp.getX();
							vote[1][1] = tmp.getY();
						}
					}
					else
						ignoreExpert[1] = true;
				}
				else
					ignoreExpert[1] = true;
				
				if (m_weights[2] != 0)
				{
					if (!m_john.isProgramDead())
					{
						numExpertsAlive++;
						
						String testFirstMove = m_john.sendCommandGenmove(color);
						
						if (testFirstMove.equalsIgnoreCase("resign") || testFirstMove.equalsIgnoreCase("pass"))
						{
							responseGenmove = testFirstMove;
							
							System.out.println(testFirstMove);
							
							//m_john.send("showboard"); // For debug // Pachi can't showboard
							
							ignoreExpert[2] = true;
							
							if (testFirstMove.equalsIgnoreCase("resign"))
								votesResign++;
							if (testFirstMove.equalsIgnoreCase("pass")) // ** I guess I can't undo a resign...
							{
								votesPass++;
								m_john.send("undo");
							}
						}
						else
						{
							//map[2] = buildQuickMap(m_john, beginX, endX, beginY, endY, color);
							//hasMap[2] = true;
							//moveValue[2] = getValue(m_john,color);
							if (m_john.m_command.equals("/home/soriano/pesquisa/desenvolvimento/go/pachi/pachi"))								
								undo(m_john);
							else
								m_john.send("undo");
							//map[2] = buildMap(m_john, color);
							GoPoint tmp = GtpUtil.parsePoint(testFirstMove, getBoard().getSize());					
							vote[2][0] = tmp.getX();
							vote[2][1] = tmp.getY();
						}
					}
					else
						ignoreExpert[2] = true;
				}
				else
					ignoreExpert[2] = true;
				
				if (m_weights[3] != 0)
				{
					if (!m_francesco.isProgramDead())
					{
						numExpertsAlive++;
						
						String testFirstMove = m_francesco.sendCommandGenmove(color);
						
						if (testFirstMove.equalsIgnoreCase("resign") || testFirstMove.equalsIgnoreCase("pass"))
						{
							responseGenmove = testFirstMove;
							
							System.out.println(testFirstMove);
							
							m_francesco.send("showboard"); // For debug
							
							ignoreExpert[3] = true;
							
							if (testFirstMove.equalsIgnoreCase("resign")) // ** I guess I can't undo a resign...
								votesResign++;
							if (testFirstMove.equalsIgnoreCase("pass"))
							{
								votesPass++;
								m_francesco.send("undo");
							}
						}
						else
						{
							//map[3] = buildQuickMap(m_francesco, beginX, endX, beginY, endY, color);
							//hasMap[3] = true;
							//moveValue[3] = getValue(m_francesco,color);
							if (m_francesco.m_command.equals("/home/soriano/pesquisa/desenvolvimento/go/pachi/pachi"))
								undo(m_francesco);
							else
								m_francesco.send("undo");
							//map[3] = buildMap(m_francesco, color);
							
							GoPoint tmp = GtpUtil.parsePoint(testFirstMove, getBoard().getSize());					
							vote[3][0] = tmp.getX();
							vote[3][1] = tmp.getY();
						}
					}
					else
						ignoreExpert[3] = true;
				}
				else
					ignoreExpert[3] = true;
					
			
			//recursiveVotes(map);			
							
				
		    	for(int i = 0; i < nExperts; i++)
				{
		    		if (ignoreExpert[i])
		    		{
						System.out.println("Warning! I am ignoring " + names[i]);
		    			continue;
		    		}
		    			    								
					System.out.println(names[i] + " votes: " + GoPoint.get(vote[i][0], vote[i][1]).toString());
					
					foundVote = false;
					for(int j = 0; j < numVotedMovs; j++)
					{
						if (votedMovs[j][0] == vote[i][0] && votedMovs[j][1] == vote[i][1])
						{
							foundVote = true;
							numVotes[j] += m_weights[i];
						}
					}
					if (!foundVote)
					{
						votedMovs[numVotedMovs][0] = vote[i][0];
						votedMovs[numVotedMovs][1] = vote[i][1];
						numVotes[numVotedMovs] = m_weights[i];
								
						numVotedMovs++;						
					}
				}
						
				for(int i = 0; i < 4; i++)
				{
					System.out.println("Move: (" + votedMovs[i][0] + "," + votedMovs[i][1] + ") # of votes: " + numVotes[i]);
					
					if (numVotes[i] > maxNumVotes)
					{
						maxNumVotes = numVotes[i];
						maxVotes[0] = i;
						maxVotesPos = 1;
					}
					else if (numVotes[i] == maxNumVotes)
					{
						maxVotes[maxVotesPos] = i;
						maxVotesPos++;
					}
				}
				
				finalMaxVote = maxVotes[generator.nextInt(maxVotesPos)];
				
				System.out.println("Chosing move " + votedMovs[finalMaxVote][0] + "," + votedMovs[finalMaxVote][1]);
				
				playX = votedMovs[finalMaxVote][0];
				playY = votedMovs[finalMaxVote][1];
				
				// Write the program's votes to their respective log files
				// Doug Chen - ADDED:
				try
				{
		        	fw_votes = new FileWriter("votes_text_file.txt");
		        	fw_votes.write("");
		        	fw_votes.close();
		        	fw_votes = new FileWriter("votes_text_file.txt");
		        	
		        	try
		        	{
		        		fw_acceptedOpinions = new FileWriter("acceptedOpinions.txt");
		        	}
		        	catch (IOException e)
		        	{
		        		System.out.println("Error: Could not create the opinions files");
		        		System.exit(0);
		        	}
		        	
		        	acceptedOpinions = new BufferedWriter(fw_acceptedOpinions);
		        	
		        	try
		        	{
		        		fw_freqAgreed = new FileWriter("freqAgreed.txt");
		        	}
		        	catch (IOException e)
		        	{
		        		System.out.println("Error: Could not create the freqAgreed files");
		        		System.exit(0);
		        	}
		        	
		        	freqAgreed = new BufferedWriter(fw_freqAgreed);
		        	
		        	int numAgreedTmp = 0;
		        	
					for(int i = 0; i < nExperts; i++)
					{
						try
						{
							if (ignoreExpert[i])
							{
								expertLog[i].write(moveNumber + ", 4, -1 \n");
								fw_votes.write("Program "+i+": -1\n");
							}
							else
							{
								if (vote[i][0] == playX && vote[i][1] == playY)
								{
									//expertLog[i].write(moveNumber + ", 1, " + GoPoint.get(vote[i][0], vote[i][1]).toString() + ", " + moveValue[i] + "\n");
									expertLog[i].write("HELLO " + moveNumber + ", 1, " + GoPoint.get(vote[i][0], vote[i][1]).toString() + "\n");
									
									numAccepted[i]++;
									
									numAgreedTmp++;
								}
								else
								{
									//expertLog[i].write(moveNumber + ", 0, " + GoPoint.get(vote[i][0], vote[i][1]).toString() + ", " + moveValue[i] + "\n");								
									expertLog[i].write("HELLO2 " + moveNumber + ", 0, " + GoPoint.get(vote[i][0], vote[i][1]).toString() + "\n");
								}
								fw_votes.write("Program "+i+": "+vote[i][0]+" "+vote[i][1]+"\n");
								
								Float tmp = (float)(numAccepted[i])/(float)(systemMoveNumber);
								acceptedOpinions.write(tmp.toString()+"\n");
							}
						}
						catch(IOException e)
						{
							System.out.println("Error writing log");
						}						
					}
					
					numAgreed[numAgreedTmp-1]++;
					
					for(int i = 0; i < 4; i++)
					{
						Float tmp = (float)numAgreed[i]/(float)systemMoveNumber;
						freqAgreed.write(tmp.toString()+"\n");
					}
					
					fw_votes.close();
					acceptedOpinions.close();
					freqAgreed.close();
				} catch(IOException e) {
					System.out.println("Problem with votes_text_file.txt");
				}
				// Doug Chen - ADDED:
				ConstBoard theBoard = getBoard();
				
				
				String showboard = program.send("showboard");
				System.out.println(showboard);
				
				if ((votesPass + votesResign) <= numExpertsAlive/2) // To run faster, I will pass/resign if more than half wants too...
				{
					// Check how this point gets a piece played there
					bestMove = GoPoint.get(playX, playY);
						
					if (color == BLACK)
					{
						program.send("play B " + bestMove.toString());
						System.out.println("play B " + bestMove.toString());
					}
					else
					{
						program.send("play W " + bestMove.toString());						
						System.out.println("play W " + bestMove.toString());
					}
					
					// passes on the move to play to this guy?
					responseGenmove = bestMove.toString();
				}
				else
				{
					System.out.println("Ooops!.. Is it a pass or a resign?..");
					
			    	if (votesPass > 0) // If at least one wants to pass, I will pass
			    		responseGenmove = "pass";
			    	else
			    		responseGenmove = "resign";
				}
			}			
		}
		else
		{
			responseGenmove = program.sendCommandGenmove(color);
			lastOponentMovement = responseGenmove;
		}
		
	    double time = (System.currentTimeMillis() - timeMillis) / 1000.;
	    m_realTime.set(color, m_realTime.get(color) + time);
	    if (responseGenmove.equalsIgnoreCase("resign"))
	    {
	        response.append("resign");
	        m_resigned = true;
	        m_resignColor = color;
	    }	    
	    else
	    {
	        ConstBoard board = getBoard();
	        GoPoint point = null;
	        	        
	        try
	        {
	            point = GtpUtil.parsePoint(responseGenmove, board.getSize());
	        }
	        catch (GtpResponseFormatError e)
	        {
	            throw new GtpError(program.getLabel()
	                               + " played invalid move: "
	                               + responseGenmove);
	        }
	        Move move = Move.get(color, point);
	        m_game.play(move);
	        program.updateAfterGenmove(board);
	        synchronize();
	        response.append(GoPoint.toString(move.getPoint()));
	    }
	    if (gameOver() && ! m_gameSaved)
	    {
	        handleEndOfGame(false, "");
	        m_gameSaved = true;
	        
	        moveNumber = -1;
	        lastOponentMovement = "";
	        
	        for(int i = 0; i < nExperts; i++)
	        {
	        	try
	        	{
	        		expertLog[i].close();
	        	}
	        	catch (IOException e)
	        	{
	        		System.out.println("Error closing log file");
	        		System.exit(0);
	        	}
	        }
	    }
    }

    private void sendIfSupported(String cmd, String cmdLine)
    {
        for (Program program : m_allPrograms)
            program.sendIfSupported(cmd, cmdLine);
    }

    private void synchronize() throws GtpError
    {
        for (Program program : m_allPrograms)
            program.synchronize(m_game);
    }

    private void synchronizeInit() throws GtpError
    {
        for (Program program : m_allPrograms)
            program.synchronizeInit(m_game);
    }

    private void twogtpColor(Program program, GtpCommand cmd) throws GtpError
    {
        cmd.setResponse(program.send(cmd.getArgLine()));
    }

    private void twogtpObserver(GtpCommand cmd) throws GtpError
    {
        if (m_observer == null)
            throw new GtpError("no observer enabled");
        twogtpColor(m_observer, cmd);
    }

    private void twogtpReferee(GtpCommand cmd) throws GtpError
    {
        if (m_referee == null)
            throw new GtpError("no referee enabled");
        twogtpColor(m_referee, cmd);
    }
}
