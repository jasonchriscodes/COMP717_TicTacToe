import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.BevelBorder;

@SuppressWarnings("serial")
public class TicTacToeUI extends JFrame implements ActionListener {
    private JButton[][] buttons;
    private char[][] board;
    private static final int BOARD_SIZE = 3;
    private static final char EMPTY_BTN = ' ';
    private static final char PLAYER_X = 'X';
    private static final char PLAYER_O = 'O';
    private boolean playerXTurn;
    private JComboBox<String> algorithmDropdown;
    private JComboBox<String> turnDropdown;
    private JComboBox<String> depthDropdown;
    private boolean isMinimaxLimited;

    /* A method which defines the main UI of the Game */
    public TicTacToeUI() {
    	getContentPane().setBackground(new Color(114, 159, 207));
    	setResizable(false);
        setTitle("Tic-Tac-Toe");
        setSize(549, 551);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(143, 188, 143));
        mainPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

        // Create game board panel
        JPanel boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        boardPanel.setBounds(72, 24, 353, 275);
        buttons = new JButton[BOARD_SIZE][BOARD_SIZE];
        board = new char[BOARD_SIZE][BOARD_SIZE];
        initializeBoard();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                buttons[i][j] = new JButton();                
                buttons[i][j].setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 60));    
                buttons[i][j].setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
                buttons[i][j].setBackground(new Color(240, 248, 255));
                buttons[i][j].addActionListener(this);
                boardPanel.add(buttons[i][j]);
            }
        }
        mainPanel.setBounds(22, 23, 494, 464);
        mainPanel.setLayout(null);
        mainPanel.add(boardPanel);

        // Create options panel
        JPanel optionsPanel = new JPanel();
        optionsPanel.setBackground(new Color(240, 248, 255));
        optionsPanel.setBounds(72, 311, 353, 130);
        algorithmDropdown = new JComboBox<>(new String[]{"Minimax Complete", "Minimax Limited"});
        algorithmDropdown.setBounds(165, 12, 176, 25);
        optionsPanel.setLayout(null);
        JLabel label = new JLabel("Algorithm:");
        label.setBounds(10, 12, 176, 25);
        optionsPanel.add(label);
        optionsPanel.add(algorithmDropdown);

        turnDropdown = new JComboBox<>(new String[]{"Human", "Computer"});
        turnDropdown.setBounds(200, 37, 141, 25);
        JLabel lblFirstTurn = new JLabel("First Turn:");
        lblFirstTurn.setBounds(10, 37, 176, 25);
        optionsPanel.add(lblFirstTurn);
        optionsPanel.add(turnDropdown);

        depthDropdown = new JComboBox<>(new String[]{"1", "2", "3", "4", "5"}); // Adjust the depth options as needed
        depthDropdown.setBounds(239, 62, 102, 25);
        JLabel label_2 = new JLabel("Depth:");
        label_2.setBounds(10, 62, 176, 25);
        optionsPanel.add(label_2);
        optionsPanel.add(depthDropdown);

        JButton startButton = new JButton("Start");
        startButton.setBackground(new Color(78, 154, 6));
        startButton.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
        startButton.setBounds(0, 105, 353, 25);
        startButton.addActionListener(e -> startGame());
        getContentPane().setLayout(null);
        optionsPanel.add(startButton);

        mainPanel.add(optionsPanel);
        getContentPane().add(mainPanel);

        playerXTurn = true;
    }

    
    /* A Method to initialize the Game main Board*/
    private void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = EMPTY_BTN;
            }
        }
    }

    
    /* A Method to print the Board UI */
    private void printBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                buttons[i][j].setText(Character.toString(board[i][j]));
            }
        }
    }

    
    /* A Method to check if the move is Valid or not */
    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE && board[row][col] == EMPTY_BTN;
    }

    
	/* A Method to check if the Game is over */
    private boolean isGameOver() {
        return checkWin(PLAYER_X) || checkWin(PLAYER_O) || isBoardFull();
    }

    
    /*A Method to check the winner of the Game*/
    private boolean checkWin(char player) {
        // Check rows and columns
        for (int i = 0; i < BOARD_SIZE; i++) {
            boolean rowWin = true;
            boolean colWin = true;
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] != player) {
                    rowWin = false;
                }
                if (board[j][i] != player) {
                    colWin = false;
                }
            }
            if (rowWin || colWin) {
                return true;
            }
        }
        
        // Check diagonals
        boolean mainDiagonalWin = true;
        boolean antiDiagonalWin = true;
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i][i] != player) {
                mainDiagonalWin = false;
            }
            if (board[i][BOARD_SIZE - 1 - i] != player) {
                antiDiagonalWin = false;
            }
        }
        if (mainDiagonalWin || antiDiagonalWin) {
            return true;
        }
        
        return false;
    }


    
    /*A Method to check if the board is Full */
    private boolean isBoardFull() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == EMPTY_BTN) {
                    return false;
                }
            }
        }
        return true;
    }

    
    /*This method is used for Computer move */
    private void computerMove() {
        int[] bestMove = isMinimaxLimited ? findBestMoveLimited() : findBestMoveComplete();
        board[bestMove[0]][bestMove[1]] = PLAYER_O;        
        
        int row = bestMove[0];
        int col = bestMove[1];
                
        // Update the button text
        buttons[row][col].setText(Character.toString(PLAYER_O));
        
        // Set color for computer moves
        buttons[row][col].setForeground(Color.red);
        
        printBoard();
        if (checkWin(PLAYER_O)) {
            JOptionPane.showMessageDialog(this, "Computer wins!");
            initializeBoard();
            printBoard();
        } else if (isBoardFull()) {
            JOptionPane.showMessageDialog(this, "It's a draw!");
            initializeBoard();
            printBoard();
        }
        playerXTurn = true;
    }

    
    /*minimax algorithm to search for the best move, which is using  alpha-beta pruning by passing appropriate parameters 
     * This function will itself recursively to find the best move*/
    private int minimax(char player, int depth, int alpha, int beta) {
    	//checking all possible conditions before searching for moves 
        if (checkWin(PLAYER_X)) {
            return 10;
        }
        if (checkWin(PLAYER_O)) {
            return -10;
        }
        if (isBoardFull()) {
            return 0;
        }
        if (depth == 0) {
            return 0; // Evaluate the board position here if needed
        }

        if (player == PLAYER_X) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (board[i][j] == EMPTY_BTN) {
                        board[i][j] = PLAYER_X;
                        int score = minimax(PLAYER_O, depth - 1, alpha, beta);
                        board[i][j] = EMPTY_BTN;
                        bestScore = Math.max(score, bestScore);
                        alpha = Math.max(alpha, bestScore);
                        if (beta <= alpha) {
                            break;
                        }
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (board[i][j] == EMPTY_BTN) {
                        board[i][j] = PLAYER_O;
                        int score = minimax(PLAYER_X, depth - 1, alpha, beta);
                        board[i][j] = EMPTY_BTN;
                        bestScore = Math.min(score, bestScore);
                        beta = Math.min(beta, bestScore);
                        if (beta <= alpha) {
                            break;
                        }
                    }
                }
            }
            return bestScore;
        }
    }

    
    /* A method to find the best move using the minimax algorithm for complete version, means this
     * will evaluates all possible moves up to the terminal state making a full depth search of the game tree.*/
    private int[] findBestMoveComplete() {
        int bestScore = Integer.MIN_VALUE;
        int[] move = new int[2];
        move[0] = -1;
        move[1] = -1;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == EMPTY_BTN) {
                    board[i][j] = PLAYER_X;
                    int score = minimax(PLAYER_O, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    board[i][j] = EMPTY_BTN;
                    if (score > bestScore) {
                        bestScore = score;
                        move[0] = i;
                        move[1] = j;
                    }
                }
            }
        }
        return move;
    }

    
    /* This is the method with same algorithm of minimax but of the limited depth of search.
     * This will search to the depth specified, This can help in reducing computational 
     * complexity for the larger tree of the Game. */
    private int[] findBestMoveLimited() {
        int depth = Integer.parseInt((String) depthDropdown.getSelectedItem());
        int bestScore = Integer.MIN_VALUE;
        int[] move = new int[2];
        move[0] = -1;
        move[1] = -1;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == EMPTY_BTN) {
                    board[i][j] = PLAYER_X;
                    int score = minimax(PLAYER_O, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    board[i][j] = EMPTY_BTN;
                    if (score > bestScore) {
                        bestScore = score;
                        move[0] = i;
                        move[1] = j;
                    }
                }
            }
        }
        return move;
    }

    
    //Method for the performing actions means when player X / User click on buttons
    // it will display the letter X on the button. and will check for the move if its a valid. 
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        int row = -1, col = -1;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (buttons[i][j] == button) {
                    row = i;
                    col = j;
                    if (playerXTurn) {
                        buttons[row][col].setForeground(Color.blue); // Set color for X moves
                    } else{
                        buttons[row][col].setForeground(Color.red); // Set color for O moves
                    }
                    break;
                }
            }
        }
        if (row != -1 && col != -1 && playerXTurn && isValidMove(row, col)) {
            board[row][col] = PLAYER_X;
            button.setText(Character.toString(PLAYER_X));
            if (checkWin(PLAYER_X)) {
                JOptionPane.showMessageDialog(this, "You win!");
                initializeBoard();
                printBoard();
            } else if (isBoardFull()) {
                JOptionPane.showMessageDialog(this, "It's a draw!");
                initializeBoard();
                printBoard();
            } else {
                computerMove();
            }
        }
    }

    
    /*** Method to Start the Game by getting the options selected by users and starting computer move or player's move ***/
    private void startGame() {
        int selectedAlgorithmIndex = algorithmDropdown.getSelectedIndex();
        int selectedTurnIndex = turnDropdown.getSelectedIndex();
        isMinimaxLimited = selectedAlgorithmIndex == 1; // Minimax Limited if index is 1

        if (selectedTurnIndex == 1) {
            computerMove();
        }
    }

    
    /****************** This is the main method of this Class which will start the game ******************/
    public static void main(String[] args) {
        TicTacToeUI game = new TicTacToeUI();
        game.setVisible(true);
    }
}