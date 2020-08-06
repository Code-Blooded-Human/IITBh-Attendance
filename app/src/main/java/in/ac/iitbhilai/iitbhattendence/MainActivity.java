package in.ac.iitbhilai.iitbhattendence;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    public final String LOG ="MainActivity";
    public static String TAG ="MainActivity";
    public static String Sbtname="Amp'ed Up!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void startAttendence(View view){
        Log.d(LOG,"Clicked on Start Attendence");
        Intent captureCard = new Intent(this, in.ac.iitbhilai.iitbhattendence.captureCard.class);
        startActivity(captureCard);
    }
}
