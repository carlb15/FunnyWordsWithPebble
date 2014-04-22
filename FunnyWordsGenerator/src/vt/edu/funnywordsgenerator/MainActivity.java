package vt.edu.funnywordsgenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.PebbleKit.PebbleDataReceiver;
import com.getpebble.android.kit.util.PebbleDictionary;

/**
 * @authors Carl Barbee, Jimmy Dagres
 * @assignment Pebble app which receives a word and definition from a text file
 *             from an Android app.
 * @date April 21, 2014
 */
public class MainActivity extends Activity
{
    // Input stream for reading from the file.
    private InputStream in;
    // The buffered reader for reading from the file.
    private BufferedReader reader;
    // Used for reading from the file.
    private String line;
    // Text view to display the word and definition from the file.
    private TextView txtview;
    // The button used to update the word and definition from the file.
    private Button button;
    // The key words used for sending the definition and word to the file.
    private static final int KEY_WORD = 0, KEY_DEFINITION = 1,
            BUTTON_EVENT_SELECT = 2, BUTTON_EVENT_UP = 3,
            BUTTON_EVENT_DOWN = 4;
    private static final UUID MSG_UUID = UUID
            .fromString( "7d881590-feb3-4f78-af55-85bb0ebc88bd" );

    // Recieves the data from pebble
    private PebbleDataReceiver mReceiver;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        txtview = (TextView) findViewById( R.id.textView1 );
        button = (Button) findViewById( R.id.button1 );

        // Opens the files and starts the input stream.
        try
        {
            in = this.getAssets().open( "FunnyWords.txt" );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        reader = new BufferedReader( new InputStreamReader( in ) );

        // Listens for button click and update textview/pebble watch
        button.setOnClickListener( new OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                doMessageUpdate();
            }
        } );
    }

    /**
     * Closes the text file of words.
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        try
        {
            in.close();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    /**
     * Sends a new word and definition to the watch and updates the textview
     * within the main activity.
     */
    private void doMessageUpdate()
    {
        String text = readLineFromFile();
        String[] words = text.split( " - " );

        txtview.setText( text );

        PebbleDictionary dict = new PebbleDictionary();
        dict.addString( KEY_WORD, words[0] );
        dict.addString( KEY_DEFINITION, words[1] );
        PebbleKit.sendDataToPebble( getApplicationContext(), MSG_UUID, dict );
    }

    /**
     * Reads a new line from the file.
     * 
     * @return The word and its definition
     */
    public String readLineFromFile()
    {
        try
        {
            line = reader.readLine();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return line;
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        mReceiver =
                new PebbleDataReceiver(
                        MSG_UUID )
                {

                    @Override
                    public void receiveData( Context context,
                            int transactionId, PebbleDictionary data )
                    {
                        // ACK the message
                        PebbleKit.sendAckToPebble( context, transactionId );

                        // Check the key exists
                        if ( data.getUnsignedInteger( KEY_WORD ) != null )
                        {
                            int button =
                                    data.getUnsignedInteger( KEY_WORD )
                                            .intValue();

                            switch ( button )
                            {

                            case BUTTON_EVENT_UP: // The UP button was pressed
                                break;
                            case BUTTON_EVENT_DOWN: // The DOWN button was
                                                    // pressed
                                break;

                            case BUTTON_EVENT_SELECT:
                                // The SELECT button was pressed, change the
                                // word
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Changing the word.",
                                        Toast.LENGTH_SHORT ).show();

                                break;
                            }
                        }
                    }

                };

        PebbleKit.registerReceivedDataHandler( this, mReceiver );
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        unregisterReceiver( mReceiver );
    }

}
