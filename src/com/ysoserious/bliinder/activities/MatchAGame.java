package com.ysoserious.bliinder.activities;

import java.util.List;

import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class MatchAGame {

	String matchClassName = "Match";
	String matchLockKey = "gameLock";
	int matchLockKeyInitial = 1;
	int matchLockKeyMax = 2;
	String CREATOR = "creator";
	String JOINER = "joiner";
	String matchStatusKey = "gameStatus";
	String matchStatusKeyWaiting = "waiting";
	String matchStatusKeyInProgress = "inprogress";
	String matchStatusKeyFinished = "finished";
	String matchStatusKeyCancelled = "cancelled";

	void joinGame(final String myId) {
		// Find number of available games.
		ParseQuery<ParseObject> matchQuery = ParseQuery.getQuery("Match");
		matchQuery.whereEqualTo(matchStatusKey, matchStatusKeyWaiting);
		// Making sure he won't play with yourself.
		matchQuery.whereNotEqualTo(CREATOR, myId);

		matchQuery.countInBackground(new CountCallback() {

			@Override
			public void done(int waitingPalyers, ParseException e) {
				if (waitingPalyers > 0) {
					fetchRandomGameAndTryToJoin(myId, waitingPalyers);
				} else {
					// There are no player waiting to play so let's be the
					// first!
					createNewMatch(myId);
				}
			}

			private void fetchRandomGameAndTryToJoin(final String player,
					int count) {
				// If matches were found, fetch random one
				ParseQuery<ParseObject> matchQuery = ParseQuery
						.getQuery("Match");
				matchQuery.whereEqualTo(matchStatusKey, matchStatusKeyWaiting);
				// Making sure he won't play with yourself.
				matchQuery.whereNotEqualTo(CREATOR, player);
				matchQuery.setLimit(1);
				// random num is 0 to count-1 so we can use it as skip
				// directly.
				matchQuery.setSkip((int) Math.floor(Math.random() * count));
				matchQuery.findInBackground(new FindCallback<ParseObject>() {

					@Override
					public void done(List<ParseObject> results, ParseException e) {
						if (results.size() > 0)
							// Attempt to join fetched game
							joinMatchAttempt(results.get(0), player);
						else
							// If something happened to the match
							// give up and create a new one
							createNewMatch(player);
					}

				});
			}

		});
	}

	private void joinMatchAttempt(final ParseObject match, final String player) {
		// get random match returned
		match.increment(matchLockKey);
		match.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				// Check if the join succeeded
				if (((Integer) match.get(matchLockKey))
						.compareTo(matchLockKeyMax) <= 0) {
					match.put(JOINER, player);
					match.put(matchStatusKey, matchStatusKeyInProgress);
					match.saveInBackground(new SaveCallback() {

						@Override
						public void done(ParseException e) {
							System.out.println("Matched to game");
						}
					});
				} else
					// If someone else joined game
					// first, give up and create new
					// one
					createNewMatch(player);

			}
		});
	}

	private void createNewMatch(String myId) {
		ParseObject match = new ParseObject(matchClassName);
		match.put(matchLockKey, matchLockKeyInitial);
		// challenger is player 1
		match.put(CREATOR, myId);
		// wait for second player
		match.put(matchStatusKey, matchStatusKeyWaiting);
		match.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException arg0) {
				// TODO Auto-generated method stub

			}
		});
	}
}
