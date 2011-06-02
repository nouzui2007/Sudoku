package jp.ccube.sudoku;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.os.Bundle;
import android.view.View;

public class Graphics extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new GraphicsView(this));
	}
	
	static public class GraphicsView extends View {
		private static final String QUOTE = "Now is the time for all " +
							"good men to come to the aid of their country.";
		
		public GraphicsView(Context context) {
			super(context);
			setBackgroundResource(R.drawable.background);
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
//			super.onDraw(canvas);
			Paint cPaint, tPaint;
			cPaint = new Paint();
			cPaint.setColor(Color.LTGRAY);

			tPaint = new Paint();
			tPaint.setColor(getResources().getColor(R.color.mycolor));
			tPaint.setTextSize(21);
			
			Path circle = new Path();
			circle.addCircle(150, 150, 100, Direction.CW);
			
			canvas.drawPath(circle, cPaint);
			canvas.drawTextOnPath(QUOTE, circle, 0, 20, tPaint);
			//PathEffect class is more interesting
		}
	}
}
