package com.londonappbrewery.bitcointicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity {

    private final String log = "MainActivity";
    /**
     * BTC moet de currency voorafgaan en staat voor  Bitcoin (BTC) zie https://apiv2.bitcoinaverage.com/#introduction
     */
    private final String BASE_URL = "https://apiv2.bitcoinaverage.com/indices/global/ticker/BTC";

    private TextView mTxtPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTxtPrice = findViewById(R.id.txtPrice);
        Spinner spinner = findViewById(R.id.spnCurrency);

        /** ArrayAdapter beheert de elementen uit een array = datamodel. Elk element uit de array
         * wordt standaard voorgesteld in een TextView → dus toString() voorzien indien de
         * elementen instanties van een eigen klasse zijn
         *
         * De presentatie van 1 item is in handen van een layout gedefinieerd in
         * een andere layout/.....xml bestand
         */
        /** instantieer de adapter: arg2 → lijst met elementen die moet worden getoond
         * arg3 → de presentatie (layout file) voor 1 enkel element (bevat enkel een tekstveld)
         */
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_array, R.layout.spinner_item);

        /** geef de adapter ook de manier waarop de lijst als keuzelijst moet getoond worden mee*/
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        /** link het datamodel (adapter) aan de spinner die gevuld moet worden met de data uit de adapter */
        spinner.setAdapter(adapter);

//        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//            }
//        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long rowId) {
                Log.d(log, " item geselecteerd op index : " + position + " met als inhoud: " + ((TextView) view).getText());
                /** getItemAtPosition() returnt het object zelf dat gepresenteerd wordt in de view dus dat is veel beter
                 dan via de view de tekst te vragen, zeker als daar niet altijd gewone tekst inzit, maar een toString() van een object */
                Log.d(log, " item : " + adapterView.getItemAtPosition(position) + " andere manier: " + view);
                handleRequest(BASE_URL + adapterView.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(log, " geen item geselecteerd ");
            }
        });
    }


    private void handleRequest(String url) {
        Log.d(log, "handleRequest: " + url);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // HTTP status is "200 OK"
                Log.d(log, "JSON: " + response.toString());
                BitcoinTickerDataModel bitcoinData = new BitcoinTickerDataModel(response);
                if (!updateUI(bitcoinData)) {
                    Toast.makeText(MainActivity.this, "Updaten van de ui faalt", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                // HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d(log, "Request fail! Status code: " + statusCode);
                Log.d(log, "Fail response: " + response);
                Log.e(log, e.toString());
                Toast.makeText(MainActivity.this, "Request Failed", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private boolean updateUI(@NotNull BitcoinTickerDataModel bitcoinData) {
        double lastPrice = bitcoinData.getLastPrice();
        if (lastPrice > 0) {
            mTxtPrice.setText(String.format("%.4f", lastPrice));
            return true;
        }
        return false;
    }


}
