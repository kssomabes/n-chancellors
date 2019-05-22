import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.imageio.ImageIO;
import java.io.File;
// import java.lang.module.FindException;
import java.io.BufferedWriter;
import java.util.Scanner;
import java.util.ArrayList;

public class Main {
    private JFrame f, solutionFrame;
    private JPanel gui, solutionPanel;
    JTextField insertField = new JTextField(2);
    JLabel insertLabel = new JLabel("Insert Size:");

    private JButton[][] chessBoardSquares;
    private Image[][] chessPieceImages = new Image[2][6];

    private JPanel chessBoard;
    private JButton[][] solutionBoardSquares;
    private Image[][] solutionChessPieceImages = new Image[2][6];
    private JPanel solutionBoard;
    public int dimension;
    int boardCount = 0; // stores the number of boards to be read
    int currBoardCtr = 0; // current board counter
    int currentSolutionBoardCounter = 0;
    int currentNumberOfSolutions = 0;
    int inputSize = 0; // for user input size n board 

    ArrayList <Integer> boardSizes = new ArrayList<Integer>(); // store the board sizes
    ArrayList <Board> boards = new ArrayList<Board>();
    ArrayList <Board> solutionBoards = new ArrayList<Board>();
    
    ArrayList <ArrayList <Integer>> tempBoard =  new ArrayList <ArrayList <Integer>>(); 
    ArrayList <Coordinate[]> solutions = new ArrayList <Coordinate[]>();

    public static final int BLACK = 0, WHITE = 1;

    public Main() {
        readFile(null);
        
        f = new JFrame("Where is Chancy?");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLocationByPlatform(true);
        f.pack();
        f.setMinimumSize(f.getSize());
        f.setVisible(true);

        initializeGui();
        loadBoard();
    }

    public void readFile(File selectedFile){
        Scanner br1 = null;
        BufferedWriter bw = null;
        File readFile = null;
        
        int lineNum = 0; // Line number checker, not 0 based 
        int currRowCtr = 0; // counter for the current row in the current board 
        int tokenCtr = 0; // token counter / y coordinate

        try{
            // default file to load upon start up is input.in
            String fileName = (selectedFile == null) ? "input.in" : selectedFile.getName(); 
            readFile = new File(fileName);  
            br1 = new Scanner(readFile);

            // reset the values
            boards.clear();
            boardSizes.clear();
            for (int i=0; i<tempBoard.size(); i++){
                tempBoard.get(i).clear();
            }
            tempBoard.clear();
            boardCount = 0;
            currBoardCtr = 0;

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

                // add new board from read file  with initial chancies
                boards.add(newBoard);

                // delete the tempBoard
                for (int i=0; i<currRowCtr; i++){
                    tempBoard.get(i).clear(); 
                } 

                tempBoard.clear();

                currRowCtr = 0; // reset to the first row 
                tokenCtr = 0; // reset token / y ctr
                currBoardCtr++; // next board 
              }

            currBoardCtr = 0; // return to 0
        }catch(Exception e){
            System.out.println("Some error " + e);
        }
    }

    public final void initializeGui() {
        gui = new JPanel(new BorderLayout(3, 3));

        // set up the main GUI
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);

        // insert label, field and button for user input size
        tools.add(insertLabel);
        tools.add(insertField);
        JButton loadN = new JButton("Load Size");
        loadN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                inputSize = Integer.parseInt(insertField.getText());
                if (inputSize > 0){
                    boards.clear();
                    boardSizes.clear();
                    tempBoard.clear();

                    boardSizes.add(inputSize);

                    // create new board with size inputSize
                    boards.add(new Board(inputSize));

                    boardCount = 1; // since only 1 board can be generated at once
                    currBoardCtr = 0; 

                    f.remove(gui);
                    initializeGui();
                    loadBoard();
                }
            }
        });
        tools.add(loadN);

        // insert file chooser 
        JButton fileButton = new JButton("Select File");
        fileButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                System.out.println(selectedFile.getName());
                inputSize = 0;
                readFile(selectedFile);
                f.remove(gui);
                initializeGui();
                loadBoard();
            }
           
          }
        });
        tools.add(fileButton);

        tools.addSeparator();

        Action prevBoardAction = new AbstractAction("Prev") {

            @Override
            public void actionPerformed(ActionEvent e) {
                prevBoard();
            }
        };
        tools.add(prevBoardAction);

        Action nextBoardAction = new AbstractAction("Next") {

            @Override
            public void actionPerformed(ActionEvent e) {
                nextBoard();
            }
        };
        tools.add(nextBoardAction);
        
        tools.addSeparator();
        
        // insert solve button (only shows the total number of solutions without the UI)
        Action solveBoardAction = new AbstractAction("Solve") {

            @Override
            public void actionPerformed(ActionEvent e) {
                // boards.get(currBoardCtr).printBoard();
                boards.get(currBoardCtr).solveBoard();
            }
        };
        tools.add(solveBoardAction);

        tools.addSeparator();
        
        // show solution with UI
        Action showSolBoardAction = new AbstractAction("Show Solutions") {

            @Override
            public void actionPerformed(ActionEvent e) {

            	//creates solutions board arraylist to add all solutions
                solutions.clear(); 
            	solutionBoards.clear();
            	boards.get(currBoardCtr).solveBoard();

                if (boards.get(currBoardCtr).solutions.size() > 0){
                    currentSolutionBoardCounter = 0;
                
                    solutions = boards.get(currBoardCtr).loadSolution();
                    currentNumberOfSolutions = solutions.size();
                    while(currentSolutionBoardCounter < currentNumberOfSolutions) {
                        int dimension = boardSizes.get(currBoardCtr);
                        ArrayList<Coordinate> solutionChancies = new ArrayList<Coordinate>();
                        
                        
                        for(int i=0; i<solutions.get(currentSolutionBoardCounter).length; i++) {
                            tempBoard.add(new ArrayList<Integer>());
                            solutionChancies.add(solutions.get(currentSolutionBoardCounter)[i]);
                            for(int j=0; j<dimension; j++) {
                                tempBoard.get(i).add(0);
                            }
                        }
                        for(int i=0; i<solutions.get(currentSolutionBoardCounter).length; i++) {
                            int x = solutions.get(currentSolutionBoardCounter)[i].x;
                            int y = solutions.get(currentSolutionBoardCounter)[i].y;
                            tempBoard.get(x-1).set(y-1, 1);
                        }
                        
                        solutionBoards.add(new Board(tempBoard, dimension, solutionChancies));
                        for (int i=0; i<tempBoard.size(); i++){
                            tempBoard.get(i).clear(); 
                        } 
                        tempBoard.clear();  
                        currentSolutionBoardCounter++;
                    }
                    
                    currentSolutionBoardCounter = 0;
                    
                    solutionFrame = new JFrame("Solutions");
                    solutionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    solutionFrame.setLocationByPlatform(true);
                    solutionFrame.pack();
                    solutionFrame.setMinimumSize(solutionFrame.getSize());
                    solutionFrame.setVisible(true);
                    showSolutions();

                }else { 
                    JOptionPane.showMessageDialog(null, "No solution", "Where is Chancy?", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        tools.add(showSolBoardAction);


    }
    
    /*ALL SOLUTIONS METHODS*/
    
    private void showSolutions() {
    	solutionPanel = new JPanel(new BorderLayout(3, 3));
        
        solutionPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        JToolBar solutionTools = new JToolBar();
        solutionTools.setFloatable(false);
        solutionPanel.add(solutionTools, BorderLayout.PAGE_START);

        Action prevSolutionAction = new AbstractAction("Prev") {

            @Override
            public void actionPerformed(ActionEvent e) {
                prevSolution();
            }
        };
        solutionTools.add(prevSolutionAction);

        Action nextSolutionAction = new AbstractAction("Next") {

            @Override
            public void actionPerformed(ActionEvent e) {
                nextSolution();
            }
        };
        solutionTools.add(nextSolutionAction);
        JLabel solCtr = new JLabel("Solutions: " + boards.get(currBoardCtr).solutions.size());

        solutionTools.add(solCtr);
//        solutionFrame.add(solutionPanel);
        loadSolutionBoards();
    }
    
    private void loadSolutionBoards() {
    	int dimension = boardSizes.get(currBoardCtr);
        JButton[][] chessBoardSquares = new JButton[dimension][dimension];

        JPanel chessBoard = new JPanel(new GridLayout(0, dimension)) {

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
        solutionPanel.add(boardConstrain);

        ButtonHandler buttonHandler = new ButtonHandler();

        // create the chess board squares
        Insets buttonMargin = new Insets(0, 0, 0, 0);
        for (int ii = 0; ii < chessBoardSquares.length; ii++) {
            for (int jj = 0; jj < chessBoardSquares[ii].length; jj++) {
                JButton b = new JButton();
                b.setMargin(buttonMargin);
                // our chess pieces are 32x32 px in size, so we'll
                // 'fill this in' using a transparent icon..
                ImageIcon icon = new ImageIcon(
                        new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB));    
                b.setIcon(icon);
                b.setEnabled(false);
                solutionButtonColor(b,ii,jj);	
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
            }
        }

        solutionFrame.add(solutionPanel);
        solutionFrame.pack();
    }
    
    private void solutionButtonColor(JButton b, int x, int y){
        if (solutionBoards.get(currentSolutionBoardCounter).board.get(x).get(y) == 1){
            // Tiles with chancies 
            b.setBackground(Color.RED);
        }else{
            if ((y % 2 == 1 && x % 2 == 1)
                || (y % 2 == 0 && x % 2 == 0)) {
                b.setBackground(Color.WHITE);
            } else {
                b.setBackground(Color.BLACK);
            }
        }
    }
    
    /*ALL MAIN BOARD METHODS*/

    private void loadBoard(){

        dimension = boardSizes.get(currBoardCtr);

        chessBoardSquares = new JButton[dimension][dimension];

        chessBoard = new JPanel(new GridLayout(0, dimension)) {

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
                // our chess pieces are 32x32 px in size, so we'll
                // 'fill this in' using a transparent icon..
                ImageIcon icon = new ImageIcon(
                        new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB));    
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
    }

    private static Icon resizeIcon(ImageIcon icon, int resizedWidth, int resizedHeight) {
        Image img = icon.getImage();  
        Image resizedImage = img.getScaledInstance(resizedWidth, resizedHeight,  java.awt.Image.SCALE_SMOOTH);  
        return new ImageIcon(resizedImage);
    }

    private void setButtonColor(JButton b, int x, int y){
        if (boards.get(currBoardCtr).board.get(x).get(y) == 1){
            // Tiles with chancies 

            // try {
            //     ImageIcon img = new ImageIcon("chancy.jpg");

            //     int offset = b.getInsets().left;
            //     b.setIcon(resizeIcon(img, b.getWidth() - offset, b.getHeight() - offset));

            //     // b.setIcon(new ImageIcon(img));
            //     // b.setSize(32,32);
            // } catch (Exception e){
                b.setBackground(Color.RED);
            // }

        } else{
            if ((y % 2 == 1 && x % 2 == 1) 
                || (y % 2 == 0 && x % 2 == 0)) {
                b.setBackground(Color.WHITE);
            } else {
                b.setBackground(Color.BLACK);
            }
        }
    }
    
    
    private void processClick(int i, int j){
        // currBoardCtr holds the currently selected board, modify the board 
        // reverse j and i because of different orientation
        int flag = (boards.get(currBoardCtr).chancies.contains(new Coordinate(j+1,i+1))) ? 0 : 1;
        
        // if flag is 0, remove chancy
        // else if flag is 1, add chancy 
        boards.get(currBoardCtr).modifyBoard(flag, j+1, i+1); 

        if (flag == 1){ 
            //  try {
            //     ImageIcon img = new ImageIcon("chancy.jpg");

            //     int offset = chessBoardSquares[i][j].getInsets().left;
            //     chessBoardSquares[i][j].setIcon(resizeIcon(img, chessBoardSquares[i][j].getWidth() - offset, chessBoardSquares[i][j].getHeight() - offset));

            //     // chessBoardSquares[i][j].setIcon(new ImageIcon(img));
            //     // chessBoardSquares[i][j].setSize(32,32);
            // } catch (Exception e){
                chessBoardSquares[i][j].setBackground(Color.RED);
            // }
        } else {
            if ((j % 2 == 1 && i % 2 == 1) || (j % 2 == 0 && i % 2 == 0)) {
                chessBoardSquares[i][j].setBackground(Color.WHITE);
            } else {
                chessBoardSquares[i][j].setBackground(Color.BLACK);
            }
        }
    }

    private class ButtonHandler implements ActionListener{
    	public void actionPerformed(ActionEvent e){
    		Object source = e.getSource();
    		for(int i=0;i<dimension;i++){
    			for(int j=0;j<dimension;j++){
    				if(source==chessBoardSquares[i][j]){
    					processClick(i,j);
    					return;
    				}
    			}
    		}
    	}
    }

    /* METHODS FOR TRAVERSING BOARDS IN UI */
    private void nextBoard() {
        f.remove(gui);
        if (currBoardCtr < boardCount-1){
            currBoardCtr+=1;
            solutionBoards.clear();
        }
        initializeGui();
        loadBoard();

    }

    private void prevBoard() {
        f.remove(gui);
        if (currBoardCtr >= 1){
            currBoardCtr-=1;
            solutionBoards.clear();
        }
        initializeGui();
        loadBoard();
    }
    
    /* EXCLUSIVE FOR SOLUTION WINDOW */
    private void nextSolution() {
    	solutionFrame.remove(solutionPanel);
        if (currentSolutionBoardCounter < currentNumberOfSolutions-1){
        	currentSolutionBoardCounter+=1;
        }
        showSolutions();

    }

    private void prevSolution() {
    	solutionFrame.remove(solutionPanel);
        if (currentSolutionBoardCounter >= 1){
        	currentSolutionBoardCounter-=1;
        }
        showSolutions();
    }
    
    public static void main(String[] args) {
        
        Main cg = new Main();
    }
}