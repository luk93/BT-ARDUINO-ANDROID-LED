    package com.example.lukasz.bt_led;

    import android.app.ProgressDialog;
    import android.bluetooth.BluetoothAdapter;
    import android.bluetooth.BluetoothDevice;
    import android.bluetooth.BluetoothSocket;
    import android.content.Intent;
    import android.os.AsyncTask;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.Button;
    import android.widget.SeekBar;
    import android.widget.TextView;
    import android.widget.Toast;

    import java.io.IOException;
    import java.util.UUID;

    public class LedContol extends AppCompatActivity
    {
        TextView lmn;
        Button btnOn, btnOff, btnDis;
        SeekBar brightness;
        String address = null;
        private ProgressDialog progress;
        BluetoothAdapter myBluetooth = null;
        BluetoothSocket btSocket = null;
        private boolean isBtConnected = false;
        static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_led_contol);

            Intent newint = getIntent();
            address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);

            btnOn = (Button)findViewById(R.id.button2);
            btnOff = (Button)findViewById(R.id.button3);
            btnDis = (Button)findViewById(R.id.button4);
            brightness = (SeekBar)findViewById(R.id.seekBar);
            lmn = (TextView) findViewById(R.id.textView2);
            brightness.setMax(255);

            new ConnectBT().execute();

            btnOn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    turnOnLed();      //method to turn on
                }
            });

            btnOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    turnOffLed();   //method to turn off
                }
            });

            btnDis.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Disconnect(); //close connection
                }
            });

            brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser==true)
                    {

                        lmn.setText(String.valueOf(progress));
                        try
                        {
                            btSocket.getOutputStream().write(String.valueOf(progress).getBytes());
                        }
                        catch (IOException e)
                        {

                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        }

        private void msg(String s)
        {
            Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
        }
        private void Disconnect()
        {
            if (btSocket!=null) //If the btSocket is busy
            {
                try
                {
                    btSocket.close(); //close connection
                }
                catch (IOException e)
                { msg("Error");}
            }
            finish(); //return to the first layout
        }
        private void turnOffLed()
        {
            if (btSocket!=null)
            {
                try
                {
                    btSocket.getOutputStream().write("OFF".toString().getBytes());
                }
                catch (IOException e)
                {
                    msg("Error");
                }
            }
        }
        private void turnOnLed()
        {
            if (btSocket!=null)
            {
                try
                {
                    btSocket.getOutputStream().write("ON".toString().getBytes());
                }
                catch (IOException e)
                {
                    msg("Error");
                }
            }
        }
        private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
        {
            private boolean ConnectSuccess = true; //if it's here, it's almost connected

            @Override
            protected void onPreExecute()
            {
                progress = ProgressDialog.show(LedContol.this, "Connecting...", "Please wait!!!");  //show a progress dialog
            }

            @Override
            protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
            {
                try
                {
                    if (btSocket == null || !isBtConnected)
                    {
                        myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                        BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                        btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                        btSocket.connect();//start connection
                    }
                }
                catch (IOException e)
                {
                    ConnectSuccess = false;//if the try failed, you can check the exception here
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
            {
                super.onPostExecute(result);

                if (!ConnectSuccess)
                {
                    msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                    finish();
                }
                else
                {
                    msg("Connected.");
                    isBtConnected = true;
                }
                progress.dismiss();
            }
        }
    }
