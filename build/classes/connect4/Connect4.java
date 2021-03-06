/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connect4;


import java.io.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;

public class Connect4 extends JFrame implements Runnable {
    static final int XBORDER = 20;
    static final int YBORDER = 20;
    static final int YTITLE = 30;
    static final int WINDOW_BORDER = 8;
    static final int WINDOW_WIDTH = 2*(WINDOW_BORDER + XBORDER) + 495;
    static final int WINDOW_HEIGHT = YTITLE + WINDOW_BORDER + 2 * YBORDER + 525;
    boolean animateFirstTime = true;
    int xsize = -1;
    int ysize = -1;
    Image image;
    Graphics2D g;

    final int numRows = 8;
    final int numColumns = 8;
    Piece board[][];
    boolean playerOnesTurn;
    boolean moveHappened;
    int currentRow;
    int currentColumn;
    enum WinState
    {
        None,PlayerOne,PlayerTwo,Tie
    }
    WinState winState;
    int winRow;
    int winColumn;
    enum WinDirection
    {
        Horizontal,Vertical,DiagonalUp,DiagonalDown
    }
    WinDirection winDirection;    
    int piecesOnBoard;
    
    static Connect4 frame1;
    public static void main(String[] args) {
        frame1 = new Connect4();
        frame1.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame1.setVisible(true);
    }

    public Connect4() {

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    //left button
                    if (moveHappened || winState != WinState.None)
                        return;
                    
                    
                    int xpos = e.getX() - getX(0);
                    int ypos = e.getY() - getY(0);
                    if (xpos < 0 || ypos < 0 || xpos > getWidth2() || ypos > getHeight2())
                        return;
//Calculate the width and height of each board square.
                    int ydelta = getHeight2()/numRows;
                    int xdelta = getWidth2()/numColumns;
                    currentColumn = xpos/xdelta;
//                    int row = ypos/ydelta;
                    currentRow = numRows - 1;
                    while (currentRow >= 0 && board[currentRow][currentColumn] != null)
                    {
                        currentRow--;
                    }
                    if (currentRow >= 0)
                    {
                        if (playerOnesTurn)
                            board[currentRow][currentColumn] = new Piece(Color.red);
                        else
                            board[currentRow][currentColumn] = new Piece(Color.black);
                        playerOnesTurn = !playerOnesTurn;
                        moveHappened = true;
                        piecesOnBoard++;
                    }
                }
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    reset();
                }
                repaint();
            }
        });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        repaint();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {
        repaint();
      }
    });

        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.VK_RIGHT == e.getKeyCode())
                {
                }
                if (e.VK_LEFT == e.getKeyCode())
                {
                }
                if (e.VK_UP == e.getKeyCode())
                {
                }
                if (e.VK_DOWN == e.getKeyCode())
                {
                }

                repaint();
            }
        });
        init();
        start();
    }




    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }
////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || xsize != getSize().width || ysize != getSize().height) {
            xsize = getSize().width;
            ysize = getSize().height;
            image = createImage(xsize, ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }

//fill background
        g.setColor(Color.cyan);

        g.fillRect(0, 0, xsize, ysize);

        int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0)};
        int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0)};
//fill border
        g.setColor(Color.white);
        g.fillPolygon(x, y, 4);
// draw border
        g.setColor(Color.red);
        g.drawPolyline(x, y, 5);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }

        g.setColor(Color.gray);
//horizontal lines
        for (int zi=1;zi<numRows;zi++)
        {
            g.drawLine(getX(0) ,getY(0)+zi*getHeight2()/numRows ,
            getX(getWidth2()) ,getY(0)+zi*getHeight2()/numRows );
        }
//vertical lines
        for (int zi=1;zi<numColumns;zi++)
        {
            g.drawLine(getX(0)+zi*getWidth2()/numColumns ,getY(0) ,
            getX(0)+zi*getWidth2()/numColumns,getY(getHeight2())  );
        }

        for (int zrow=0;zrow<numRows;zrow++)
        {
            for (int zcolumn=0;zcolumn<numColumns;zcolumn++)
            {
                if (board[zrow][zcolumn] != null)
                {
                    g.setColor(board[zrow][zcolumn].getColor());
                    g.fillOval(getX(0)+zcolumn*getWidth2()/numColumns,
                    getY(0)+zrow*getHeight2()/numRows,
                    getWidth2()/numColumns,
                    getHeight2()/numRows);
                }
            }
        }
    
        if (winState == WinState.PlayerOne)
        {
            g.setColor(Color.gray);
            g.setFont(new Font("Monospaced",Font.BOLD,40) );
            g.drawString("Player 1 has won.", 50, 200);            
        }
        else if (winState == WinState.PlayerTwo)
        {
            g.setColor(Color.gray);
            g.setFont(new Font("Monospaced",Font.BOLD,40) );
            g.drawString("Player 2 has won.", 50, 200);            
        }
        else if (winState == WinState.Tie)
        {
            g.setColor(Color.gray);
            g.setFont(new Font("Monospaced",Font.BOLD,40) );
            g.drawString("It is a tie.", 50, 200);            
        }

        gOld.drawImage(image, 0, 0, null);
    }


////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            double seconds = 0.03;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {
        board = new Piece[numRows][numColumns];
//        for (int zrow = 0;zrow < numRows;zrow++)
//        {
//            for (int zcolumn = 0;zcolumn < numColumns;zcolumn++)
//            {
//                board[zrow][zcolumn] = null;
//            }
//        }
        playerOnesTurn = true;
        moveHappened = false;
        winState = WinState.None;
        piecesOnBoard = 0;
    }
/////////////////////////////////////////////////////////////////////////
    public void animate() {

        if (animateFirstTime) {
            animateFirstTime = false;
            if (xsize != getSize().width || ysize != getSize().height) {
                xsize = getSize().width;
                ysize = getSize().height;
            }

            reset();
        }
        
        
        if (moveHappened)
        {
            moveHappened = false;
            checkWin();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public boolean checkWin() {
//check horizontal.
        int startColumn = currentColumn - 3;
        if (startColumn < 0)
            startColumn = 0;
        int endColumn = currentColumn + 3;
        if (endColumn > numColumns-1)
            endColumn = numColumns - 1;
        int numMatch = 0;
        
        for (int col = startColumn;numMatch != 4 && col<=endColumn;col++)
        {
            if (board[currentRow][col] != null && board[currentRow][col].getColor() == board[currentRow][currentColumn].getColor())
                numMatch++;
            else
                numMatch = 0;
            if (numMatch == 1)
            {
                winColumn = col;
                winRow = currentRow;
            }
        }
        
        if (numMatch == 4)
        {
            if (board[currentRow][currentColumn].getColor() == Color.red)
                winState = WinState.PlayerOne;
            else
                winState = WinState.PlayerTwo;
            {
                board[winRow][winColumn].setColor(Color.blue);
                board[winRow][winColumn+1].setColor(Color.blue);
                board[winRow][winColumn+2].setColor(Color.blue);
                board[winRow][winColumn+3].setColor(Color.blue);
            }            
            return (true);
        }
        
//check vertical.
        int startRow = currentRow - 3;
        if (startRow < 0)
            startRow = 0;
        int endRow = currentRow + 3;
        if (endRow > numRows-1)
            endRow = numRows - 1;
        numMatch = 0;
        
        for (int row = startRow;numMatch != 4 && row<=endRow;row++)
        {
            if (board[row][currentColumn] != null && board[row][currentColumn].getColor() == board[currentRow][currentColumn].getColor())
                numMatch++;
            else
                numMatch = 0;
            if (numMatch == 1)
            {
                winColumn = currentColumn;
                winRow = row;
            }            
        }
        
        if (numMatch == 4)
        {
            if (board[currentRow][currentColumn].getColor() == Color.red)
                winState = WinState.PlayerOne;
            else
                winState = WinState.PlayerTwo;
            {
                board[winRow][winColumn].setColor(Color.blue);
                board[winRow+1][winColumn].setColor(Color.blue);
                board[winRow+2][winColumn].setColor(Color.blue);
                board[winRow+3][winColumn].setColor(Color.blue);
            }             
            return (true);
        }        
//check diagonal right down.
        startColumn = currentColumn - 3;
        startRow = currentRow - 3;
        if (startColumn < 0 || startRow < 0)
        {
            if (startColumn < startRow)
            {
                startRow -= startColumn;
                startColumn = 0;
            }
            else
            {
                startColumn -= startRow;
                startRow = 0;
            }
        }
        endColumn = currentColumn + 3;
        endRow = currentRow + 3;
        if (endColumn > numColumns-1 || endRow > numRows-1)
        {
            if (endColumn > endRow)
            {
                endRow -= (endColumn - (numColumns - 1));
                endColumn = numColumns-1;
            }
            else
            {
                endColumn -= (endRow - (numRows - 1));
                endRow = numRows-1;
            }
        }
 
        numMatch = 0;    
        int row = startRow;
        for (int col = startColumn;numMatch != 4 && col<=endColumn;col++)
        {
            if (board[row][col] != null && board[row][col].getColor() == board[currentRow][currentColumn].getColor())
                numMatch++;
            else
                numMatch = 0;
            if (numMatch == 1)
            {
                winColumn = col;
                winRow = row;
            }
            row++;
        }
        
        if (numMatch == 4)
        {
            if (board[currentRow][currentColumn].getColor() == Color.red)
                winState = WinState.PlayerOne;
            else
                winState = WinState.PlayerTwo;
            {
                board[winRow][winColumn].setColor(Color.blue);
                board[winRow+1][winColumn+1].setColor(Color.blue);
                board[winRow+2][winColumn+2].setColor(Color.blue);
                board[winRow+3][winColumn+3].setColor(Color.blue);
            }            
            return (true);
        }
                
 
//check diagonal right up.
        startColumn = currentColumn - 3;
        startRow = currentRow + 3;
        if (startColumn < 0 || startRow > numRows-1)
        {
            if (startColumn < numRows - 1 - startRow)
            {
                startRow += startColumn;
                startColumn = 0;
            }
            else
            {
                startColumn += startRow - (numRows - 1);
                startRow = numRows - 1;
            }
        }
        endRow = currentRow - 3;
        endColumn = currentColumn + 3;
        if (endRow < 0 || endColumn > numColumns-1)
        {
            if (endRow < numColumns - 1 - endColumn)
            {
                endColumn += endRow;
                endRow = 0;
            }
            else
            {
                endRow += endColumn - (numColumns - 1);
                endColumn = numColumns - 1;
            }
        }        
 
        numMatch = 0;    
        row = startRow;
        for (int col = startColumn;numMatch != 4 && col<=endColumn;col++)
        {
            if (board[row][col] != null && board[row][col].getColor() == board[currentRow][currentColumn].getColor())
                numMatch++;
            else
                numMatch = 0;
            if (numMatch == 1)
            {
                winColumn = col;
                winRow = row;
            }
            row--;
        }
        
        if (numMatch == 4)
        {
            if (board[currentRow][currentColumn].getColor() == Color.red)
                winState = WinState.PlayerOne;
            else
                winState = WinState.PlayerTwo;
            {
                board[winRow][winColumn].setColor(Color.blue);
                board[winRow-1][winColumn+1].setColor(Color.blue);
                board[winRow-2][winColumn+2].setColor(Color.blue);
                board[winRow-3][winColumn+3].setColor(Color.blue);
            }            
            return (true);
        }
                  
        if (piecesOnBoard >= numRows*numColumns)
        {
            winState = WinState.Tie;
            return(true);
        }
        return(false);
    }
////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }
/////////////////////////////////////////////////////////////////////////
    public int getX(int x) {
        return (x + XBORDER + WINDOW_BORDER);
    }

    public int getY(int y) {
        return (y + YBORDER + YTITLE );
    }

    public int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }
    
    public int getWidth2() {
        return (xsize - 2 * (XBORDER + WINDOW_BORDER));
    }

    public int getHeight2() {
        return (ysize - 2 * YBORDER - WINDOW_BORDER - YTITLE);
    }
}
