package in.ac.iitbhilai.iitbhattendence;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class showAttendence extends AppCompatActivity {
    public final String TAG = "Show Attendence";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
                                         
        setContentView(R.layout.activity_show_attendence);
        Intent intent = getIntent();
        String roll = "";
        for(int i=0;i<captureCard.rollID.size();i++){
            roll += captureCard.rollID.get(i)+"\n";
        }
        TextView showRoll =  findViewById(R.id.showRoll);
        showRoll.setText(roll);
        Log.d(TAG,"Hello"+captureCard.rollID);
    }
}
