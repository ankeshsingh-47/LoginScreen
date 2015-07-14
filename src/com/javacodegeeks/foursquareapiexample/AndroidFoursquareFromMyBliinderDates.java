package com.javacodegeeks.foursquareapiexample;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.ysoserious.bliinder.R;
import com.ysoserious.bliinder.activities.FillDetails;
import com.ysoserious.bliinder.entities.BliinderDate;
import com.ysoserious.bliinder.entities.Server;
import com.ysoserious.bliinder.utils.Data;
import com.ysoserious.bliinder.utils.FoursquareVenue;
import com.ysoserious.bliinder.utils.PlaceType;

public class AndroidFoursquareFromMyBliinderDates extends ListActivity {
	private static final int FIRST = 0;

	ArrayList<FoursquareVenue> venuesList;

	private static String RESTAURANT = "4d4b7105d754a06374d81259"; // Food
	private static String BAR = "4d4b7105d754a06376d81259"; // NightLife Spot
	private static String CINEMA = "4bf58dd8d48988d17f941735"; // Movie Theater
	static Activity activity;

	private static Context c;
	// the foursquare client_id and the client_secret

	// ============== Bliinder KEYS ====================//
	final String CLIENT_ID = "RF51SSSHFMQPSSERQK2PZKX130I1R50RTX2KVZASKAKPMDQE";
	final String CLIENT_SECRET = "U5GVWO2QNHMVFNSCLI2OZCNHRWXG1IVKOMCUHU2UIVULCHG4";

	// we will need to take the latitude and the logntitude from a certain point
	// this is the center of Be'er Sheva.
	String latitude/* = "31.252973" */;
	String longtitude/* = "34.791462" */;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		c = getApplicationContext();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_foursquare);
		this.setVisible(false);
	//comment
		/*Server.runningAFSA.decrementAndGet();*/
		activity = this;

		latitude = new Double(Data.location.getLatitude()).toString();
		longtitude = new Double(Data.location.getLongitude()).toString();
		// start the AsyncTask that makes the call for the venus search.
		new fourquare().execute();
	}

	private class fourquare extends AsyncTask<View, Void, String> {
		String temp;

		@Override
		protected String doInBackground(View... urls) {
			if (Server.itsADateList.size() > 0) {
				// Determine the type of date by the current date preferences.
				BliinderDate date = Server.itsADateList.get(0);
				date.setPlaceType(FillDetails.getDatePlaceType(
						date.getMyPref(), date.getPartnerPref(),
						date.getPartner()));
				String typeString = date.getPlaceType().toString();
				String fourquareType = "";
				if (typeString.equals(PlaceType.BAR.toString())) {
					fourquareType = BAR;
				} else if (typeString.equals(PlaceType.CINEMA.toString())) {
					fourquareType = CINEMA;
				} else {
					fourquareType = RESTAURANT;
				}
				// make Call to the url - ask for the place which is from a
				// suitable
				// type.
				temp = makeCall("https://api.foursquare.com/v2/venues/search?client_id="
						+ CLIENT_ID
						+ "&client_secret="
						+ CLIENT_SECRET
						+ "&v=20130815&ll="
						+ latitude
						+ ","
						+ longtitude
						+ "&categoryId=" + fourquareType);
			}
			return "";
		}

		@Override
		protected void onPreExecute() {
			// we can start a progress bar here
		}

		@Override
		protected void onPostExecute(String result) {
			if (temp == null) {
				// we have an error to the call
				// we can also stop the progress bar
			} else {
				// all things went right
				// parseFoursquare venues search result
				// and at the end (after updating the date place) save the dates
				// in the parse server.
				parseFoursquare(temp);

				// After updating the dates in the server (with their location)
				// We need to reset the itsADateList to an empty list.
				// Server.itsADateList = new ArrayList<BliinderDate>();
			}
			AndroidFoursquareFromMyBliinderDates.activity.finish();
		}
	}

	public static String makeCall(String url) {

		// string buffers the url
		StringBuffer buffer_string = new StringBuffer(url);
		String replyString = "";

		// instanciate an HttpClient
		HttpClient httpclient = new DefaultHttpClient();
		// instanciate an HttpGet
		HttpGet httpget = new HttpGet(buffer_string.toString());

		try {
			// get the responce of the httpclient execution of the url
			HttpResponse response = httpclient.execute(httpget);
			InputStream is = response.getEntity().getContent();

			// buffer input stream the result
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(20);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}
			// the result as a string is ready for parsing
			replyString = new String(baf.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// trim the whitespaces
		return replyString.trim();
	}

	private void parseFoursquare(final String response) {

		ArrayList<FoursquareVenue> temp = new ArrayList<FoursquareVenue>();
		try {

			// make an jsonObject in order to parse the response
			JSONObject jsonObject = new JSONObject(response);

			// make an jsonObject in order to parse the response
			if (jsonObject.has("response")) {
				if (jsonObject.getJSONObject("response").has("venues")) {
					JSONArray jsonArray = jsonObject.getJSONObject("response")
							.getJSONArray("venues");

					for (int i = 0; i < jsonArray.length(); i++) {
						FoursquareVenue poi = new FoursquareVenue();
						if (jsonArray.getJSONObject(i).has("name")) {
							poi.setName(jsonArray.getJSONObject(i).getString(
									"name"));

							if (jsonArray.getJSONObject(i).has("location")) {
								if (jsonArray.getJSONObject(i)
										.getJSONObject("location")
										.has("address")) {
									if (jsonArray.getJSONObject(i)
											.getJSONObject("location")
											.has("city")) {
										poi.setCity(jsonArray.getJSONObject(i)
												.getJSONObject("location")
												.getString("city"));
										poi.setLongitude(jsonArray
												.getJSONObject(i)
												.getJSONObject("location")
												.getString("lng"));
										poi.setLatitude(jsonArray
												.getJSONObject(i)
												.getJSONObject("location")
												.getString("lat"));
									}
									if (jsonArray.getJSONObject(i).has(
											"categories")) {
										if (jsonArray.getJSONObject(i)
												.getJSONArray("categories")
												.length() > 0) {
											if (jsonArray.getJSONObject(i)
													.getJSONArray("categories")
													.getJSONObject(0)
													.has("icon")) {
												poi.setCategory(jsonArray
														.getJSONObject(i)
														.getJSONArray(
																"categories")
														.getJSONObject(0)
														.getString("name"));
											}
										}
									}
									temp.add(poi);

								}
							}
						}
					}

				}

			}
			BliinderDate d = Server.itsADateList.get(0);
			String dateDescription = "No place found...";
			if (temp.size() > 0) {
				int index = (int) (Math.random() * (double) (temp.size() - 1));
				dateDescription = temp.get(index).getName() + ", "
				// + temp.get(index).getCategory() + ", "
						+ temp.get(index).getCity();
				d.setDateLocation(dateDescription);
				d.setLocation(
						Double.parseDouble(temp.get(index).getLatitude()),
						Double.parseDouble(temp.get(index).getLongitude()));
			}

			Server.saveUpcomingDate(d);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Server.itsADateList.remove(0);
		// if (Server.itsADateList.size() > 0) {
		// c.startActivity(new Intent(c,
		// AndroidFoursquareFromMyBliinderDates.class));
		// }
	}
}
