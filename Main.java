import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;
import java.net.URL;
import javax.imageio.ImageIO;
import java.io.*;
import java.io.FileReader;
import java.util.Scanner;
import java.util.ArrayList;

public class Main {

    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    // private JButton[][] chessBoardSquares = new JButton[8][8];
    private JButton next = new JButton("NEXT");
    private JButton prev = new JButton("PREVIOUS");

    private JButton[][] chessBoardSquares;
    private Image[][] chessPieceImages = new Image[2][6];
    private JPanel chessBoard;
    public int dimension;
    int boardCount = 0; // stores the number of boards to be read
    int currBoardCtr = 0; // current board counter
    ArrayList <Integer> boardSizes = new ArrayList<Integer>(); // store the board sizes
    ArrayList <Board> boards = new ArrayList<Board>();

    ArrayList <ArrayList <Integer>> tempBoard =  new ArrayList <ArrayList <Integer>>(); 
        // temp board to store the currently read board 

    // private final JLabel message = new JLabel(
    //         "Chess Champ is ready to play!");
    private static final String COLS = "ABCDEFGH";
    // public static final int QUEEN = 0, KING = 1,
    //         ROOK = 2, KNIGHT = 3, BISHOP = 4, PAWN = 5;
    // public static final int[] STARTING_ROW = {
    //     ROOK, KNIGHT, BISHOP, KING, QUEEN, BISHOP, KNIGHT, ROOK
    // };
    public static final int BLACK = 0, WHITE = 1;

    public Main() {
        readFile();
        this.dimension = boardSizes.get(0);
        this.chessBoardSquares = new JButton[this.dimension][this.dimension];
        initializeGui();
    }

    public void readFile(){
        Scanner br1 = null;
        BufferedWriter bw = null;
        File readFile = null;
        
        int lineNum = 0; // Line number checker, not 0 based 
        int currRowCtr = 0; // counter for the current row in the current board 

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

                // get the next lines/rows for the current board's input 

                for (int i=0; i<boardSizes.get(currBoardCtr); i++){
                    tempBoard.add(new ArrayList <Integer>());
                    String rowLine = br1.nextLine();
                    // System.out.println(rowLine);

                    String[] tokens = rowLine.split(" ");
                    // store each token to current row
                    for (String token : tokens){
                        tempBoard.get(currRowCtr).add(Integer.parseInt(token));
                    }
                    currRowCtr++;
                }

                Board newBoard = new Board(tempBoard, boardSizes.get(currBoardCtr)); 
                newBoard.printBoard();
                System.out.println();

                boards.add(newBoard);

                // delete the tempBoard
                for (int i=0; i<currRowCtr; i++){
                    tempBoard.get(i).clear(); 
                } 
                tempBoard.clear();

                currRowCtr = 0; // reset to the first row 
                currBoardCtr++; // next board 
                // System.out.println();
              }
            }catch(Exception e){
             System.out.println("Some error " + e);
          }
    }

    public final void initializeGui() {
        // create the images for the chess pieces
        // createImages();

        // set up the main GUI
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);
        // Action newGameAction = new AbstractAction("New") {

        //     @Override
        //     public void actionPerformed(ActionEvent e) {
        //         setupNewGame();
        //     }
        // };
        // tools.add(newGameAction);
        tools.add(new JButton("Prev")); // TODO - add functionality!
        tools.add(new JButton("Next")); // TODO - add functionality!
        tools.addSeparator();
        tools.add(new JButton("Solve")); // TODO - add functionality!
        // tools.addSeparator();
        // tools.add(message);

        // gui.add(new JLabel("?"), BorderLayout.LINE_START);

        chessBoard = new JPanel(new GridLayout(0, (dimension+1))) {

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
                new EmptyBorder(dimension,dimension,dimension,dimension),
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
                if ((jj % 2 == 1 && ii % 2 == 1)
                        //) {
                        || (jj % 2 == 0 && ii % 2 == 0)) {
                    b.setBackground(Color.WHITE);
                } else {
                    b.setBackground(Color.BLACK);
                }
                chessBoardSquares[jj][ii] = b;
            }
        }

        /*
         * fill the chess board
         */
        chessBoard.add(new JLabel(""));
        // fill the top row
        for (int ii = 0; ii < dimension; ii++) {
            chessBoard.add(
                    new JLabel(COLS.substring(ii, ii + 1),
                    SwingConstants.CENTER));
        }
        // fill the black non-pawn piece row
        for (int ii = 0; ii < dimension; ii++) {
            for (int jj = 0; jj < dimension; jj++) {
                switch (jj) {
                    case 0:
                        chessBoard.add(new JLabel("" + (9-(ii + 1)),
                                SwingConstants.CENTER));
                    default:
                        chessBoard.add(chessBoardSquares[jj][ii]);
                        chessBoardSquares[jj][ii].addActionListener(buttonHandler);
                }
            }
        }
    }

    private void processClick(int i, int j){
    	chessBoardSquares[i][j].setBackground(Color.RED);
    }

    private class ButtonHandler implements ActionListener{
    	public void actionPerformed(ActionEvent e){
    		Object source = e.getSource();
    		for(int i=0;i<dimension;i++){
    			for(int j=0;j<dimension;j++){
    				if(source==chessBoardSquares[i][j]){
    					System.out.println("i: "+i+" j: "+j);
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

    // private final void createImages() {
    //     try {
    //         URL url = new URL("http://i.stack.imgur.com/memI0.png");
    //         BufferedImage bi = ImageIO.read(url);
    //         for (int ii = 0; ii < 2; ii++) {
    //             for (int jj = 0; jj < 6; jj++) {
    //                 chessPieceImages[ii][jj] = bi.getSubimage(
    //                         jj * 64, ii * 64, 64, 64);
    //             }
    //         }
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         System.exit(1);
    //     }
    // }

    /**
     * Initializes the icons of the initial chess board piece places
     */
    // private final void setupNewGame() {
    //     message.setText("Make your move!");
    //     // set up the black pieces
    //     for (int ii = 0; ii < STARTING_ROW.length; ii++) {
    //         chessBoardSquares[ii][0].setIcon(new ImageIcon(
    //                 chessPieceImages[BLACK][STARTING_ROW[ii]]));
    //     }
    //     for (int ii = 0; ii < STARTING_ROW.length; ii++) {
    //         chessBoardSquares[ii][1].setIcon(new ImageIcon(
    //                 chessPieceImages[BLACK][PAWN]));
    //     }
    //     // set up the white pieces
    //     for (int ii = 0; ii < STARTING_ROW.length; ii++) {
    //         chessBoardSquares[ii][6].setIcon(new ImageIcon(
    //                 chessPieceImages[WHITE][PAWN]));
    //     }
    //     for (int ii = 0; ii < STARTING_ROW.length; ii++) {
    //         chessBoardSquares[ii][7].setIcon(new ImageIcon(
    //                 chessPieceImages[WHITE][STARTING_ROW[ii]]));
    //     }
    // }

    public static void main(String[] args) {
        
        Main cg = new Main();
        JFrame f = new JFrame("ChessChamp");
        f.add(cg.getGui());
        // Ensures JVM closes after frame(s) closed and
        // all non-daemon threads are finished
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // See https://stackoverflow.com/a/7143398/418556 for demo.
        f.setLocationByPlatform(true);

        // ensures the frame is the minimum size it needs to be
        // in order display the components within it
        f.pack();
        // ensures the minimum size is enforced.
        f.setMinimumSize(f.getSize());
        f.setVisible(true);
    }
}