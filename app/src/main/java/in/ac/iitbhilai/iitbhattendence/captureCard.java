package in.ac.iitbhilai.iitbhattendence;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.leopard.api.Clscr;
import com.leopard.api.HexString;
import com.leopard.api.Setup;

import java.util.ArrayList;
import java.util.List;

public class captureCard extends AppCompatActivity {
    public static Setup insSetup = null;
    public static volatile boolean help = true;
    public final String TAG="captureCard";
    final Context context=this;
    BluetoothComm blc;
    Clscr clscr;
    TextView status;
    public static List<String> rollID = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_card);
        status = (TextView) findViewById(R.id.status);
        try {
            insSetup = new Setup();
            boolean activate = insSetup.blActivateLibrary(context,R.raw.licence);
            if (activate == true) {
                if(help) status.setText("Library Activated");
            } else if (activate == false) {
                if(help) status.setText("Failed to activate lib");
            }
        } catch (Exception e) { }
        blc = new BluetoothComm("00:04:3E:6F:26:F4");
        boolean conn = blc.createConn();
        if(conn) {
            Log.d(TAG, "TRUE");
        }
        if(blc.isConnect()){
            status.setText("Bluetooth Connection Success!");
        }
        try {
            clscr = new Clscr(insSetup,blc.mosOut,blc.misIn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int iRetVal =clscr.iInitialize();
        Log.d(TAG,"CLSCR Initiatied");
        status.setText("Touch your ID ");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pollCard pc = new pollCard();
        new Thread(pc).start();


    }



    class pollCard implements Runnable{
        @Override
        public void run(){
            Log.d(TAG,"started thread");
            while(true){
                Log.d(TAG,"IN LOOP");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(TAG,"IN LOOP2");
                if(cardDetected()){
                    final String id = getID();
                    runOnUiThread(new Runnable(){
                        public void run() {
                            Toast toast = Toast.makeText(getApplicationContext(), parseID(id), Toast.LENGTH_SHORT);

                            toast.show();
                            rollID.add(parseID(id));
                        }
                    });
                }
            }
        }
    }

    public boolean cardDetected(){
        try{
            Log.d(TAG,"AS");
            byte[] bAtq=new byte[300];
            bAtq = clscr.bGetATQ();
            String atqTest= HexString.bufferToHex(bAtq);
            Log.d(TAG,atqTest+"AS");
            if(!atqTest.equals("00")){
                Log.d(TAG,"Card Found");
                return true;
            }else{
                Log.d(TAG,"waiting for card");
            }

        }catch(NullPointerException e){
            Log.d(TAG,"NPE");
            return false;

        }catch(StringIndexOutOfBoundsException e){
            e.printStackTrace();
            Log.e(TAG, "StringIndexOutOfBoundsException");
            return false;
        }
        return false;
   }

    public String getID(){
        Log.d(TAG,"getting ID");
        byte[] bApduCmd = HexString.hexToBuffer("00A40004023F0420");
        byte[] bRespBuffer = clscr.bSendRecvAPDU(bApduCmd);

        bApduCmd = HexString.hexToBuffer("00B0000050");
        bRespBuffer = clscr.bSendRecvAPDU(bApduCmd);
        Log.d(TAG,"CARD DATA "+HexString.bufferToHex(bRespBuffer));
        return HexString.bufferToHex(bRespBuffer);

    }

    public String parseID(String rawResponse){
        String ID= "";
        ID=""+rawResponse.charAt(23)+rawResponse.charAt(25)+rawResponse.charAt(27)+rawResponse.charAt(29)+rawResponse.charAt(31)+rawResponse.charAt(33)+rawResponse.charAt(35)+rawResponse.charAt(37);
        return  ID;
    }

    public void endAttendence(View view){
        //kll thread
        Intent showAttendence = new Intent(this, in.ac.iitbhilai.iitbhattendence.showAttendence.class);
        showAttendence.putExtra("ID", rollID.toString());
        startActivity(showAttendence);

    }

}
