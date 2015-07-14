//package com.ysoserious.bliinder.utils;
//
//import java.io.BufferedInputStream;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.util.ByteArrayBuffer;
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import android.app.ListActivity;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ArrayAdapter;
//
//public class DatePlaceUtils extends ListActivity {
//	ArrayList<FoursquareVenue> venuesList;
//
//	private static String RESTAURANT = "4d4b7105d754a06374d81259"; // Food
//	private static String BAR = "4d4b7105d754a06376d81259"; // NightLife Spot
//	private static String CINEMA = "4bf58dd8d48988d17f941735"; // Movie Theater
//
//	// the foursquare client_id and the client_secret
//
//	// ============== Bliinder KEYS ====================//
//	final String CLIENT_ID = "RF51SSSHFMQPSSERQK2PZKX130I1R50RTX2KVZASKAKPMDQE";
//	final String CLIENT_SECRET = "U5GVWO2QNHMVFNSCLI2OZCNHRWXG1IVKOMCUHU2UIVULCHG4";
//
//	// we will need to take the latitude and the logntitude from a certain point
//	// this is the center of Be'er Sheva.
//	private static String latitude = "31.252973";
//	private static String longtitude = "34.791462";
//
//	ArrayAdapter<String> myAdapter;
//
//	public static String getDateLocation(String _latitude, String _longtitude) {
//		latitude = _latitude;
//		longtitude = _longtitude;
//		new fourquare().execute();
//		return null;
//	}
//
//	private class fourquare extends AsyncTask<View, Void, String> {
//
//		String temp;
//
//		@Override
//		protected String doInBackground(View... urls) {
//			// make Call to the url
//			temp = makeCall("https://api.foursquare.com/v2/venues/search?client_id="
//					+ CLIENT_ID
//					+ "&client_secret="
//					+ CLIENT_SECRET
//					+ "&v=20130815&ll="
//					+ latitude
//					+ ","
//					+ longtitude
//					+ "&categoryId=" + RESTAURANT + "," + BAR + "," + CINEMA);
//			return "";
//		}
//
//		@Override
//		protected void onPreExecute() {
//			// we can start a progress bar here
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//			if (temp == null) {
//				// we have an error to the call
//				// we can also stop the progress bar
//			} else {
//				// all things went right
//
//				// parseFoursquare venues search result
//				String dateLocation = (String) parseFoursquare(temp);
//
//			}
//		}
//	}
//
//	public static String makeCall(String url) {
//
//		// string buffers the url
//		StringBuffer buffer_string = new StringBuffer(url);
//		String replyString = "";
//
//		// instanciate an HttpClient
//		HttpClient httpclient = new DefaultHttpClient();
//		// instanciate an HttpGet
//		HttpGet httpget = new HttpGet(buffer_string.toString());
//
//		try {
//			// get the responce of the httpclient execution of the url
//			HttpResponse response = httpclient.execute(httpget);
//			InputStream is = response.getEntity().getContent();
//
//			// buffer input stream the result
//			BufferedInputStream bis = new BufferedInputStream(is);
//			ByteArrayBuffer baf = new ByteArrayBuffer(20);
//			int current = 0;
//			while ((current = bis.read()) != -1) {
//				baf.append((byte) current);
//			}
//			// the result as a string is ready for parsing
//			replyString = new String(baf.toByteArray());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		// trim the whitespaces
//		return replyString.trim();
//	}
//
//	private static String parseFoursquare(final String response) {
//
//		ArrayList<FoursquareVenue> temp = new ArrayList<FoursquareVenue>();
//		try {
//
//			// make an jsonObject in order to parse the response
//			JSONObject jsonObject = new JSONObject(response);
//
//			// make an jsonObject in order to parse the response
//			if (jsonObject.has("response")) {
//				if (jsonObject.getJSONObject("response").has("venues")) {
//					JSONArray jsonArray = jsonObject.getJSONObject("response")
//							.getJSONArray("venues");
//
//					for (int i = 0; i < jsonArray.length(); i++) {
//						FoursquareVenue poi = new FoursquareVenue();
//						if (jsonArray.getJSONObject(i).has("name")) {
//							poi.setName(jsonArray.getJSONObject(i).getString(
//									"name"));
//
//							if (jsonArray.getJSONObject(i).has("location")) {
//								if (jsonArray.getJSONObject(i)
//										.getJSONObject("location")
//										.has("address")) {
//									if (jsonArray.getJSONObject(i)
//											.getJSONObject("location")
//											.has("city")) {
//										poi.setCity(jsonArray.getJSONObject(i)
//												.getJSONObject("location")
//												.getString("city"));
//									}
//									if (jsonArray.getJSONObject(i).has(
//											"categories")) {
//										if (jsonArray.getJSONObject(i)
//												.getJSONArray("categories")
//												.length() > 0) {
//											if (jsonArray.getJSONObject(i)
//													.getJSONArray("categories")
//													.getJSONObject(0)
//													.has("icon")) {
//												poi.setCategory(jsonArray
//														.getJSONObject(i)
//														.getJSONArray(
//																"categories")
//														.getJSONObject(0)
//														.getString("name"));
//											}
//										}
//									}
//									temp.add(poi);
//								}
//							}
//						}
//					}
//
//					if (temp.size() <= 0) {
//						return "No place found...";
//					}
//					int index = (int) Math.random() * temp.size();
//					return temp.get(index).getName() + ", "
//							+ temp.get(index).getCategory() + ""
//							+ temp.get(index).getCity();
//
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "No place found...";
//		}
//		return "No place found...";
//	}
//}
