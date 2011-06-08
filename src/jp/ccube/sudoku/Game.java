package jp.ccube.sudoku;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class Game extends Activity {
	private static final String TAG = "Sudoku";
	
	public static final String KEY_DIFFICULTY = "difficulty";
	public static final int DIFFICULTY_EASY = 0;
	public static final int DIFFICULTY_MEDIUM = 1;
	public static final int DIFFICULTY_HARD = 2;
	
	private int puzzle[] = new int [9 * 9];
	private final int used[][][] = new int[9][9][];
	
	private PuzzleView puzzleView;
	
	private final String easyPuzzle =   "360000000004230800000004200" +
										  "070460003820000014500013020" +
										  "001900000007048300000000045";
	private final String mediumPuzzle = "650000070000506000014000005" +
										  "007009000002314700000700800" +
										  "500000630000201000030000097";
	private final String hardPuzzle =   "009000000080605020501078000" +
										  "000000700706040102004000000" +
										  "000720903090301080000000600";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		
		int diff = getIntent().getIntExtra(KEY_DIFFICULTY, DIFFICULTY_EASY);
		puzzle = getPuzzle(diff);
		calculateUsedTiles();
		
		puzzleView = new PuzzleView(this);
		setContentView(puzzleView);
		puzzleView.requestFocus();
	}

	/**
	 * マスごとのつかえない数リストを計算する
	 */
	private void calculateUsedTiles() {
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 9; y++) {
				used[x][y] = calculateUsedTiles(x, y);
			}
		}
	}

	/**
	 * 指定されたマスですでに使われている数を計算
	 * @param x
	 * @param y
	 * @return
	 */
	private int[] calculateUsedTiles(int x, int y) {
		int c[] = new int[9];
		
		// 横
		for (int i = 0; i < 9; i++) {
			if (i == y) 
				continue;
			int t  = getTile(x, i);
			if (t != 0)
				c[t - 1] = t;
		}
		
		// 縦
		for (int i = 0; i < 9; i++) {
			if (i == x)
				continue;
			int  t = getTile(i, y);
			if (t != 0)
				c[t - 1] = t;
		}
		
		// ブロック
		int startx = (x / 3) * 3;
		int starty = (y / 3) * 3;
		for (int i = startx; i < startx + 3; i++) {
			for (int j = starty; j < starty + 3; j++) {
				if (i == x && j == y)
					continue;
				int t = getTile(i, j);
				if (t != 0)
					c[t - 1] = t;
			}
		}
		
		// 圧縮
		int nused = 0;
		for (int t : c) {
			if (t != 0)
				c[nused++] = t;
		}
		
		return c;
	}

	/**
	 * 指定したマスの数字を取得
	 * @param x
	 * @param y
	 * @return
	 */
	private int getTile(int x, int y) {
		return puzzle[y * 9 + x];
	}

	/**
	 * 難易度にあった新しいパズルを返す
	 * @param diff
	 * @return
	 */
	private int[] getPuzzle(int diff) {
		String puz;
		// TODO 前回の続行は未対応
		
		switch (diff) {
		case DIFFICULTY_HARD:
			puz = hardPuzzle;
			break;
		case DIFFICULTY_MEDIUM:
			puz = mediumPuzzle;
			break;
		case DIFFICULTY_EASY:
		default:
			puz = easyPuzzle;
			break;
		}
		
		return fromPuzzleString(puz);
	}

	private int[] fromPuzzleString(String string) {
		int[] puz = new int[string.length()];
		for (int i = 0; i < puz.length; i++) {
			puz[i] = string.charAt(i) - '0';
		}

		return puz;
	}

	/**
	 * 指定された座標のマスの数字を文字列で返す
	 * @param x
	 * @param y
	 * @return
	 */
	public String getTileString(int x, int y) {
		Log.d(TAG, "getTileString(" + x + ", " + y + ")");
		
		int v = getTile(x, y);
		if (v == 0)
			return "";
		else
			return String.valueOf(v);
	}

	/**
	 * 有効な手のときにのみ、マスを変更する
	 * @param x
	 * @param y
	 * @param value
	 * @return
	 */
	public boolean setTileIfValid(int x, int y, int value) {
		int tiles[] = getUsedTiles(x, y);
		if (value != 0) {
			for (int tile: tiles) {
				if (tile == value)
					return false;
			}
		}
		setTile(x, y, value);
		calculateUsedTiles();
		return true;
	}

	/**
	 * 指定座標のマスに数字を表示する
	 * @param x
	 * @param y
	 * @param value
	 */
	private void setTile(int x, int y, int value) {
		puzzle[y * 9 + x] = value;
	}

	/**
	 * 有効な手があれば、キーパッドをオープンする
	 * @param x
	 * @param y
	 */
	public void showKeypadOrError(int x, int y) {
		int tiles[] = getUsedTiles(x, y);
		if (tiles.length == 9) {
			Toast toast = Toast.makeText(this, R.string.no_moves_label, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		} else {
			Log.d(TAG, "showKeypad: used=" + toPuzzleString(tiles));
			Dialog v = new Keypad(this, tiles, puzzleView);
			v.show();
		}
	}

	private String toPuzzleString(int[] puz) {
		StringBuilder buf = new StringBuilder();
		for (int element : puz)
			buf.append(element);

		return buf.toString();
	}

	/**
	 * 現在のマスの、ブロックの使用済の数を配列で返す
	 * @param x
	 * @param y
	 * @return
	 */
	public int[] getUsedTiles(int x, int y) {
		return this.used[x][y];
	}
}
