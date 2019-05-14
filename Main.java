import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.BufferedWriter;
import java.util.Scanner;
import java.util.ArrayList;

public class Main {
    private JFrame f;
    // private JFrame f = new JFrame("Where is Chancy?");
    private JPanel gui;
    // private JButton[][] chessBoardSquares = new JButton[8][8];
    
    private JButton[][] chessBoardSquares;
    private Image[][] chessPieceImages = new Image[2][6];
    private JPanel chessBoard;
    public int dimension;
    int boardCount = 0; // stores the number of boards to be read
    int currBoardCtr = 0; // current board counter
    int uiBoardCounter = 0;

    ArrayList <Integer> boardSizes = new ArrayList<Integer>(); // store the board sizes
    ArrayList <Board> boards = new ArrayList<Board>();
    // ArrayList <BoardSquares> uiBoards = new ArrayList<BoardSquares>();

    ArrayList <ArrayList <Integer>> tempBoard =  new ArrayList <ArrayList <Integer>>(); 

    public static final int BLACK = 0, WHITE = 1;

    public Main() {
        readFile();
        // f.add(cg.getGui());
        f = new JFrame("Where is Chancy?");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLocationByPlatform(true);
        f.pack();
        // ensures the minimum size is enforced.
        f.setMinimumSize(f.getSize());
        f.setVisible(true);

        
        initializeGui();
    }

    public void readFile(){
        Scanner br1 = null;
        BufferedWriter bw = null;
        File readFile = null;
        
        int lineNum = 0; // Line number checker, not 0 based 
        int currRowCtr = 0; // counter for the current row in the current board 
        int tokenCtr = 0; // token counter / y coordinate

        try{
              readFile = new File("input.in");  
              br1 = new Scanner(readFile);

              if (br1.hasNext()){
                // get the number of boards first
                if (lineNum == 0){
                  String line = br1.nextLine(); 
                  boardCount = Integer.parseInt(line);
                  lineNum = 1; 
                }
              }

              while (currBoardCtr < boardCount){
                String boardSizeLine = br1.nextLine();
                Integer currBoardSize = Integer.parseInt(boardSizeLine);
                boardSizes.add(currBoardSize);

                ArrayList <Coordinate> tempChancies = new ArrayList<Coordinate>();
                // positions of the initial chancies from the input file

                // get the next lines/rows for the current board's input 

                // i => number of rows
                for (int i=0; i<boardSizes.get(currBoardCtr); i++){
                    tokenCtr = 0;

                    tempBoard.add(new ArrayList <Integer>());
                    String rowLine = br1.nextLine();
                    // System.out.println(rowLine);

                    String[] tokens = rowLine.split(" ");
                    // store each token to current row
                    for (String token : tokens){
                        if (token.equals("1")){
                            tempChancies.add(new Coordinate(i+1, tokenCtr+1));
                        }
                        tempBoard.get(currRowCtr).add(Integer.parseInt(token));
                        tokenCtr++; 
                    }

                    currRowCtr++;
                }

                Board newBoard = new Board(tempBoard, boardSizes.get(currBoardCtr), tempChancies); 
                newBoard.printBoard();
                System.out.println();
                // newBoard.printChancies();
                // System.out.println();
                newBoard.solveBoard();
                System.out.println();


                // added new board with initial chancies
                boards.add(newBoard);

                // delete the tempBoard
                for (int i=0; i<currRowCtr; i++){
                    tempBoard.get(i).clear(); 
                } 
                tempBoard.clear();

                currRowCtr = 0; // reset to the first row 
                tokenCtr = 0; // reset token / y ctr
                currBoardCtr++; // next board 
                // System.out.println();
              }

            currBoardCtr = 0; // return to 0
        }catch(Exception e){
            System.out.println("Some error " + e);
        }
    }

    public void solve(){

    }

    public final void initializeGui() {
        System.out.println(boardSizes.get(uiBoardCounter));
        // f = new JFrame("Where is Chancy?");

        gui = new JPanel(new BorderLayout(3, 3));

        // set up the main GUI
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);
        Action nextBoardAction = new AbstractAction("Next") {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(uiBoardCounter == (boardCount-2)) {
                    System.out.println(uiBoardCounter);
                    setEnabled(false);
                }
                else setEnabled(true);
                nextBoard();
            }
        };
        tools.add(nextBoardAction);
        // tools.add(new JButton("Prev")); // TODO - add functionality!
        // tools.add(new JButton("Next")); // TODO - add functionality!

        tools.addSeparator();
        // tools.add(new JButton("Solve")); // TODO - add functionality!
        Action solveBoardAction = new AbstractAction("Solve") {

            @Override
            public void actionPerformed(ActionEvent e) {
                boards.get(uiBoardCounter).solveBoard();
            }
        };
        tools.add(solveBoardAction);

        tools.addSeparator();
        // tools.add(new JButton("Solve")); // TODO - add functionality!
        Action showSolBoardAction = new AbstractAction("Show Solution") {

            @Override
            public void actionPerformed(ActionEvent e) {
                boards.get(uiBoardCounter).loadSolution();
            }
        };
        tools.add(showSolBoardAction);
        // tools.addSeparator();
        // tools.add(message);

        // gui.add(new JLabel("?"), BorderLayout.LINE_START);
        loadBoard();

        
    }

    private void loadBoard(){
        dimension = boardSizes.get(uiBoardCounter);
        chessBoardSquares = new JButton[dimension][dimension];

        chessBoard = new JPanel(new GridLayout(0, boardSizes.get(uiBoardCounter))) {

            /**
             * Override the preferred size to return the largest it can, in
             * a square shape.  Must (must, must) be added to a GridBagLayout
             * as the only component (it uses the parent as a guide to size)
             * with no GridBagConstaint (so it is centered).
             */
            @Override
            public final Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                Dimension prefSize = null;
                Component c = getParent();
                if (c == null) {
                    prefSize = new Dimension(
                            (int)d.getWidth(),(int)d.getHeight());
                } else if (c!=null &&
                        c.getWidth()>d.getWidth() &&
                        c.getHeight()>d.getHeight()) {
                    prefSize = c.getSize();
                } else {
                    prefSize = d;
                }
                int w = (int) prefSize.getWidth();
                int h = (int) prefSize.getHeight();
                // the smaller of the two sizes
                int s = (w>h ? h : w);
                return new Dimension(s,s);
            }
        };
        chessBoard.setBorder(new CompoundBorder(
                new EmptyBorder(boardSizes.get(uiBoardCounter),boardSizes.get(uiBoardCounter),boardSizes.get(uiBoardCounter),boardSizes.get(uiBoardCounter)),
                new LineBorder(Color.BLACK)
                ));
        // Set the BG to be ochre
        Color ochre = new Color(204,119,34);
        chessBoard.setBackground(ochre);
        JPanel boardConstrain = new JPanel(new GridBagLayout());
        boardConstrain.setBackground(ochre);
        boardConstrain.add(chessBoard);
        gui.add(boardConstrain);

        ButtonHandler buttonHandler = new ButtonHandler();

        // create the chess board squares
        Insets buttonMargin = new Insets(0, 0, 0, 0);
        for (int ii = 0; ii < chessBoardSquares.length; ii++) {
            for (int jj = 0; jj < chessBoardSquares[ii].length; jj++) {
                JButton b = new JButton();
                b.setMargin(buttonMargin);
                // our chess pieces are 64x64 px in size, so we'll
                // 'fill this in' using a transparent icon..
                ImageIcon icon = new ImageIcon(
                        new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));    
                b.setIcon(icon);
                setButtonColor(b,ii,jj);
                chessBoardSquares[jj][ii] = b;
            }            
        }

        /*
         * fill the chess board
         */
        // fill the black non-pawn piece row
        for (int ii = 0; ii < dimension; ii++) {
            for (int jj = 0; jj < dimension; jj++) {
                chessBoard.add(chessBoardSquares[jj][ii]);
                chessBoardSquares[jj][ii].addActionListener(buttonHandler);
            }
        }

        f.add(gui);
        f.pack();
        // f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // f.setLocationByPlatform(true);
        // f.pack();
        // // ensures the minimum size is enforced.
        // f.setMinimumSize(f.getSize());
        // f.setVisible(true);
        
    }

    private void setButtonColor(JButton b, int x, int y){
        if (boards.get(uiBoardCounter).board.get(x).get(y) == 1){
            // Tiles with chancies 
            b.setBackground(Color.RED);
        }else{
            if ((y % 2 == 1 && x % 2 == 1)
                    //) {
                    || (y % 2 == 0 && x % 2 == 0)) {
                b.setBackground(Color.WHITE);
            } else {
                b.setBackground(Color.BLACK);
            }
        }
    }

    private void processClick(int i, int j){
        // uiBoardCounter holds the currently selected board, modify the board 

        // boards.get(uiBoardCounter).printChancies();
        // reverse j and i because of different orientation
        int flag = (boards.get(uiBoardCounter).chancies.contains(new Coordinate(j+1,i+1))) ? 0 : 1;
        // System.out.println("Flag is " + flag );    

        // if flag is 0, remove chancy
        // else if flag is 1, add chancy 
        boards.get(uiBoardCounter).modifyBoard(flag, j+1, i+1); 

        if (flag == 1) chessBoardSquares[i][j].setBackground(Color.RED);
        else {
            if ((j % 2 == 1 && i % 2 == 1) || (j % 2 == 0 && i % 2 == 0)) {
                chessBoardSquares[i][j].setBackground(Color.WHITE);
            } else {
                chessBoardSquares[i][j].setBackground(Color.BLACK);
            }}
    }

    private class ButtonHandler implements ActionListener{
    	public void actionPerformed(ActionEvent e){
    		Object source = e.getSource();
    		for(int i=0;i<dimension;i++){
    			for(int j=0;j<dimension;j++){
    				if(source==chessBoardSquares[i][j]){
    					// System.out.println("buttonHandler " + (i+1)+ " " + (j+1));
    					processClick(i,j);
    					return;
    				}
    			}
    		}
    	}
    }

    public final JComponent getGui() {
        return gui;
    }

    
    private void nextBoard() {
        if(uiBoardCounter !=boardCount){
            f.remove(gui);
            uiBoardCounter+=1;
            initializeGui();            
        }

    }
    
    public static void main(String[] args) {
        
        Main cg = new Main();
        // JFrame f = new JFrame("Where is Chancy?");
        // f.add(cg.getGui());
        // f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // f.setLocationByPlatform(true);
        // f.pack();
        // // ensures the minimum size is enforced.
        // f.setMinimumSize(f.getSize());
        // f.setVisible(true);
    }
}