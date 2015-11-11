package tu_darmstadt.sudoku.game;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Christopher Beckmann on 06.11.2015.
 */
public class GameField implements Cloneable {

    //private int id;
    private int sectionHeight;
    private int sectionWidth;
    //private List additionalSections
    private CellConflictList errorList = new CellConflictList();
    private int size;
    private GameCell[][] field;

    public GameField(int size, int sectionHeight, int sectionWidth) {
        this.sectionHeight = sectionHeight;
        this.sectionWidth = sectionWidth;
        this.size = size;

        field = new GameCell[size][size];
        initCells(null);
    }

    public void initCells(int[][] level) {
        // TODO: this is a placeholder, because we don't have real levels yet.
        int[][] placeholder = {{ 5, 0, 1,  9, 0, 0,  0, 0, 0 },
                { 2, 0, 0,  0, 0, 4,  9, 5, 0 },
                { 3, 9, 0,  7, 0, 0,  0, 2, 6 },

                { 0, 3, 0,  0, 0, 1,  0, 7, 2 },
                { 0, 0, 6,  0, 5, 7,  0, 0, 0 },
                { 0, 7, 2,  0, 0, 9,  0, 4, 1 },

                { 0, 0, 0,  0, 7, 0,  4, 0, 9 },
                { 6, 4, 0,  0, 0, 0,  0, 0, 0 },
                { 7, 0, 0,  0, 1, 0,  3, 0, 5 }};

        // Initit the game field
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                field[i][j] = new GameCell(i,j,size,placeholder[i][j]);
            }
        }
    }

    public GameCell getCell(int row, int col) {
        return field[row][col];
    }

    public GameCell[][] getField() {
        return field;
    }

    public LinkedList<GameCell> getRow(int row) {
        LinkedList<GameCell> result = new LinkedList<GameCell>();
        for(GameCell c : field[row]) {
            result.add(c);
        }
        return result;
    }

    public LinkedList<GameCell> getColumn(int col) {
        LinkedList<GameCell> result = new LinkedList<GameCell>();
        for(int i = 0; i < size ; i++) {    // row
            for(int j = 0 ; j < size ; j++) {   // col
                if(j == col) {
                    result.add(field[i][j]);
                }
            }
        }
        return result;
    }

    public LinkedList<GameCell> getSection(final int sec) {
        return actionOnCells(new ICellAction<LinkedList<GameCell>>() {
            @Override
            public LinkedList<GameCell> action(GameCell gc, LinkedList<GameCell> existing) {
                if ((int) (Math.floor(gc.getRow() / sectionHeight) * sectionHeight + Math.floor(gc.getCol() / sectionWidth)) == sec) {
                    existing.add(gc);
                }
                return existing;
            }
        }, new LinkedList<GameCell>());
    }

    public LinkedList<GameCell> getSection(int row, int col) {
        int sec = (int) (Math.floor(row / sectionHeight) * sectionHeight + Math.floor(col / sectionWidth));
        return getSection(sec);
    }

    public int getSize() {
        return size;
    }

    public <T> T actionOnCells(ICellAction<T> ca, T existing) {
        for(int i = 0; i < field.length; i++) {
            for(int j = 0; j < field[i].length; j++) {
                existing = ca.action(field[i][j], existing);
            }
        }
        return existing;
    }

    public boolean isSolved(final List<CellConflict> errorList) {
        boolean solved = true;

        if(errorList == null) {
            throw new IllegalArgumentException("ErrorList may not be null.");
        }
        errorList.clear();

        // this will automatically build the CellConflict list. so we reset it before we call the checks

        for(int i = 0; i < size; i++) {
            if(!checkList(getRow(i), errorList)) solved = false;
            if(!checkList(getColumn(i), errorList)) solved = false;
            if(!checkList(getSection(i), errorList)) solved = false;
        }
        return solved;
    }

    /**
     * Checks the given list if every number occurs only once.
     * This method will automatically build the errorList.
     * @param list the list of {@link GameCell}s that is supposed to be tested.
     * @return true if every cell has a value and every value occurs only once
     */
    private boolean checkList(final List<GameCell> list, final List<CellConflict> errorList) {
        boolean isNothingEmpty = true;
        CellConflict lastFound = null;

        for(int i = 0; i < list.size(); i++) {
            for(int j = i + 1; j < list.size(); j++) {
                GameCell c1 = list.get(i);
                GameCell c2 = list.get(j);

                if(c1.getValue() == 0 || c2.getValue() == 0) {
                    isNothingEmpty = false;
                }

                // Same value in one set should not exist
                if(c1.getValue() != 0 && c1.getValue() == c2.getValue()) {
                    // we found an error..
                    if(errorList != null) {
                        errorList.add(new CellConflict(c1, c2));
                    }
                }
            }
        }
        return isNothingEmpty ? (errorList.size() == 0) : false;
    }

    @Override
    public GameField clone() throws CloneNotSupportedException {
        GameField clone = (GameField) super.clone();

        GameCell[][] cloneField = new GameCell[size][size];
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                cloneField[i][j] = field[i][j].clone();
            }
        }
        clone.field = cloneField;

        return clone;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[GameField: \n");

        for (int i = 0; i < size; i++) {

            for (int j = 0; j < size; j++) {
                if (j % sectionWidth == 0) {
                    sb.append("\t");
                }

                sb.append(getField()[i][j]);
                sb.append(" ");
            }

            sb.append("]");
        }
        return sb.toString();
    }
}
