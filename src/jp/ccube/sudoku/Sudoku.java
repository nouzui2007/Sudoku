package jp.ccube.sudoku;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class Sudoku extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //ボタンのクリックイベント紐付け
        View aboutButton = findViewById(R.id.abount_button);
        aboutButton.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.abount_button:
			//About Activity
			Intent intent = new Intent(this, About.class);
			startActivity(intent);
			break;
		case R.id.continue_button:
			break;
		}
	}
}