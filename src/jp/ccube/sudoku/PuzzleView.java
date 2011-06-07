package jp.ccube.sudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class PuzzleView extends View {
	private static final String TAG = "Sudoku";
	private final Game game;
	
	public PuzzleView(Context context) {
		super(context);
		this.game = (Game)context;
		
		// focus options: for inputed by user
		setFocusable(true);
		setFocusableInTouchMode(true);
	}
	
	private float width;
	private float height;
	private int selX;
	private int selY;
	private final Rect selRect = new Rect();

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// 描画するマスの大きさを計算する
		// ビューのサイズを81分割する
		width = w / 9f;
		height = h / 9f;
		getRect(selX, selY, selRect);
		
		Log.d(TAG, "onSizeChanged: width = " + width +", height = " + height);
		
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	private void select(int x, int y) {
		invalidate(selRect); // 現在選択中のマス目を再描画するため
		this.selX = Math.min(Math.max(x, 0), 8);
		this.selY = Math.min(Math.max(y, 0), 8);
		getRect(selX, selY, selRect);
		invalidate(selRect); // 新しい選択中のマス目を再描画するため
		/*
		 * invalidateとonDrawについて、もっと調査
		 * invalidateの引数にRect指定しているのは、ダーティマークをつけるためで、
		 * この領域はクリッピング領域になるので、画面更新は実際に変更のある領域のみ
		 * 適用されるらしい
		 * 画面全体が描画されていないことと、一部だけ描画することがどの辺で実現
		 * されているのかが、今のところ不明
		 */
	}
	
	private void getRect(int x, int y, Rect rect) {
		int left = (int)(x * this.width);
		int top = (int)(y * this.height);
		int right = left + (int)this.width;
		int bottom = top + (int)this.height;
		
		rect.set(left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.d(TAG, "onDraw: width = " + width +", height = " + height);
		
		// 1.背景を描画する
		Paint background = new Paint();
		background.setColor(getResources().getColor(R.color.puzzle_background));
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
		
		// 2.盤面を描画する
		//  2-1.枠線の色
		Paint dark = new Paint();
		dark.setColor(getResources().getColor(R.color.puzzle_dark));
		Paint hilite = new Paint();
		hilite.setColor(getResources().getColor(R.color.puzzle_hilite));
		Paint light = new Paint();
		light.setColor(getResources().getColor(R.color.puzzle_light));
		
		//  2-2.マス目を区切る線
		for (int i = 0; i < 9; i++) {
			canvas.drawLine(0, i * this.height, getWidth(), i * this.height, light);
			canvas.drawLine(0, i * this.height + 1, getWidth(), i * this.height + 1, hilite);
			canvas.drawLine(i * this.width, 0, i * this.width, getHeight(), light);
			canvas.drawLine(i * this.width + 1, 0, i * this.width + 1, getHeight(), hilite);
		}
		
		//  2-3.3x3のブロックを区切る線
		for (int i = 0; i < 9; i++) {
			if (i % 3 != 0)
				continue;
			canvas.drawLine(0, i * this.height, getWidth(), i * this.height, dark);
			canvas.drawLine(0, i * this.height + 1, getWidth(), i * this.height + 1, hilite);
			canvas.drawLine(i * this.width, 0, i * this.width, getHeight(), dark);
			canvas.drawLine(i * this.width + 1, 0, i * this.width + 1, getHeight(), hilite);
		}

		// 3.数値を描画する
		//  3-1.数値の色とスタイルを定義する
		Paint foreground = new Paint();
		foreground.setColor(getResources().getColor(R.color.puzzle_foreground));
		foreground.setStyle(Style.FILL);
		foreground.setTextSize(this.height * 0.75f);
		foreground.setTextScaleX(this.width / this.height);
		foreground.setTextAlign(Paint.Align.CENTER);
		
		// 3-2.マス目の中央に数字を置く
		FontMetrics fm = foreground.getFontMetrics();
		//  3-2-1.X軸方向でセンタリングする。アラインメントを使う
		float x = this.width / 2;
		//  3-2-2.Y軸方向でセンタリングする
		//   3-2-2-1.まずアセント/ディセント（上半分と下半分）を調べる
		float y = this.height / 2 - (fm.ascent + fm.descent) / 2;

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				canvas.drawText(this.game.getTileString(i, j), 
						i * this.width + x, j * this.height + y, foreground);
			}
		}
		
		// 4.ヒントを描画する
		// 4-1.残された手の数に基づいて、ヒント色を塗る
		Paint hint = new Paint();
		int c[] = {getResources().getColor(R.color.puzzle_hint_0),
				getResources().getColor(R.color.puzzle_hint_1),
				getResources().getColor(R.color.puzzle_hint_2), 
		};
		Rect r = new Rect();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				int movesleft = 9 - game.getUsedTiles(i, j).length;
				if (movesleft < c.length) {
					getRect(i, j, r);
					hint.setColor(c[movesleft]);
					canvas.drawRect(r, hint);
				}
			}
		}
		
		// 5.選択されたマスを描画する
		Log.d(TAG, "selRect=" + selRect);
		Paint selected = new Paint();
		selected.setColor(getResources().getColor(R.color.puzzle_selected));
		canvas.drawRect(selRect, selected);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "onKeyDown: Keycode=" + keyCode + ", event=" + event);
		
		switch(keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
			select(selX, selY - 1);
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			select(selX, selY + 1);
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			select(selX - 1, selY);
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			select(selX + 1, selY);
			break;
		case KeyEvent.KEYCODE_0:
		case KeyEvent.KEYCODE_SPACE:
			setSelectedTile(0);
		case KeyEvent.KEYCODE_1: setSelectedTile(1); break;
		case KeyEvent.KEYCODE_2: setSelectedTile(2); break;
		case KeyEvent.KEYCODE_3: setSelectedTile(3); break;
		case KeyEvent.KEYCODE_4: setSelectedTile(4); break;
		case KeyEvent.KEYCODE_5: setSelectedTile(5); break;
		case KeyEvent.KEYCODE_6: setSelectedTile(6); break;
		case KeyEvent.KEYCODE_7: setSelectedTile(7); break;
		case KeyEvent.KEYCODE_8: setSelectedTile(8); break;
		case KeyEvent.KEYCODE_9: setSelectedTile(9); break;
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_DPAD_CENTER:
			game.showKeypadOrError(selX, selY);
		default:
			return super.onKeyDown(keyCode, event);				
		}
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN) {
			return super.onTouchEvent(event);
		}
		
		select((int)(event.getX() / this.width), (int)(event.getY() / this.height));
		game.showKeypadOrError(selX, selY);
		Log.d(TAG, "onTouchEvent: x " + selX + ", y " + selY);
		
		return true;
	}
	
	public void setSelectedTile(int tile) {
		if (game.setTileIfValid(selX, selY, tile)) {
			invalidate(); //ヒントが変わる可能性があるので、全体描画
		} else {
			//このマスの数値は選べない値
			Log.d(TAG, "setSelectedTile: invalid: " + tile);
		}
	}
}
