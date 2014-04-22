package vt.edu.funnywordsgenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
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
            BUTTON_EVENT_DOWN = 4, PEBBLE_PACKAGE = 5;
    private static final UUID MSG_UUID = UUID
            .fromString( "7d881590-feb3-4f78-af55-85bb0ebc88bd" );

    // Used to generate random numbers, the number of words is 102
    private static Random randomNumberGenerator_ = new Random();

    // Receives the data from pebble
    private PebbleDataReceiver mReceiver;

    // Store the index of the current position
    private static int currentIndex = 0;

    // Stores all of the words and their definitions
    private static ArrayList<String> definitionsList =
            new ArrayList<String>();
    private static ArrayList<String> wordsList =
            new ArrayList<String>();

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        txtview = (TextView) findViewById( R.id.textView1 );
        button = (Button) findViewById( R.id.button1 );

        // Initialize the arrayLists
        wordsList = new ArrayList<String>();
        definitionsList = new ArrayList<String>();

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

        try
        {

            String text = readLineFromFile();
            int incrementer = 0;
            String[] words;

            // Populate the arrays with all 102 words
            while ( "-1" != text && incrementer < 102 )
            {
                if ( 0 != incrementer )
                {
                    text = readLineFromFile();
                }

                words = text.split( " - " );

                wordsList.add( incrementer, words[0] );
                definitionsList.add( incrementer, words[1] );
                incrementer++;
            }
        }
        catch ( Exception ex )
        {
            System.err.print( ex );
        }

        // Listens for button click and update textview/pebble watch
        button.setOnClickListener( new OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                setRandomIndex();
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

        if ( null != mReceiver )
        {
            unregisterReceiver( mReceiver );
        }

        PebbleKit.closeAppOnPebble( getApplicationContext(), MSG_UUID );
    }

    /**
     * Sends a new word and definition to the watch and updates the textview
     * within the main activity.
     */
    private void doMessageUpdate()
    {
        // Make sure the watch is connected
        if ( PebbleKit.isWatchConnected( getApplicationContext() ) )
        {
            // Make sure the current index is valid first
            if ( currentIndex < 0 || currentIndex > wordsList.size() )
            {
                setRandomIndex();
            }

            PebbleDictionary dict = new PebbleDictionary();
            dict.addString(
                    KEY_WORD, wordsList.get( currentIndex ) );
            dict.addString( KEY_DEFINITION, definitionsList.get( currentIndex ) );
            PebbleKit.sendDataToPebble( getApplicationContext(), MSG_UUID,
                    dict
                    );
            txtview.setText( wordsList.get( currentIndex ) + " - "
                    + definitionsList.get( currentIndex ) );
        }
        else
        {
            Toast.makeText(
                    getApplicationContext(),
                    "Warning, a pebble is not connected!",
                    Toast.LENGTH_LONG ).show();
        }
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
            return "-1";
        }
        return line;
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // The following code is for receiving commands from the pebble watch
        mReceiver =
                new PebbleDataReceiver(
                        UUID.fromString( "7d881590-feb3-4f78-af55-85bb0ebc88bd" ) )
                {

                    @Override
                    public void receiveData( Context context, int
                            transactionId, PebbleDictionary data )
                    {
                        // ACK the message
                        PebbleKit.sendAckToPebble( context, transactionId );

                        // Check the key exists
                        if ( data.getUnsignedInteger( PEBBLE_PACKAGE ) != null )
                        {
                            int button =
                                    data.getUnsignedInteger( PEBBLE_PACKAGE )
                                            .intValue();

                            switch ( button )
                            {

                            case BUTTON_EVENT_UP: // The UP button was pressed
                                currentIndex++;
                                break;
                            case BUTTON_EVENT_DOWN: // The DOWN button was
                                                    // pressed
                                currentIndex--;
                                break;
                            case BUTTON_EVENT_SELECT: // The SELECT button was
                                                      // pressed
                                setRandomIndex();
                                break;
                            default:
                                // A weird signal was received
                                return;
                            }
                        }

                        doMessageUpdate();
                    }

                };

        PebbleKit.registerReceivedDataHandler( this, mReceiver );
    }

    /**
     * Sets the current index to a random number within the valid range of
     * numbers
     */
    private void setRandomIndex()
    {
        currentIndex = randomNumberGenerator_.nextInt( wordsList
                .size() - 1 );
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if ( null != mReceiver )
        {
            unregisterReceiver( mReceiver );
        }
    }
}
