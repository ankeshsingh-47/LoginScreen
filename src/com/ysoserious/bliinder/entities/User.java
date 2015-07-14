package com.ysoserious.bliinder.entities;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.StrictMode;
import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.parse.ParseObject;
import com.ysoserious.bliinder.utils.Data;

public class User {
	private static final String TAG = "UserClass";
	private String name;
	private String facebookId;
	private List<Bitmap> profilePic;
	private boolean isFemale;
	private int relationshipType;
	private boolean wantMale;
	private String profession;
	private String about;
	private int yearOfBirth;
	private Location location;

	public User(List<Bitmap> profilePic, boolean isFemale, int type,
			String facebookId, Location location, String profession,
			int yearOfBirth, boolean wantMale) {
		this.profilePic = profilePic;
		this.isFemale = isFemale;
		this.relationshipType = type;
		this.facebookId = facebookId;
		this.location = location;
		this.profession = profession;
		this.yearOfBirth = yearOfBirth;
		this.wantMale = wantMale;
	}

	public User() {
		// TODO Auto-generated constructor stub

	}

	public User(List<Bitmap> profilePic, boolean isFemale, int type,
			String facebookId, String profession, int yearOfBirth,
			boolean wantMale) {
		this.profilePic = profilePic;
		this.isFemale = isFemale;
		this.relationshipType = type;
		this.facebookId = facebookId;
		this.location = null;
		this.profession = profession;
		this.yearOfBirth = yearOfBirth;
		this.wantMale = wantMale;
	}

	public User(ParseObject po) {
		facebookId = po.getString("ID");
		isFemale = "FEMALE".equals(po.getString("gender"));
		relationshipType = po.getInt("relationType");
		wantMale = "MALE".equals(po.getString("want"));
		yearOfBirth = po.getInt("yearOfBirth");
		/* make the API call for user info */
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		Response response = (new Request(Data.facebookSession,
				"/" + facebookId, null, HttpMethod.GET)).executeAndWait();
		if (response.getError() != null) {
			Log.i(TAG, response.getError().toString());
			return;
		}
		try {
			JSONObject user = new JSONObject(response.getRawResponse());
			name = (user.get("first_name")).toString();
			about = (user.get("bio")).toString();
		} catch (JSONException e) {
			if (e.getMessage().contains("bio"))
				System.out.println("That suppose to be ok, once the"
						+ "user will be on the app that should work.");
		}

	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public int getAge() {
		return Calendar.getInstance().get(Calendar.YEAR) - yearOfBirth;
	}

	public int getYearOfBirth() {
		return yearOfBirth;
	}

	public void setYearOfBirth(int age) {
		this.yearOfBirth = age;
	}

	public List<Bitmap> getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(List<Bitmap> profilePic) {
		this.profilePic = profilePic;
	}

	public boolean lookingForMale() {
		return wantMale;
	}

	public void setLookingForMale(boolean wantMale) {
		this.wantMale = wantMale;
	}

	public boolean isFemale() {
		return isFemale;
	}

	public void setFemale(boolean isFemale) {
		this.isFemale = isFemale;
	}

	public int getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(int type) {
		this.relationshipType = type;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public void setFemale(String string) {
		isFemale = getGenderFromString(string);
	}

	private boolean getGenderFromString(String maleOrFemale) {
		if (maleOrFemale == null)
			return false;
		if (maleOrFemale.equals("male"))
			return false;
		return true;
	}

	public void setYearOfBirth(String yearString) {
		yearOfBirth = Integer.parseInt(yearString);
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public String getName() {
		return name;
	}

	public String getFirstName() {
		return (name.split(" "))[0];
	}

	public void setName(String fullName) {
		name = fullName;
	}

	public static List<User> parseObjectListToUserList(
			List<ParseObject> parseObjectList) {
		List<User> users = new LinkedList<User>();
		for (ParseObject po : parseObjectList) {
			users.add(new User(po));
		}
		return users;
	}
}
