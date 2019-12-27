package org.tensorflow.lite.examples.detection.Bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ConnectedThread extends Thread {
    public static final int RECEVEID_MESSAGE = 9;
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    public static final int RESPONSE_MESSAGE = 10;
    Handler uih;
    public ConnectedThread(BluetoothSocket socket, Handler uih){
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        this.uih = uih;
        Log.i("[THREAD-CT]","Creating thread");
        try{
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch(IOException e) {
            Log.e("[THREAD-CT]","Error:"+ e.getMessage());
        }
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        try {
            mmOutStream.flush();
        } catch (IOException e) {
            return;
        }
        Log.i("[THREAD-CT]","IO's obtained");
    }
    @Override
    public void run(){

        BufferedReader br;
        br = new BufferedReader(new InputStreamReader(mmInStream));
        byte[] buffer = new byte[256];
        int bytes;
        while(true){
            Log.d("MESSAGE","BEFORELOOPING");
            try{
                    Log.d("MESSAGE","tryblock");
                bytes = mmInStream.read(buffer);

                Message msg = new Message();
                msg.what = RECEVEID_MESSAGE;
                msg.obj = buffer.toString();


                uih.obtainMessage(RECEVEID_MESSAGE,bytes,-1,buffer).sendToTarget();

            }catch(IOException e){
                Log.e("THREADERROR","NO");
                break;
            }
        }
        Log.i("[THREAD-CT]","While loop ended");
    }
    public void write(byte[] bytes){
        try{
            Log.i("[THREAD-CT]", "Writting bytes");

            mmOutStream.write(bytes);
        }catch(IOException e){}
    }
    public void cancel(){
        try{
            mmSocket.close();
        }catch(IOException e){}
    }

}
