package administrator.ui;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.qrcodescan.R;

public class TypeNumActivity extends AppCompatActivity {
    private EditText editText;
    private Button nextBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_num);

        editText = (EditText) findViewById(R.id.number);
        nextBtn = (Button)findViewById(R.id.button);

        nextBtn.setEnabled(false);
        editText.addTextChangedListener(new numberChangeedListener());
    }

    private void handleText(){
        String text = editText.getText().toString();
//        Snackbar.make(nextBtn,text,Snackbar.LENGTH_SHORT).show();
        if(text.length() == 9) {
//            Log.d("lalala", "handleText: "+text);
            nextBtn.setEnabled(true);
        }
    }

    class numberChangeedListener implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            handleText();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            handleText();
        }

        @Override
        public void afterTextChanged(Editable s) {
            handleText();
        }
    }
}
