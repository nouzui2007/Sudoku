package jp.ccube.sudoku;

import android.content.Context;
import android.view.View;

public class PuzzleView extends View {
	private static final String TAG = "Sudoku";
	private final Game game;
	
	public PuzzleView(Context context) {
		super(context);
		this.game = (Game)context;
		
		// forcus options: for inputed by user
		setFocusable(true);
		setFocusableInTouchMode(true);
	}

}
